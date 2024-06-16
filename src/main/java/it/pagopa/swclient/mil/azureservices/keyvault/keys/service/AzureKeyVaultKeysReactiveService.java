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
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyVaultKeysReactiveService {
	/*
	 * 
	 */
	@ConfigProperty(name = "azure-key-vault-keys.backoff.initial-duration", defaultValue = "1")
	int initialBackoff;

	/*
	 * 
	 */
	@ConfigProperty(name = "azure-key-vault-keys.backoff.jitter", defaultValue = "0.2")
	double jitter;

	/*
	 * 
	 */
	@ConfigProperty(name = "azure-key-vault-keys.backoff.number-of-attempts", defaultValue = "3")
	int numberOfAttempts;

	/*
	 * 
	 */
	private AzureIdentityReactiveService identityService;

	/*
	 * 
	 */
	@RestClient
	AzureKeyVaultKeysReactiveClient keysClient;

	/*
	 * 
	 */
	private String accessTokenValue;

	/**
	 * 
	 * @param identityService
	 */
	@Inject
	AzureKeyVaultKeysReactiveService(
		AzureIdentityReactiveService identityService) {
		this.identityService = identityService;
	}

	/**
	 * 
	 * @param context
	 * @return
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
	 * 
	 * @param context
	 * @return
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
	 * 
	 * @param keyName
	 * @param keyCreateParameters
	 * @return
	 */
	public Uni<KeyBundle> createKey(String keyName, KeyCreateParameters keyCreateParameters) {
		return keysClient.createKey(accessTokenValue, keyName, keyCreateParameters);
	}

	/**
	 * 
	 * @return
	 */
	public Uni<KeyListResult> getKeys() {
		Log.trace("Get keys");
		return keysClient.getKeys(accessTokenValue);
	}

	/**
	 * 
	 * @param skiptoken
	 * @return
	 */
	public Uni<KeyListResult> getKeys(String skiptoken) {
		return keysClient.getKeys(accessTokenValue, skiptoken);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	public Uni<KeyBundle> getKey(String keyName, String keyVersion) {
		return keysClient.getKey(accessTokenValue, keyName, keyVersion);
	}

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	public Uni<KeyListResult> getKeyVersions(String keyName) {
		return keysClient.getKeyVersions(accessTokenValue, keyName);
	}

	/**
	 * 
	 * @param keyName
	 * @param skiptoken
	 * @return
	 */
	public Uni<KeyListResult> getKeyVersions(String keyName, String skiptoken) {
		return keysClient.getKeyVersions(accessTokenValue, keyName, skiptoken);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keySignParameters
	 * @return
	 */
	public Uni<KeyOperationResult> sign(String keyName, String keyVersion, KeySignParameters keySignParameters) {
		return keysClient.sign(accessTokenValue, keyName, keyVersion, keySignParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyVerifyParameters
	 * @return
	 */
	public Uni<KeyVerifyResult> verify(String keyName, String keyVersion, KeyVerifyParameters keyVerifyParameters) {
		return keysClient.verify(accessTokenValue, keyName, keyVersion, keyVerifyParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyOperationParameters
	 * @return
	 */
	public Uni<KeyOperationResult> encrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.encrypt(accessTokenValue, keyName, keyVersion, keyOperationParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyOperationParameters
	 * @return
	 */
	public Uni<KeyOperationResult> decrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.decrypt(accessTokenValue, keyName, keyVersion, keyOperationParameters);
	}
}