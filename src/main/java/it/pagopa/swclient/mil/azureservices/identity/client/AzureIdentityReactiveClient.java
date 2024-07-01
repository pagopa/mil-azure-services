/*
 * AzureIdentityReactiveClient.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * <p>
 * Reactive REST client to get access token from Microsoft Entra ID.
 * </p>
 * 
 * <p>
 * To use this client, the {@code application.properties} must have the definition of the following
 * properties:
 * </p>
 * <ul>
 * <li>{@code quarkus.rest-client.azure-identity.url} must be <code>${IDENTITY_ENDPOINT}</code>, if
 * Azure Container Apps is used;</li>
 * <li>{@code azure-identity.api-version} must be {@code 2019-08-01};</li>
 * <li>{@code azure-identity.x-identity-header} must be <code>${IDENTITY_HEADER}</code>, if Azure
 * Container Apps is used.</li>
 * </ul>
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-identity")
public interface AzureIdentityReactiveClient {
	/**
	 * <p>
	 * Retrieves an access token for an Azure resource.
	 * </p>
	 * 
	 * @param scope {@link it.pagopa.swclient.mil.azureservices.identity.bean.Scope Scope}
	 * @return {@link it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken AccessToken}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientQueryParam(name = "api-version", value = "${azure-identity.api-version}")
	@ClientHeaderParam(name = "x-identity-header", value = "${azure-identity.x-identity-header}")
	Uni<AccessToken> getAccessToken(@QueryParam("resource") String scope);
}
