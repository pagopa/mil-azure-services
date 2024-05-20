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
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-key-vault-keys")
public interface AzureKeyVaultKeysReactiveClient {
	/**
	 * <p>
	 * Creates a new key, stores it, then returns key parameters and attributes to the client.
	 * </p>
	 * <p>
	 * The create key operation can be used to create any key type in Azure Key Vault.
	 * </p>
	 * <p>
	 * If the named key already exists, Azure Key Vault creates a new version of the key.
	 * </p>
	 * <p>
	 * It requires the keys/create permission.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken
	 * @param keyName
	 *                            <p>
	 *                            The name for the new key.
	 *                            </p>
	 *                            <p>
	 *                            The system will generate the version name for the new key.
	 *                            </p>
	 *                            <p>
	 *                            The value you provide may be copied globally for the purpose of
	 *                            running the service.
	 *                            </p>
	 *                            <p>
	 *                            The value provided should not include personally identifiable or
	 *                            sensitive information.
	 *                            </p>
	 *                            <p>
	 *                            Regex pattern: ^[0-9a-zA-Z-]+$
	 *                            </p>
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
	 * <p>
	 * List keys in the specified vault.
	 * </p>
	 * <p>
	 * Retrieves a list of the keys in the Key Vault as JSON Web Key structures that contain the public
	 * part of a stored key.
	 * </p>
	 * <p>
	 * The LIST operation is applicable to all key types, however only the base key identifier,
	 * attributes, and tags are provided in the response.
	 * </p>
	 * <p>
	 * Individual versions of a key are not listed in the response.
	 * </p>
	 * <p>
	 * This operation requires the keys/list permission.
	 * </p>
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
	 * <p>
	 * Gets the public part of a stored key.
	 * </p>
	 * <p>
	 * The get key operation is applicable to all key types.
	 * </p>
	 * <p>
	 * If the requested key is symmetric, then no key material is released in the response.
	 * </p>
	 * <p>
	 * This operation requires the keys/get permission.
	 * </p>
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-key/get-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * 
	 * @param accessToken
	 * @param keyName     The name of the key to get.
	 * @param keyVersion  Adding the version parameter retrieves a specific version of a key. This URI
	 *                    fragment is optional. If not specified, the latest version of the key is
	 *                    returned.
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
	 * <p>
	 * Retrieves a list of individual key versions with the same key name.
	 * </p>
	 * <p>
	 * The full key identifier, attributes, and tags are provided in the response.
	 * </p>
	 * <p>
	 * This operation requires the keys/list permission.
	 * </p>
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
	 * <p>
	 * Creates a signature from a digest using the specified key.
	 * </p>
	 * <p>
	 * The SIGN operation is applicable to asymmetric and symmetric keys stored in Azure Key Vault since
	 * this operation uses the private portion of the key.
	 * </p>
	 * <p>
	 * This operation requires the keys/sign permission.
	 * </p>
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
	 * <p>
	 * Verifies a signature using a specified key.
	 * </p>
	 * <p>
	 * The VERIFY operation is applicable to symmetric keys stored in Azure Key Vault.
	 * </p>
	 * <p>
	 * VERIFY is not strictly necessary for asymmetric keys stored in Azure Key Vault since signature
	 * verification can be performed using the public portion of the key but this operation is supported
	 * as a convenience for callers that only have a key-reference and not the public portion of the
	 * key.
	 * </p>
	 * <p>
	 * This operation requires the keys/verify permission.
	 * </p>
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
	 * <p>
	 * Encrypts an arbitrary sequence of bytes using an encryption key that is stored in a key vault.
	 * </p>
	 * <p>
	 * The ENCRYPT operation encrypts an arbitrary sequence of bytes using an encryption key that is
	 * stored in Azure Key Vault.
	 * </p>
	 * <p>
	 * Note that the ENCRYPT operation only supports a single block of data, the size of which is
	 * dependent on the target key and the encryption algorithm to be used.
	 * </p>
	 * <p>
	 * The ENCRYPT operation is only strictly necessary for symmetric keys stored in Azure Key Vault
	 * since protection with an asymmetric key can be performed using public portion of the key.
	 * </p>
	 * <p>
	 * This operation is supported for asymmetric keys as a convenience for callers that have a
	 * key-reference but do not have access to the public key material.
	 * </p>
	 * <p>
	 * This operation requires the keys/encrypt permission.
	 * </p>
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
	 * <p>
	 * Decrypts a single block of encrypted data.
	 * </p>
	 * <p>
	 * The DECRYPT operation decrypts a well-formed block of ciphertext using the target encryption key
	 * and specified algorithm.
	 * </p>
	 * <p>
	 * This operation is the reverse of the ENCRYPT operation; only a single block of data may be
	 * decrypted, the size of this block is dependent on the target key and the algorithm to be used.
	 * </p>
	 * <p>
	 * The DECRYPT operation applies to asymmetric and symmetric keys stored in Azure Key Vault since it
	 * uses the private portion of the key.
	 * </p>
	 * <p>
	 * This operation requires the keys/decrypt permission.
	 * </p>
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