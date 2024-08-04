/*
 * AzureIdentityReactiveService.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * <p>
 * Reactive service to get an access token for an Azure resource using a cache to reduce the
 * invocations of Microsoft Entra ID.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureIdentityReactiveService {
	/**
	 * <p>
	 * Reactive rest client to retrieve an access token from Microsoft Entra ID.
	 * </p>
	 */
	AzureIdentityReactiveClient identityClient;

	/**
	 * <p>
	 * Cache of access tokens. The key is the
	 * {@link it.pagopa.swclient.mil.azureservices.identity.bean.Scope Scope}.
	 * </p>
	 */
	private Map<String, AccessToken> cache;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 * 
	 * @param identityEndpoint   End point to get access token by means of system managed identity
	 * @param identityHeader     Value to use to set x-identity-header
	 * @param authorityHost      End point to get access token by means of workload identity
	 * @param tenantId           Tenant ID
	 * @param clientId           Client ID
	 * @param federatedTokenFile Token file with client assertion
	 */
	@Inject
	AzureIdentityReactiveService(AzureIdentityReactiveClient identityClient) {
		/*
		 * Initialize access token cache.
		 */
		cache = new HashMap<>();

		/*
		 * Initialize of the REST client.
		 */
		this.identityClient = identityClient;
	}

	/**
	 * <p>
	 * Retrieves an access token from Microsoft Entra ID and stores it in the cache.
	 * </p>
	 * 
	 * @param scope {@link it.pagopa.swclient.mil.azureservices.identity.bean.Scope Scope}
	 * @return {@link it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken AccessToken}
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
	 * <p>
	 * Retrieves an access token for an Azure resource looking in the cache for a valid one and, in case
	 * of cache-miss, invokes Microsoft Entra ID.
	 * </p>
	 * 
	 * @param scope {@link it.pagopa.swclient.mil.azureservices.identity.bean.Scope Scope}
	 * @return {@link it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken AccessToken}
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
	 * <p>
	 * Clears the access tokens cache.
	 * </p>
	 */
	public void clearAccessTokenCache() {
		cache.clear();
	}
}