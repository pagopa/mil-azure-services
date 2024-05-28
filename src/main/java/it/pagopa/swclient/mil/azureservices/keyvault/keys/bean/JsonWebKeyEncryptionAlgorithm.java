/*
 * JsonWebKeyEncryptionAlgorithm.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * Algorithm identifier.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/encrypt/encrypt?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeyencryptionalgorithm">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public class JsonWebKeyEncryptionAlgorithm {
	public static final String A128CBC = "A128CBC";
	public static final String A128CBCPAD = "A128CBCPAD";
	public static final String A128GCM = "A128GCM";
	public static final String A128KW = "A128KW";
	public static final String A192CBC = "A192CBC";
	public static final String A192CBCPAD = "A192CBCPAD";
	public static final String A192GCM = "A192GCM";
	public static final String A192KW = "A192KW";
	public static final String A256CBC = "A256CBC";
	public static final String A256CBCPAD = "A256CBCPAD";
	public static final String A256GCM = "A256GCM";
	public static final String A256KW = "A256KW";
	public static final String RSAOAEP = "RSA-OAEP";
	public static final String RSAOAEP256 = "RSA-OAEP-256";
	public static final String RSA1_5 = "RSA1_5";

	/**
	 * 
	 */
	private JsonWebKeyEncryptionAlgorithm() {
	}
}