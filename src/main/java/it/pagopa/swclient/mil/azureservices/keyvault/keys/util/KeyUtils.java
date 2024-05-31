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
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;

/**
 * 
 * @author Antonio Tarricone
 */
public class KeyUtils {
	/**
	 * 
	 */
	private KeyUtils() {
	}

	/**
	 * 
	 * @param keyItem
	 * @return
	 */
	public static String getKeyName(KeyItem keyItem) {
		return URI.create(keyItem.getKid()).getPath().split("/")[2];
	}

	/**
	 * 
	 * @param keyItem
	 * @return
	 */
	public static String[] getKeyNameVersion(KeyItem keyItem) {
		String[] segments = URI.create(keyItem.getKid()).getPath().split("/");
		return new String[] {
			segments[2], segments[3]
		};
	}

	/**
	 * 
	 * @param keyName
	 * @param prefix
	 * @return
	 */
	public static boolean doesPrefixMatch(String keyName, String prefix) {
		if (prefix == null || keyName.startsWith(prefix)) {
			Log.tracef("Prefix matches or is null: keyName = %s, prefix = %s", keyName, prefix);
			return true;
		}

		Log.debugf("Prefix doesn't match: keyName = %s, prefix = %s", keyName, prefix);
		return false;
	}

	/**
	 * 
	 * @param kid
	 * @param keyAttributes
	 * @return
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
	 * 
	 * @param keyItem
	 * @return
	 */
	public static boolean isValid(KeyItem keyItem) {
		return isValid(keyItem.getKid(), keyItem.getAttributes());
	}

	/**
	 * 
	 * @param keyBundle
	 * @param expectedOps {@link JsonWebKeyOperation}
	 * @return
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
	 * 
	 * @param keyBundle
	 * @param expectedKtys {@link JsonWebKeyType}
	 * @return
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
	 * 
	 * @param url
	 * @return
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
			throw new RuntimeException(e); //NOSONAR
		}
		return queryParameters;
	}
}
