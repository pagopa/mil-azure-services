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
 * <p>
 * A KeyBundle consisting of a WebKey plus its attributes.
 * </p>
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
	 * <p>
	 * The key management attributes.
	 * </p>
	 */
	@JsonProperty("attributes")
	private KeyAttributes attributes;

	/**
	 * <p>
	 * The Json web key.
	 * </p>
	 */
	@JsonProperty("key")
	private JsonWebKey key;

	/**
	 * <p>
	 * True if the key's lifetime is managed by key vault. If this is a key backing a certificate, then
	 * managed will be true.
	 * </p>
	 */
	@JsonProperty("managed")
	private Boolean managed;

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
	public KeyBundle() {
		// Default constructor.
	}
}
