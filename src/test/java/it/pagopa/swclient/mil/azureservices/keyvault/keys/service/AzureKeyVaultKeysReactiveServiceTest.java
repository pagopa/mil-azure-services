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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletedKeyBundle;
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
 * @author Antonio Tarricone
 */
@QuarkusTest
class AzureKeyVaultKeysReactiveServiceTest {
	/*
	 * 
	 */
	@InjectMock
	AzureIdentityReactiveService identityService;

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
		Mockito.reset(keysClient);
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityService.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));
		when(identityService.getNewAccessTokenAndCacheIt(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));
	}

	/**
	 * 
	 */
	@Test
	void given_createKeyRequest_when_createKeyIsInvoked_then_getKeyBundle() {
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
	void given_createKeyRequest_when_keysClientReturns401_then_getNewAccessTokenAndRetry() {
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
	void given_createKeyRequest_when_keysClientReturns403_then_getNewAccessTokenAndRetry() {
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
	void given_createKeyRequest_when_keysClientReturns404_then_getFailure() {
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
	void given_createKeyRequest_when_keysClientReturnsFailure_then_getFailure() {
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
	void given_createKeyRequest_when_keysClientThrowsException_then_getFailure() {
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
	void given_keyList_when_getKeysInvoked_then_getKeyList() {
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
	void given_exceptionFromKv_when_getKeysInvoked_then_getFailure() {
		/*
		 * Setup.
		 */
		when(keysClient.getKeys("access_token_string"))
			.thenThrow(WebApplicationException.class);

		/*
		 * Test.
		 */
		keysService.getKeys()
			.log(">>>")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();
	}

	/**
	 * 
	 */
	@Test
	void given_keyBundle_when_getKeyInvoked_then_getKeyBundle() {
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
	void given_keyVersionList_when_getKeyVersionInvoked_then_getKeyVersionList() {
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
	void given_signRequest_when_signMethodInvoked_then_getSignature() {
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
	void given_verifyRequest_when_verifyMethodInvoked_then_getVerificationResult() {
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
	void given_encryptRequest_when_encryptMethodInvoked_then_getEncryptedData() {
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
	void given_decryptRequest_when_decryptMethodInvoked_then_getDecryptedData() {
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

	/**
	 * 
	 */
	@Test
	void given_keyName_when_deleteKeyInvoked_then_getDeletedKeyBundle() {
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
		DeletedKeyBundle keyBundle = new DeletedKeyBundle()
			.setAttributes(keyAttributes)
			.setKey(jsonWebKey)
			.setManaged(Boolean.TRUE);
		when(keysClient.deleteKey("access_token_string", "key_name"))
			.thenReturn(Uni.createFrom().item(keyBundle));

		/*
		 * Test.
		 */
		keysService.deleteKey("key_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(keyBundle);
	}
}
