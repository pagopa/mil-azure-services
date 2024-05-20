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
	 * JSON keys.
	 */
	public static final String TYPE_JK = "token_type";
	public static final String EXPIRES_ON_JK = "expires_on";
	public static final String CLIENT_ID_JK = "client_id";
	public static final String RESOURCE_JK = "resource";
	public static final String ACCESS_TOKEN_JK = "access_token";

	/*
	 *
	 */
	@JsonProperty(TYPE_JK)
	private String type;

	/*
	 *
	 */
	@JsonProperty(value = EXPIRES_ON_JK, required = true)
	private long expiresOn;

	/*
	 *
	 */
	@JsonProperty(CLIENT_ID_JK)
	private String clientId;

	/*
	 *
	 */
	@JsonProperty(RESOURCE_JK)
	private String resource;

	/*
	 *
	 */
	@JsonProperty(value = ACCESS_TOKEN_JK, required = true)
	private String value;
}