/*
 * KeyAttributes.java
 *
 * 10 apr 2024
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
 * The attributes of a key managed by the key vault service.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#keyattributes">Microsoft
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
public class KeyAttributes {
	/**
	 * <p>
	 * Creation time in UTC (Unix epoch in seconds).
	 * </p>
	 */
	@JsonProperty("created")
	private Long created;

	/**
	 * <p>
	 * Determines whether the object is enabled.
	 * </p>
	 */
	@JsonProperty("enabled")
	private Boolean enabled;

	/**
	 * <p>
	 * Expiry date in UTC (Unix epoch in seconds).
	 * </p>
	 */
	@JsonProperty("exp")
	private Long exp;

	/**
	 * <p>
	 * Indicates if the private key can be exported. Release policy must be provided when creating the
	 * first version of an exportable key.
	 * </p>
	 */
	@JsonProperty("exportable")
	private Boolean exportable;

	/**
	 * <p>
	 * Not before date in UTC (Unix epoch in seconds).
	 * </p>
	 */
	@JsonProperty("nbf")
	private Long nbf;

	/**
	 * <p>
	 * softDelete data retention days. Value should be &ge;7 and &le; 90 when softDelete enabled,
	 * otherwise 0.
	 * </p>
	 */
	@JsonProperty("recoverableDays")
	private Integer recoverableDays;

	/**
	 * <p>
	 * Reflects the deletion recovery level currently in effect for keys in the current vault.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletionRecoveryLevel
	 *      DeletionRecoveryLevel
	 */
	@JsonProperty("recoveryLevel")
	private String recoveryLevel;

	/**
	 * <p>
	 * Last updated time in UTC (Unix epoch in seconds).
	 * </p>
	 */
	@JsonProperty("updated")
	private Long updated;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public KeyAttributes() {
		// Default constructor.
	}
}