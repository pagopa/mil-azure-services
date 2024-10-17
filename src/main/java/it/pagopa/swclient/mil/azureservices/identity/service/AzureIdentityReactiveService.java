/*
 * AzureIdentityReactiveService.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityClient;
import it.pagopa.swclient.mil.azureservices.identity.client.systemmanaged.AzureSystemManagedIdentityClient;
import it.pagopa.swclient.mil.azureservices.identity.client.usermanaged.AzureUserManagedIdentityClient;
import it.pagopa.swclient.mil.azureservices.identity.client.workload.AzureWorkloadIdentityClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.DeploymentException;
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
	 * Reactive client to retrieve an access token from Microsoft Entra ID.
	 * </p>
	 */
	private AzureIdentityClient identityClient;

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
	 * @patam identityClientId   Client ID to get access token by means of user managed identity
	 * @param identityEndpoint   Endpoint to get access token by means of system/user managed identity
	 * @param identityHeader     Value to use to set x-identity-header
	 * @param authorityHost      Endpoint to get access token by means of workload identity
	 * @param tenantId           Tenant ID
	 * @param clientId           Client ID
	 * @param federatedTokenFile Token file with client assertion
	 * @param anyIdentityClient  Any identity client
	 */
	@Inject
	AzureIdentityReactiveService(
		@ConfigProperty(name = "IDENTITY_CLIENT_ID") Optional<String> identityClientId,
		@ConfigProperty(name = "IDENTITY_ENDPOINT") Optional<String> identityEndpoint,
		@ConfigProperty(name = "IDENTITY_HEADER") Optional<String> identityHeader,
		@ConfigProperty(name = "AZURE_AUTHORITY_HOST") Optional<String> authorityHost,
		@ConfigProperty(name = "AZURE_TENANT_ID") Optional<String> tenantId,
		@ConfigProperty(name = "AZURE_CLIENT_ID") Optional<String> clientId,
		@ConfigProperty(name = "AZURE_FEDERATED_TOKEN_FILE") Optional<String> federatedTokenFile,
		@Any Instance<AzureIdentityClient> anyIdentityClient) {
		/*
		 * Initialize identity client.
		 */
		if (identityEndpoint.isPresent() && identityHeader.isPresent() && identityClientId.isPresent()) {
			Log.debug("Azure User Managed Identity will be use");
			identityClient = anyIdentityClient.select(AzureUserManagedIdentityClient.class).get();
	    } else if (identityEndpoint.isPresent() && identityHeader.isPresent()) {
			Log.debug("Azure System Managed Identity will be use");
			identityClient = anyIdentityClient.select(AzureSystemManagedIdentityClient.class).get();
		} else if (authorityHost.isPresent() && tenantId.isPresent() && clientId.isPresent() && federatedTokenFile.isPresent()) {
			Log.debug("Azure Workload Identity will be use");
			identityClient = anyIdentityClient.select(AzureWorkloadIdentityClient.class).get();
		} else {
			Log.fatal("IDENTITY_CLIENT_ID and IDENTITY_ENDPOINT and IDENTITY_HEADER must not be null or IDENTITY_ENDPOINT and IDENTITY_HEADER must not be null or AZURE_AUTHORITY_HOST and AZURE_TENANT_ID and AZURE_CLIENT_ID and AZURE_FEDERATED_TOKEN_FILE must not be null");
			throw new DeploymentException("IDENTITY_CLIENT_ID and IDENTITY_ENDPOINT and IDENTITY_HEADER must not be null or IDENTITY_ENDPOINT and IDENTITY_HEADER must not be null or AZURE_AUTHORITY_HOST and AZURE_TENANT_ID and AZURE_CLIENT_ID and AZURE_FEDERATED_TOKEN_FILE must not be null");
		}

		/*
		 * Initialize access token cache.
		 */
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

	/**
	 * <p>
	 * Returns identity client in use.
	 * </p>
	 * 
	 * @return {@link it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityClient
	 *         AzureIdentityClient}
	 */
	public AzureIdentityClient getIdentityClient() {
		return identityClient;
	}
}