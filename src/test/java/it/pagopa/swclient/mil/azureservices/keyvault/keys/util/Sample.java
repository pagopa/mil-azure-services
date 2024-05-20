/*
 * Sample.java
 *
 * 20 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@Getter
@Setter
@Accessors(chain = true)
public class Sample {
	/**
	 * JSON keys.
	 */
	public static final String BYTES_JK = "bytes";

	/**
	 * 
	 */
	@JsonProperty(BYTES_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] bytes;
}