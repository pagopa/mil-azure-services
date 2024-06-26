/*
 * KeyListResult.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

import java.util.List;

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
 * The key list result.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-keys/get-keys?view=rest-keyvault-keys-7.4&tabs=HTTP#keylistresult">Microsoft
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
public class KeyListResult {
	/**
	 * <p>
	 * The URL to get the next set of keys.
	 * </p>
	 */
	@JsonProperty("nextLink")
	private String nextLink;

	/**
	 * <p>
	 * A response message containing a list of keys in the key vault along with a link to the next page
	 * of keys.
	 * </p>
	 */
	@JsonProperty("value")
	private List<KeyItem> value;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public KeyListResult() {
		// Default constructor.
	}
}