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
 * The key create parameters.
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
	 * JSON keys.
	 */
	public static final String KTY_JK = "kty";
	public static final String ATTRIBUTES_JK = "attributes";
	public static final String CRV_JK = "crv";
	public static final String KEY_OPS_JK = "key_ops";
	public static final String KEY_SIZE_JK = "key_size";
	public static final String PUBLIC_EXPONENT_JK = "public_exponent";
	public static final String RELEASE_POLICY_JK = "release_policy";
	public static final String TAGS_JK = "tags";

	/**
	 * The type of key to create. See {@link JsonWebKeyType}
	 */
	@JsonProperty(KTY_JK)
	private String kty;

	/**
	 * The attributes of a key managed by the key vault service.
	 */
	@JsonProperty(ATTRIBUTES_JK)
	private KeyAttributes attributes;

	/**
	 * Elliptic curve name. See {@link JsonWebKeyCurveName}
	 */
	@JsonProperty(CRV_JK)
	private String crv;

	/**
	 * JSON web key operations. See {@link JsonWebKeyOperation}
	 */
	@JsonProperty(KEY_OPS_JK)
	private List<String> keyOps;

	/**
	 * The key size in bits.
	 */
	@JsonProperty(KEY_SIZE_JK)
	private Integer keySize;

	/**
	 * The public exponent for a RSA key.
	 */
	@JsonProperty(PUBLIC_EXPONENT_JK)
	private Integer publicExponent;

	/**
	 * The policy rules under which the key can be exported.
	 */
	@JsonProperty(RELEASE_POLICY_JK)
	private KeyReleasePolicy releasePolicy;

	/**
	 * Application specific metadata in the form of key-value pairs.
	 */
	@JsonProperty(TAGS_JK)
	private Map<String, String> tags;
}