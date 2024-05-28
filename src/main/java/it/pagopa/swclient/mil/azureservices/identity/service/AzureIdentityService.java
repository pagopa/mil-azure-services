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

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureIdentityService {
	/*
	 * 
	 */
	private AzureIdentityClient identityClient;

	/*
	 * 
	 */
	private Map<String, AccessToken> cache;

	/**
	 * 
	 * @param identityClient
	 */
	AzureIdentityService(@RestClient AzureIdentityClient identityClient) {
		this.identityClient = identityClient;
		cache = new HashMap<>();
	}

	/**
	 * 
	 * @param scope {@link Scope}
	 * @return
	 */
	public AccessToken getNewAccessTokenAndCacheIt(String scope) {
		Log.debug("Get new access token");
		AccessToken accessToken = identityClient.getAccessToken(scope);
		Log.trace("Store access token");
		cache.put(scope, accessToken);
		return accessToken;
	}

	/**
	 * 
	 * @param scope {@link Scope}
	 * @return
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
	 * 
	 */
	public void clearAccessTokenCache() {
		cache.clear();
	}
}