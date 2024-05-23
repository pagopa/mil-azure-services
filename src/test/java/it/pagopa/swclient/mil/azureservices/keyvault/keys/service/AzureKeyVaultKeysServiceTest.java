/*
 * AzureKeyVaultKeysServiceTest.java
 *
 * 20 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletionRecoveryLevel;
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
import it.pagopa.swclient.mil.azureservices.keyvault.keys.client.AzureKeyVaultKeysClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class AzureKeyVaultKeysServiceTest {
	/*
	 * 
	 */
	@InjectMock
	AzureIdentityService identityService;

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureKeyVaultKeysClient keysClient;

	/*
	 * 
	 */
	@Inject
	AzureKeyVaultKeysService keysService;

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
			.thenReturn(accessToken);
		AccessToken newAccessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("new_access_token_string");
		when(identityService.getNewAccessTokenAndCacheIt(Scope.VAULT))
			.thenReturn(newAccessToken);
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
			.thenReturn(keyBundle);

		/*
		 * Test.
		 */
		keysService.createKey("key_name", keyCreateParameters);

		assertEquals(keyBundle, keysService.createKey("key_name", keyCreateParameters));
	}

	/**
	 * 
	 */
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
			.thenThrow(new WebApplicationException(401));
		when(keysClient.createKey("new_access_token_string", "key_name", keyCreateParameters))
			.thenReturn(keyBundle);

		/*
		 * Test.
		 */
		assertEquals(keyBundle, keysService.createKey("key_name", keyCreateParameters));
	}

	/**
	 * 
	 */
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
			.thenThrow(new WebApplicationException(403));
		when(keysClient.createKey("new_access_token_string", "key_name", keyCreateParameters))
			.thenReturn(keyBundle);

		/*
		 * Test.
		 */
		assertEquals(keyBundle, keysService.createKey("key_name", keyCreateParameters));
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
			.thenThrow(new WebApplicationException(404));

		/*
		 * Test.
		 */
		assertThrows(WebApplicationException.class, () -> keysService.createKey("key_name", keyCreateParameters));
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
		assertThrows(RuntimeException.class, () -> keysService.createKey("key_name", keyCreateParameters));
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
			.thenReturn(keyList);

		/*
		 * Test.
		 */
		assertEquals(keyList, keysService.getKeys());
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
			.thenReturn(keyBundle);

		/*
		 * Test.
		 */
		assertEquals(keyBundle, keysService.getKey("key_name", "key_version"));
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
			.thenReturn(keyList);

		/*
		 * Test.
		 */
		assertEquals(keyList, keysService.getKeyVersions("key_name"));
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
			.thenReturn(keyOperationResult);

		/*
		 * Test.
		 */
		assertEquals(keyOperationResult, keysService.sign("key_name", "key_version", keySignParameters));
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
			.thenReturn(keyVerifyResult);

		/*
		 * Test.
		 */
		assertEquals(keyVerifyResult, keysService.verify("key_name", "key_version", keyVerifyParameters));
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
			.thenReturn(keyOperationResult);

		/*
		 * Test.
		 */
		assertEquals(keyOperationResult, keysService.encrypt("key_name", "key_version", keyOperationParameters));
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
			.thenReturn(keyOperationResult);

		/*
		 * Test.
		 */
		assertEquals(keyOperationResult, keysService.decrypt("key_name", "key_version", keyOperationParameters));
	}
}
