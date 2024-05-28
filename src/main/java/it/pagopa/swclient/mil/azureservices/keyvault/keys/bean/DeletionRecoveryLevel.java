/*
 * DeletionRecoveryLevel.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

/**
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#deletionrecoverylevel">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
public class DeletionRecoveryLevel {
	/**
	 * <p>
	 * Denotes a vault state in which deletion is recoverable without the possibility for immediate and
	 * permanent deletion.
	 * </p>
	 */
	public static final String CUSTOMIZED_RECOVERABLE = "CustomizedRecoverable";

	/**
	 * <p>
	 * Denotes a vault and subscription state in which deletion is recoverable, immediate and permanent
	 * deletion (i.e. purge) is not permitted, and in which the subscription itself cannot be
	 * permanently canceled when 7 <= SoftDeleteRetentionInDays < 90.
	 * </p>
	 */
	public static final String CUSTOMIZED_RECOVERABLE_PROTECTED_SUBSCRIPTION = "CustomizedRecoverable+ProtectedSubscription";

	/**
	 * <p>
	 * Denotes a vault state in which deletion is recoverable, and which also permits immediate and
	 * permanent deletion.
	 * </p>
	 */
	public static final String CUSTOMIZED_RECOVERABLE_PURGEABLE = "CustomizedRecoverable+Purgeable";

	/**
	 * <p>
	 * Denotes a vault state in which deletion is an irreversible operation, without the possibility for
	 * recovery.
	 * </p>
	 */
	public static final String PURGEABLE = "Purgeable";

	/**
	 * <p>
	 * Denotes a vault state in which deletion is recoverable without the possibility for immediate and
	 * permanent deletion.
	 * </p>
	 */
	public static final String RECOVERABLE = "Recoverable";

	/**
	 * <p>
	 * Denotes a vault and subscription state in which deletion is recoverable within retention interval
	 * (90 days), immediate and permanent deletion (i.e. purge) is not permitted, and in which the
	 * subscription itself cannot be permanently canceled.
	 * </p>
	 */
	public static final String RECOVERABLE_PROTECTED_SUBSCRIPTION = "Recoverable+ProtectedSubscription";

	/**
	 * <p>
	 * Denotes a vault state in which deletion is recoverable, and which also permits immediate and
	 * permanent deletion.
	 * </p>
	 */
	public static final String RECOVERABLE_PURGEABLE = "Recoverable+Purgeable";

	/**
	 * 
	 */
	private DeletionRecoveryLevel() {
	}
}