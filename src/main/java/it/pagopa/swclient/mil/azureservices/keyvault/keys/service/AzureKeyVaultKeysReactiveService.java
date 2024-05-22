/*
 * AzureKeyVaultKeysReactiveService.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import java.lang.reflect.Method;
import java.time.Instant;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClient;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysReactiveClient;
import it.pagopa.swclient.mil.azureservices.util.NoAround;
import it.pagopa.swclient.mil.azureservices.util.WebAppExcUtils;
import jakarta.enterprise.context.ApplicationScoped;
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
	private AzureIdentityReactiveClient identityClient;

	/*
	 * 
	 */
	private AzureKeyVaultKeysReactiveClient keysClient;

	/*
	 * 
	 */
	private AccessToken accessToken;

	/**
	 * 
	 * @param identityClient
	 * @param keysClient
	 */
	AzureKeyVaultKeysReactiveService(
		@RestClient AzureIdentityReactiveClient identityClient,
		@RestClient AzureKeyVaultKeysReactiveClient keysClient) {
		this.identityClient = identityClient;
		this.keysClient = keysClient;
	}

	/**
	 * 
	 * @return
	 */
	private Uni<Void> getNewAccessTokenAndCacheIt() {
		Log.debug("Get new access token");
		return identityClient.getAccessToken(Scope.VAULT)
			.invoke(newAccessToken -> {
				Log.trace("Caching access token");
				accessToken = newAccessToken;
			})
			.replaceWithVoid();
	}

	/**
	 * 
	 * @return
	 */
	private Uni<Void> getAccessToken() {
		if (accessToken != null && accessToken.getExpiresOn() > Instant.now().getEpochSecond()) {
			Log.trace("Cached access token is going to be used");
			return Uni.createFrom().voidItem();
		} else {
			Log.debug("There's no cached access token or it is expired");
			return getNewAccessTokenAndCacheIt();
		}
	}

	/**
	 * 
	 */
	@NoAround
	void resetCachedAccessToken() {
		accessToken = null;
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
		NoAround noAround = method.getAnnotation(NoAround.class);
		if (noAround == null) {
			Log.tracef("Around invoke: %s.%s", context.getTarget().getClass().getSimpleName(), method.getName());
			return getAccessToken()
				.chain(() -> proceed(context))
				.onFailure(WebAppExcUtils::isUnauthorizedOrForbidden) // On 401 or 403...
				.recoverWithUni(f -> {
					Log.debug("Recovering");
					return getNewAccessTokenAndCacheIt() // ...get a new access token...
						.chain(() -> proceed(context));
				}); // ...and retry!
		} else {
			Log.tracef("No around: %s.%s", context.getTarget().getClass().getSimpleName(), method.getName());
			return proceed(context);
		}
	}

	/**
	 * 
	 * @param keyName
	 * @param keyCreateParameters
	 * @return
	 */
	public Uni<KeyBundle> createKey(String keyName, KeyCreateParameters keyCreateParameters) {
		return keysClient.createKey(accessToken.getValue(), keyName, keyCreateParameters);
	}

	/**
	 * 
	 * @return
	 */
	public Uni<KeyListResult> getKeys() {
		return keysClient.getKeys(accessToken.getValue());
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	public Uni<KeyBundle> getKey(String keyName, String keyVersion) {
		return keysClient.getKey(accessToken.getValue(), keyName, keyVersion);
	}

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	public Uni<KeyListResult> getKeyVersions(String keyName) {
		return keysClient.getKeyVersions(accessToken.getValue(), keyName);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keySignParameters
	 * @return
	 */
	public Uni<KeyOperationResult> sign(String keyName, String keyVersion, KeySignParameters keySignParameters) {
		return keysClient.sign(accessToken.getValue(), keyName, keyVersion, keySignParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyVerifyParameters
	 * @return
	 */
	public Uni<KeyVerifyResult> verify(String keyName, String keyVersion, KeyVerifyParameters keyVerifyParameters) {
		return keysClient.verify(accessToken.getValue(), keyName, keyVersion, keyVerifyParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyOperationParameters
	 * @return
	 */
	public Uni<KeyOperationResult> encrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.encrypt(accessToken.getValue(), keyName, keyVersion, keyOperationParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyOperationParameters
	 * @return
	 */
	public Uni<KeyOperationResult> decrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.decrypt(accessToken.getValue(), keyName, keyVersion, keyOperationParameters);
	}
}