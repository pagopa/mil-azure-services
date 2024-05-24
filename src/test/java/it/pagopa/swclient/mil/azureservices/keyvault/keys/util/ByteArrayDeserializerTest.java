/*
 * ByteArrayDeserializerTest.java
 *
 * 20 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class ByteArrayDeserializerTest {
	/**
	 * 
	 * @param testInfo
	 */
	@BeforeEach
	void init(TestInfo testInfo) {
		String frame = "*".repeat(testInfo.getDisplayName().length() + 11);
		System.out.println(frame);
		System.out.printf("* %s: START *%n", testInfo.getDisplayName());
		System.out.println(frame);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArrayDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)}.
	 * 
	 * @throws IOException
	 */
	@Test
	void testDeserializeJsonParserDeserializationContext() throws IOException {
		byte[] expected = "Hi?".getBytes();

		String json = "{\"bytes\":\"SGk_\"}";

		Sample sample = new ObjectMapper().readValue(json, Sample.class);

		byte[] actual = sample.getBytes();

		assertArrayEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArrayDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)}.
	 * 
	 * @throws IOException
	 * @throws JsonParseException
	 */
	@Test
	void testDeserializeJsonParserDeserializationContextWithNull() throws IOException {
		byte[] expected = null;

		String json = "{\"bytes\":null}";

		Sample sample = new ObjectMapper().readValue(json, Sample.class);

		byte[] actual = sample.getBytes();

		assertArrayEquals(expected, actual);
	}
}
