/*
 * AzureIdentityReactiveService.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureIdentityReactiveService {
	/*
	 * 
	 */
	private AzureIdentityReactiveClient identityClient;

	/*
	 * 
	 */
	private Map<String, AccessToken> cache;

	/**
	 * 
	 * @param identityClient
	 */
	AzureIdentityReactiveService(@RestClient AzureIdentityReactiveClient identityClient) {
		this.identityClient = identityClient;
		cache = new HashMap<>();
	}

	/**
	 * 
	 * @param scope {@link Scope}
	 * @return
	 */
	public Uni<AccessToken> getNewAccessTokenAndCacheIt(String scope) {
		Log.debug("Get new access token");
		return identityClient.getAccessToken(scope)
			.invoke(accessToken -> {
				Log.trace("Store access token");
				cache.put(scope, accessToken);
			});
	}

	/**
	 * 
	 * @param scope {@link Scope}
	 * @return
	 */
	public Uni<AccessToken> getAccessToken(String scope) {
		AccessToken accessToken = cache.get(scope);
		if (accessToken != null && accessToken.getExpiresOn() > Instant.now().getEpochSecond()) {
			Log.trace("Stored access token is going to be used");
			return Uni.createFrom().item(accessToken);
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