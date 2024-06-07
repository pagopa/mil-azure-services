/*
 * AzureKeyVaultKeysExtReactiveService.java
 *
 * 21 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Context;
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

	/*
	 * 
	 */
	private static final String SKIPTOKEN_KEYS_KEY = "skiptoken-keys";

	/*
	 * 
	 */
	private static final String SKIPTOKEN_VERS_KEY = "skiptoken-vers";

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
		Log.trace("Get keys");
		Context context = Context.empty();
		return Multi.createBy().repeating()
			.uni(
				() -> context,
				c -> {
					if (c.contains(SKIPTOKEN_KEYS_KEY)) {
						Log.trace("$skiptoken present");
						return keysService.getKeys(c.get(SKIPTOKEN_KEYS_KEY));
					} else {
						Log.trace("$skiptoken not present");
						return keysService.getKeys();
					}
				})
			.whilst(page -> {
				Log.trace("Verify stop/continue");
				String nextLink = page.getNextLink();
				if (nextLink == null) {
					Log.trace("There are no other key pages");
					context.delete(SKIPTOKEN_KEYS_KEY);
					return false;
				} else {
					Map<String, String> queryParameters = KeyUtils.getQueryParameters(nextLink);
					String skiptoken = queryParameters.get("$skiptoken");
					if (skiptoken == null) {
						Log.warnf("nextLink present but doesn't have $skiptoken query param: %s", nextLink);
						context.delete(SKIPTOKEN_KEYS_KEY);
						return false;
					} else {
						Log.tracef("There are other key pages: %s", skiptoken);
						context.put(SKIPTOKEN_KEYS_KEY, skiptoken);
						return true;
					}
				}
			})
			.map(KeyListResult::getValue)
			.onItem()
			.disjoint();
	}

	/**
	 * 
	 * @return
	 */
	private Multi<KeyItem> getKeyVersions(String keyName) {
		Context context = Context.empty();
		return Multi.createBy().repeating()
			.uni(
				() -> context,
				c -> {
					if (c.contains(SKIPTOKEN_VERS_KEY)) {
						return keysService.getKeyVersions(keyName, c.get(SKIPTOKEN_VERS_KEY));
					} else {
						return keysService.getKeyVersions(keyName);
					}
				})
			.whilst(page -> {
				String nextLink = page.getNextLink();
				if (nextLink == null) {
					Log.trace("There are no other key version pages");
					context.delete(SKIPTOKEN_VERS_KEY);
					return false;
				} else {
					Map<String, String> queryParameters = KeyUtils.getQueryParameters(nextLink);
					String skiptoken = queryParameters.get("$skiptoken");
					if (skiptoken == null) {
						Log.warnf("nextLink present but doesn't have $skiptoken query param: %s", nextLink);
						context.delete(SKIPTOKEN_VERS_KEY);
						return false;
					} else {
						Log.tracef("There are other key version pages: %s", skiptoken);
						context.put(SKIPTOKEN_VERS_KEY, skiptoken);
						return true;
					}
				}
			})
			.map(KeyListResult::getValue)
			.onItem()
			.disjoint();
	}

	/**
	 * 
	 * @param domain
	 * @param expectedOps  {@link JsonWebKeyOperation}
	 * @param expectedKtys {@link JsonWebKeyType}
	 * @return
	 */
	public Multi<KeyBundle> getKeys(String domain, List<String> expectedOps, List<String> expectedKtys) {
		return getKeys() // Multi<KeyItem>
			.filter(keyItem -> KeyUtils.doesDomainMatch(keyItem, domain))
			.map(KeyUtils::getKeyName) // Multi<String> keyName
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