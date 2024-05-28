/*
 * JsonWebKeyOperation.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeyoperation">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public class JsonWebKeyOperation {
	public static final String DECRYPT = "decrypt";
	public static final String ENCRYPT = "encrypt";
	public static final String EXPORT = "export";
	public static final String IMPORT = "import";
	public static final String SIGN = "sign";
	public static final String UNWRAP_KEY = "unwrapKey";
	public static final String VERIFY = "verify";
	public static final String WRAP_KEY = "wrapKey";

	/**
	 * 
	 */
	private JsonWebKeyOperation() {
	}
}
