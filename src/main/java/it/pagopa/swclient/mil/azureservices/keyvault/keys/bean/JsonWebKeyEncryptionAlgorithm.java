/*
 * JsonWebKeyEncryptionAlgorithm.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * <p>
 * Algorithm identifiers.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/encrypt/encrypt?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeyencryptionalgorithm">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public class JsonWebKeyEncryptionAlgorithm {
	/**
	 * <p>
	 * A128CBC
	 * </p>
	 */
	public static final String A128CBC = "A128CBC";

	/**
	 * <p>
	 * A128CBCPAD
	 * </p>
	 */
	public static final String A128CBCPAD = "A128CBCPAD";

	/**
	 * <p>
	 * A128GCM
	 * </p>
	 */
	public static final String A128GCM = "A128GCM";

	/**
	 * <p>
	 * A128KW
	 * </p>
	 */
	public static final String A128KW = "A128KW";

	/**
	 * <p>
	 * A192CBC
	 * </p>
	 */
	public static final String A192CBC = "A192CBC";

	/**
	 * <p>
	 * A192CBCPAD
	 * </p>
	 */
	public static final String A192CBCPAD = "A192CBCPAD";

	/**
	 * <p>
	 * A192GCM
	 * </p>
	 */
	public static final String A192GCM = "A192GCM";

	/**
	 * <p>
	 * A192KW
	 * </p>
	 */
	public static final String A192KW = "A192KW";

	/**
	 * <p>
	 * A256CBC
	 * </p>
	 */
	public static final String A256CBC = "A256CBC";

	/**
	 * <p>
	 * A256CBCPAD
	 * </p>
	 */
	public static final String A256CBCPAD = "A256CBCPAD";

	/**
	 * <p>
	 * A256GCM
	 * </p>
	 */
	public static final String A256GCM = "A256GCM";

	/**
	 * <p>
	 * A256KW
	 * </p>
	 */
	public static final String A256KW = "A256KW";

	/**
	 * <p>
	 * RSA-OAEP
	 * </p>
	 */
	public static final String RSAOAEP = "RSA-OAEP";

	/**
	 * <p>
	 * RSA-OAEP-256
	 * </p>
	 */
	public static final String RSAOAEP256 = "RSA-OAEP-256";

	/**
	 * <p>
	 * RSA 1.5
	 * </p>
	 */
	public static final String RSA1_5 = "RSA1_5";

	/**
	 * <p>
	 * This class contains constants only.
	 * </p>
	 */
	private JsonWebKeyEncryptionAlgorithm() {
		// This class contains constants only.
	}
}