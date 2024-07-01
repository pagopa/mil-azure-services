/*
 * JsonWebKeyOperation.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * <p>
 * Key operations.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeyoperation">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public class JsonWebKeyOperation {
	/**
	 * <p>
	 * decrypt
	 * </p>
	 */
	public static final String DECRYPT = "decrypt";

	/**
	 * <p>
	 * encrypt
	 * </p>
	 */
	public static final String ENCRYPT = "encrypt";

	/**
	 * <p>
	 * export
	 * </p>
	 */
	public static final String EXPORT = "export";

	/**
	 * <p>
	 * import
	 * </p>
	 */
	public static final String IMPORT = "import";

	/**
	 * <p>
	 * sign
	 * </p>
	 */
	public static final String SIGN = "sign";

	/**
	 * <p>
	 * unwrap key
	 * </p>
	 */
	public static final String UNWRAP_KEY = "unwrapKey";

	/**
	 * <p>
	 * verify
	 * </p>
	 */
	public static final String VERIFY = "verify";

	/**
	 * <p>
	 * wrap key
	 * </p>
	 */
	public static final String WRAP_KEY = "wrapKey";

	/**
	 * <p>
	 * This class contains constants only.
	 * </p>
	 */
	private JsonWebKeyOperation() {
		// This class contains constants only.
	}
}
