/*
 * AzureIdentityService.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.arc.Unremovable;
import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * <p>
 * Service to get an access token for an Azure resource using a cache to reduce the invocations of
 * Microsoft Entra ID.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
@Unremovable // There isn't any other service that uses this, so the compiler tries to remove this!
public class AzureIdentityService {
	/**
	 * <p>
	 * Rest client to retrieve an access token from Microsoft Entra ID.
	 * </p>
	 */
	@RestClient
	AzureIdentityClient identityClient;

	/**
	 * <p>
	 * Cache of access tokens. The key is the
	 * {@link it.pagopa.swclient.mil.azureservices.identity.bean.Scope Scope}.
	 * </p>
	 */
	private Map<String, AccessToken> cache;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	AzureIdentityService() {
		cache = new HashMap<>();
	}

	/**
	 * <p>
	 * Retrieves an access token from Microsoft Entra ID and stores it in the cache.
	 * </p>
	 * 
	 * @param scope {@link it.pagopa.swclient.mil.azureservices.identity.bean.Scope Scope}
	 * @return {@link it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken AccessToken}
	 */
	public AccessToken getNewAccessTokenAndCacheIt(String scope) {
		Log.debug("Get new access token");
		AccessToken accessToken = identityClient.getAccessToken(scope);
		Log.trace("Store access token");
		cache.put(scope, accessToken);
		return accessToken;
	}

	/**
	 * <p>
	 * Retrieves an access token for an Azure resource looking in the cache for a valid one and, in case
	 * of cache-miss, invokes Microsoft Entra ID.
	 * </p>
	 * 
	 * @param scope {@link it.pagopa.swclient.mil.azureservices.identity.bean.Scope Scope}
	 * @return {@link it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken AccessToken}
	 */
	public AccessToken getAccessToken(String scope) {
		AccessToken accessToken = cache.get(scope);
		if (accessToken != null && accessToken.getExpiresOn() > Instant.now().getEpochSecond()) {
			Log.trace("Stored access token is going to be used");
			return accessToken;
		}
		Log.debug("There's no stored access token or it is expired");
		return getNewAccessTokenAndCacheIt(scope);
	}

	/**
	 * <p>
	 * Clears the access tokens cache.
	 * </p>
	 */
	public void clearAccessTokenCache() {
		cache.clear();
	}
}