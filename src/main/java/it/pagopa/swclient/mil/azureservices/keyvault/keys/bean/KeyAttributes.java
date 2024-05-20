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
 * The attributes of a key managed by the key vault service.
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
	 * JSON keys.
	 */
	public static final String CREATED_JK = "created";
	public static final String ENABLED_JK = "enabled";
	public static final String EXP_JK = "exp";
	public static final String EXPORTABLE_JK = "exportable";
	public static final String NBF_JK = "nbf";
	public static final String RECOVERABLE_DAYS_JK = "recoverableDays";
	public static final String RECOVERY_LEVEL_JK = "recoveryLevel";
	public static final String UPDATED_JK = "updated";

	/**
	 * Creation time in UTC (Unix epoch in seconds).
	 */
	@JsonProperty(CREATED_JK)
	private Long created;

	/**
	 * Determines whether the object is enabled.
	 */
	@JsonProperty(ENABLED_JK)
	private Boolean enabled;

	/**
	 * Expiry date in UTC (Unix epoch in seconds).
	 */
	@JsonProperty(EXP_JK)
	private Long exp;

	/**
	 * Indicates if the private key can be exported. Release policy must be provided when creating the
	 * first version of an exportable key.
	 */
	@JsonProperty(EXPORTABLE_JK)
	private Boolean exportable;

	/**
	 * Not before date in UTC (Unix epoch in seconds).
	 */
	@JsonProperty(NBF_JK)
	private Long nbf;

	/**
	 * softDelete data retention days. Value should be >=7 and <=90 when softDelete enabled, otherwise
	 * 0.
	 */
	@JsonProperty(RECOVERABLE_DAYS_JK)
	private Integer recoverableDays;

	/**
	 * <p>
	 * Reflects the deletion recovery level currently in effect for keys in the current vault.
	 * </p>
	 * <p>
	 * If it contains 'Purgeable' the key can be permanently deleted by a privileged user; otherwise,
	 * only the system can purge the key, at the end of the retention interval.
	 * </p>
	 */
	@JsonProperty(RECOVERY_LEVEL_JK)
	private DeletionRecoveryLevel recoveryLevel;

	/**
	 * Last updated time in UTC (Unix epoch in seconds).
	 */
	@JsonProperty(UPDATED_JK)
	private Long updated;
}