/*
 * JsonWebKeySignatureAlgorithm.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * The signing/verification algorithm identifier.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/sign/sign?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeysignaturealgorithm">Microsoft
 *      Azure Documentation</a>
 * 
 * @see <a href="https://tools.ietf.org/html/rfc7518">RFC 7518</a>
 * 
 * @author Antonio Tarricone
 */
public class JsonWebKeySignatureAlgorithm {
	/**
	 * ECDSA using P-256 and SHA-256.
	 */
	public static final String ES256 = "ES256";

	/**
	 * ECDSA using P-256K and SHA-256.
	 */
	public static final String ES256K = "ES256K";

	/**
	 * ECDSA using P-384 and SHA-384.
	 */
	public static final String ES384 = "ES384";

	/**
	 * ECDSA using P-521 and SHA-512.
	 */
	public static final String ES512 = "ES512";

	/**
	 * RSASSA-PSS using SHA-256 and MGF1 with SHA-256.
	 */
	public static final String PS256 = "PS256";

	/**
	 * RSASSA-PSS using SHA-384 and MGF1 with SHA-384.
	 */
	public static final String PS384 = "PS384";

	/**
	 * RSASSA-PSS using SHA-512 and MGF1 with SHA-512.
	 */
	public static final String PS512 = "PS512";

	/**
	 * RSASSA-PKCS1-v1_5 using SHA-256.
	 */
	public static final String RS256 = "RS256";

	/**
	 * RSASSA-PKCS1-v1_5 using SHA-384.
	 */
	public static final String RS384 = "RS384";

	/**
	 * RSASSA-PKCS1-v1_5 using SHA-512.
	 */
	public static final String RS512 = "RS512";

	/**
	 * Reserved.
	 */
	public static final String RSNULL = "RSNULL";

	/**
	 * 
	 */
	private JsonWebKeySignatureAlgorithm() {
	}
}