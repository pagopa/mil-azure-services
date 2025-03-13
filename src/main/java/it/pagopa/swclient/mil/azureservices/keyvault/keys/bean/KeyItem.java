/*
 * KeyItem.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * The key item containing key metadata.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-keys/get-keys?view=rest-keyvault-keys-7.4&tabs=HTTP#keyitem">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
@JsonInclude(value = Include.NON_NULL)
public class KeyItem {
	/**
	 * <p>
	 * The key management attributes.
	 * </p>
	 */
	@JsonProperty("attributes")
	private KeyAttributes attributes;

	/**
	 * <p>
	 * Key identifier.
	 * </p>
	 */
	@JsonProperty("kid")
	private String kid;

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
	public KeyItem() {
		// Default constructor.
	}
}