/*
 * AzureWorkloadIdentityClient.java
 *
 * 7 ago 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client.workload;

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
 * means of Workload Identity.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureWorkloadIdentityClient implements AzureIdentityClient {
	/**
	 * <p>
	 * Reactive REST client to get access token from Microsoft Entra ID by means of Workload Identity.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.identity.client.workload.AzureWorkloadIdentityRestClient
	 *      AzureWorkloadIdentityRestClient
	 */
	private AzureWorkloadIdentityRestClient restClient;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 * 
	 * @param authorityHost Endpoint to get access token by means of workload identity
	 * @param tenantId      Tenant ID
	 */
	AzureWorkloadIdentityClient(
		@ConfigProperty(name = "AZURE_AUTHORITY_HOST") Optional<String> authorityHost,
		@ConfigProperty(name = "AZURE_TENANT_ID") Optional<String> tenantId) {
		Log.trace("Azure Workload Identity client initialization");
		restClient = QuarkusRestClientBuilder.newBuilder()
			.baseUri(URI.create(authorityHost.orElseThrow() + tenantId.orElseThrow()))
			.build(AzureWorkloadIdentityRestClient.class);
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.identity.client.workload.AzureWorkloadIdentityRestClient#getAccessToken(String)
	 */
	@Override
	public Uni<AccessToken> getAccessToken(String scope) {
		Log.tracef("Get access token with Workload Identity for %s", scope);
		return restClient.getAccessToken(scope);
	}
}