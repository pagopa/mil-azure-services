/*
 * DeletedKeyBundle.java
 *
 * 12 lug 2024
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
 * A DeletedKeyBundle consisting of a WebKey plus its Attributes and deletion info
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/delete-key/delete-key?view=rest-keyvault-keys-7.4&tabs=HTTP#deletedkeybundle">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true, prefix = "del")
@ToString
@JsonInclude(value = Include.NON_NULL)
public class DeletedKeyBundle {
	/**
	 * <p>
	 * The key management attributes.
	 * </p>
	 */
	@JsonProperty("attributes")
	private KeyAttributes delAttributes;
	
	/**
	 * <p>
	 * The time when the key was deleted, in UTC.
	 * </p>
	 */
	@JsonProperty("deletedDate")
	private Long delDeletedDate;

	/**
	 * <p>
	 * The Json web key.
	 * </p>
	 */
	@JsonProperty("key")
	private JsonWebKey delKey;

	/**
	 * <p>
	 * True if the key's lifetime is managed by key vault. If this is a key backing a certificate, then
	 * managed will be true.
	 * </p>
	 */
	@JsonProperty("managed")
	private Boolean delManaged;
	
	/**
	 * <p>
	 * The url of the recovery object, used to identify and recover the deleted key.
	 * </p>
	 */
	@JsonProperty("recoveryId")
	private String delRecoveryId;

	/**
	 * <p>
	 * The policy rules under which the key can be exported.
	 * </p>
	 */
	@JsonProperty("release_policy")
	private KeyReleasePolicy delReleasePolicy;
	
	/**
	 * <p>
	 * The time when the key is scheduled to be purged, in UTC.
	 * </p>
	 */
	@JsonProperty("scheduledPurgeDate")
	private Long delScheduledPurgeDate;

	/**
	 * <p>
	 * Application specific metadata in the form of key-value pairs.
	 * </p>
	 */
	@JsonProperty("tags")
	private Map<String, String> delTags;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public DeletedKeyBundle() {
		// Default constructor.
	}
}
