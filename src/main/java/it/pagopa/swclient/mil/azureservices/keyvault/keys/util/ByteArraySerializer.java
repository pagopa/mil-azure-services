/*
 * ByteArraySerializer.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.util;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import io.quarkus.logging.Log;

/**
 * <p>
 * Serialize array of bytes in Base64 URL-safe string.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public class ByteArraySerializer extends JsonSerializer<byte[]> {
	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public ByteArraySerializer() {
		super();
	}

	/**
	 * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(Object, JsonGenerator,
	 *      SerializerProvider) JsonSerializer#serialize(Object, JsonGenerator, SerializerProvider)
	 */
	@Override
	public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		Log.trace("serialize");
		gen.writeString(Base64.getUrlEncoder().withoutPadding().encodeToString(value));
	}
}