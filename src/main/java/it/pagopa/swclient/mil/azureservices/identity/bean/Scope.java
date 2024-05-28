/*
 * Scope.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.bean;

/**
 * Scopes of access tokens.
 * 
 * @author Antonio Tarricone
 */
public class Scope {
	/**
	 * Scope to get an access token to access to Key Vault APIs.
	 */
	public static final String VAULT = "https://vault.azure.net";

	/**
	 * Scope to get an access token to access to Storage Account APIs.
	 */
	public static final String STORAGE = "https://storage.azure.com";

	/**
	 * 
	 */
	private Scope() {
	}
}