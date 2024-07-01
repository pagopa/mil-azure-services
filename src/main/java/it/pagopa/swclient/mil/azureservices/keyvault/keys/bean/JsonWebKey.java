/*
 * JsonWebKey.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

import java.util.List;

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
 * As of http://tools.ietf.org/html/draft-ietf-jose-json-web-key-18
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkey">Microsoft
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
public class JsonWebKey {
	/**
	 * <p>
	 * Elliptic curve name.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyCurveName
	 *      JsonWebKeyCurveName
	 */
	@JsonProperty("crv")
	private String crv;

	/**
	 * <p>
	 * RSA private exponent, or the D component of an EC private key.
	 * </p>
	 */
	@JsonProperty("d")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] d;

	/**
	 * <p>
	 * RSA private key parameter.
	 * </p>
	 */
	@JsonProperty("dp")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] dp;

	/**
	 * <p>
	 * RSA private key parameter.
	 * </p>
	 */
	@JsonProperty("dq")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] dq;

	/**
	 * <p>
	 * RSA public exponent.
	 * </p>
	 */
	@JsonProperty("e")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] e;

	/**
	 * <p>
	 * Symmetric key.
	 * </p>
	 */
	@JsonProperty("k")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] k;

	/**
	 * <p>
	 * Protected Key, used with 'Bring Your Own Key'.
	 * </p>
	 */
	@JsonProperty("key_hsm")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] keyHsm;

	/**
	 * <p>
	 * Supported key operations.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation
	 *      JsonWebKeyOperation
	 */
	@JsonProperty("key_ops")
	private List<String> keyOps;

	/**
	 * <p>
	 * Key identifier.
	 * </p>
	 */
	@JsonProperty("kid")
	private String kid;

	/**
	 * <p>
	 * JsonWebKey Key Type (kty), as defined in <a href=
	 * "https://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-40">https://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-40</a>
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType JsonWebKeyType
	 */
	@JsonProperty("kty")
	private String kty;

	/**
	 * <p>
	 * RSA modulus.
	 * </p>
	 */
	@JsonProperty("n")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] n;

	/**
	 * <p>
	 * RSA secret prime.
	 * </p>
	 */
	@JsonProperty("p")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] p;

	/**
	 * <p>
	 * RSA secret prime, with p &lt; q.
	 * </p>
	 */
	@JsonProperty("q")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] q;

	/**
	 * <p>
	 * RSA private key parameter.
	 * </p>
	 */
	@JsonProperty("qi")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] qi;

	/**
	 * <p>
	 * X component of an EC public key.
	 * </p>
	 */
	@JsonProperty("x")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] x;

	/**
	 * <p>
	 * Y component of an EC public key.
	 * </p>
	 */
	@JsonProperty("y")
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] y;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public JsonWebKey() {
		// Default constructor.
	}
}