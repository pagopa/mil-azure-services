/*
 * Scope.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.bean;

import java.util.Map;

/**
 * <p>
 * Scopes of access tokens.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public class Scope {
	/**
	 * <p>
	 * Scope to get an access token to access to Key Vault APIs.
	 * </p>
	 */
	public static final String VAULT = "https://vault.azure.net";

	/**
	 * <p>
	 * Scope to get an access token to access to Storage Account APIs.
	 * </p>
	 */
	public static final String STORAGE = "https://storage.azure.com";

	/**
	 * <p>
	 * Scope to get an access token to access to Key Vault APIs by means of Workload Identity.
	 * </p>
	 */
	public static final String VAULT_WORKLOAD_IDENTITY = "https://vault.azure.net/.default";

	/**
	 * <p>
	 * Scope to get an access token to access to Storage Account APIs by means of Workload Identity.
	 * </p>
	 */
	public static final String STORAGE_WORKLOAD_IDENTITY = "https://storage.azure.com/.default";

	/**
	 * <p>
	 * Mapping between scope values required by System Managed Identity and value required by Workload
	 * Identity.
	 * </p>
	 */
	private static final Map<String, String> FOR_WORKLOAD_IDENTITY = Map.of(
		VAULT, VAULT_WORKLOAD_IDENTITY,
		STORAGE, STORAGE_WORKLOAD_IDENTITY);

	/**
	 * <p>
	 * Maps the scope value required by System Managed Identity to value required by Workload Identity.
	 * </p>
	 * 
	 * @param scope Scope to be mapped to value required by Workload Identity.
	 * @return Value required by Workload Identity.
	 */
	public static String getForWorkloadIdentity(String scope) {
		return FOR_WORKLOAD_IDENTITY.getOrDefault(scope, scope);
	}

	/**
	 * <p>
	 * This class contains static stuff only.
	 * </p>
	 */
	private Scope() {
		// This class contains static stuff only.
	}
}