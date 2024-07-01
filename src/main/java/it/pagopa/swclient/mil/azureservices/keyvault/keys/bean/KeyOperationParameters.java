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
 * <p>
 * The key operations parameters.
 * </p>
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
	 * <p>
	 * Additional data to authenticate but not encrypt/decrypt when using authenticated crypto
	 * algorithms.
	 * </p>
	 */
	@JsonProperty("aad")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] aad;

	/**
	 * <p>
	 * Algorithm identifier.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyEncryptionAlgorithm
	 *      JsonWebKeyEncryptionAlgorithm
	 */
	@JsonProperty("alg")
	private String alg;

	/**
	 * <p>
	 * Cryptographically random, non-repeating initialization vector for symmetric algorithms.
	 * </p>
	 */
	@JsonProperty("iv")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] iv;

	/**
	 * <p>
	 * The tag to authenticate when performing decryption with an authenticated algorithm.
	 * </p>
	 */
	@JsonProperty("tag")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] tag;

	/**
	 * <p>
	 * Result.
	 * </p>
	 */
	@JsonProperty("value")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] value;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public KeyOperationParameters() {
		// Default constructor.
	}
}
