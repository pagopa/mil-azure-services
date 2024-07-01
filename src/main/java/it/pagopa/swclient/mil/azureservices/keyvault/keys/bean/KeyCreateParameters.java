/*
 * KeyCreateParameters.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * The key create parameters.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#keycreateparameters">Microsoft
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
public class KeyCreateParameters {
	/**
	 * <p>
	 * The type of key to create.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType JsonWebKeyType
	 */
	@JsonProperty("kty")
	private String kty;

	/**
	 * <p>
	 * The attributes of a key managed by the key vault service.
	 * </p>
	 */
	@JsonProperty("attributes")
	private KeyAttributes attributes;

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
	 * JSON web key operations.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation
	 *      JsonWebKeyOperation
	 */
	@JsonProperty("key_ops")
	private List<String> keyOps;

	/**
	 * <p>
	 * The key size in bits.
	 * </p>
	 */
	@JsonProperty("key_size")
	private Integer keySize;

	/**
	 * <p>
	 * The public exponent for a RSA key.
	 * </p>
	 */
	@JsonProperty("public_exponent")
	private Integer publicExponent;

	/**
	 * <p>
	 * The policy rules under which the key can be exported.
	 * </p>
	 */
	@JsonProperty("release_policy")
	private KeyReleasePolicy releasePolicy;

	/**
	 * <p>
	 * Application specific metadata in the form of key-value pairs.
	 * </p>
	 */
	@JsonProperty("tags")
	private Map<String, String> tags;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public KeyCreateParameters() {
		// Default constructor.
	}
}