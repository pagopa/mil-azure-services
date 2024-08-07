/*
 * AzureIdentityClient.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;

/**
 * <p>
 * Reactive client to get access token from Microsoft Entra ID.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public interface AzureIdentityClient {
	/**
	 * <p>
	 * Retrieves an access token for an Azure resource.
	 * </p>
	 * 
	 * @param scope {@link it.pagopa.swclient.mil.azureservices.identity.bean.Scope Scope}
	 * @return {@link it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken AccessToken}
	 */
	Uni<AccessToken> getAccessToken(String scope);
}
