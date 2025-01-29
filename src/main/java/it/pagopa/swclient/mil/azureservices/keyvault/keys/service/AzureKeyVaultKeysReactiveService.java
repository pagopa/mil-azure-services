/*
 * AzureKeyVaultKeysReactiveService.java
 *
 * 8 jan 2025
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletedKeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;

/**
 * <p>
 * This is the interface of a wrapper of
 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysReactiveClient
 * AzureKeyVaultKeysReactiveClient}.
 * </p>
 * <p>
 * When the used profile is {@code dev},
 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev
 * AzureKeyVaultKeysReactiveServiceDev} is used as implementation and the Azure Key Vault APIs are
 * emulated.
 * </p>
 * <p>
 * When the used profile is not {@code dev},
 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceImpl
 * AzureKeyVaultKeysReactiveServiceImpl} is used as implementation, the real Azure Key Vault is used
 * and some advanced features are offered.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public interface AzureKeyVaultKeysReactiveService {
	/**
	 * <p>
	 * Creates a new key, stores it, then returns key parameters and attributes to the client.
	 * </p>
	 * 
	 * @param keyName             The name for the new key. Regex pattern: ^[0-9a-zA-Z-]+$
	 * @param keyCreateParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters
	 *                            KeyCreateParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle KeyBundle}
	 */
	Uni<KeyBundle> createKey(String keyName, KeyCreateParameters keyCreateParameters);

	/**
	 * <p>
	 * Lists keys in the specified vault.
	 * </p>
	 * 
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	Uni<KeyListResult> getKeys();

	/**
	 * <p>
	 * Lists keys in the specified vault.
	 * </p>
	 * 
	 * @param skiptoken Token to handle paging.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	Uni<KeyListResult> getKeys(String skiptoken);

	/**
	 * <p>
	 * Returns the public part of a stored key.
	 * </p>
	 * 
	 * @param keyName    The name of the key to get.
	 * @param keyVersion The version of the key.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle KeyBundle}
	 */
	Uni<KeyBundle> getKey(String keyName, String keyVersion);

	/**
	 * <p>
	 * Returns a list of individual key versions with the same key name.
	 * </p>
	 * 
	 * @param keyName The name of the key.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	Uni<KeyListResult> getKeyVersions(String keyName);

	/**
	 * <p>
	 * Returns a list of individual key versions with the same key name.
	 * </p>
	 * 
	 * @param keyName   The name of the key.
	 * @param skiptoken Token to handle paging.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult
	 *         KeyListResult}
	 */
	Uni<KeyListResult> getKeyVersions(String keyName, String skiptoken);

	/**
	 * <p>
	 * Creates a signature from a digest using the specified key.
	 * </p>
	 * 
	 * @param keyName           The name of the key.
	 * @param keyVersion        The version of the key.
	 * @param keySignParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters
	 *                          KeySignParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult
	 *         KeyOperationResult}
	 */
	Uni<KeyOperationResult> sign(String keyName, String keyVersion, KeySignParameters keySignParameters);

	/**
	 * <p>
	 * Verifies a signature using a specified key.
	 * </p>
	 * 
	 * @param keyName             The name of the key.
	 * @param keyVersion          The version of the key.
	 * @param keyVerifyParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters
	 *                            KeyVerifyParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult
	 *         KeyVerifyResult}
	 */
	Uni<KeyVerifyResult> verify(String keyName, String keyVersion, KeyVerifyParameters keyVerifyParameters);

	/**
	 * <p>
	 * Encrypts an arbitrary sequence of bytes using an encryption key that is stored in a key vault.
	 * </p>
	 * 
	 * @param keyName                The name of the key.
	 * @param keyVersion             The version of the key.
	 * @param keyOperationParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters
	 *                               KeyOperationParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult
	 *         KeyOperationResult}
	 */
	Uni<KeyOperationResult> encrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters);

	/**
	 * <p>
	 * Decrypts a single block of encrypted data.
	 * </p>
	 * 
	 * @param keyName                The name of the key.
	 * @param keyVersion             The version of the key.
	 * @param keyOperationParameters {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters
	 *                               KeyOperationParameters}
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult
	 *         KeyOperationResult}
	 */
	Uni<KeyOperationResult> decrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters);

	/**
	 * <p>
	 * Deletes a key of any type from storage in Azure Key Vault.
	 * </p>
	 * 
	 * @param keyName The name of the key to delete.
	 * @return {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletedKeyBundle
	 *         DeletedKeyBundle}
	 */
	Uni<DeletedKeyBundle> deleteKey(String keyName);

}