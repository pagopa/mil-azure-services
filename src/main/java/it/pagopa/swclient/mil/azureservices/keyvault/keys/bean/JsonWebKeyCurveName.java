/*
 * JsonWebKeyCurveName.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * <p>
 * Elliptic curve names.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeycurvename">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public class JsonWebKeyCurveName {
	/**
	 * <p>
	 * The NIST P-256 elliptic curve, AKA SECG curve SECP256R1.
	 * </p>
	 */
	public static final String P256 = "P-256";

	/**
	 * <p>
	 * The SECG SECP256K1 elliptic curve.
	 * </p>
	 */
	public static final String P256K = "P-256K";

	/**
	 * <p>
	 * The NIST P-384 elliptic curve, AKA SECG curve SECP384R1.
	 * </p>
	 */
	public static final String P384 = "P-384";

	/**
	 * <p>
	 * The NIST P-521 elliptic curve, AKA SECG curve SECP521R1.
	 * </p>
	 */
	public static final String P521 = "P-521";

	/**
	 * <p>
	 * This class contains constants only.
	 * </p>
	 */
	private JsonWebKeyCurveName() {
		// This class contains constants only.
	}
}
