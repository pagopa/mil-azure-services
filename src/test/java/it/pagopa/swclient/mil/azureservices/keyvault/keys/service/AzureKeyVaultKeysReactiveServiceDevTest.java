/*
 * AzureKeyVaultKeysReactiveServiceDevTest.java
 *
 * 10 gen 2025
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.MockedStatic;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletedKeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyEncryptionAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeySignatureAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils;
import jakarta.ws.rs.NotFoundException;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class AzureKeyVaultKeysReactiveServiceDevTest {
	/*
	 * 
	 */
	private static final String NAME = "newkey";
	private static final String NAME_2 = "newkey2";
	private static final int DURATION = 12 * 60 * 60;
	private static final int SIZE = 2048;
	private static final String DOMAIN = "test_domain";
	private static final String SKIP_TOKEN = "skip_token";

	/*
	 * 
	 */
	private static final byte[] VALUE_TO_SIGN = new byte[] {
		0, 1, 2, 3, 4, 5, 6, 7
	};

	/*
	 * 
	 */
	private static final byte[] CLEAR_VALUE = new byte[] {
		0, 1, 2, 3, 4, 5, 6, 7
	};

	/*
	 * 
	 */
	private AzureKeyVaultKeysReactiveServiceDev service = new AzureKeyVaultKeysReactiveServiceDev();

	/**
	 * 
	 * @param testInfo
	 */
	@BeforeEach
	void init(TestInfo testInfo) {
		String frame = "*".repeat(testInfo.getDisplayName().length() + 11);
		System.out.println(frame);
		System.out.printf("* %s: START *%n", testInfo.getDisplayName());
		System.out.println(frame);
		service.reset();
	}

	/**
	 * 
	 * @param bundle
	 * @return
	 */
	private KeyItem bundle2item(KeyBundle bundle) {
		return new KeyItem()
			.setAttributes(bundle.getAttributes())
			.setKid(bundle.getKey().getKid())
			.setManaged(bundle.getManaged())
			.setTags(bundle.getTags());
	}

	/**
	 * 
	 */
	private record KeyData(String kid, String version) {
	}

	/**
	 * 
	 * @return
	 */
	private KeyData createKey(List<String> keyOps) {
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(keyOps)
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		KeyItem keyItem = service.createKey(NAME, params)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		String[] comps = KeyUtils.getKeyNameVersion(keyItem);
		return new KeyData(keyItem.getKid(), comps[1]);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#createKey(java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters)}.
	 */
	@Test
	void given_keyParameters_when_createKeyIsInvoked_then_getKeyBundle() {
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME, params)
			.subscribe()
			.with(
				keyBundle -> assertEquals(params.getAttributes(), keyBundle.getAttributes()),
				Assertions::fail);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#createKey(java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters)}.
	 */
	@Test
	void given_keyParametersWithPublicExponent_when_createKeyIsInvoked_then_getKeyBundle() {
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(RSAKeyGenParameterSpec.F4.intValue());

		service.createKey(NAME, params)
			.subscribe()
			.with(
				keyBundle -> {
					assertEquals(params.getAttributes(), keyBundle.getAttributes());
					assertEquals(params.getPublicExponent(), new BigInteger(1, keyBundle.getKey().getE()).intValue());
				},
				Assertions::fail);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#createKey(java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters)}.
	 */
	@Test
	void given_keyParameters_when_createKeyIsInvokedAndExceptionIsThrown_then_getFailure() {
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		try (MockedStatic<KeyPairGenerator> generator = mockStatic(KeyPairGenerator.class)) {
			generator.when(() -> KeyPairGenerator.getInstance("RSA")).thenThrow(NoSuchAlgorithmException.class);

			service.createKey(NAME, params)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.awaitFailure()
				.assertFailedWith(NoSuchAlgorithmException.class);
		}
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#createKey(java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters)}.
	 */
	@Test
	void given_keyParametersWithWrongKeyType_when_createKeyIsInvoked_then_getFailure() {
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.EC);

		service.createKey(NAME, params)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(UnsupportedOperationException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#getKeys()}.
	 */
	@Test
	void given_setOfKeys_when_getKeysIsInvoked_then_getListOfKeys() {
		/*
		 * Key #1, Version #1
		 */
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params11 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		KeyItem item11 = service.createKey(NAME, params11)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #1, Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params12 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		KeyItem item12 = service.createKey(NAME, params12)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #1
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params21 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		KeyItem item21 = service.createKey(NAME_2, params21)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params22 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		KeyItem item22 = service.createKey(NAME_2, params22)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Test
		 */
		List<KeyItem> actual = service.getKeys()
			.map(KeyListResult::getValue)
			.await()
			.indefinitely();

		assertThat(actual).containsExactlyInAnyOrder(item11, item12, item21, item22);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#getKeys(java.lang.String)}.
	 */
	@Test
	void given_skipToken_when_getKeysIsInvoked_then_getFailure() {
		service.getKeys(SKIP_TOKEN)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(UnsupportedOperationException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#getKey(java.lang.String, java.lang.String)}.
	 */
	@Test
	void given_nameAndVersionOfExistentKey_when_getKeyIsInvoked_then_getKey() {
		/*
		 * Key #1, Version #1
		 */
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params11 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME, params11)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #1, Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params12 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME, params12)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #1
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params21 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME_2, params21)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params22 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		KeyBundle bundle = service.createKey(NAME_2, params22)
			.await()
			.indefinitely();

		/*
		 * Expected
		 */
		String[] comps = KeyUtils.getKeyNameVersion(bundle2item(bundle));
		String name = comps[0];
		String version = comps[1];

		/*
		 * Test
		 */
		service.getKey(name, version)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertItem(bundle);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#getKey(java.lang.String, java.lang.String)}.
	 */
	@Test
	void given_nameAndVersionOfNonExistentKey_when_getKeyIsInvoked_then_getFailure() {
		/*
		 * Key #1, Version #1
		 */
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params11 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME, params11)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #1, Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params12 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME, params12)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #1
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params21 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME_2, params21)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params22 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME_2, params22)
			.await()
			.indefinitely();

		/*
		 * Test
		 */
		service.getKey("nonexistentkey", "dontcare")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(NotFoundException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#getKey(java.lang.String, java.lang.String)}.
	 */
	@Test
	void given_nameAndNonExistentVersion_when_getKeyIsInvoked_then_getFailure() {
		/*
		 * Key #1, Version #1
		 */
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params11 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME, params11)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #1, Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params12 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME, params12)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #1
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params21 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME_2, params21)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params22 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME_2, params22)
			.await()
			.indefinitely();

		/*
		 * Test
		 */
		service.getKey(NAME, "nonexistentversion")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(NotFoundException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#getKeyVersions(java.lang.String)}.
	 */
	@Test
	void given_nameOfExistentKey_when_getKeyVersionsIsInvoked_then_getVersions() {
		/*
		 * Version #1
		 */
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params1 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		KeyItem keyItem1 = service.createKey(NAME, params1)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params2 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		KeyItem keyItem2 = service.createKey(NAME, params2)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Test
		 */
		List<KeyItem> actual = service.getKeyVersions(NAME)
			.await()
			.indefinitely()
			.getValue();

		assertThat(actual).containsExactlyInAnyOrder(keyItem1, keyItem2);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#getKeyVersions(java.lang.String)}.
	 */
	@Test
	void given_nameOfNonExistentKey_when_getKeyVersionsIsInvoked_then_getFailure() {
		/*
		 * Version #1
		 */
		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params1 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME, params1)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Version #2
		 */
		now = Instant.now().getEpochSecond();

		KeyCreateParameters params2 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + DURATION)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, DOMAIN))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(SIZE)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(NAME, params2)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Test
		 */
		service.getKeyVersions("nonexistentkey")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(NotFoundException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#getKeyVersions(java.lang.String, java.lang.String)}.
	 */
	@Test
	void given_skipToken_when_getKeyVersionsIsInvoked_then_getFailure() {
		service.getKeyVersions(NAME, SKIP_TOKEN)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(UnsupportedOperationException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#sign(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters)}.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	@Test
	void given_signParams_when_signIsInvoked_then_getSignature() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY));

		/*
		 * Expected
		 */
		JsonWebKey key = service.getKey(NAME, keyData.version)
			.await()
			.indefinitely()
			.getKey();
		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(
				new RSAPrivateKeySpec(
					new BigInteger(1, key.getN()),
					new BigInteger(1, key.getD())));
		Signature signer = Signature.getInstance("SHA256withRSA");
		signer.initSign(privateKey);
		signer.update(VALUE_TO_SIGN);
		byte[] expected = signer.sign();

		/*
		 * Test
		 */
		KeySignParameters signParams = new KeySignParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256)
			.setValue(VALUE_TO_SIGN);

		KeyOperationResult signResult = service.sign(NAME, keyData.version, signParams)
			.await()
			.indefinitely();

		assertEquals(keyData.kid, signResult.getKid());
		assertArrayEquals(expected, signResult.getValue());
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#sign(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters)}.
	 */
	@Test
	void given_signParams_when_notSuitableKeyIsUsed_then_getFailure() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT));

		/*
		 * Test
		 */
		KeySignParameters signParams = new KeySignParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256)
			.setValue(VALUE_TO_SIGN);

		service.sign(NAME, keyData.version, signParams)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(RuntimeException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#sign(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters)}.
	 */
	@Test
	void given_signParams_when_algIsNotSupported_then_getFailure() {
		service.sign("dontcare", "dontcare", new KeySignParameters().setAlg("unsupported"))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(UnsupportedOperationException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#sign(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters)}.
	 */
	@Test
	void given_badSignParams_when_verifyIsInvoked_then_getFailure() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY));

		KeyVerifyParameters verifyParams = new KeyVerifyParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256);

		try (MockedStatic<KeyFactory> keyFactory = mockStatic(KeyFactory.class)) {
			keyFactory.when(() -> KeyFactory.getInstance("RSA"))
				.thenThrow(NoSuchAlgorithmException.class);

			service.verify(NAME, keyData.version, verifyParams)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.awaitFailure()
				.assertFailedWith(RuntimeException.class);
		}
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#verify(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters)}.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	@Test
	void given_verifyParams_when_verifyIsInvoked_then_getResult() throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY));

		/*
		 * Signature calculation
		 */
		JsonWebKey key = service.getKey(NAME, keyData.version)
			.await()
			.indefinitely()
			.getKey();
		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(
				new RSAPrivateKeySpec(
					new BigInteger(1, key.getN()),
					new BigInteger(1, key.getD())));
		Signature signer = Signature.getInstance("SHA256withRSA");
		signer.initSign(privateKey);
		signer.update(VALUE_TO_SIGN);
		byte[] signature = signer.sign();

		/*
		 * Test
		 */
		KeyVerifyParameters verifyParams = new KeyVerifyParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256)
			.setValue(signature)
			.setDigest(VALUE_TO_SIGN);

		KeyVerifyResult verifyResult = service.verify(NAME, keyData.version, verifyParams)
			.await()
			.indefinitely();

		assertTrue(verifyResult.getValue());
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#verify(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters)}.
	 */
	@Test
	void given_badSignature_when_verifyIsInvoked_then_getResult() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY));

		/*
		 * Test
		 */
		KeyVerifyParameters verifyParams = new KeyVerifyParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256)
			.setValue(new byte[0])
			.setDigest(VALUE_TO_SIGN);

		KeyVerifyResult verifyResult = service.verify(NAME, keyData.version, verifyParams)
			.await()
			.indefinitely();

		assertFalse(verifyResult.getValue());
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#verify(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters)}.
	 */
	@Test
	void given_verifyParams_when_algIsNotSupported_then_getFailure() {
		service.verify("dontcare", "dontcare", new KeyVerifyParameters().setAlg("unsupported"))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(UnsupportedOperationException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#verify(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters)}.
	 */
	@Test
	void given_verifyParams_when_notSuitableKeyIsUsed_then_getFailure() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT));

		/*
		 * Test
		 */
		KeyVerifyParameters verifyParams = new KeyVerifyParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256);

		service.verify(NAME, keyData.version, verifyParams)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(RuntimeException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#verify(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters)}.
	 */
	@Test
	void given_badSignParams_when_signIsInvoked_then_getFailure() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY));

		KeySignParameters signParams = new KeySignParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256);

		try (MockedStatic<KeyFactory> keyFactory = mockStatic(KeyFactory.class)) {
			keyFactory.when(() -> KeyFactory.getInstance("RSA"))
				.thenThrow(NoSuchAlgorithmException.class);

			service.sign(NAME, keyData.version, signParams)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.awaitFailure()
				.assertFailedWith(RuntimeException.class);
		}
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#encrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 * 
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	@Test
	void given_opParms_when_encryptIsInvoked_then_getEncryptedValue() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT));

		/*
		 * Test
		 */
		KeyOperationParameters opParams = new KeyOperationParameters()
			.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
			.setValue(CLEAR_VALUE);

		KeyOperationResult opResult = service.encrypt(NAME, keyData.version, opParams)
			.await()
			.indefinitely();

		/*
		 * Expected
		 */
		JsonWebKey key = service.getKey(NAME, keyData.version)
			.await()
			.indefinitely()
			.getKey();
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
		byte[] clear = cipher.doFinal(opResult.getValue());

		assertArrayEquals(CLEAR_VALUE, clear);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#encrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 */
	@Test
	void given_unsupportedAlg_when_encryptIsInvoked_then_getFailure() {
		service.encrypt("dontcare", "dontcare", new KeyOperationParameters().setAlg("unsupported"))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(UnsupportedOperationException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#encrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 */
	@Test
	void given_notSuitableKey_when_encryptIsInvoked_then_getFailure() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY));

		service.encrypt(NAME, keyData.version, new KeyOperationParameters()
			.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
			.setValue(CLEAR_VALUE))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(RuntimeException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#encrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 */
	@Test
	void given_badOpParms_when_encryptIsInvoked_then_getFailure() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT));

		/*
		 * Test
		 */
		KeyOperationParameters opParams = new KeyOperationParameters()
			.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
			.setValue(CLEAR_VALUE);

		try (MockedStatic<KeyFactory> keyFactory = mockStatic(KeyFactory.class)) {
			keyFactory.when(() -> KeyFactory.getInstance("RSA"))
				.thenThrow(NoSuchAlgorithmException.class);

			service.encrypt(NAME, keyData.version, opParams)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.awaitFailure()
				.assertFailedWith(RuntimeException.class);
		}
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#decrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	@Test
	void given_opParms_when_decryptIsInvoked_then_getClearValue() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT));

		/*
		 * Encrypted value
		 */
		JsonWebKey key = service.getKey(NAME, keyData.version)
			.await()
			.indefinitely()
			.getKey();
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
		byte[] encrypted = cipher.doFinal(CLEAR_VALUE);

		/*
		 * Test
		 */
		KeyOperationParameters opParams = new KeyOperationParameters()
			.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
			.setValue(encrypted);

		KeyOperationResult opResult = service.decrypt(NAME, keyData.version, opParams)
			.await()
			.indefinitely();

		assertArrayEquals(CLEAR_VALUE, opResult.getValue());
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#encrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 */
	@Test
	void given_unsupportedAlg_when_decryptIsInvoked_then_getFailure() {
		service.decrypt("dontcare", "dontcare", new KeyOperationParameters().setAlg("unsupported"))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(UnsupportedOperationException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#encrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 */
	@Test
	void given_notSuitableKey_when_decryptIsInvoked_then_getFailure() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY));

		service.decrypt(NAME, keyData.version, new KeyOperationParameters().setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(RuntimeException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#encrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 */
	@Test
	void given_badOpParms_when_decryptIsInvoked_then_getFailure() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT));

		/*
		 * Test
		 */
		KeyOperationParameters opParams = new KeyOperationParameters()
			.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256);

		try (MockedStatic<KeyFactory> keyFactory = mockStatic(KeyFactory.class)) {
			keyFactory.when(() -> KeyFactory.getInstance("RSA"))
				.thenThrow(NoSuchAlgorithmException.class);

			service.decrypt(NAME, keyData.version, opParams)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.awaitFailure()
				.assertFailedWith(RuntimeException.class);
		}
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#deleteKey(java.lang.String)}.
	 */
	@Test
	void given_aKey_when_deleteKeyIsInvoked_then_getDataOfDeletedKey() {
		KeyData keyData = createKey(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT));

		KeyBundle bundle = service.getKey(NAME, keyData.version)
			.await()
			.indefinitely();

		DeletedKeyBundle deletedBundle = service.deleteKey(NAME)
			.await()
			.indefinitely();

		assertEquals(bundle.getAttributes(), deletedBundle.getAttributes());
		assertEquals(bundle.getKey(), deletedBundle.getKey());
		assertEquals(bundle.getManaged(), deletedBundle.getManaged());
		assertEquals(bundle.getReleasePolicy(), deletedBundle.getReleasePolicy());
		assertEquals(bundle.getTags(), deletedBundle.getTags());

		service.getKey(NAME, keyData.version)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(NotFoundException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#deleteKey(java.lang.String)}.
	 */
	@Test
	void given_nameOfNonExistentaKey_when_deleteKeyIsInvoked_then_getFailure() {
		service.deleteKey("nonexistentkey")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(NotFoundException.class);
	}
}
