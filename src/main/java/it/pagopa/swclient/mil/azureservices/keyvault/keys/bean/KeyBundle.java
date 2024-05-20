/*
 * KeyBundle.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

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
 * A KeyBundle consisting of a WebKey plus its attributes.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#keybundle">Microsoft
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
public class KeyBundle {
	/**
	 * JSON keys.
	 */
	public static final String ATTRIBUTES_JK = "attributes";
	public static final String KEY_JK = "key";
	public static final String MANAGED_JK = "managed";
	public static final String RELEASE_POLICY_JK = "release_policy";
	public static final String TAGS_JK = "tags";

	/**
	 * The key management attributes.
	 */
	@JsonProperty(ATTRIBUTES_JK)
	private KeyAttributes attributes;

	/**
	 * The Json web key.
	 */
	@JsonProperty(KEY_JK)
	private JsonWebKey key;

	/**
	 * True if the key's lifetime is managed by key vault. If this is a key backing a certificate, then
	 * managed will be true.
	 */
	@JsonProperty(MANAGED_JK)
	private Boolean managed;

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
