/*
 * KeyVerifyParameters.java
 *
 * 11 apr 2024
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
 * The key verify parameters.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/verify/verify?view=rest-keyvault-keys-7.4&tabs=HTTP#keyverifyparameters">Microsoft
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
public class KeyVerifyParameters {
	/**
	 * JSON keys.
	 */
	public static final String ALG_JK = "alg";
	public static final String DIGEST_JK = "digest";
	public static final String VALUE_JK = "value";

	/**
	 * <p>
	 * The signing/verification algorithm.
	 * </p>
	 * <p>
	 * For more information on possible algorithm types, see JsonWebKeySignatureAlgorithm.
	 * </p>
	 */
	@JsonProperty(ALG_JK)
	private JsonWebKeySignatureAlgorithm alg;

	/**
	 * The digest used for signing.
	 */
	@JsonProperty(DIGEST_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] digest;

	/**
	 * The signature to be verified.
	 */
	@JsonProperty(VALUE_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] value;
}
