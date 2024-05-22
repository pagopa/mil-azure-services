/*
 * AzureKeyVaultKeysService.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import java.lang.reflect.Method;
import java.time.Instant;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityClient;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysClient;
import it.pagopa.swclient.mil.azureservices.util.NoAround;
import it.pagopa.swclient.mil.azureservices.util.WebAppExcUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.WebApplicationException;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyVaultKeysService {
	/*
	 * 
	 */
	private AzureIdentityClient identityClient;

	/*
	 * 
	 */
	private AzureKeyVaultKeysClient keysClient;

	/*
	 * 
	 */
	private AccessToken accessToken;

	/**
	 * 
	 * @param identityClient
	 * @param keysClient
	 */
	AzureKeyVaultKeysService(
		@RestClient AzureIdentityClient identityClient,
		@RestClient AzureKeyVaultKeysClient keysClient) {
		this.identityClient = identityClient;
		this.keysClient = keysClient;
	}

	/**
	 * 
	 */
	private void getNewAccessTokenAndCacheIt() {
		Log.debug("Get new access token");
		accessToken = identityClient.getAccessToken(Scope.VAULT);
	}

	/**
	 * 
	 */
	private void getAccessToken() {
		if (accessToken != null && accessToken.getExpiresOn() > Instant.now().getEpochSecond()) {
			Log.trace("Cached access token is going to be used");
		} else {
			Log.debug("There's no cached access token or it is expired");
			getNewAccessTokenAndCacheIt();
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
	 * @throws Exception
	 */
	@AroundInvoke
	Object authenticate(InvocationContext context) throws Exception {
		Method method = context.getMethod();
		NoAround noAround = method.getAnnotation(NoAround.class);
		if (noAround == null) {
			Log.tracef("Around invoke: %s.%s", context.getTarget().getClass().getSimpleName(), method.getName());
			getAccessToken();
			try {
				return context.proceed();
			} catch (WebApplicationException e) {
				if (WebAppExcUtils.isUnauthorizedOrForbidden(e)) { // On 401 or 403...
					Log.debug("Recovering");
					getNewAccessTokenAndCacheIt(); // ...get a new access token...
					return context.proceed(); // ...and retry!
				} else {
					throw e;
				}
			}
		} else {
			Log.tracef("No around: %s.%s", context.getTarget().getClass().getSimpleName(), method.getName());
			return context.proceed();
		}
	}

	/**
	 * 
	 * @param keyName
	 * @param keyCreateParameters
	 * @return
	 */
	public KeyBundle createKey(String keyName, KeyCreateParameters keyCreateParameters) {
		return keysClient.createKey(accessToken.getValue(), keyName, keyCreateParameters);
	}

	/**
	 * 
	 * @return
	 */
	public KeyListResult getKeys() {
		return keysClient.getKeys(accessToken.getValue());
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	public KeyBundle getKey(String keyName, String keyVersion) {
		return keysClient.getKey(accessToken.getValue(), keyName, keyVersion);
	}

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	public KeyListResult getKeyVersions(String keyName) {
		return keysClient.getKeyVersions(accessToken.getValue(), keyName);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keySignParameters
	 * @return
	 */
	public KeyOperationResult sign(String keyName, String keyVersion, KeySignParameters keySignParameters) {
		return keysClient.sign(accessToken.getValue(), keyName, keyVersion, keySignParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyVerifyParameters
	 * @return
	 */
	public KeyVerifyResult verify(String keyName, String keyVersion, KeyVerifyParameters keyVerifyParameters) {
		return keysClient.verify(accessToken.getValue(), keyName, keyVersion, keyVerifyParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyOperationParameters
	 * @return
	 */
	public KeyOperationResult encrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.encrypt(accessToken.getValue(), keyName, keyVersion, keyOperationParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyOperationParameters
	 * @return
	 */
	public KeyOperationResult decrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.decrypt(accessToken.getValue(), keyName, keyVersion, keyOperationParameters);
	}
}