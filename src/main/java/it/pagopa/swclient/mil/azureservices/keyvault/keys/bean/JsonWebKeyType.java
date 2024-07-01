/*
 * JsonWebKeyType.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * <p>
 * JsonWebKey Key Types (kty; as defined in <a href=
 * "https://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-40">draft-ietf-jose-json-web-algorithms-40</a>.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeytype">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public class JsonWebKeyType {
	/**
	 * <p>
	 * Elliptic Curve.
	 * </p>
	 */
	public static final String EC = "EC";

	/**
	 * <p>
	 * Elliptic Curve with a private key which is stored in the HSM.
	 * </p>
	 */
	public static final String EC_HSM = "EC-HSM";

	/**
	 * <p>
	 * RSA (https://tools.ietf.org/html/rfc3447).
	 * </p>
	 */
	public static final String RSA = "RSA";

	/**
	 * <p>
	 * RSA with a private key which is stored in the HSM.
	 * </p>
	 */
	public static final String RSA_HSM = "RSA-HSM";

	/**
	 * <p>
	 * Octet sequence (used to represent symmetric keys).
	 * </p>
	 */
	public static final String OCT = "oct";

	/**
	 * <p>
	 * Octet sequence (used to represent symmetric keys) which is stored the HSM.
	 * </p>
	 */
	public static final String OCT_HSM = "oct-HSM";

	/**
	 * <p>
	 * This class contains constants only.
	 * </p>
	 */
	private JsonWebKeyType() {
		// This class contains constants only.
	}
}
