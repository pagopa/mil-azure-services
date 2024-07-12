/*
 * KeyUtilsTest.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;

/**
 * Additional tests.
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class KeyUtilsTest {
	/**
	 * 
	 */
	@Test
	void testDoesDomainMatch_ok() {
		assertTrue(KeyUtils.doesDomainMatch(new KeyItem().setTags(Map.of(KeyUtils.DOMAIN_KEY, "my_domain")), "my_domain"));
	}

	/**
	 * 
	 */
	@Test
	void testDoesDomainMatch_wo_tags() {
		assertFalse(KeyUtils.doesDomainMatch(new KeyItem(), "my_domain"));
	}

	/**
	 * 
	 */
	@Test
	void testDoesDomainMatch_ko() {
		assertFalse(KeyUtils.doesDomainMatch(new KeyItem().setTags(Map.of(KeyUtils.DOMAIN_KEY, "my_domain")), "different_domain"));
	}

	/**
	 * 
	 */
	@Test
	void testDoesDomainMatch_wo_tag() {
		assertFalse(KeyUtils.doesDomainMatch(new KeyItem().setTags(Map.of()), "my_domain"));
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