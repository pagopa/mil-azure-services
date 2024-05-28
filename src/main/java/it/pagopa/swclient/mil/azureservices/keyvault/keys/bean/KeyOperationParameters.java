/*
 * KeyOperationParameters.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArrayDeserializer;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArraySerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * The key operations parameters.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/encrypt/encrypt?view=rest-keyvault-keys-7.4&tabs=HTTP#keyoperationsparameters">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@ToString
@JsonInclude(value = Include.NON_NULL)
public class KeyOperationParameters {
	/**
	 * JSON keys.
	 */
	public static final String AAD_JK = "aad";
	public static final String ALG_JK = "alg";
	public static final String IV_JK = "iv";
	public static final String TAG_JK = "tag";
	public static final String VALUE_JK = "value";

	/**
	 * Additional data to authenticate but not encrypt/decrypt when using authenticated crypto
	 * algorithms.
	 */
	@JsonProperty(AAD_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] aad;

	/**
	 * Algorithm identifier. See {@link JsonWebKeyEncryptionAlgorithm}
	 */
	@JsonProperty(ALG_JK)
	private String alg;

	/**
	 * Cryptographically random, non-repeating initialization vector for symmetric algorithms.
	 */
	@JsonProperty(IV_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] iv;

	/**
	 * The tag to authenticate when performing decryption with an authenticated algorithm.
	 */
	@JsonProperty(TAG_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] tag;

	/**
	 * Result.
	 */
	@JsonProperty(VALUE_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] value;
}
