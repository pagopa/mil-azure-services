/*
 * AzureKeyVaultKeysClient.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.quarkus.rest.client.reactive.NotBody;
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
 * <p>
 * REST client for Azure Key Vault.
 * </p>
 * <p>
 * To use this client, the {@code application.properties} must have the definition of the following
 * properties:
 * </p>
 * <ul>
 * <li>{@code quarkus.rest-client.azure-key-vault-keys.url} must be set with the URL of Azure Key
 * Vault;</li>
 * <li>{@code azure-key-vault-keys.api-version} must be {@code 7.4};</li>
 * <li>{@code azure-key-vault-keys.get-keys.maxresults} must be set with the maximum number of items
 * returned in a page by get operations.</li>
 * </ul>
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-key-vault-keys")
public interface AzureKeyVaultKeysClient {
	/**
	 * <p>
	 * Creates a new key, stores it, then returns key parameters and attributes to the client.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken         The value of access token got by Microsoft Entra ID.
	 * @param keyName             The name for the new key. Regex pattern: ^[0-9a-zA-Z-]+$
	 * @param keyCreateParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters
	 *                            KeyCreateParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle KeyBundle}
	 */
	@Path("/keys/{keyName}/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	KeyBundle createKey(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		KeyCreateParameters keyCreateParameters);

	/**
	 * <p>
	 * List keys in the specified vault.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-keys/get-keys?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	@Path("/keys")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-keys.maxresults}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	KeyListResult getKeys(@NotBody String accessToken);

	/**
	 * <p>
	 * List keys in the specified vault.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-keys/get-keys?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param skiptoken   Token to handle paging.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	@Path("/keys")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-keys.maxresults}")
	KeyListResult getKeys(
		@NotBody String accessToken,
		@QueryParam("$skiptoken") String skiptoken);

	/**
	 * <p>
	 * Gets the public part of a stored key.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-key/get-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param keyName     The name of the key to get.
	 * @param keyVersion  The version of the key.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle KeyBundle}
	 */
	@Path("/keys/{keyName}/{keyVersion}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	KeyBundle getKey(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion);

	/**
	 * <p>
	 * Retrieves a list of individual key versions with the same key name.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-key/get-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param keyName     The name of the key.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	@Path("/keys/{keyName}/versions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-key-version.maxresults}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	KeyListResult getKeyVersions(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName);

	/**
	 * <p>
	 * Retrieves a list of individual key versions with the same key name.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-key/get-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param keyName     The name of the key.
	 * @param skiptoken   Token to handle paging.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	@Path("/keys/{keyName}/versions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-keys.maxresults}")
	KeyListResult getKeyVersions(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@QueryParam("$skiptoken") String skiptoken);

	/**
	 * <p>
	 * Creates a signature from a digest using the specified key.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/sign/sign?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken       The value of access token got by Microsoft Entra ID.
	 * @param keyName           The name of the key.
	 * @param keyVersion        The version of the key.
	 * @param keySignParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters
	 *                          KeySignParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult
	 *         KeyOperationResult}
	 */
	@Path("/keys/{keyName}/{keyVersion}/sign")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	KeyOperationResult sign(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeySignParameters keySignParameters);

	/**
	 * <p>
	 * Verifies a signature using a specified key.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/verify/verify?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken         The value of access token got by Microsoft Entra ID.
	 * @param keyName             The name of the key.
	 * @param keyVersion          The version of the key.
	 * @param keyVerifyParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters
	 *                            KeyVerifyParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult
	 *         KeyVerifyResult}
	 */
	@Path("/keys/{keyName}/{keyVersion}/verify")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	KeyVerifyResult verify(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeyVerifyParameters keyVerifyParameters);

	/**
	 * <p>
	 * Encrypts an arbitrary sequence of bytes using an encryption key that is stored in a key vault.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/encrypt/encrypt?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken            The value of access token got by Microsoft Entra ID.
	 * @param keyName                The name of the key.
	 * @param keyVersion             The version of the key.
	 * @param keyOperationParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters
	 *                               KeyOperationParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult
	 *         KeyOperationResult}
	 */
	@Path("/keys/{keyName}/{keyVersion}/encrypt")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	KeyOperationResult encrypt(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeyOperationParameters keyOperationParameters);

	/**
	 * <p>
	 * Decrypts a single block of encrypted data.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/decrypt/decrypt?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken            The value of access token got by Microsoft Entra ID.
	 * @param keyName                The name of the key.
	 * @param keyVersion             The version of the key.
	 * @param keyOperationParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters
	 *                               KeyOperationParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult
	 *         KeyOperationResult}
	 */
	@Path("/keys/{keyName}/{keyVersion}/decrypt")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	KeyOperationResult decrypt(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeyOperationParameters keyOperationParameters);
}