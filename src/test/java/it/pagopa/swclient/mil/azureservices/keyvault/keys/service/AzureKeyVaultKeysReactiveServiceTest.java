/*
 * AzureKeyVaultKeysReactiveServiceTest.java
 *
 * 19 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClient;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletionRecoveryLevel;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyCurveName;
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
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyReleasePolicy;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysReactiveClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

/**
 * 
 * @author antonio.tarricone
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class AzureKeyVaultKeysReactiveServiceTest {
	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureIdentityReactiveClient identityClient;

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureKeyVaultKeysReactiveClient keysClient;

	/*
	 * 
	 */
	@Inject
	AzureKeyVaultKeysReactiveService keysService;

	/*
	 * 
	 */
	private Instant now;

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
		now = Instant.now();
	}

	/**
	 * 
	 * @param testInfo
	 */
	@AfterEach
	void reset(TestInfo testInfo) {
		String frame = "*".repeat(testInfo.getDisplayName().length() + 11);
		System.out.println(frame);
		System.out.printf("* %s: RESET *%n", testInfo.getDisplayName());
		System.out.println(frame);
		Mockito.reset(identityClient, keysClient);
		keysService.resetCachedAccessToken();
	}

	/**
	 * 
	 */
	@Test
	@Order(1)
	void given_createKeyRequest_when_accessTokenIsNull_then_getItAndReturnKeyBundle() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyReleasePolicy keyReleasePolicy = new KeyReleasePolicy()
			.setData(null);
		KeyCreateParameters keyCreateParameters = new KeyCreateParameters()
			.setAttributes(keyAttributes)
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(4096)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(Integer.MAX_VALUE)
			.setCrv(JsonWebKeyCurveName.P256K)
			.setReleasePolicy(keyReleasePolicy);
		JsonWebKey jsonWebKey = new JsonWebKey()
			.setE(BigInteger.ONE.toByteArray())
			.setN(BigInteger.TEN.toByteArray())
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKid("key_id")
			.setKty(JsonWebKeyType.RSA);
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(keyAttributes)
			.setKey(jsonWebKey)
			.setManaged(Boolean.TRUE);
		when(keysClient.createKey("access_token_string", "key_name", keyCreateParameters))
			.thenReturn(Uni.createFrom().item(keyBundle));

		/*
		 * Test.
		 */
		keysService.createKey("key_name", keyCreateParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyBundle);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	@Order(2)
	void given_createKeyRequest_when_accessTokenIsExpired_then_getNewOneAndReturnKeyBundle() {
		/*
		 * Setup.
		 */
		AccessToken expiredToken = new AccessToken()
			.setExpiresOn(now.minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(
				Uni.createFrom().item(expiredToken),
				Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyCreateParameters keyCreateParameters = new KeyCreateParameters()
			.setAttributes(keyAttributes)
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(4096)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(Integer.MAX_VALUE);
		JsonWebKey jsonWebKey = new JsonWebKey()
			.setE(BigInteger.ONE.toByteArray())
			.setN(BigInteger.TEN.toByteArray())
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKid("key_id")
			.setKty(JsonWebKeyType.RSA);
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(keyAttributes)
			.setKey(jsonWebKey)
			.setManaged(Boolean.TRUE);
		when(keysClient.createKey("access_token_string", "key_name", keyCreateParameters))
			.thenReturn(Uni.createFrom().item(keyBundle));

		/*
		 * Test.
		 */
		keysService.createKey("key_name", keyCreateParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyBundle);
	}

	/**
	 * 
	 */
	@Test
	@Order(3)
	void given_createKeyRequest_when_accessTokenIsNotNull_then_useItAndReturnKeyBundle() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyCreateParameters keyCreateParameters = new KeyCreateParameters()
			.setAttributes(keyAttributes)
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(4096)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(Integer.MAX_VALUE);
		JsonWebKey jsonWebKey = new JsonWebKey()
			.setE(BigInteger.ONE.toByteArray())
			.setN(BigInteger.TEN.toByteArray())
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKid("key_id")
			.setKty(JsonWebKeyType.RSA);
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(keyAttributes)
			.setKey(jsonWebKey)
			.setManaged(Boolean.TRUE);
		when(keysClient.createKey("access_token_string", "key_name", keyCreateParameters))
			.thenReturn(Uni.createFrom().item(keyBundle));

		/*
		 * Test.
		 */
		keysService.createKey("key_name", keyCreateParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem();

		keysService.createKey("key_name", keyCreateParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyBundle);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	@Order(4)
	void given_createKeyRequest_when_keysClientReturns401_then_getNewAccessTokenAndRetry() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyCreateParameters keyCreateParameters = new KeyCreateParameters()
			.setAttributes(keyAttributes)
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(4096)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(Integer.MAX_VALUE);
		JsonWebKey jsonWebKey = new JsonWebKey()
			.setE(BigInteger.ONE.toByteArray())
			.setN(BigInteger.TEN.toByteArray())
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKid("key_id")
			.setKty(JsonWebKeyType.RSA);
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(keyAttributes)
			.setKey(jsonWebKey)
			.setManaged(Boolean.TRUE);
		when(keysClient.createKey("access_token_string", "key_name", keyCreateParameters))
			.thenReturn(
				Uni.createFrom().failure(new WebApplicationException(401)),
				Uni.createFrom().item(keyBundle));

		/*
		 * Test.
		 */
		keysService.createKey("key_name", keyCreateParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyBundle);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	@Order(5)
	void given_createKeyRequest_when_keysClientReturns403_then_getNewAccessTokenAndRetry() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyCreateParameters keyCreateParameters = new KeyCreateParameters()
			.setAttributes(keyAttributes)
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(4096)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(Integer.MAX_VALUE);
		JsonWebKey jsonWebKey = new JsonWebKey()
			.setE(BigInteger.ONE.toByteArray())
			.setN(BigInteger.TEN.toByteArray())
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKid("key_id")
			.setKty(JsonWebKeyType.RSA);
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(keyAttributes)
			.setKey(jsonWebKey)
			.setManaged(Boolean.TRUE);
		when(keysClient.createKey("access_token_string", "key_name", keyCreateParameters))
			.thenReturn(
				Uni.createFrom().failure(new WebApplicationException(403)),
				Uni.createFrom().item(keyBundle));

		/*
		 * Test.
		 */
		keysService.createKey("key_name", keyCreateParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyBundle);
	}

	/**
	 * 
	 */
	@Test
	@Order(6)
	void given_createKeyRequest_when_keysClientReturns404_then_getFailure() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyCreateParameters keyCreateParameters = new KeyCreateParameters()
			.setAttributes(keyAttributes)
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(4096)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(Integer.MAX_VALUE);
		when(keysClient.createKey("access_token_string", "key_name", keyCreateParameters))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(404)));

		/*
		 * Test.
		 */
		keysService.createKey("key_name", keyCreateParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailed();
	}

	/**
	 * 
	 */
	@Test
	@Order(7)
	void given_createKeyRequest_when_keysClientReturnsFailure_then_getFailure() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyCreateParameters keyCreateParameters = new KeyCreateParameters()
			.setAttributes(keyAttributes)
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(4096)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(Integer.MAX_VALUE);
		when(keysClient.createKey("access_token_string", "key_name", keyCreateParameters))
			.thenReturn(Uni.createFrom().failure(new Exception("other_failure")));

		/*
		 * Test.
		 */
		keysService.createKey("key_name", keyCreateParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailed();
	}

	/**
	 * 
	 */
	@Test
	@Order(8)
	void given_createKeyRequest_when_keysClientThrowsException_then_getFailure() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyCreateParameters keyCreateParameters = new KeyCreateParameters()
			.setAttributes(keyAttributes)
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKeySize(4096)
			.setKty(JsonWebKeyType.RSA)
			.setPublicExponent(Integer.MAX_VALUE);
		when(keysClient.createKey("access_token_string", "key_name", keyCreateParameters))
			.thenThrow(new RuntimeException("exception_while_proceeding"));

		/*
		 * Test.
		 */
		keysService.createKey("key_name", keyCreateParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailed();
	}

	/**
	 * 
	 */
	@Test
	@Order(9)
	void given_keyList_when_getKeysInvoked_then_getKeyList() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyItem keyItem = new KeyItem()
			.setAttributes(keyAttributes)
			.setKid("key_id")
			.setManaged(Boolean.TRUE);
		KeyListResult keyList = new KeyListResult()
			.setValue(List.of(keyItem));
		when(keysClient.getKeys("access_token_string"))
			.thenReturn(Uni.createFrom().item(keyList));

		/*
		 * Test.
		 */
		keysService.getKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyList);
	}

	/**
	 * 
	 */
	@Test
	@Order(10)
	void given_keyBundle_when_getKeyInvoked_then_getKeyBundle() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		JsonWebKey jsonWebKey = new JsonWebKey()
			.setE(BigInteger.ONE.toByteArray())
			.setN(BigInteger.TEN.toByteArray())
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setKid("key_id")
			.setKty(JsonWebKeyType.RSA);
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(keyAttributes)
			.setKey(jsonWebKey)
			.setManaged(Boolean.TRUE);
		when(keysClient.getKey("access_token_string", "key_name", "key_version"))
			.thenReturn(Uni.createFrom().item(keyBundle));

		/*
		 * Test.
		 */
		keysService.getKey("key_name", "key_version")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyBundle);
	}

	/**
	 * 
	 */
	@Test
	@Order(11)
	void given_keyVersionList_when_getKeyVersionInvoked_then_getKeyVersionList() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyAttributes keyAttributes = new KeyAttributes()
			.setCreated(now.getEpochSecond())
			.setEnabled(Boolean.TRUE)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setExportable(Boolean.FALSE)
			.setNbf(now.getEpochSecond())
			.setRecoverableDays(90)
			.setRecoveryLevel(DeletionRecoveryLevel.RECOVERABLE_PURGEABLE)
			.setUpdated(now.getEpochSecond());
		KeyItem keyItem = new KeyItem()
			.setAttributes(keyAttributes)
			.setKid("key_id")
			.setManaged(Boolean.TRUE);
		KeyListResult keyList = new KeyListResult()
			.setValue(List.of(keyItem));
		when(keysClient.getKeyVersions("access_token_string", "key_name"))
			.thenReturn(Uni.createFrom().item(keyList));

		/*
		 * Test.
		 */
		keysService.getKeyVersions("key_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyList);
	}

	/**
	 * 
	 */
	@Test
	@Order(12)
	void given_signRequest_when_signMethodInvoked_then_getSignature() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeySignParameters keySignParameters = new KeySignParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256)
			.setValue(new byte[0]);
		KeyOperationResult keyOperationResult = new KeyOperationResult()
			.setValue(new byte[0]);
		when(keysClient.sign("access_token_string", "key_name", "key_version", keySignParameters))
			.thenReturn(Uni.createFrom().item(keyOperationResult));

		/*
		 * Test.
		 */
		keysService.sign("key_name", "key_version", keySignParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyOperationResult);
	}

	/**
	 * 
	 */
	@Test
	@Order(13)
	void given_verifyRequest_when_verifyMethodInvoked_then_getVerificationResult() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyVerifyParameters keyVerifyParameters = new KeyVerifyParameters()
			.setAlg(JsonWebKeySignatureAlgorithm.RS256)
			.setValue(new byte[0]);
		KeyVerifyResult keyVerifyResult = new KeyVerifyResult()
			.setValue(Boolean.TRUE);
		when(keysClient.verify("access_token_string", "key_name", "key_version", keyVerifyParameters))
			.thenReturn(Uni.createFrom().item(keyVerifyResult));

		/*
		 * Test.
		 */
		keysService.verify("key_name", "key_version", keyVerifyParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyVerifyResult);
	}

	/**
	 * 
	 */
	@Test
	@Order(14)
	void given_encryptRequest_when_encryptMethodInvoked_then_getEncryptedData() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyOperationParameters keyOperationParameters = new KeyOperationParameters()
			.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
			.setValue(new byte[0]);
		KeyOperationResult keyOperationResult = new KeyOperationResult()
			.setValue(new byte[0]);
		when(keysClient.encrypt("access_token_string", "key_name", "key_version", keyOperationParameters))
			.thenReturn(Uni.createFrom().item(keyOperationResult));

		/*
		 * Test.
		 */
		keysService.encrypt("key_name", "key_version", keyOperationParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyOperationResult);
	}

	/**
	 * 
	 */
	@Test
	@Order(15)
	void given_decryptRequest_when_decryptMethodInvoked_then_getDecryptedData() {
		/*
		 * Setup.
		 */
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Setup.
		 */
		KeyOperationParameters keyOperationParameters = new KeyOperationParameters()
			.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
			.setValue(new byte[0]);
		KeyOperationResult keyOperationResult = new KeyOperationResult()
			.setValue(new byte[0]);
		when(keysClient.decrypt("access_token_string", "key_name", "key_version", keyOperationParameters))
			.thenReturn(Uni.createFrom().item(keyOperationResult));

		/*
		 * Test.
		 */
		keysService.decrypt("key_name", "key_version", keyOperationParameters)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyOperationResult);
	}
}
