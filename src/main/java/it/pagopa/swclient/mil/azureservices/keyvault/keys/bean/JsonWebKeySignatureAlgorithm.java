/*
 * JsonWebKeySignatureAlgorithm.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * <p>
 * The signing/verification algorithm identifiers.
 * </p>
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
	 * <p>
	 * ECDSA using P-256 and SHA-256.
	 * </p>
	 */
	public static final String ES256 = "ES256";

	/**
	 * <p>
	 * ECDSA using P-256K and SHA-256.
	 * </p>
	 */
	public static final String ES256K = "ES256K";

	/**
	 * <p>
	 * ECDSA using P-384 and SHA-384.
	 * </p>
	 */
	public static final String ES384 = "ES384";

	/**
	 * <p>
	 * ECDSA using P-521 and SHA-512.
	 * </p>
	 */
	public static final String ES512 = "ES512";

	/**
	 * <p>
	 * RSASSA-PSS using SHA-256 and MGF1 with SHA-256.
	 * </p>
	 */
	public static final String PS256 = "PS256";

	/**
	 * <p>
	 * RSASSA-PSS using SHA-384 and MGF1 with SHA-384.
	 * </p>
	 */
	public static final String PS384 = "PS384";

	/**
	 * <p>
	 * RSASSA-PSS using SHA-512 and MGF1 with SHA-512.
	 * </p>
	 */
	public static final String PS512 = "PS512";

	/**
	 * <p>
	 * RSASSA-PKCS1-v1_5 using SHA-256.
	 * </p>
	 */
	public static final String RS256 = "RS256";

	/**
	 * <p>
	 * RSASSA-PKCS1-v1_5 using SHA-384.
	 * </p>
	 */
	public static final String RS384 = "RS384";

	/**
	 * <p>
	 * RSASSA-PKCS1-v1_5 using SHA-512.
	 * </p>
	 */
	public static final String RS512 = "RS512";

	/**
	 * <p>
	 * Reserved.
	 * </p>
	 */
	public static final String RSNULL = "RSNULL";

	/**
	 * <p>
	 * This class contains constants only.
	 * </p>
	 */
	private JsonWebKeySignatureAlgorithm() {
		// This class contains constants only.
	}
}