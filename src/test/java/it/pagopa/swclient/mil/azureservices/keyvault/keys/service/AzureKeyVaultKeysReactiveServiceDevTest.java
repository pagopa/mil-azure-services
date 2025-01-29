/*
 * AzureKeyVaultKeysReactiveServiceDevTest.java
 *
 * 10 gen 2025
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mockStatic;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.MockedStatic;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeySignatureAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils;
import jakarta.ws.rs.NotFoundException;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class AzureKeyVaultKeysReactiveServiceDevTest {
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
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#createKey(java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters)}.
	 */
	@Test
	void given_keyParameters_when_createKeyIsInvoked_then_getKeyBundle() {
		String name = "newkey";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		new AzureKeyVaultKeysReactiveServiceDev()
			.createKey(name, params)
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
		String name = "newkey";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(RSAKeyGenParameterSpec.F4.intValue());

		new AzureKeyVaultKeysReactiveServiceDev()
			.createKey(name, params)
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
		String name = "newkey";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		try (MockedStatic<KeyPairGenerator> generator = mockStatic(KeyPairGenerator.class)) {
			generator.when(() -> KeyPairGenerator.getInstance("RSA")).thenThrow(NoSuchAlgorithmException.class);

			new AzureKeyVaultKeysReactiveServiceDev()
				.createKey(name, params)
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
		String name = "newkey";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.EC);

		new AzureKeyVaultKeysReactiveServiceDev()
			.createKey(name, params)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(UnsupportedOperationException.class);
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
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#getKeys()}.
	 */
	@Test
	void given_setOfKeys_when_getKeysIsInvoked_then_getListOfKeys() {
		AzureKeyVaultKeysReactiveServiceDev service = new AzureKeyVaultKeysReactiveServiceDev();

		/*
		 * Key #1, Version #1
		 */
		String name1 = "newkey1";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params11 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		KeyItem item11 = service.createKey(name1, params11)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		KeyItem item12 = service.createKey(name1, params12)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #1
		 */
		String name2 = "newkey2";

		now = Instant.now().getEpochSecond();

		KeyCreateParameters params21 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		KeyItem item21 = service.createKey(name2, params21)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		KeyItem item22 = service.createKey(name1, params22)
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
		new AzureKeyVaultKeysReactiveServiceDev()
			.getKeys("skip_token")
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
		AzureKeyVaultKeysReactiveServiceDev service = new AzureKeyVaultKeysReactiveServiceDev();

		/*
		 * Key #1, Version #1
		 */
		String name1 = "newkey1";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params11 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name1, params11)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name1, params12)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #1
		 */
		String name2 = "newkey2";

		now = Instant.now().getEpochSecond();

		KeyCreateParameters params21 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name2, params21)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		KeyBundle bundle = service.createKey(name1, params22)
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
		AzureKeyVaultKeysReactiveServiceDev service = new AzureKeyVaultKeysReactiveServiceDev();

		/*
		 * Key #1, Version #1
		 */
		String name1 = "newkey1";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params11 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name1, params11)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name1, params12)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #1
		 */
		String name2 = "newkey2";

		now = Instant.now().getEpochSecond();

		KeyCreateParameters params21 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name2, params21)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name1, params22)
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
		AzureKeyVaultKeysReactiveServiceDev service = new AzureKeyVaultKeysReactiveServiceDev();

		/*
		 * Key #1, Version #1
		 */
		String name1 = "newkey1";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params11 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name1, params11)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name1, params12)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Key #2, Version #1
		 */
		String name2 = "newkey2";

		now = Instant.now().getEpochSecond();

		KeyCreateParameters params21 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name2, params21)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name1, params22)
			.await()
			.indefinitely();

		/*
		 * Test
		 */
		service.getKey("newkey1", "nonexistentversion")
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
		AzureKeyVaultKeysReactiveServiceDev service = new AzureKeyVaultKeysReactiveServiceDev();

		/*
		 * Version #1
		 */
		String name = "newkey";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params1 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		KeyItem keyItem1 = service.createKey(name, params1)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		KeyItem keyItem2 = service.createKey(name, params2)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		/*
		 * Test
		 */
		List<KeyItem> actual = service.getKeyVersions(name)
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
		AzureKeyVaultKeysReactiveServiceDev service = new AzureKeyVaultKeysReactiveServiceDev();

		/*
		 * Version #1
		 */
		String name = "newkey";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params1 = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name, params1)
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
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		service.createKey(name, params2)
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
		new AzureKeyVaultKeysReactiveServiceDev()
			.getKeyVersions("key_name", "skip_token")
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
	void given_signParams_when_signIsInvoked_then_getSignature() {
		AzureKeyVaultKeysReactiveServiceDev service = new AzureKeyVaultKeysReactiveServiceDev();

		String name = "newkey";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		KeyItem keyItem = service.createKey(name, params)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		String[] comps = KeyUtils.getKeyNameVersion(keyItem);
		String version = comps[1];

		KeySignParameters signParams = new KeySignParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256)
			.setValue(new byte[] {
				0, 1, 2, 3, 4, 5, 6, 7
			});

		KeyOperationResult signResult = service.sign(name, version, signParams)
			.await()
			.indefinitely();

		assertEquals(keyItem.getKid(), signResult.getKid());
		assertNotNull(signResult.getValue());
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#sign(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters)}.
	 */
	@Test
	void given_signParams_when_algIsNotSupported_then_getFailure() {
		new AzureKeyVaultKeysReactiveServiceDev()
			.sign("dontcare", "dontcare", new KeySignParameters().setAlg("unsupported"))
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
	void given_badSignParams_when_signIsInvoked_then_getFailure() {
		AzureKeyVaultKeysReactiveServiceDev service = new AzureKeyVaultKeysReactiveServiceDev();

		String name = "newkey";

		long now = Instant.now().getEpochSecond();

		KeyCreateParameters params = new KeyCreateParameters()
			.setAttributes(new KeyAttributes()
				.setCreated(now)
				.setEnabled(Boolean.TRUE)
				.setExp(now + 12 * 60 * 60)
				.setExportable(Boolean.FALSE)
				.setNbf(now))
			.setTags(Map.of(KeyUtils.DOMAIN_KEY, "test_domain"))
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(2048)
			.setKty(JsonWebKeyType.RSA);

		KeyItem keyItem = service.createKey(name, params)
			.map(this::bundle2item)
			.await()
			.indefinitely();

		String[] comps = KeyUtils.getKeyNameVersion(keyItem);
		String version = comps[1];

		KeySignParameters signParams = new KeySignParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256)
			.setValue(new byte[0]);

		try (MockedStatic<KeyFactory> keyFactory = mockStatic(KeyFactory.class)) {
			keyFactory.when(() -> KeyFactory.getInstance("RSA"))
				.thenThrow(NoSuchAlgorithmException.class);

			service.sign(name, version, signParams)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.awaitFailure()
				.assertFailedWith(RuntimeException.class);
		}
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#verify(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters)}.
	 */
	@Test
	void testVerify() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#encrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 */
	@Test
	void testEncrypt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#decrypt(java.lang.String, java.lang.String, it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters)}.
	 */
	@Test
	void testDecrypt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveServiceDev#deleteKey(java.lang.String)}.
	 */
	@Test
	void testDeleteKey() {
		fail("Not yet implemented"); // TODO
	}

}
