/*
 * AzureKeyVaultKeysReactiveServiceDev.java
 *
 * 8 gen 2025
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletedKeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyEncryptionAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeySignatureAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

/**
 * <p>
 * This implementation is used when {@code application.properties} has
 * {@code azure-key-vault-keys.emulator.enabled=true}. In this case the Azure Key Vault APIs will be
 * emulated.
 * </p>
 * <p>
 * <strong>Don't use this in production!</strong>
 * </p>
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
@IfBuildProperty(name = "azure-key-vault-keys.emulator.enabled", stringValue = "true", enableIfMissing = false)
public class AzureKeyVaultKeysReactiveServiceDev implements AzureKeyVaultKeysReactiveService {
	/*
	 * 
	 */
	private HashMap<String, LinkedHashMap<String, KeyBundle>> keyVault;

	/*
	 * 
	 */
	private static final List<String> SUPPORTED_KEY_TYPES = List.of(JsonWebKeyType.RSA);

	/*
	 * 
	 */
	private static final List<String> SUPPORTED_SIGN_ALGS = List.of(JsonWebKeySignatureAlgorithm.RS256);

	/*
	 * 
	 */
	private static final List<String> SUPPORTED_ENC_ALGS = List.of(JsonWebKeyEncryptionAlgorithm.RSAOAEP256);

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 */
	AzureKeyVaultKeysReactiveServiceDev() {
		Log.warn("**** AZURE KEY VAULT EMULATOR IN USE! BE SURE THAT THIS ISN'T PRODUCTION ENVIRONMENT! ****");
		keyVault = new HashMap<>();
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#createKey(String,
	 *      KeyCreateParameters)
	 */
	@Override
	public Uni<KeyBundle> createKey(String keyName, KeyCreateParameters keyCreateParameters) {
		if (SUPPORTED_KEY_TYPES.contains(keyCreateParameters.getKty())) {
			/*
			 * Generate a new key.
			 */
			String keyVersion = UUID.randomUUID().toString().replace("-", "");
			String kid = "https://myvault.vault.azure.net/keys/%s/%s".formatted(keyName, keyVersion);

			try {
				KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
				if (keyCreateParameters.getPublicExponent() != null) {
					generator.initialize(new RSAKeyGenParameterSpec(keyCreateParameters.getKeySize(), BigInteger.valueOf(keyCreateParameters.getPublicExponent())));
				} else {
					generator.initialize(keyCreateParameters.getKeySize());
				}

				KeyPair pair = generator.generateKeyPair();
				RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
				RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();

				JsonWebKey key = new JsonWebKey()
					.setCrv(keyCreateParameters.getCrv())
					.setKeyOps(keyCreateParameters.getKeyOps())
					.setKid(kid)
					.setKty(keyCreateParameters.getKty())
					.setE(publicKey.getPublicExponent().toByteArray())
					.setN(publicKey.getModulus().toByteArray())
					.setD(privateKey.getPrivateExponent().toByteArray());

				KeyBundle keyBundle = new KeyBundle()
					.setAttributes(keyCreateParameters.getAttributes())
					.setKey(key)
					.setReleasePolicy(keyCreateParameters.getReleasePolicy())
					.setTags(keyCreateParameters.getTags());

				/*
				 * Store the generated key.
				 */
				synchronized (keyVault) {
					LinkedHashMap<String, KeyBundle> keyVersions = keyVault.get(keyName);
					if (keyVersions == null) {
						keyVersions = new LinkedHashMap<>();
						keyVault.put(keyName, keyVersions);
					}
					keyVersions.put(keyVersion, keyBundle);
				}

				/*
				 * Return the generated key.
				 */
				return Uni.createFrom().item(keyBundle);
			} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
				Log.errorf(e, "Exception while creating key");
				return Uni.createFrom().failure(e);
			}
		} else {
			final String message = "DEV implementation doesn't support %s".formatted(keyCreateParameters.getKty());
			Log.error(message);
			return Uni.createFrom().failure(new UnsupportedOperationException(message));
		}
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#getKeys()
	 */
	@Override
	public Uni<KeyListResult> getKeys() {
		List<KeyItem> keyItems = new LinkedList<>();
		synchronized (keyVault) {
			keyVault.forEach(
				(keyName, keyVersions) -> keyVersions.forEach(
					(keyVersion, keyBundle) -> keyItems.add(new KeyItem()
						.setAttributes(keyBundle.getAttributes())
						.setKid(keyBundle.getKey().getKid())
						.setManaged(keyBundle.getManaged())
						.setTags(keyBundle.getTags()))));
		}
		return Uni.createFrom().item(new KeyListResult().setValue(keyItems));
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#getKeys(String)
	 */
	@Override
	public Uni<KeyListResult> getKeys(String skiptoken) {
		final String message = "DEV implementation doesn't support skiptoken";
		Log.error(message);
		return Uni.createFrom().failure(new UnsupportedOperationException(message));
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#getKey(String,
	 *      String)
	 */
	@Override
	public Uni<KeyBundle> getKey(String keyName, String keyVersion) {
		synchronized (keyVault) {
			LinkedHashMap<String, KeyBundle> keyVersions = keyVault.get(keyName);
			if (keyVersions != null) {
				KeyBundle keyBundle = keyVersions.get(keyVersion);
				if (keyBundle != null) {
					return Uni.createFrom().item(keyBundle);
				} else {
					final String message = "Key with name %s and version %s doesn't exist".formatted(keyName, keyVersion);
					Log.warnf(message);
					return Uni.createFrom().failure(new NotFoundException(message));
				}
			} else {
				final String message = "Key with name %s and version %s doesn't exist".formatted(keyName, keyVersion);
				Log.warnf(message);
				return Uni.createFrom().failure(new NotFoundException(message));
			}
		}
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#getKeyVersions(String)
	 */
	@Override
	public Uni<KeyListResult> getKeyVersions(String keyName) {
		synchronized (keyVault) {
			HashMap<String, KeyBundle> keyVersions = keyVault.get(keyName);
			if (keyVersions != null) {
				List<KeyItem> keyItems = new LinkedList<>();
				keyVersions.forEach((keyVersion, keyBundle) -> keyItems.add(
					new KeyItem()
						.setAttributes(keyBundle.getAttributes())
						.setKid(keyBundle.getKey().getKid())
						.setManaged(keyBundle.getManaged())
						.setTags(keyBundle.getTags())));
				return Uni.createFrom().item(new KeyListResult().setValue(keyItems));
			} else {
				final String message = "Key with name %s doesn't exist".formatted(keyName);
				Log.warnf(message);
				return Uni.createFrom().failure(new NotFoundException(message));
			}
		}
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#getKeyVersions(String,
	 *      String)
	 */
	@Override
	public Uni<KeyListResult> getKeyVersions(String keyName, String skiptoken) {
		final String message = "DEV implementation doesn't support skiptoken";
		Log.error(message);
		return Uni.createFrom().failure(new UnsupportedOperationException(message));
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#sign(String,
	 *      String, KeySignParameters)
	 */
	@Override
	public Uni<KeyOperationResult> sign(String keyName, String keyVersion, KeySignParameters keySignParameters) {
		if (SUPPORTED_SIGN_ALGS.contains(keySignParameters.getAlg())) {
			return getKey(keyName, keyVersion)
				.map(KeyBundle::getKey)
				.map(key -> {
					if (key.getKeyOps().contains(JsonWebKeyOperation.SIGN)) {
						try {
							KeyFactory factory = KeyFactory.getInstance("RSA");
							PrivateKey privateKey = factory.generatePrivate(
								new RSAPrivateKeySpec(
									new BigInteger(1, key.getN()),
									new BigInteger(1, key.getD())));

							Signature signer = Signature.getInstance("SHA256withRSA");
							signer.initSign(privateKey);
							signer.update(keySignParameters.getValue());
							byte[] signature = signer.sign();

							return new KeyOperationResult()
								.setKid(key.getKid())
								.setValue(signature);
						} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
							Log.errorf(e, "Signing error");
							throw new RuntimeException(e); // NOSONAR
						}
					} else {
						final String message = "Operation not supported by the key";
						Log.error(message);
						throw new RuntimeException(message); // NOSONAR
					}
				});
		} else {
			final String message = "DEV implementation doesn't support %s".formatted(keySignParameters.getAlg());
			Log.error(message);
			return Uni.createFrom().failure(new UnsupportedOperationException(message));
		}
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#verify(String,
	 *      String, KeyVerifyParameters)
	 */
	@Override
	public Uni<KeyVerifyResult> verify(String keyName, String keyVersion, KeyVerifyParameters keyVerifyParameters) {
		if (SUPPORTED_SIGN_ALGS.contains(keyVerifyParameters.getAlg())) {
			return getKey(keyName, keyVersion)
				.map(KeyBundle::getKey)
				.map(key -> {
					if (key.getKeyOps().contains(JsonWebKeyOperation.VERIFY)) {
						try {
							KeyFactory factory = KeyFactory.getInstance("RSA");
							PublicKey publicKey = factory.generatePublic(
								new RSAPublicKeySpec(
									new BigInteger(1, key.getN()),
									new BigInteger(1, key.getE())));

							Signature verifier = Signature.getInstance("SHA256withRSA");
							verifier.initVerify(publicKey);
							verifier.update(keyVerifyParameters.getDigest());
							boolean isVerificationOk = verifier.verify(keyVerifyParameters.getValue());

							return new KeyVerifyResult().setValue(isVerificationOk);
						} catch (SignatureException e) {
							return new KeyVerifyResult().setValue(false);
						} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
							Log.errorf(e, "Verifing error");
							throw new RuntimeException(e); // NOSONAR
						}
					} else {
						final String message = "Operation not supported by the key";
						Log.error(message);
						throw new RuntimeException(message); // NOSONAR
					}
				});
		} else {
			final String message = "DEV implementation doesn't support %s".formatted(keyVerifyParameters.getAlg());
			Log.error(message);
			return Uni.createFrom().failure(new UnsupportedOperationException(message));
		}
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#encrypt(String,
	 *      String, KeyOperationParameters)
	 */
	@Override
	public Uni<KeyOperationResult> encrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		if (SUPPORTED_ENC_ALGS.contains(keyOperationParameters.getAlg())) {
			return getKey(keyName, keyVersion)
				.map(KeyBundle::getKey)
				.map(key -> {
					if (key.getKeyOps().contains(JsonWebKeyOperation.ENCRYPT)) {
						try {
							KeyFactory factory = KeyFactory.getInstance("RSA");
							PublicKey publicKey = factory.generatePublic(
								new RSAPublicKeySpec(
									new BigInteger(1, key.getN()),
									new BigInteger(1, key.getE())));

							Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
							OAEPParameterSpec param = new OAEPParameterSpec(
								"SHA-256",
								"MGF1",
								MGF1ParameterSpec.SHA256,
								PSource.PSpecified.DEFAULT);
							cipher.init(Cipher.ENCRYPT_MODE, publicKey, param);
							byte[] encrypted = cipher.doFinal(keyOperationParameters.getValue());

							return new KeyOperationResult()
								.setKid(key.getKid())
								.setValue(encrypted);
						} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
							Log.errorf(e, "Encrypting error");
							throw new RuntimeException(e); // NOSONAR
						}
					} else {
						final String message = "Operation not supported by the key";
						Log.error(message);
						throw new RuntimeException(message); // NOSONAR
					}
				});
		} else {
			final String message = "DEV implementation doesn't support %s".formatted(keyOperationParameters.getAlg());
			Log.error(message);
			return Uni.createFrom().failure(new UnsupportedOperationException(message));
		}
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#decrypt(String,
	 *      String, KeyOperationParameters)
	 */
	@Override
	public Uni<KeyOperationResult> decrypt(String keyName, String keyVersion, KeyOperationParameters keyOperationParameters) {
		if (SUPPORTED_ENC_ALGS.contains(keyOperationParameters.getAlg())) {
			return getKey(keyName, keyVersion)
				.map(KeyBundle::getKey)
				.map(key -> {
					if (key.getKeyOps().contains(JsonWebKeyOperation.DECRYPT)) {
						try {
							KeyFactory factory = KeyFactory.getInstance("RSA");
							PrivateKey privateKey = factory.generatePrivate(
								new RSAPrivateKeySpec(
									new BigInteger(1, key.getN()),
									new BigInteger(1, key.getD())));

							Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
							OAEPParameterSpec param = new OAEPParameterSpec(
								"SHA-256",
								"MGF1",
								MGF1ParameterSpec.SHA256,
								PSource.PSpecified.DEFAULT);
							cipher.init(Cipher.DECRYPT_MODE, privateKey, param);
							byte[] decrypted = cipher.doFinal(keyOperationParameters.getValue());

							return new KeyOperationResult()
								.setKid(key.getKid())
								.setValue(decrypted);
						} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
							Log.errorf(e, "Decrypting error");
							throw new RuntimeException(e); // NOSONAR
						}
					} else {
						final String message = "Operation not supported by the key";
						Log.error(message);
						throw new RuntimeException(message); // NOSONAR
					}
				});
		} else {
			final String message = "DEV implementation doesn't support %s".formatted(keyOperationParameters.getAlg());
			Log.error(message);
			return Uni.createFrom().failure(new UnsupportedOperationException(message));
		}
	}

	/**
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService#deleteKey(String)
	 */
	@Override
	public Uni<DeletedKeyBundle> deleteKey(String keyName) {
		synchronized (keyVault) {
			LinkedHashMap<String, KeyBundle> keyVersions = keyVault.remove(keyName);
			if (keyVersions != null) {
				KeyBundle keyBundle = keyVersions.lastEntry().getValue();
				return Uni.createFrom().item(new DeletedKeyBundle()
					.setAttributes(keyBundle.getAttributes())
					.setKey(keyBundle.getKey())
					.setManaged(keyBundle.getManaged())
					.setReleasePolicy(keyBundle.getReleasePolicy())
					.setTags(keyBundle.getTags()));
			} else {
				final String message = "Key with name %s doesn't exist".formatted(keyName);
				Log.warnf(message);
				return Uni.createFrom().failure(new NotFoundException(message));
			}
		}
	}

	/**
	 * <p>
	 * Clear all keys.
	 * </p>
	 */
	void reset() {
		if (keyVault != null)
			keyVault.clear();
	}
}
