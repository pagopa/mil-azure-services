/*
 * AzureIdentityReactiveServiceTest.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityClient;
import it.pagopa.swclient.mil.azureservices.identity.client.systemmanaged.AzureSystemManagedIdentityClient;
import it.pagopa.swclient.mil.azureservices.identity.client.usermanaged.AzureUserManagedIdentityClient;
import it.pagopa.swclient.mil.azureservices.identity.client.workload.AzureWorkloadIdentityClient;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.DeploymentException;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@SuppressWarnings("unchecked")
class AzureIdentityReactiveServiceTest {
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
	 * 
	 */
	@Test
	void given_emptyCache_when_getAccessTokenInvoked_then_getNewOneCacheAndReturnIt() {
		/*
		 * Setup
		 */
		Instant now = Instant.now();
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");

		AzureWorkloadIdentityClient identityClient = mock(AzureWorkloadIdentityClient.class);
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		Instance<AzureWorkloadIdentityClient> identityClientInstance = mock(Instance.class);
		when(identityClientInstance.get())
			.thenReturn(identityClient);

		Instance<AzureIdentityClient> anyIdentityClient = mock(Instance.class);
		when(anyIdentityClient.select(AzureWorkloadIdentityClient.class))
			.thenReturn(identityClientInstance);

		/*
		 * Test
		 */
		AzureIdentityReactiveService identityService = spy(new AzureIdentityReactiveService(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.of("https://login.microsoftonline.com/"),
			Optional.of("da795842-fa15-4fd4-b556-f371ac9bafed"),
			Optional.of("aeeb30a1-2d89-42bd-832c-69dc15a53d36"),
			Optional.of("/var/run/secrets/azure/tokens/azure-identity-token"),
			anyIdentityClient));

		identityService.getAccessToken(Scope.VAULT)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(accessToken);

		verify(identityService).getNewAccessTokenAndCacheIt(Scope.VAULT);
	}

	/**
	 * 
	 */
	@Test
	void given_storedAccessToken_when_getAccessTokenInvoked_then_getReturnIt() {
		/*
		 * Setup
		 */
		Instant now = Instant.now();
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");

		AzureSystemManagedIdentityClient identityClient = mock(AzureSystemManagedIdentityClient.class);
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		Instance<AzureSystemManagedIdentityClient> identityClientInstance = mock(Instance.class);
		when(identityClientInstance.get())
			.thenReturn(identityClient);

		Instance<AzureIdentityClient> anyIdentityClient = mock(Instance.class);
		when(anyIdentityClient.select(AzureSystemManagedIdentityClient.class))
			.thenReturn(identityClientInstance);

		/*
		 * Test
		 */
		AzureIdentityReactiveService identityService = spy(new AzureIdentityReactiveService(
			Optional.empty(),
			Optional.empty(),
			Optional.of("https://login.microsoftonline.com/"),
			Optional.of("45ed57a0-ec26-41c9-8333-29daf37697d3"),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			anyIdentityClient));

		identityService.getAccessToken(Scope.VAULT)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(accessToken);

		identityService.getAccessToken(Scope.VAULT)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(accessToken);

		verify(identityService, times(1)).getNewAccessTokenAndCacheIt(Scope.VAULT);
	}

	/**
	 * 
	 */
	@Test
	void given_expAccessTokenStored_when_getAccessTokenInvoked_then_getNewOneCacheAndReturnIt() {
		/*
		 * Setup
		 */
		Instant now = Instant.now();
		AccessToken expAccessToken = new AccessToken()
			.setExpiresOn(now.minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");

		AzureWorkloadIdentityClient identityClient = mock(AzureWorkloadIdentityClient.class);
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(
				Uni.createFrom().item(expAccessToken),
				Uni.createFrom().item(accessToken));

		Instance<AzureWorkloadIdentityClient> identityClientInstance = mock(Instance.class);
		when(identityClientInstance.get())
			.thenReturn(identityClient);

		Instance<AzureIdentityClient> anyIdentityClient = mock(Instance.class);
		when(anyIdentityClient.select(AzureWorkloadIdentityClient.class))
			.thenReturn(identityClientInstance);

		/*
		 * Test
		 */
		AzureIdentityReactiveService identityService = spy(new AzureIdentityReactiveService(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.of("https://login.microsoftonline.com/"),
			Optional.of("da795842-fa15-4fd4-b556-f371ac9bafed"),
			Optional.of("aeeb30a1-2d89-42bd-832c-69dc15a53d36"),
			Optional.of("/var/run/secrets/azure/tokens/azure-identity-token"),
			anyIdentityClient));

		identityService.getAccessToken(Scope.VAULT)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(expAccessToken);

		identityService.getAccessToken(Scope.VAULT)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(accessToken);

		verify(identityService, times(2)).getNewAccessTokenAndCacheIt(Scope.VAULT);
	}

	/**
	 * 
	 */
	@Test
	void given_systemManagedIdEnvironment_when_invokeGet_then_returnSuitableClient() {
		AzureSystemManagedIdentityClient identityClient = mock(AzureSystemManagedIdentityClient.class);

		Instance<AzureSystemManagedIdentityClient> identityClientInstance = mock(Instance.class);
		when(identityClientInstance.get())
			.thenReturn(identityClient);

		Instance<AzureIdentityClient> anyIdentityClient = mock(Instance.class);
		when(anyIdentityClient.select(AzureSystemManagedIdentityClient.class))
			.thenReturn(identityClientInstance);

		AzureIdentityReactiveService service = new AzureIdentityReactiveService(
			Optional.empty(),
			Optional.empty(),
			Optional.of("https://login.microsoftonline.com/"),
			Optional.of("45ed57a0-ec26-41c9-8333-29daf37697d3"),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			anyIdentityClient);

		assertTrue(service.getIdentityClient() instanceof AzureSystemManagedIdentityClient);
	}

	/**
	 * 
	 */
	@Test
	void given_partialSystemManagedIdEnvironment_when_invokeGet_then_throwException() {
		assertThrows( // NOSONAR
			DeploymentException.class,
			() -> new AzureIdentityReactiveService(
				Optional.empty(),
				Optional.empty(),
				Optional.of("https://login.microsoftonline.com/"),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				null));
	}
	
	/**
	 * 
	 */
	@Test
	void given_userManagedIdEnvironment_when_invokeGet_then_returnSuitableClient() {
		AzureUserManagedIdentityClient identityClient = mock(AzureUserManagedIdentityClient.class);

		Instance<AzureUserManagedIdentityClient> identityClientInstance = mock(Instance.class);
		when(identityClientInstance.get())
			.thenReturn(identityClient);

		Instance<AzureIdentityClient> anyIdentityClient = mock(Instance.class);
		when(anyIdentityClient.select(AzureUserManagedIdentityClient.class))
			.thenReturn(identityClientInstance);

		AzureIdentityReactiveService service = new AzureIdentityReactiveService(
			Optional.of("67a40498-91c1-4e4c-9c43-8aeb09c0de5e"),
			Optional.of("https://login.microsoftonline.com/"),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			anyIdentityClient);

		assertTrue(service.getIdentityClient() instanceof AzureUserManagedIdentityClient);
	}

	/**
	 * 
	 */
	@Test
	void given_partialUserManagedIdEnvironment_when_invokeGet_then_throwException() {
		assertThrows( // NOSONAR
			DeploymentException.class,
			() -> new AzureIdentityReactiveService(
				Optional.empty(),
				Optional.of("https://login.microsoftonline.com/"),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				null));
	}

	/**
	 * 
	 */
	@Test
	void given_workloadIdEnvironment_when_invokeGet_then_returnSuitableClient() {
		AzureWorkloadIdentityClient identityClient = mock(AzureWorkloadIdentityClient.class);

		Instance<AzureWorkloadIdentityClient> identityClientInstance = mock(Instance.class);
		when(identityClientInstance.get())
			.thenReturn(identityClient);

		Instance<AzureIdentityClient> anyIdentityClient = mock(Instance.class);
		when(anyIdentityClient.select(AzureWorkloadIdentityClient.class))
			.thenReturn(identityClientInstance);

		AzureIdentityReactiveService service = new AzureIdentityReactiveService(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.of("https://login.microsoftonline.com/"),
			Optional.of("da795842-fa15-4fd4-b556-f371ac9bafed"),
			Optional.of("aeeb30a1-2d89-42bd-832c-69dc15a53d36"),
			Optional.of("/var/run/secrets/azure/tokens/azure-identity-token"),
			anyIdentityClient);

		assertTrue(service.getIdentityClient() instanceof AzureWorkloadIdentityClient);
	}

	/**
	 * 
	 */
	@Test
	void given_partialWorkloadIdEnvironment_when_invokeGet_then_throwException() {
		assertThrows( // NOSONAR
			DeploymentException.class,
			() -> new AzureIdentityReactiveService(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of("https://login.microsoftonline.com/"),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				null));
	}

	/**
	 * 
	 */
	@Test
	void given_noIdentityEnvironment_when_invokeGet_then_throwException() {
		assertThrows( // NOSONAR
			DeploymentException.class,
			() -> new AzureIdentityReactiveService(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				null));
	}
}
