/*
 * KeyUtilsTest.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;

/**
 * These added tests are needed to reach expected coverage!
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class KeyUtilsTest {
	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils#doesPrefixMatch(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testDoesPrefixMatch() {
		assertTrue(KeyUtils.doesPrefixMatch("key_name", null));
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils#doOpsMatch(it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle, java.util.List)}.
	 */
	@Test
	void testDoOpsMatch() {
		assertTrue(KeyUtils.doOpsMatch(new KeyBundle()
			.setAttributes(new KeyAttributes())
			.setKey(new JsonWebKey()
				.setKeyOps(List.of())),
			null));
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils#doesTypeMatch(it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle, java.util.List)}.
	 */
	@Test
	void testDoesTypeMatch() {
		assertTrue(KeyUtils.doesTypeMatch(new KeyBundle()
			.setAttributes(new KeyAttributes())
			.setKey(new JsonWebKey()
				.setKty(JsonWebKeyType.RSA)),
			null));
	}
}