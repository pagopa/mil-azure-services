/*
 * AzureKeyVaultKeysExtReactiveService.java
 *
 * 21 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyListResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyVaultKeysExtReactiveService {
	/*
	 * 
	 */
	private AzureKeyVaultKeysReactiveService keysService;

	/**
	 * 
	 * @param keysService
	 */
	@Inject
	AzureKeyVaultKeysExtReactiveService(AzureKeyVaultKeysReactiveService keysService) {
		this.keysService = keysService;
	}

	/**
	 * 
	 * @return
	 */
	private Multi<KeyItem> getKeys() {
		return keysService.getKeys()
			.map(KeyListResult::getValue)
			.onItem().transformToMulti(Multi.createFrom()::iterable);
	}

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	private Multi<KeyItem> getKeyVersions(String keyName) {
		return keysService.getKeyVersions(keyName)
			.map(KeyListResult::getValue)
			.onItem().transformToMulti(Multi.createFrom()::iterable);
	}

	/**
	 * 
	 * @param prefix
	 * @param expectedOps  {@link JsonWebKeyOperation}
	 * @param expectedKtys {@link JsonWebKeyType}
	 * @return
	 */
	public Multi<KeyBundle> getKeys(String prefix, List<String> expectedOps, List<String> expectedKtys) {
		return getKeys() // Multi<KeyItem>
			.map(KeyUtils::getKeyName) // Multi<String> keyName
			.filter(keyName -> KeyUtils.doesPrefixMatch(keyName, prefix))
			.onItem().transformToMultiAndConcatenate(this::getKeyVersions) // Multi<KeyItem>
			.filter(KeyUtils::isValid)
			.map(KeyUtils::getKeyNameVersion) // Multi<String[]>
			.onItem().transformToMultiAndConcatenate(keyNameVersion -> keysService.getKey(keyNameVersion[0], keyNameVersion[1]).toMulti()) // Multi<KeyBundle>
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
	public Uni<Optional<KeyBundle>> getKeyWithLongestExp(String prefix, List<String> expectedOps, List<String> expectedKtys) {
		return getKeys(prefix, expectedOps, expectedKtys)
			.collect()
			.asList()
			.map(keyList -> {
				if (keyList.isEmpty()) {
					Log.debug("No key found");
					return Optional.empty();
				} else {
					Log.trace("Keys found");
					
					Comparator<KeyBundle> comparator = Comparator.comparing(
						new Function<KeyBundle, Long>() { // NOSONAR
							@Override
							public Long apply(KeyBundle t) {
								return t.getAttributes().getExp();
							}

						})
						.reversed();
					
					keyList.sort(comparator);
					return Optional.of(keyList.getFirst());
				}
			});
	}
}