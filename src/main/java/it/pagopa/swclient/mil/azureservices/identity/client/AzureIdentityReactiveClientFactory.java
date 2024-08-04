/*
 * AzureIdentityReactiveClientFactory.java
 *
 * 4 ago 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client;

import java.net.URI;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.ws.rs.ext.Provider;

/**
 * <p>
 * Initializes the right Azure Identity REST client depending on environment variables found.
 * </p>
 * 
 * <p>
 * If environment variables <code>IDENTITY_ENDPOINT</code> and <code>IDENTITY_HEADER</code> are set,
 * <b>System Assigned Managed Identity</b> will be used.
 * </p>
 * 
 * <p>
 * If environment variables <code>AZURE_FEDERATED_TOKEN_FILE</code>, <code>AZURE_TENANT_ID</code>,
 * <code>AZURE_CLIENT_ID</code> and <code>AZURE_AUTHORITY_HOST</code> are set, <b>Workload
 * Identity</b> will be used.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@Provider
public class AzureIdentityReactiveClientFactory {
	/**
	 * <p>
	 * End point to get access token by means of system managed identity.
	 * </p>
	 */
	@ConfigProperty(name = "IDENTITY_ENDPOINT")
	Optional<String> identityEndpoint;

	/**
	 * <p>
	 * Value to use to set x-identity-header.
	 * </p>
	 */
	@ConfigProperty(name = "IDENTITY_HEADER")
	Optional<String> identityHeader;

	/**
	 * <p>
	 * End point to get access token by means of workload identity.
	 * </p>
	 */
	@ConfigProperty(name = "AZURE_AUTHORITY_HOST")
	Optional<String> authorityHost;

	/**
	 * <p>
	 * Tenant ID.
	 * </p>
	 */
	@ConfigProperty(name = "AZURE_TENANT_ID")
	Optional<String> tenantId;

	/**
	 * <p>
	 * Client ID.
	 * </p>
	 */
	@ConfigProperty(name = "AZURE_CLIENT_ID")
	Optional<String> clientId;

	/**
	 * <p>
	 * Token file with client assertion.
	 * </p>
	 */
	@ConfigProperty(name = "AZURE_FEDERATED_TOKEN_FILE")
	Optional<String> federatedTokenFile;

	/**
	 * <p>
	 * Initializes the right Azure Identity REST client depending on environment variables found.
	 * </p>
	 * 
	 * @return {@link it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClient
	 *         AzureIdentityReactiveClient}
	 */
	@Produces
	AzureIdentityReactiveClient get() {
		Log.trace("Azure Identity REST Client factory invoked!");

		if (identityEndpoint.isPresent() && identityHeader.isPresent()) {
			Log.debug("Azure System Managed Identity will be use");
			return QuarkusRestClientBuilder.newBuilder()
				.baseUri(URI.create(identityEndpoint.get()))
				.build(AzureSystemManagedIdentityReactiveClient.class);
		} else if (authorityHost.isPresent() && tenantId.isPresent() && clientId.isPresent() && federatedTokenFile.isPresent()) {
			Log.debug("Azure Workload Identity will be use");
			return QuarkusRestClientBuilder.newBuilder()
				.baseUri(URI.create(authorityHost.get() + tenantId.get()))
				.build(AzureWorkloadIdentityReactiveClient.class);
		} else {
			Log.fatal("IDENTITY_ENDPOINT and IDENTITY_HEADER must not be null or AZURE_AUTHORITY_HOST and AZURE_TENANT_ID and AZURE_CLIENT_ID and AZURE_FEDERATED_TOKEN_FILE must not be null");
			throw new DeploymentException("IDENTITY_ENDPOINT and IDENTITY_HEADER must not be null or AZURE_AUTHORITY_HOST and AZURE_TENANT_ID and AZURE_CLIENT_ID and AZURE_FEDERATED_TOKEN_FILE must not be null");
		}
	}
}
