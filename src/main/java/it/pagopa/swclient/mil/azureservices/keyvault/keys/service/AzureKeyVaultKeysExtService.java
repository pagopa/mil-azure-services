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

import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
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
	 * @param expectedOps  {@link JsonWebKeyOperation}
	 * @param expectedKtys {@link JsonWebKeyType}
	 * @return
	 */
	public Stream<KeyBundle> getKeys(String prefix, List<String> expectedOps, List<String> expectedKtys) {
		return getKeys() // Stream<KeyItem>
			.map(KeyUtils::getKeyName) // Stream<String> keyName
			.filter(keyName -> KeyUtils.doesPrefixMatch(keyName, prefix))
			.flatMap(this::getKeyVersions) // Stream<KeyItem>
			.filter(KeyUtils::isValid)
			.map(KeyUtils::getKeyNameVersion) // Stream<String[]>
			.map(keyNameVersion -> keysService.getKey(keyNameVersion[0], keyNameVersion[1])) // Stream<KeyBundle>
			.filter(keyBundle -> KeyUtils.doOpsMatch(keyBundle, expectedOps))
			.filter(keyBundle -> KeyUtils.doesTypeMatch(keyBundle, expectedKtys));
	}

	/**
	 * 
	 * @param prefix
	 * @param expectedOps  {@link JsonWebKeyOperation}
	 * @param expectedKtys {@link JsonWebKeyType}
	 * @return
	 */
	public Optional<KeyBundle> getKeyWithLongestExp(String prefix, List<String> expectedOps, List<String> expectedKtys) {
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