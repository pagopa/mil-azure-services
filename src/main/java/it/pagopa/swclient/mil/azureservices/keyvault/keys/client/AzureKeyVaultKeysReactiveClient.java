/*
 * AzureKeyVaultKeysReactiveClient.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.quarkus.rest.client.reactive.NotBody;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-key-vault-keys")
public interface AzureKeyVaultKeysReactiveClient {
	/**
	 * Creates a new key, stores it, then returns key parameters and attributes to the client.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken
	 * @param keyName             The name for the new key. Regex pattern: ^[0-9a-zA-Z-]+$
	 * @param keyCreateParameters
	 * @return
	 */
	@Path("/keys/{keyName}/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyBundle> createKey(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		KeyCreateParameters keyCreateParameters);

	/**
	 * List keys in the specified vault.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-keys/get-keys?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken
	 * @return
	 */
	@Path("/keys")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-keys.maxresults}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyListResult> getKeys(@NotBody String accessToken);
	
	/**
	 * 
	 * @param accessToken
	 * @param skiptoken
	 * @return
	 */
	@Path("/keys")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-keys.maxresults}")
	Uni<KeyListResult> getKeys(
		@NotBody String accessToken,
		@QueryParam("$skiptoken") String skiptoken);

	/**
	 * Gets the public part of a stored key.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-key/get-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken
	 * @param keyName     The name of the key to get.
	 * @param keyVersion  The version of the key.
	 * @return
	 */
	@Path("/keys/{keyName}/{keyVersion}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyBundle> getKey(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion);

	/**
	 * Retrieves a list of individual key versions with the same key name.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-key/get-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken
	 * @param keyName     The name of the key.
	 * @return
	 */
	@Path("/keys/{keyName}/versions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-key-version.maxresults}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyListResult> getKeyVersions(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName);
	
	/**
	 * 
	 * @param accessToken
	 * @param keyName
	 * @param skiptoken
	 * @return
	 */
	@Path("/keys/{keyName}/versions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-keys.maxresults}")
	Uni<KeyListResult> getKeyVersions(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@QueryParam("$skiptoken") String skiptoken);

	/**
	 * Creates a signature from a digest using the specified key.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/sign/sign?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken
	 * @param keyName           The name of the key.
	 * @param keyVersion        The version of the key.
	 * @param keySignParameters
	 * @return
	 */
	@Path("/keys/{keyName}/{keyVersion}/sign")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyOperationResult> sign(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeySignParameters keySignParameters);

	/**
	 * Verifies a signature using a specified key.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/verify/verify?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param authorization
	 * @param keyName                The name of the key.
	 * @param keyVersion             The version of the key.
	 * @param verifySignatureRequest
	 * @return
	 */
	@Path("/keys/{keyName}/{keyVersion}/verify")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyVerifyResult> verify(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeyVerifyParameters keyVerifyParameters);

	/**
	 * Encrypts an arbitrary sequence of bytes using an encryption key that is stored in a key vault.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/encrypt/encrypt?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken
	 * @param keyName     The name of the key.
	 * @param keyVersion  The version of the key.
	 * @return
	 */
	@Path("/keys/{keyName}/{keyVersion}/encrypt")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyOperationResult> encrypt(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeyOperationParameters keyOperationParameters);

	/**
	 * Decrypts a single block of encrypted data.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/decrypt/decrypt?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken
	 * @param keyName     The name of the key.
	 * @param keyVersion  The version of the key.
	 * @return
	 */
	@Path("/keys/{keyName}/{keyVersion}/decrypt")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyOperationResult> decrypt(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeyOperationParameters keyOperationParameters);
}