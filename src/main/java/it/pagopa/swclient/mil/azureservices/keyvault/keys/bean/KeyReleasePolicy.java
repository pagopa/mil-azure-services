/*
 * KeyReleasePolicy.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

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
 * The policy rules under which the key can be exported.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#keyreleasepolicy">Microsoft
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
public class KeyReleasePolicy {
	/**
	 * <p>
	 * Content type and version of key release policy. Default value: application/json; charset=utf-8
	 * </p>
	 */
	@JsonProperty("contentType")
	private String contentType;

	/**
	 * <p>
	 * Blob encoding the policy rules under which the key can be released. Blob must be base64 URL
	 * encoded.
	 * </p>
	 */
	@JsonProperty("data")
	private String data;

	/**
	 * <p>
	 * Defines the mutability state of the policy. Once marked immutable, this flag cannot be reset and
	 * the policy cannot be changed under any circumstances.
	 * </p>
	 */
	@JsonProperty("immutable")
	private Boolean immutable;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public KeyReleasePolicy() {
		// Default constructor.
	}
}