/*
 * AccessToken.java
 *
 * 17 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * DTO of the response from Microsoft Entra ID to the request for an access token.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class AccessToken {
	/**
	 * <p>
	 * Type of the returned access token. It should be always {@code Bearer}.
	 * </p>
	 */
	@JsonProperty("token_type")
	private String type;

	/**
	 * <p>
	 * Unix epoch in seconds of expiration of the returned access token.
	 * </p>
	 */
	@JsonProperty(value = "expires_on", required = true)
	private long expiresOn;

	/**
	 * <p>
	 * Client ID of the applicant.
	 * </p>
	 */
	@JsonProperty("client_id")
	private String clientId;

	/**
	 * <p>
	 * Resource which can be accessed using the returned access token.
	 * </p>
	 */
	@JsonProperty("resource")
	private String resource;

	/**
	 * <p>
	 * Value of the returned access token.
	 * </p>
	 */
	@JsonProperty(value = "access_token", required = true)
	private String value;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public AccessToken() {
		// Default constructor.
	}
}