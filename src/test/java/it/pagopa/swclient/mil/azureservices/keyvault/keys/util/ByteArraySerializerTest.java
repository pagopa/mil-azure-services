/*
 * ByteArraySerializerTest.java
 *
 * 20 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class ByteArraySerializerTest {
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
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArraySerializer#serialize(byte[], com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	void testSerializeByteArrayJsonGeneratorSerializerProvider() throws JsonProcessingException {
		byte[] bytes = "Hi?".getBytes();

		Sample sample = new Sample()
			.setBytes(bytes);

		String actual = new ObjectMapper().writeValueAsString(sample);
		String expected = "{\"bytes\":\"SGk_\"}";

		assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArraySerializer#serialize(byte[], com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	void testSerializeByteArrayJsonGeneratorSerializerProviderWithNull() throws JsonProcessingException {
		byte[] bytes = null;

		Sample sample = new Sample()
			.setBytes(bytes);

		String actual = new ObjectMapper().writeValueAsString(sample);
		String expected = "{\"bytes\":null}";

		assertEquals(expected, actual);
	}

}
