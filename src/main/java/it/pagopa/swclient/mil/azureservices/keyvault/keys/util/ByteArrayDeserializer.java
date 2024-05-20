/*
 * ByteArrayDeserializer.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.util;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.quarkus.logging.Log;

/**
 * 
 * @author Antonio Tarricone
 */
public class ByteArrayDeserializer extends JsonDeserializer<byte[]> {
	/**
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(JsonParser,
	 *      DeserializationContext)
	 */
	@Override
	public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		Log.trace("deserialize");
		return Base64.getUrlDecoder().decode(p.getText());
	}
}
