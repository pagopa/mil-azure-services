/*
 * JsonWebKeyEncryptionAlgorithm.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Algorithm identifier.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/encrypt/encrypt?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeyencryptionalgorithm">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public enum JsonWebKeyEncryptionAlgorithm {
	/*
	 * 
	 */
	@JsonProperty("A128CBC")
	A128CBC("A128CBC"),

	@JsonProperty("A128CBCPAD")
	A128CBCPAD("A128CBCPAD"),

	@JsonProperty("A128GCM")
	A128GCM("A128GCM"),

	@JsonProperty("A128KW")
	A128KW("A128KW"),

	@JsonProperty("A192CBC")
	A192CBC("A192CBC"),

	@JsonProperty("A192CBCPAD")
	A192CBCPAD("A192CBCPAD"),

	@JsonProperty("A192GCM")
	A192GCM("A192GCM"),

	@JsonProperty("A192KW")
	A192KW("A192KW"),

	@JsonProperty("A256CBC")
	A256CBC("A256CBC"),

	@JsonProperty("A256CBCPAD")
	A256CBCPAD("A256CBCPAD"),

	@JsonProperty("A256GCM")
	A256GCM("A256GCM"),

	@JsonProperty("A256KW")
	A256KW("A256KW"),

	@JsonProperty("RSA-OAEP")
	RSAOAEP("RSA-OAEP"),

	@JsonProperty("RSA-OAEP-256")
	RSAOAEP256("RSA-OAEP-256"),

	@JsonProperty("RSA1_5")
	RSA1_5("RSA1_5");

	/*
	 * 
	 */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	private JsonWebKeyEncryptionAlgorithm(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
}