/*
 * AzureWorkloadIdentityReactiveClientTest.java
 *
 * 5 ago 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.MockedStatic;

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.spi.DeploymentException;

/**
 * 
 * @author antonio.tarricone
 */
@QuarkusTest
class AzureWorkloadIdentityReactiveClientTest {
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
	 * {@link it.pagopa.swclient.mil.azureservices.identity.client.AzureWorkloadIdentityReactiveClient#getClientAssertion(java.lang.String)}.
	 */
	@Test
	void given_tokenFile_when_invokeGetClientAssertion_then_returnFileContent() {
		AzureWorkloadIdentityReactiveClient client = QuarkusRestClientBuilder.newBuilder()
			.baseUri(URI.create("https://login.microsoftonline.com/da795842-fa15-4fd4-b556-f371ac9bafed"))
			.build(AzureWorkloadIdentityReactiveClient.class);

		assertEquals(
			"This is a test!",
			client.getClientAssertion("client_assertion"));
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.client.AzureWorkloadIdentityReactiveClient#getClientAssertion(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	void given_ioExceptionReadingTokenFile_when_invokeGetClientAssertion_then_throwException() throws IOException {
		try (MockedStatic<Files> files = mockStatic(Files.class)) {

			files.when(() -> Files.readAllBytes(Paths.get(
				ConfigProvider.getConfig()
					.getValue(
						"AZURE_FEDERATED_TOKEN_FILE",
						String.class))))
				.thenThrow(IOException.class);

			AzureWorkloadIdentityReactiveClient client = QuarkusRestClientBuilder.newBuilder()
				.baseUri(URI.create("https://login.microsoftonline.com/da795842-fa15-4fd4-b556-f371ac9bafed"))
				.build(AzureWorkloadIdentityReactiveClient.class);

			assertThrows(DeploymentException.class,
				() -> client.getClientAssertion("client_assertion"));
		}
	}
}
