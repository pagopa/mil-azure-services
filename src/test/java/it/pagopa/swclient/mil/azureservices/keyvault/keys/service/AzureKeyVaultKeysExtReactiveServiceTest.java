/*
 * AzureKeyVaultKeysExtReactiveServiceTest.java
 *
 * 22 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class AzureKeyVaultKeysExtReactiveServiceTest {
	/*
	 * 
	 */
	@InjectMock
	AzureKeyVaultKeysReactiveService keysService;

	/*
	 * 
	 */
	@Inject
	AzureKeyVaultKeysExtReactiveService extService;

	/*
	 * 
	 */
	private KeyBundle bundle__attr_ok__key_rsa_sign_verify;
	private KeyBundle bundle__attr_ok_longest_exp__key_rsa_sign_verify;
	private KeyBundle bundle__attr_wo_created__key_rsa_sign_verify;
	private KeyBundle bundle__attr_wo_nbf__key_rsa_sign_verify;

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
	 * 
	 * @param testInfo
	 */
	@AfterEach
	void reset(TestInfo testInfo) {
		String frame = "*".repeat(testInfo.getDisplayName().length() + 11);
		System.out.println(frame);
		System.out.printf("* %s: RESET *%n", testInfo.getDisplayName());
		System.out.println(frame);
		Mockito.reset(keysService);
	}

	/**
	 * 
	 */
	private void setup() {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		/*
		 * Attributes
		 */
		KeyAttributes attr_ok = new KeyAttributes()
			.setCreated(now.minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setEnabled(true)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setNbf(now.minus(3, ChronoUnit.MINUTES).getEpochSecond());

		KeyAttributes attr_ok_longest_exp = new KeyAttributes()
			.setCreated(now.minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setEnabled(true)
			.setExp(now.plus(10, ChronoUnit.MINUTES).getEpochSecond())
			.setNbf(now.minus(3, ChronoUnit.MINUTES).getEpochSecond());

		KeyAttributes attr_wo_nbf = new KeyAttributes()
			.setCreated(now.minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setEnabled(true)
			.setExp(now.plus(3, ChronoUnit.MINUTES).getEpochSecond())
			.setNbf(null);

		KeyAttributes attr_nbf_not_reached = new KeyAttributes()
			.setCreated(now.minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setEnabled(true)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setNbf(now.plus(3, ChronoUnit.MINUTES).getEpochSecond());

		KeyAttributes attr_expired = new KeyAttributes()
			.setCreated(now.minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setEnabled(true)
			.setExp(now.minus(1, ChronoUnit.MINUTES).getEpochSecond())
			.setNbf(now.minus(3, ChronoUnit.MINUTES).getEpochSecond());

		KeyAttributes attr_wo_exp = new KeyAttributes()
			.setCreated(now.minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setEnabled(true)
			.setExp(null)
			.setNbf(now.minus(3, ChronoUnit.MINUTES).getEpochSecond());

		KeyAttributes attr_not_enabled = new KeyAttributes()
			.setCreated(now.minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setEnabled(false)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setNbf(now.minus(3, ChronoUnit.MINUTES).getEpochSecond());

		KeyAttributes attr_wo_created = new KeyAttributes()
			.setCreated(null)
			.setEnabled(true)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setNbf(now.minus(3, ChronoUnit.MINUTES).getEpochSecond());

		KeyAttributes attr_inconsistent_created = new KeyAttributes()
			.setCreated(now.plus(3, ChronoUnit.MINUTES).getEpochSecond())
			.setEnabled(true)
			.setExp(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setNbf(now.minus(3, ChronoUnit.MINUTES).getEpochSecond());

		/*
		 * Items
		 */
		KeyItem item__wo_prefix = new KeyItem()
			.setAttributes(attr_ok)
			.setKid("https://myvault.vault.azure.net/keys/wo_prefix");

		KeyItem item__attr_ok__key_no_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_ok)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok__key_no_rsa_sign_verify");

		KeyItem item__attr_ok__key_rsa_no_sign_verify = new KeyItem()
			.setAttributes(attr_ok)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok__key_rsa_no_sign_verify");

		KeyItem item__attr_ok_longest_exp__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_ok_longest_exp)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_rsa_sign_verify");

		KeyItem item__attr_ok_longest_exp__key_no_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_ok_longest_exp)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_no_rsa_sign_verify");

		KeyItem item__attr_ok_longest_exp__key_rsa_no_sign_verify = new KeyItem()
			.setAttributes(attr_ok_longest_exp)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_rsa_no_sign_verify");

		KeyItem item__attr_wo_nbf__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_wo_nbf)
			.setKid("https://myvault.vault.azure.net/keys/attr_wo_nbf__key_rsa_sign_verify");

		KeyItem item__attr_nbf_not_reached__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_nbf_not_reached)
			.setKid("https://myvault.vault.azure.net/keys/attr_nbf_not_reached__key_rsa_sign_verify");

		KeyItem item__attr_expired__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_expired)
			.setKid("https://myvault.vault.azure.net/keys/attr_expired__key_rsa_sign_verify");

		KeyItem item__attr_wo_exp__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_wo_exp)
			.setKid("https://myvault.vault.azure.net/keys/attr_wo_exp__key_rsa_sign_verify");

		KeyItem item__attr_not_enabled__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_not_enabled)
			.setKid("https://myvault.vault.azure.net/keys/attr_not_enabled__key_rsa_sign_verify");

		KeyItem item__attr_wo_created__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_wo_created)
			.setKid("https://myvault.vault.azure.net/keys/attr_wo_created__key_rsa_sign_verify");

		KeyItem item__attr_inconsistent_created__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_inconsistent_created)
			.setKid("https://myvault.vault.azure.net/keys/attr_inconsistent_created__key_rsa_sign_verify");

		KeyListResult keyListPage1 = new KeyListResult()
			.setValue(List.of(
				item__wo_prefix,
				item__attr_ok__key_no_rsa_sign_verify,
				item__attr_ok__key_rsa_no_sign_verify,
				item__attr_ok_longest_exp__key_rsa_sign_verify))
			.setNextLink("https://myvault.vault.azure.net:443/keys?api-version=7.2&$skiptoken=skip_1st_page&maxresults=4");

		KeyListResult keyListPage2 = new KeyListResult()
			.setValue(List.of(
				item__attr_ok_longest_exp__key_no_rsa_sign_verify,
				item__attr_ok_longest_exp__key_rsa_no_sign_verify,
				item__attr_wo_nbf__key_rsa_sign_verify,
				item__attr_nbf_not_reached__key_rsa_sign_verify))
			.setNextLink("https://myvault.vault.azure.net:443/keys?api-version=7.2&$skiptoken=skip_2nd_page&maxresults=4");

		KeyListResult keyListPage3 = new KeyListResult()
			.setValue(List.of(
				item__attr_expired__key_rsa_sign_verify,
				item__attr_wo_exp__key_rsa_sign_verify,
				item__attr_not_enabled__key_rsa_sign_verify,
				item__attr_wo_created__key_rsa_sign_verify))
			.setNextLink("https://myvault.vault.azure.net:443/keys?api-version=7.2&$skiptoken=skip_3rd_page&maxresults=4");

		KeyListResult keyListPage4 = new KeyListResult()
			.setValue(List.of(item__attr_inconsistent_created__key_rsa_sign_verify))
			.setNextLink(null);

		when(keysService.getKeys())
			.thenReturn(Uni.createFrom().item(keyListPage1));

		when(keysService.getKeys("skip_1st_page"))
			.thenReturn(Uni.createFrom().item(keyListPage2));

		when(keysService.getKeys("skip_2nd_page"))
			.thenReturn(Uni.createFrom().item(keyListPage3));

		when(keysService.getKeys("skip_3rd_page"))
			.thenReturn(Uni.createFrom().item(keyListPage4));

		/*
		 * Versions
		 */
		KeyItem version__attr_ok__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_ok)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_rsa_sign_verify/shortest_exp");

		KeyItem version__attr_ok__key_no_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_ok)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok__key_no_rsa_sign_verify/dont_care");

		KeyItem version__attr_ok__key_rsa_no_sign_verify = new KeyItem()
			.setAttributes(attr_ok)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok__key_rsa_no_sign_verify/dont_care");

		KeyItem version__attr_ok_longest_exp__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_ok_longest_exp)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_rsa_sign_verify/longest_exp");

		KeyItem version__attr_ok_longest_exp__key_no_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_ok_longest_exp)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_no_rsa_sign_verify/dont_care");

		KeyItem version__attr_ok_longest_exp__key_rsa_no_sign_verify = new KeyItem()
			.setAttributes(attr_ok_longest_exp)
			.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_rsa_no_sign_verify/dont_care");

		KeyItem version__attr_wo_nbf__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_wo_nbf)
			.setKid("https://myvault.vault.azure.net/keys/attr_wo_nbf__key_rsa_sign_verify/dont_care");

		KeyItem version__attr_nbf_not_reached__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_nbf_not_reached)
			.setKid("https://myvault.vault.azure.net/keys/attr_nbf_not_reached__key_rsa_sign_verify/dont_care");

		KeyItem version__attr_expired__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_expired)
			.setKid("https://myvault.vault.azure.net/keys/attr_expired__key_rsa_sign_verify/dont_care");

		KeyItem version__attr_wo_exp__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_wo_exp)
			.setKid("https://myvault.vault.azure.net/keys/attr_wo_exp__key_rsa_sign_verify/dont_care");

		KeyItem version__attr_not_enabled__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_not_enabled)
			.setKid("https://myvault.vault.azure.net/keys/attr_not_enabled__key_rsa_sign_verify/dont_care");

		KeyItem version__attr_wo_created__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_wo_created)
			.setKid("https://myvault.vault.azure.net/keys/attr_wo_created__key_rsa_sign_verify/dont_care");

		KeyItem version__attr_inconsistent_created__key_rsa_sign_verify = new KeyItem()
			.setAttributes(attr_inconsistent_created)
			.setKid("https://myvault.vault.azure.net/keys/attr_inconsistent_created__key_rsa_sign_verify/dont_care");

		KeyListResult versionList__attr_ok__key_no_rsa_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_ok__key_no_rsa_sign_verify));

		KeyListResult versionList__attr_ok__key_rsa_no_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_ok__key_rsa_no_sign_verify));

		KeyListResult versionList__attr_ok_longest_exp__key_rsa_sign_verify_page1 = new KeyListResult()
			.setValue(List.of(
				version__attr_ok__key_rsa_sign_verify))
			.setNextLink("https://myvault.vault.azure.net:443/keys/attr_ok_longest_exp__key_rsa_sign_verify/versions?api-version=7.2&$skiptoken=skip_1st_page&maxresults=1");

		KeyListResult versionList__attr_ok_longest_exp__key_rsa_sign_verify_page2 = new KeyListResult()
			.setValue(List.of(
				version__attr_ok_longest_exp__key_rsa_sign_verify))
			.setNextLink(null);

		KeyListResult versionList__attr_ok_longest_exp__key_no_rsa_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_ok_longest_exp__key_no_rsa_sign_verify));

		KeyListResult versionList__attr_ok_longest_exp__key_rsa_no_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_ok_longest_exp__key_rsa_no_sign_verify));

		KeyListResult versionList__attr_wo_nbf__key_rsa_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_wo_nbf__key_rsa_sign_verify));

		KeyListResult versionList__attr_nbf_not_reached__key_rsa_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_nbf_not_reached__key_rsa_sign_verify));

		KeyListResult versionList__attr_expired__key_rsa_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_expired__key_rsa_sign_verify));

		KeyListResult versionList__attr_wo_exp__key_rsa_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_wo_exp__key_rsa_sign_verify));

		KeyListResult versionList__attr_not_enabled__key_rsa_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_not_enabled__key_rsa_sign_verify));

		KeyListResult versionList__attr_wo_created__key_rsa_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_wo_created__key_rsa_sign_verify));

		KeyListResult versionList__attr_inconsistent_created__key_rsa_sign_verify = new KeyListResult()
			.setValue(List.of(version__attr_inconsistent_created__key_rsa_sign_verify));

		when(keysService.getKeyVersions("attr_ok__key_no_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_ok__key_no_rsa_sign_verify));

		when(keysService.getKeyVersions("attr_ok__key_rsa_no_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_ok__key_rsa_no_sign_verify));

		when(keysService.getKeyVersions("attr_ok_longest_exp__key_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_ok_longest_exp__key_rsa_sign_verify_page1));

		when(keysService.getKeyVersions("attr_ok_longest_exp__key_rsa_sign_verify", "skip_1st_page"))
			.thenReturn(Uni.createFrom().item(versionList__attr_ok_longest_exp__key_rsa_sign_verify_page2));

		when(keysService.getKeyVersions("attr_ok_longest_exp__key_no_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_ok_longest_exp__key_no_rsa_sign_verify));

		when(keysService.getKeyVersions("attr_ok_longest_exp__key_rsa_no_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_ok_longest_exp__key_rsa_no_sign_verify));

		when(keysService.getKeyVersions("attr_wo_nbf__key_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_wo_nbf__key_rsa_sign_verify));

		when(keysService.getKeyVersions("attr_nbf_not_reached__key_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_nbf_not_reached__key_rsa_sign_verify));

		when(keysService.getKeyVersions("attr_expired__key_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_expired__key_rsa_sign_verify));

		when(keysService.getKeyVersions("attr_wo_exp__key_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_wo_exp__key_rsa_sign_verify));

		when(keysService.getKeyVersions("attr_not_enabled__key_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_not_enabled__key_rsa_sign_verify));

		when(keysService.getKeyVersions("attr_wo_created__key_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_wo_created__key_rsa_sign_verify));

		when(keysService.getKeyVersions("attr_inconsistent_created__key_rsa_sign_verify"))
			.thenReturn(Uni.createFrom().item(versionList__attr_inconsistent_created__key_rsa_sign_verify));

		/*
		 * Bundles
		 */
		bundle__attr_ok__key_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_ok)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_rsa_sign_verify/shortest_exp"));

		KeyBundle bundle__attr_ok__key_no_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_ok)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.EC)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_ok__key_no_rsa_sign_verify/dont_care"));

		KeyBundle bundle__attr_ok__key_rsa_no_sign_verify = new KeyBundle()
			.setAttributes(attr_ok)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_ok__key_rsa_no_sign_verify/dont_care"));

		bundle__attr_ok_longest_exp__key_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_ok_longest_exp)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_rsa_sign_verify/longest_exp"));

		KeyBundle bundle__attr_ok_longest_exp__key_no_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_ok_longest_exp)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.EC)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_no_rsa_sign_verify/dont_care"));

		KeyBundle bundle__attr_ok_longest_exp__key_rsa_no_sign_verify = new KeyBundle()
			.setAttributes(attr_ok_longest_exp)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_ok_longest_exp__key_rsa_no_sign_verify/dont_care"));

		bundle__attr_wo_nbf__key_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_wo_nbf)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_wo_nbf__key_rsa_sign_verify/dont_care"));

		KeyBundle bundle__attr_nbf_not_reached__key_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_nbf_not_reached)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_nbf_not_reached__key_rsa_sign_verify/dont_care"));

		KeyBundle bundle__attr_expired__key_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_expired)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_expired__key_rsa_sign_verify/dont_care"));

		KeyBundle bundle__attr_wo_exp__key_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_wo_exp)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_wo_exp__key_rsa_sign_verify/dont_care"));

		KeyBundle bundle__attr_not_enabled__key_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_not_enabled)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_not_enabled__key_rsa_sign_verify/dont_care"));

		bundle__attr_wo_created__key_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_wo_created)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_wo_created__key_rsa_sign_verify/dont_care"));

		KeyBundle bundle__attr_inconsistent_created__key_rsa_sign_verify = new KeyBundle()
			.setAttributes(attr_inconsistent_created)
			.setKey(new JsonWebKey()
				.setD(new byte[0])
				.setE(new byte[0])
				.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
				.setKty(JsonWebKeyType.RSA)
				.setN(new byte[0])
				.setKid("https://myvault.vault.azure.net/keys/attr_inconsistent_created__key_rsa_sign_verify/dont_care"));

		when(keysService.getKey("attr_ok_longest_exp__key_rsa_sign_verify", "shortest_exp"))
			.thenReturn(Uni.createFrom().item(bundle__attr_ok__key_rsa_sign_verify));

		when(keysService.getKey("attr_ok__key_no_rsa_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_ok__key_no_rsa_sign_verify));

		when(keysService.getKey("attr_ok__key_rsa_no_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_ok__key_rsa_no_sign_verify));

		when(keysService.getKey("attr_ok_longest_exp__key_rsa_sign_verify", "longest_exp"))
			.thenReturn(Uni.createFrom().item(bundle__attr_ok_longest_exp__key_rsa_sign_verify));

		when(keysService.getKey("attr_ok_longest_exp__key_no_rsa_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_ok_longest_exp__key_no_rsa_sign_verify));

		when(keysService.getKey("attr_ok_longest_exp__key_rsa_no_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_ok_longest_exp__key_rsa_no_sign_verify));

		when(keysService.getKey("attr_wo_nbf__key_rsa_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_wo_nbf__key_rsa_sign_verify));

		when(keysService.getKey("attr_nbf_not_reached__key_rsa_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_nbf_not_reached__key_rsa_sign_verify));

		when(keysService.getKey("attr_expired__key_rsa_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_expired__key_rsa_sign_verify));

		when(keysService.getKey("attr_wo_exp__key_rsa_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_wo_exp__key_rsa_sign_verify));

		when(keysService.getKey("attr_not_enabled__key_rsa_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_not_enabled__key_rsa_sign_verify));

		when(keysService.getKey("attr_wo_created__key_rsa_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_wo_created__key_rsa_sign_verify));

		when(keysService.getKey("attr_inconsistent_created__key_rsa_sign_verify", "dont_care"))
			.thenReturn(Uni.createFrom().item(bundle__attr_inconsistent_created__key_rsa_sign_verify));
	}

	/**
	 * 
	 */
	@Test
	void given_setOfKeys_when_getKeysInvoked_then_getRelevantKeys() {
		/*
		 * Setup
		 */
		setup();

		/*
		 * Test
		 */
		Iterable<KeyBundle> actualBundles = extService.getKeys(
			"attr",
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA))
			.subscribe()
			.asIterable();

		assertThat(actualBundles)
			.containsExactlyInAnyOrder(
				bundle__attr_ok__key_rsa_sign_verify,
				bundle__attr_ok_longest_exp__key_rsa_sign_verify,
				bundle__attr_wo_created__key_rsa_sign_verify,
				bundle__attr_wo_nbf__key_rsa_sign_verify);
	}

	/**
	 * 
	 */
	@Test
	void given_setOfKeys_when_getKeyWithLongestExpInvoked_then_getRelevantKey() {
		/*
		 * Setup
		 */
		setup();

		/*
		 * Test
		 */
		extService.getKeyWithLongestExp(
			"attr",
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(Optional.of(bundle__attr_ok_longest_exp__key_rsa_sign_verify));
	}

	/**
	 * 
	 */
	@Test
	void given_noKey_when_getKeyWithLongestExpInvoked_then_getEmpty() {
		/*
		 * Setup
		 */
		when(keysService.getKeys())
			.thenReturn(Uni.createFrom().item(new KeyListResult()
				.setValue(List.of())));

		/*
		 * Test
		 */
		extService.getKeyWithLongestExp(
			"attr",
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(Optional.empty());
	}
}
