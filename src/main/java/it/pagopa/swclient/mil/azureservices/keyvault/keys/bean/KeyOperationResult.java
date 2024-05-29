/*
 * KeyOperationResult.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArrayDeserializer;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArraySerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * The key operation result.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/sign/sign?view=rest-keyvault-keys-7.4&tabs=HTTP#keyoperationresult">Microsoft
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
public class KeyOperationResult {
	/**
	 * JSON keys.
	 */
	public static final String AAD_JK = "aad";
	public static final String IV_JK = "iv";
	public static final String KID_JK = "kid";
	public static final String TAG_JK = "tag";
	public static final String VALUE_JK = "value";

	/**
	 * Additional authenticated data.
	 */
	@JsonProperty(AAD_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] aad;

	/**
	 * Initialization vector.
	 */
	@JsonProperty(IV_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] iv;

	/**
	 * Key identifier.
	 */
	@JsonProperty(KID_JK)
	private String kid;

	/**
	 * Authentication tag.
	 */
	@JsonProperty(TAG_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] tag;

	/**
	 * Result.
	 */
	@JsonProperty(VALUE_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] value;
}