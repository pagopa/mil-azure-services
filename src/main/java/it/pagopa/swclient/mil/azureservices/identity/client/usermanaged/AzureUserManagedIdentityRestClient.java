/*
 * AzureUserManagedIdentityRestClient.java
 *
 * 17 ott 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client.usermanaged;

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
 * Reactive REST client to get access token from Microsoft Entra ID by means of User Managed
 * Identity.
 * </p>
 * <p>
 * To use this method, the environment variables {@code USER_MANAGED_IDENTITY_CLIENT_ID} and
 * {@code USER_MANAGED_IDENTITY_ENDPOINT} must be set.
 * </p>
 * <p>
 * At the moment the value for {@code USER_MANAGED_IDENTITY_ENDPOINT} if
 * {@code http://169.254.169.254/metadata/identity/oauth2/token}.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public interface AzureUserManagedIdentityRestClient {
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
	@ClientQueryParam(name = "api-version", value = "2018-02-01")
	@ClientQueryParam(name = "client_id", value = "${USER_MANAGED_IDENTITY_CLIENT_ID}")
	Uni<AccessToken> getAccessToken(@QueryParam("resource") String scope);
}
