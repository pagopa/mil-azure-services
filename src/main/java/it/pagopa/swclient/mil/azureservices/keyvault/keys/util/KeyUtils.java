/*
 * KeyUtils.java
 *
 * 21 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;

/**
 * <p>
 * Provides utilities to handle keys in different formats.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public class KeyUtils {
	/**
	 * <p>
	 * Key of tag to mark the domain (who uses it) of a Key handled by Azure Key Vault.
	 * </p>
	 */
	public static final String DOMAIN_KEY = "domain";

	/**
	 * <p>
	 * This class has static methods only.
	 * </p>
	 */
	private KeyUtils() {
		// This class has static methods only.
	}

	/**
	 * <p>
	 * Returns the name of an Azure Key Vault key.
	 * </p>
	 * 
	 * @param keyItem {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem KeyItem}
	 * @return The name of the key.
	 */
	public static String getKeyName(KeyItem keyItem) {
		return URI.create(keyItem.getKid()).getPath().split("/")[2];
	}

	/**
	 * <p>
	 * Returns the name and the version of an Azure Key Vault key.
	 * </p>
	 * 
	 * @param keyItem {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem KeyItem}
	 * @return An array which contains in first position the name of the key and in second the version.
	 */
	public static String[] getKeyNameVersion(KeyItem keyItem) {
		String[] segments = URI.create(keyItem.getKid()).getPath().split("/");
		return new String[] {
			segments[2], segments[3]
		};
	}

	/**
	 * <p>
	 * Verifies if an Azure Key Vault key has the wanted domain (who use it).
	 * </p>
	 * 
	 * @param keyItem {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem KeyItem}
	 * @param domain  The wanted domain.
	 * @return {@code true} if the domain matches, otherwise {@code false}.
	 */
	public static boolean doesDomainMatch(KeyItem keyItem, String domain) {
		Map<String, String> tags = keyItem.getTags();
		return (tags != null && Objects.equals(domain, tags.get(DOMAIN_KEY))) ||
			(tags == null && domain == null);
	}

	/**
	 * <p>
	 * Verifies if a key:
	 * </p>
	 * <ul>
	 * <li>is enabled;</li>
	 * <li>is not expired;</li>
	 * <li>has coherent creation date;</li>
	 * <li>has coherent not-before-date.</li>
	 * </ul>
	 * 
	 * @param kid           The ID of the key.
	 * @param keyAttributes {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes
	 *                      KeyAttributes}
	 * @return {@code true} if the key is valid, otherwise {@code false}.
	 */
	public static boolean isValid(String kid, KeyAttributes keyAttributes) {
		/*
		 * 
		 */
		if (!Objects.equals(keyAttributes.getEnabled(), Boolean.TRUE)) {
			Log.debugf("Key isn't enabled: kid = %s", kid);
			return false;
		}
		Log.tracef("Key is enabled: kid = %s", kid);

		/*
		 * 
		 */
		long now = Instant.now().getEpochSecond();
		Long exp = keyAttributes.getExp();
		if (exp == null || exp < now) {
			Log.debugf("Key is expired or hasn't an expiration: kid = %s, exp = %d, now = %d", kid, exp, now);
			return false;
		}
		Log.tracef("Key isn't expired: kid = %s, exp = %d, now = %d", kid, exp, now);

		/*
		 * 
		 */
		Long created = keyAttributes.getCreated();
		if (created != null && created > now) {
			Log.warnf("Key has an inconsistent creation date: kid = %s, created = %d, now = %d", kid, created, now);
			return false;
		}
		Log.tracef("Key has a consistent creation date or hasn't one: kid = %s, created = %d, now = %d", kid, created, now);

		/*
		 * 
		 */
		Long nbf = keyAttributes.getNbf();
		if (nbf != null && nbf > now) {
			Log.debugf("Key not-before-date hasn't been reached: kid = %s, nbf = %d, now = %d", kid, nbf, now);
			return false;
		}
		Log.tracef("Key not-before-date has been reached or hasn't one: kid = %s, nbf = %d, now = %d", kid, nbf, now);

		return true;
	}

	/**
	 * <p>
	 * Verifies if a key:
	 * </p>
	 * <ul>
	 * <li>is enabled;</li>
	 * <li>is not expired;</li>
	 * <li>has coherent creation date;</li>
	 * <li>has coherent not-before-date.</li>
	 * </ul>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils#isValid(String,
	 *      KeyAttributes) isValid(String, KeyAttributes)
	 * @param keyItem {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem KeyItem}
	 * @return {@code true} if the key is valid, otherwise {@code false}.
	 */
	public static boolean isValid(KeyItem keyItem) {
		return isValid(keyItem.getKid(), keyItem.getAttributes());
	}

	/**
	 * <p>
	 * Verifies if a key is suitable for given operations.
	 * </p>
	 * 
	 * @param keyBundle   {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle
	 *                    KeyBundle}
	 * @param expectedOps {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation
	 *                    JsonWebKeyOperation}
	 * @return {@code true} if the key is suitable, otherwise {@code false}.
	 */
	public static boolean doOpsMatch(KeyBundle keyBundle, List<String> expectedOps) {
		JsonWebKey key = keyBundle.getKey();
		String kid = key.getKid();
		List<String> actualOps = key.getKeyOps();
		if (expectedOps == null || actualOps.containsAll(expectedOps)) {
			Log.tracef("Operations match or are null: kid = %s, actualOps = %s, expectedOps = %s", kid, actualOps, expectedOps);
			return true;
		}

		Log.debugf("Operations don't match: kid = %s, actualOps = %s, expectedOps = %s", kid, actualOps, expectedOps);
		return false;
	}

	/**
	 * <p>
	 * Verifies if a key has a given type.
	 * </p>
	 * 
	 * @param keyBundle    {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle
	 *                     KeyBundle}
	 * @param expectedKtys {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType
	 *                     JsonWebKeyType}
	 * @return {@code true} if the key has the given type, otherwise {@code false}.
	 */
	public static boolean doesTypeMatch(KeyBundle keyBundle, List<String> expectedKtys) {
		JsonWebKey key = keyBundle.getKey();
		String kid = key.getKid();
		String actualKty = key.getKty();
		if (expectedKtys == null || expectedKtys.contains(actualKty)) {
			Log.tracef("Key type matches: kid = %s, actualKty = %s, expectedKtys = %s", kid, actualKty, expectedKtys);
			return true;
		}

		Log.debugf("Key type doesn't match: kid = %s, actualKty = %s, expectedKtys = %s", kid, actualKty, expectedKtys);
		return false;
	}

	/**
	 * <p>
	 * Extracts query parameters from link to handle paged results from Azure Key Vault.
	 * </p>
	 * 
	 * @param url The URL to be process.
	 * @return The query parameters.
	 */
	public static Map<String, String> getQueryParameters(String url) {
		Map<String, String> queryParameters = new HashMap<>();
		try {
			URI uri = new URI(url);
			String query = uri.getQuery();
			String[] tokens = query.split("&");
			for (String token : tokens) {
				int i = token.indexOf("=");
				String key = null;
				String value = null;
				if (i >= 0) {
					key = URLDecoder.decode(token.substring(0, i), StandardCharsets.UTF_8);
					value = URLDecoder.decode(token.substring(i + 1), StandardCharsets.UTF_8);
				} else {
					key = URLDecoder.decode(token, StandardCharsets.UTF_8);
					value = null;
				}
				queryParameters.put(key, value);
			}
		} catch (URISyntaxException e) {
			Log.warnf(e, "Error parsing URL: %s", url);
			throw new RuntimeException(e); // NOSONAR
		}
		return queryParameters;
	}
}
