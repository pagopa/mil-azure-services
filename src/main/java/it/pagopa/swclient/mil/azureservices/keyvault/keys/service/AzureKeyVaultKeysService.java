/*
 * AzureKeyVaultKeysService.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import java.lang.reflect.Method;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysClient;
import it.pagopa.swclient.mil.azureservices.util.WebAppExcUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.WebApplicationException;

/**
 * 
 * @deprecated The non-reactive version doen't yet have all features of reactive one.
 * @author Antonio Tarricone
 */
@ApplicationScoped
@Deprecated(forRemoval = false)
public class AzureKeyVaultKeysService {
	/*
	 * 
	 */
	private AzureIdentityService identityService;

	/*
	 * 
	 */
	private AzureKeyVaultKeysClient keysClient;

	/*
	 * 
	 */
	private String accessTokenValue;

	/**
	 * 
	 * @param identityService
	 * @param keysClient
	 */
	@Inject
	AzureKeyVaultKeysService(
		AzureIdentityService identityService,
		@RestClient AzureKeyVaultKeysClient keysClient) {
		this.identityService = identityService;
		this.keysClient = keysClient;
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
		Log.tracef("Around invoke: %s.%s", context.getTarget().getClass().getSimpleName(), method.getName());
		accessTokenValue = identityService.getAccessToken(Scope.VAULT).getValue();
		try {
			return context.proceed();
		} catch (WebApplicationException e) {
			if (WebAppExcUtils.isUnauthorizedOrForbidden(e)) { // On 401 or 403...
				Log.debug("Recovering");
				accessTokenValue = identityService.getNewAccessTokenAndCacheIt(Scope.VAULT).getValue(); // ...get a new access token...
				return context.proceed(); // ...and retry!
			} else {
				throw e;
			}
		}
	}

	/**
	 * 
	 * @param keyName
	 * @param keyCreateParameters
	 * @return
	 */
	public KeyBundle createKey(String keyName, KeyCreateParameters keyCreateParameters) {
		return keysClient.createKey(accessTokenValue, keyName, keyCreateParameters);
	}

	/**
	 * 
	 * @return
	 */
	public KeyListResult getKeys() {
		return keysClient.getKeys(accessTokenValue);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	public KeyBundle getKey(String keyName, String keyVersion) {
		return keysClient.getKey(accessTokenValue, keyName, keyVersion);
	}

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	public KeyListResult getKeyVersions(String keyName) {
		return keysClient.getKeyVersions(accessTokenValue, keyName);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keySignParameters
	 * @return
	 */
	public KeyOperationResult sign(String keyName, String keyVersion, KeySignParameters keySignParameters) {
		return keysClient.sign(accessTokenValue, keyName, keyVersion, keySignParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyVerifyParameters
	 * @return
	 */
	public KeyVerifyResult verify(String keyName, String keyVersion, KeyVerifyParameters keyVerifyParameters) {
		return keysClient.verify(accessTokenValue, keyName, keyVersion, keyVerifyParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyOperationParameters
	 * @return
	 */
	public KeyOperationResult encrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.encrypt(accessTokenValue, keyName, keyVersion, keyOperationParameters);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keyOperationParameters
	 * @return
	 */
	public KeyOperationResult decrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		return keysClient.decrypt(accessTokenValue, keyName, keyVersion, keyOperationParameters);
	}
}