/*
 * AzureSystemManagedIdentityClient.java
 *
 * 7 ago 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client.usermanaged;

import java.net.URI;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * <p>
 * Reactive client (it's a proxy of REST client) to get access token from Microsoft Entra ID by
 * means of User Managed Identity.
 * </p>
 * <p>
 * To use this method, the environment variable {@code IDENTITY_CLIENT_ID} must be set.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureUserManagedIdentityClient implements AzureIdentityClient {
	/**
	 * <p>
	 * Reactive REST client to get access token from Microsoft Entra ID by means of User Managed
	 * Identity.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.identity.client.usermanaged.AzureUserManagedIdentityRestClient
	 *      AzureSystemManagedIdentityRestClient
	 */
	private AzureUserManagedIdentityRestClient restClient;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 * 
	 * @param identityEndpoint Endpoint to get access token by means of user managed identity
	 */
	AzureUserManagedIdentityClient(@ConfigProperty(name = "USER_MANAGED_IDENTITY_ENDPOINT") Optional<String> identityEndpoint) {
		Log.trace("Azure User Managed Identity client initialization");
		restClient = QuarkusRestClientBuilder.newBuilder()
			.baseUri(URI.create(identityEndpoint.orElseThrow()))
			.build(AzureUserManagedIdentityRestClient.class);
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.identity.client.systemmanaged.AzureSystemManagedIdentityRestClient#getAccessToken(String)
	 */
	@Override
	public Uni<AccessToken> getAccessToken(String scope) {
		Log.tracef("Get access token with System Managed Identity for %s", scope);
		return restClient.getAccessToken(scope);
	}
}
