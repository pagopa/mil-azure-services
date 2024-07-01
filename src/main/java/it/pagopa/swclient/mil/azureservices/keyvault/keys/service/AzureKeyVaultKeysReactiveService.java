/*
 * AzureKeyVaultKeysReactiveService.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import java.lang.reflect.Method;
import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysReactiveClient;
import it.pagopa.swclient.mil.azureservices.util.WebAppExcUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

/**
 * <p>
 * This service is a kind of wrapper of
 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysReactiveClient
 * AzureKeyVaultKeysReactiveClient} which implements:
 * </p>
 * <ul>
 * <li>the retrieving, caching (done by means of
 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService
 * AzureIdentityReactiveService}) and renewal (when it expires or when used the resource API returns
 * 401 or 403) of the access token from Microsoft Entra ID that will be used with Azure Key Vault
 * (by means of
 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysReactiveClient
 * AzureKeyVaultKeysReactiveClient});</li>
 * <li>the retrying with exponential back-off in case of 429 from Azure Key Vault.</li>
 * </ul>
 * <p>
 * To use this service, the {@code application.properties} should have the definition of the
 * following properties to control the back-off policy:
 * </p>
 * <ul>
 * <li>{@code azure-key-vault-keys.backoff.initial-duration}</li>
 * <li>{@code azure-key-vault-keys.backoff.jitter}</li>
 * <li>{@code azure-key-vault-keys.backoff.number-of-attempts}</li>
 * </ul>
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyVaultKeysReactiveService {
	/**
	 * <p>
	 * Initial back-off duration in seconds.
	 * </p>
	 */
	@ConfigProperty(name = "azure-key-vault-keys.backoff.initial-duration", defaultValue = "1")
	int initialBackoff;

	/**
	 * <p>
	 * Jitter of back-off policy: it must be in [0; 1]
	 * </p>
	 */
	@ConfigProperty(name = "azure-key-vault-keys.backoff.jitter", defaultValue = "0.2")
	double jitter;

	/**
	 * <p>
	 * Number of retries.
	 * </p>
	 */
	@ConfigProperty(name = "azure-key-vault-keys.backoff.number-of-attempts", defaultValue = "3")
	int numberOfAttempts;

	/**
	 * <p>
	 * Service to retrieve the access token from Microsoft Entra ID.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService
	 *      AzureIdentityReactiveService
	 */
	private AzureIdentityReactiveService identityService;

	/**
	 * <p>
	 * REST client to use Azure Key Vault.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysReactiveClient
	 *      AzureKeyVaultKeysReactiveClient
	 */
	@RestClient
	AzureKeyVaultKeysReactiveClient keysClient;

	/**
	 * <p>
	 * Cached access token.
	 * </p>
	 */
	private String accessTokenValue;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 * 
	 * @param identityService {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService
	 *                        AzureIdentityReactiveService}
	 */
	@Inject
	AzureKeyVaultKeysReactiveService(AzureIdentityReactiveService identityService) {
		this.identityService = identityService;
	}

	/**
	 * <p>
	 * Transforms {@link java.lang.Exception Exception} in {@link java.lang.RuntimeException
	 * RuntimeException} to allow handling with Mutiny.
	 * </p>
	 * 
	 * @param context {@link jakarta.interceptor.InvocationContext InvocationContext}
	 * @return Object returned by the target method.
	 */
	@SuppressWarnings("unchecked")
	private Uni<Object> proceed(InvocationContext context) {
		Log.trace("Proceed");
		try {
			return (Uni<Object>) (context.proceed());
		} catch (Exception e) {
			throw new RuntimeException(e); // NOSONAR
		}
	}

	/**
	 * <p>
	 * This method decorates the others:
	 * </p>
	 * <ul>
	 * <li>retrieving the access token from Microsoft Entra ID by means of
	 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService
	 * AzureIdentityReactiveService};</li>
	 * <li>renewing of the access token from Microsoft Entra ID the invoked target API returns 401 or
	 * 403;</li>
	 * <li>retrying with exponential back-off in case of 429 from Azure Key Vault.</li>
	 * </ul>
	 * 
	 * @param context {@link jakarta.interceptor.InvocationContext InvocationContext}
	 * @return Object returned by the target method.
	 */
	@AroundInvoke
	Object authenticate(InvocationContext context) {
		Method method = context.getMethod();
		Log.tracef("Around invoke: %s.%s", context.getTarget().getClass().getSimpleName(), method.getName());
		return identityService.getAccessToken(Scope.VAULT)
			.invoke(accessToken -> accessTokenValue = accessToken.getValue())
			.chain(() -> proceed(context))
			.onFailure(WebAppExcUtils::isUnauthorizedOrForbidden) // On 401 or 403...
			.recoverWithUni(f -> {
				Log.debug("Recovering");
				return identityService.getNewAccessTokenAndCacheIt(Scope.VAULT) // ...get a new access token...
					.invoke(accessToken -> accessTokenValue = accessToken.getValue())
					.chain(() -> proceed(context));
			}) // ...and retry!
			.onFailure(WebAppExcUtils::isTooManyRequests) // On 429...
			.retry() // ...retry...
			.withBackOff(Duration.ofSeconds(initialBackoff)) // ...with backoff...
			.withJitter(jitter)
			.atMost(numberOfAttempts);
	}

	/**
	 * <p>
	 * Creates a new key, stores it, then returns key parameters and attributes to the client.
	 * </p>
	 * 
	 * @param keyName             The name for the new key. Regex pattern: ^[0-9a-zA-Z-]+$
	 * @param keyCreateParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters
	 *                            KeyCreateParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle KeyBundle}
	 */
	public Uni<KeyBundle> createKey(String keyName, KeyCreateParameters keyCreateParameters) {
		return keysClient.createKey(accessTokenValue, keyName, keyCreateParameters);
	}

	/**
	 * <p>
	 * Lists keys in the specified vault.
	 * </p>
	 * 
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	public Uni<KeyListResult> getKeys() {
		Log.trace("Get keys");
		return keysClient.getKeys(accessTokenValue);
	}

	/**
	 * <p>
	 * Lists keys in the specified vault.
	 * </p>
	 * 
	 * @param skiptoken Token to handle paging.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	public Uni<KeyListResult> getKeys(String skiptoken) {
		return keysClient.getKeys(accessTokenValue, skiptoken);
	}

	/**
	 * <p>
	 * Returns the public part of a stored key.
	 * </p>
	 * 
	 * @param keyName    The name of the key to get.
	 * @param keyVersion The version of the key.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle KeyBundle}
	 */
	public Uni<KeyBundle> getKey(String keyName, String keyVersion) {
		return keysClient.getKey(accessTokenValue, keyName, keyVersion);
	}

	/**
	 * <p>
	 * Returns a list of individual key versions with the same key name.
	 * </p>
	 * 
	 * @param keyName The name of the key.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	public Uni<KeyListResult> getKeyVersions(String keyName) {
		return keysClient.getKeyVersions(accessTokenValue, keyName);
	}

	/**
	 * <p>
	 * Returns a list of individual key versions with the same key name.
	 * </p>
	 * 
	 * @param keyName   The name of the key.
	 * @param skiptoken Token to handle paging.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	public Uni<KeyListResult> getKeyVersions(String keyName, String skiptoken) {
		return keysClient.getKeyVersions(accessTokenValue, keyName, skiptoken);
	}

	/**
	 * <p>
	 * Creates a signature from a digest using the specified key.
	 * </p>
	 * 
	 * @param keyName           The name of the key.
	 * @param keyVersion        The version of the key.
	 * @param keySignParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters
	 *                          KeySignParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult
	 *         KeyOperationResult}
	 */
	public Uni<KeyOperationResult> sign(String keyName, String keyVersion, KeySignParameters keySignParameters) {
		return keysClient.sign(accessTokenValue, keyName, keyVersion, keySignParameters);
	}

	/**
	 * <p>
	 * Verifies a signature using a specified key.
	 * </p>
	 * 
	 * @param keyName             The name of the key.
	 * @param keyVersion          The version of the key.
	 * @param keyVerifyParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters
	 *                            KeyVerifyParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult
	 *         KeyVerifyResult}
	 */
	public Uni<KeyVerifyResult> verify(String keyName, String keyVersion, KeyVerifyParameters keyVerifyParameters) {
		return keysClient.verify(accessTokenValue, keyName, keyVersion, keyVerifyParameters);
	}

	/**
	 * <p>
	 * Encrypts an arbitrary sequence of bytes using an encryption key that is stored in a key vault.
	 * </p>
	 * 
	 * @param keyName                The name of the key.
	 * @param keyVersion             The version of the key.
	 * @param keyOperationParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters
	 *                               KeyOperationParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult
	 *         KeyOperationResult}
	 */
	public Uni<KeyOperationResult> encrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.encrypt(accessTokenValue, keyName, keyVersion, keyOperationParameters);
	}

	/**
	 * <p>
	 * Decrypts a single block of encrypted data.
	 * </p>
	 * 
	 * @param keyName                The name of the key.
	 * @param keyVersion             The version of the key.
	 * @param keyOperationParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters
	 *                               KeyOperationParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult
	 *         KeyOperationResult}
	 */
	public Uni<KeyOperationResult> decrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.decrypt(accessTokenValue, keyName, keyVersion, keyOperationParameters);
	}
}