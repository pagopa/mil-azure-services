/*
 * Sample.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.storageblob.service;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	public static final String FIELD1_JK = "field1";
	public static final String FIELD2_JK = "field2";

	/**
	 * 
	 */
	@JsonProperty(FIELD1_JK)
	private String field1;

	/**
	 * 
	 */
	@JsonProperty(FIELD2_JK)
	private String field2;
}