/*
 * AzureSystemManagedIdentityRestClient.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client.systemmanaged;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * <p>
 * Reactive REST client to get access token from Microsoft Entra ID by means of System Managed
 * Identity.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public interface AzureSystemManagedIdentityRestClient {
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
	@ClientQueryParam(name = "api-version", value = "2019-08-01")
	@ClientHeaderParam(name = "x-identity-header", value = "${IDENTITY_HEADER}")
	Uni<AccessToken> getAccessToken(@QueryParam("resource") String scope);
}
