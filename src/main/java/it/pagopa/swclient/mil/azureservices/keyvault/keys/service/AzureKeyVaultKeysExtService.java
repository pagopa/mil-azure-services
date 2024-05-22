/*
 * AzureKeyVaultKeysExtService.java
 *
 * 22 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author antonio.tarricone
 */
@ApplicationScoped
public class AzureKeyVaultKeysExtService {
	/*
	 * 
	 */
	private AzureKeyVaultKeysService keysService;

	/**
	 * 
	 * @param keysService
	 */
	@Inject
	AzureKeyVaultKeysExtService(AzureKeyVaultKeysService keysService) {
		this.keysService = keysService;
	}

	/**
	 * 
	 * @return
	 */
	private Stream<KeyItem> getKeys() {
		return keysService.getKeys()
			.getValue()
			.stream();
	}

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	private Stream<KeyItem> getKeyVersions(String keyName) {
		return keysService.getKeyVersions(keyName)
			.getValue()
			.stream();
	}

	/**
	 * 
	 * @param prefix
	 * @param expectedOps
	 * @param expectedKtys
	 * @return
	 */
	public Stream<KeyBundle> getKeys(String prefix, List<JsonWebKeyOperation> expectedOps, List<JsonWebKeyType> expectedKtys) {
		return getKeys() // Stream<KeyItem>
			.map(KeyUtils::getKeyName) // Stream<String> keyName
			.filter(keyName -> KeyUtils.doesPrefixMatch(keyName, prefix))
			.flatMap(this::getKeyVersions) // Stream<KeyItem>
			.filter(KeyUtils::isValid)
			.map(KeyUtils::getKeyNameVersion) // Stream<String[]>
			.map(keyNameVersion -> {
				Log.tracef("name = %s, version = %s", keyNameVersion[0], keyNameVersion[1]);
				return keysService.getKey(keyNameVersion[0], keyNameVersion[1]);
			}) // Stream<KeyBundle>
			.filter(keyBundle -> KeyUtils.doOpsMatch(keyBundle, expectedOps))
			.filter(keyBundle -> KeyUtils.doesTypeMatch(keyBundle, expectedKtys));
	}

	/**
	 * 
	 * @param prefix
	 * @param expectedOps
	 * @param expectedKtys
	 * @return
	 */
	public Optional<KeyBundle> getKeyWithLongestExp(String prefix, List<JsonWebKeyOperation> expectedOps, List<JsonWebKeyType> expectedKtys) {
		Comparator<KeyBundle> comparator = Comparator.comparing(
			new Function<KeyBundle, Long>() { // NOSONAR
				@Override
				public Long apply(KeyBundle t) {
					return t.getAttributes().getExp();
				}

			})
			.reversed();

		return getKeys(prefix, expectedOps, expectedKtys)
			.sorted(comparator)
			.findFirst();
	}
}