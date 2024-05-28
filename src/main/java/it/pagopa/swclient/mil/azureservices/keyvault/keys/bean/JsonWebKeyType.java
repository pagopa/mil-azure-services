/*
 * JsonWebKeyType.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * JsonWebKey Key Type (kty; as defined in <a href=
 * "https://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-40">draft-ietf-jose-json-web-algorithms-40</a>.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeytype">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public class JsonWebKeyType {
	/**
	 * Elliptic Curve.
	 */
	public static final String EC = "EC";

	/**
	 * Elliptic Curve with a private key which is stored in the HSM.
	 */
	public static final String EC_HSM = "EC-HSM";

	/**
	 * RSA =https://tools.ietf.org/html/rfc3447).
	 */
	public static final String RSA = "RSA";

	/**
	 * RSA with a private key which is stored in the HSM.
	 */
	public static final String RSA_HSM = "RSA-HSM";

	/**
	 * Octet sequence =used to represent symmetric keys).
	 */
	public static final String OCT = "oct";

	/**
	 * Octet sequence =used to represent symmetric keys) which is stored the HSM.
	 */
	public static final String OCT_HSM = "oct-HSM";

	/**
	 * 
	 */
	private JsonWebKeyType() {
	}
}
