/*
 * AzureWorkloadIdentityReactiveClient.java
 *
 * 3 ago 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.logging.Log;
import io.quarkus.rest.client.reactive.ClientFormParam;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * <p>
 * Reactive REST client to get access token from Microsoft Entra ID by means of Workload Identity.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@Path("/oauth2/v2.0/token")
public interface AzureWorkloadIdentityReactiveClient extends AzureIdentityReactiveClient {
	/**
	 * <p>
	 * Retrieves an access token for an Azure resource.
	 * </p>
	 * 
	 * @param scope {@link it.pagopa.swclient.mil.azureservices.identity.bean.Scope Scope}
	 * @return {@link it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken AccessToken}
	 * @param scope
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientFormParam(name = "grant_type", value = "client_credentials")
	@ClientFormParam(name = "client_id", value = "${AZURE_CLIENT_ID}")
	@ClientFormParam(name = "client_assertion", value = "{getClientAssertion}")
	@ClientFormParam(name = "client_assertion_type", value = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
	Uni<AccessToken> getAccessToken(@FormParam("scope") String scope);

	/**
	 * <p>
	 * Retrieve the client assertion from Azure Federated TokenFile.
	 * </p>
	 * 
	 * @param headerName Header to set.
	 * @return Value to use to set header.
	 */
	default String getClientAssertion(String headerName) {
		try {
			return new String(
				Files.readAllBytes(
					Paths.get(
						ConfigProvider.getConfig()
							.getValue(
								"AZURE_FEDERATED_TOKEN_FILE",
								String.class))),
				StandardCharsets.UTF_8);
		} catch (IOException e) {
			Log.errorf(e, "Error reading Azure federated token file");
			throw new DeploymentException("Error reading Azure federated token file", e);
		}
	}
}
