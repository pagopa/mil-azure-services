/*
 * KeyUtils.java
 *
 * 21 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.service;

import java.net.URI;
import java.time.Instant;
import java.util.List;
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
		Long created=keyAttributes.getCreated();
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
	 * @param expectedOps
	 * @return
	 */
	public static boolean doOpsMatch(KeyBundle keyBundle, List<JsonWebKeyOperation> expectedOps) {
		JsonWebKey key = keyBundle.getKey();
		String kid = key.getKid();
		List<JsonWebKeyOperation> actualOps = key.getKeyOps();
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
	 * @param expectedKtys
	 * @return
	 */
	public static boolean doesTypeMatch(KeyBundle keyBundle, List<JsonWebKeyType> expectedKtys) {
		JsonWebKey key = keyBundle.getKey();
		String kid = key.getKid();
		JsonWebKeyType actualKty = key.getKty();
		if (expectedKtys == null || expectedKtys.contains(actualKty)) {
			Log.tracef("Key type matches: kid = %s, actualKty = %s, expectedKtys = %s", kid, actualKty, expectedKtys);
			return true;
		}

		Log.debugf("Key type doesn't match: kid = %s, actualKty = %s, expectedKtys = %s", kid, actualKty, expectedKtys);
		return false;
	}
}
