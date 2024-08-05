/*
 * AzureIdentityReactiveClientFactoryTest.java
 *
 * 5 ago 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.spi.DeploymentException;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class AzureIdentityReactiveClientFactoryTest {
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
	 * {@link it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClientFactory#get()}.
	 */
	@Test
	void given_systemManagedIdEnvironment_when_invokeGet_then_returnSuitableClient() {
		AzureIdentityReactiveClientFactory factory = new AzureIdentityReactiveClientFactory(
			Optional.of("https://login.microsoftonline.com/"),
			Optional.of("45ed57a0-ec26-41c9-8333-29daf37697d3"),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty());
		
		assertTrue(factory.get() instanceof AzureSystemManagedIdentityReactiveClient);
	}
	
	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClientFactory#get()}.
	 */
	@Test
	void given_partialsystemManagedIdEnvironment_when_invokeGet_then_throwException() {
		AzureIdentityReactiveClientFactory factory = new AzureIdentityReactiveClientFactory(
			Optional.of("https://login.microsoftonline.com/"),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty());
		
		assertThrows(
			DeploymentException.class,
			() -> factory.get());
	}
	
	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClientFactory#get()}.
	 */
	@Test
	void given_workloadIdEnvironment_when_invokeGet_then_returnSuitableClient() {
		AzureIdentityReactiveClientFactory factory = new AzureIdentityReactiveClientFactory(
			Optional.empty(),
			Optional.empty(),
			Optional.of("https://login.microsoftonline.com/"),
			Optional.of("da795842-fa15-4fd4-b556-f371ac9bafed"),
			Optional.of("aeeb30a1-2d89-42bd-832c-69dc15a53d36"),
			Optional.of("/var/run/secrets/azure/tokens/azure-identity-token"));
		
		assertTrue(factory.get() instanceof AzureWorkloadIdentityReactiveClient);
	}
	
	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClientFactory#get()}.
	 */
	@Test
	void given_partialWorkloadIdEnvironment_when_invokeGet_then_throwException() {
		AzureIdentityReactiveClientFactory factory = new AzureIdentityReactiveClientFactory(
			Optional.empty(),
			Optional.empty(),
			Optional.of("https://login.microsoftonline.com/"),
			Optional.empty(),
			Optional.empty(),
			Optional.empty());
		
		assertThrows(
			DeploymentException.class,
			() -> factory.get());
	}
	
	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClientFactory#get()}.
	 */
	@Test
	void given_noIdentityEnvironment_when_invokeGet_then_throwException() {
		AzureIdentityReactiveClientFactory factory = new AzureIdentityReactiveClientFactory(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty());
		
		assertThrows(
			DeploymentException.class,
			() -> factory.get());
	}
}
