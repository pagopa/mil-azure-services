/*
 * DeletionRecoveryLevel.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * Reflects the deletion recovery level currently in effect for keys in the current vault.
 * </p>
 * <p>
 * If it contains 'Purgeable' the key can be permanently deleted by a privileged user; otherwise,
 * only the system can purge the key, at the end of the retention interval.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#deletionrecoverylevel">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public enum DeletionRecoveryLevel {
	/**
	 * <p>
	 * Denotes a vault state in which deletion is recoverable without the possibility for immediate and
	 * permanent deletion (i.e. purge when 7 <= SoftDeleteRetentionInDays < 90).
	 * </p>
	 * <p>
	 * This level guarantees the recoverability of the deleted entity during the retention interval and
	 * while the subscription is still available.
	 * </p>
	 */
	@JsonProperty("CustomizedRecoverable")
	CUSTOMIZED_RECOVERABLE("CustomizedRecoverable"),

	/**
	 * <p>
	 * Denotes a vault and subscription state in which deletion is recoverable, immediate and permanent
	 * deletion (i.e. purge) is not permitted, and in which the subscription itself cannot be
	 * permanently canceled when 7 <= SoftDeleteRetentionInDays < 90.
	 * </p>
	 * <p>
	 * This level guarantees the recoverability of the deleted entity during the retention interval, and
	 * also reflects the fact that the subscription itself cannot be cancelled.
	 * </p>
	 */
	@JsonProperty("CustomizedRecoverable+ProtectedSubscription")
	CUSTOMIZED_RECOVERABLE_PROTECTED_SUBSCRIPTION("CustomizedRecoverable+ProtectedSubscription"),

	/**
	 * <p>
	 * Denotes a vault state in which deletion is recoverable, and which also permits immediate and
	 * permanent deletion (i.e. purge when 7 <= SoftDeleteRetentionInDays < 90).
	 * </p>
	 * <p>
	 * This level guarantees the recoverability of the deleted entity during the retention interval,
	 * unless a Purge operation is requested, or the subscription is cancelled.
	 * </p>
	 */
	@JsonProperty("CustomizedRecoverable+Purgeable")
	CUSTOMIZED_RECOVERABLE_PURGEABLE("CustomizedRecoverable+Purgeable"),

	/**
	 * <p>
	 * Denotes a vault state in which deletion is an irreversible operation, without the possibility for
	 * recovery. This level corresponds to no protection being available against a Delete operation; the
	 * data is irretrievably lost upon accepting a Delete operation at the entity level or higher
	 * (vault, resource group, subscription etc.)
	 * </p>
	 */
	@JsonProperty("Purgeable")
	PURGEABLE("Purgeable"),

	/**
	 * <p>
	 * Denotes a vault state in which deletion is recoverable without the possibility for immediate and
	 * permanent deletion (i.e. purge).
	 * </p>
	 * <p>
	 * This level guarantees the recoverability of the deleted entity during the retention interval(90
	 * days) and while the subscription is still available. System will permanently delete it after 90
	 * days, if not recovered.
	 * </p>
	 */
	@JsonProperty("Recoverable")
	RECOVERABLE("Recoverable"),

	/**
	 * <p>
	 * Denotes a vault and subscription state in which deletion is recoverable within retention interval
	 * (90 days), immediate and permanent deletion (i.e. purge) is not permitted, and in which the
	 * subscription itself cannot be permanently canceled.
	 * </p>
	 * <p>
	 * System will permanently delete it after 90 days, if not recovered.
	 * </p>
	 */
	@JsonProperty("Recoverable+ProtectedSubscription")
	RECOVERABLE_PROTECTED_SUBSCRIPTION("Recoverable+ProtectedSubscription"),

	/**
	 * <p>
	 * Denotes a vault state in which deletion is recoverable, and which also permits immediate and
	 * permanent deletion (i.e. purge).
	 * </p>
	 * <p>
	 * This level guarantees the recoverability of the deleted entity during the retention interval (90
	 * days), unless a Purge operation is requested, or the subscription is cancelled. System will
	 * permanently delete it after 90 days, if not recovered.
	 * </p>
	 */
	@JsonProperty("Recoverable+Purgeable")
	RECOVERABLE_PURGEABLE("Recoverable+Purgeable");

	/*
	 * 
	 */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	private DeletionRecoveryLevel(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
}