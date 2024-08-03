/*
 * AzureIdentityReactiveService.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.service;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClient;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureSystemManagedIdentityReactiveClient;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureWorkloadIdentityReactiveClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.inject.Inject;

/**
 * <p>
 * Reactive service to get an access token for an Azure resource using a cache to reduce the
 * invocations of Microsoft Entra ID.
 * </p>
 * <p>
 * If environment variables <code>IDENTITY_ENDPOINT</code> and <code>IDENTITY_HEADER</code> are set,
 * <b>System Assigned Managed Identity</b> will be used.
 * </p>
 * <p>
 * If environment variables <code>AZURE_FEDERATED_TOKEN_FILE</code>, <code>AZURE_TENANT_ID</code>,
 * <code>AZURE_CLIENT_ID</code> and <code>AZURE_AUTHORITY_HOST</code> are set, <b>Workload
 * Identity</b> will be used.
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
	 */
	@Inject
	AzureIdentityReactiveService(
		@ConfigProperty(name = "IDENTITY_ENDPOINT") Optional<String> identityEndpoint,
		@ConfigProperty(name = "IDENTITY_HEADER") Optional<String> identityHeader,
		@ConfigProperty(name = "AZURE_AUTHORITY_HOST") Optional<String> authorityHost,
		@ConfigProperty(name = "AZURE_TENANT_ID") Optional<String> tenantId,
		@ConfigProperty(name = "AZURE_CLIENT_ID") Optional<String> clientId,
		@ConfigProperty(name = "AZURE_FEDERATED_TOKEN_FILE") Optional<String> federatedTokenFile) {
		/*
		 * Initialize access token cache.
		 */
		cache = new HashMap<>();

		/*
		 * Initialize of the REST client depending on systems variables found.
		 */
		if (identityEndpoint.isPresent() && identityHeader.isPresent()) {
			Log.debug("Azure System Managed Identity will be use");
			identityClient = QuarkusRestClientBuilder.newBuilder()
				.baseUri(URI.create(identityEndpoint.get()))
				.build(AzureSystemManagedIdentityReactiveClient.class);
		} else if (authorityHost.isPresent() && tenantId.isPresent() && clientId.isPresent() && federatedTokenFile.isPresent()) {
			Log.debug("Azure Workload Identity will be use");
			identityClient = QuarkusRestClientBuilder.newBuilder()
				.baseUri(URI.create(authorityHost.get() + tenantId.get()))
				.build(AzureWorkloadIdentityReactiveClient.class);
		} else {
			Log.fatal("IDENTITY_ENDPOINT and IDENTITY_HEADER must not be null or AZURE_AUTHORITY_HOST and AZURE_TENANT_ID and AZURE_CLIENT_ID and AZURE_FEDERATED_TOKEN_FILE must not be null");
			throw new DeploymentException("IDENTITY_ENDPOINT and IDENTITY_HEADER must not be null or AZURE_AUTHORITY_HOST and AZURE_TENANT_ID and AZURE_CLIENT_ID and AZURE_FEDERATED_TOKEN_FILE must not be null");
		}
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