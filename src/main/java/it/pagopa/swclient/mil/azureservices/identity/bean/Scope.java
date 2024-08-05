/*
 * Scope.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.bean;

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
	public static final String VAULT = "https://vault.azure.net/.default";

	/**
	 * <p>
	 * Scope to get an access token to access to Storage Account APIs.
	 * </p>
	 */
	public static final String STORAGE = "https://storage.azure.com/.default";

	/**
	 * <p>
	 * This class contains constants only.
	 * </p>
	 */
	private Scope() {
		// This class contains constants only.
	}
}