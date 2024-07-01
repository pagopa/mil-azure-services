/*
 * AzureIdentityReactiveServiceTest.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClient;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class AzureIdentityReactiveServiceTest {
	/*
	 * 
	 */
	@InjectSpy
	AzureIdentityReactiveService identityService;

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureIdentityReactiveClient identityClient;

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
		Mockito.reset(identityClient);
		identityService.clearAccessTokenCache();
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService#getAccessToken(it.pagopa.swclient.mil.azureservices.identity.bean.Scope)}.
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
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Test
		 */
		identityService.getAccessToken(Scope.VAULT)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(accessToken);

		verify(identityService).getNewAccessTokenAndCacheIt(Scope.VAULT);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService#getAccessToken(it.pagopa.swclient.mil.azureservices.identity.bean.Scope)}.
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
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(Uni.createFrom().item(accessToken));

		/*
		 * Test
		 */
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
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService#getAccessToken(it.pagopa.swclient.mil.azureservices.identity.bean.Scope)}.
	 */
	@SuppressWarnings("unchecked")
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
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(
				Uni.createFrom().item(expAccessToken),
				Uni.createFrom().item(accessToken));

		/*
		 * Test
		 */
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
}
