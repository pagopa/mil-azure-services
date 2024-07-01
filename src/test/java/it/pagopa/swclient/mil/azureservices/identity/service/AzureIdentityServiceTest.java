/*
 * AzureIdentityServiceTest.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityClient;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class AzureIdentityServiceTest {
	/*
	 * 
	 */
	@InjectSpy
	AzureIdentityService identityService;

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureIdentityClient identityClient;

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
	 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityService#getAccessToken(it.pagopa.swclient.mil.azureservices.identity.bean.Scope)}.
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
			.thenReturn(accessToken);

		/*
		 * Test
		 */
		assertEquals(identityService.getAccessToken(Scope.VAULT), accessToken);
		verify(identityService).getNewAccessTokenAndCacheIt(Scope.VAULT);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityService#getAccessToken(it.pagopa.swclient.mil.azureservices.identity.bean.Scope)}.
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
			.thenReturn(accessToken);

		/*
		 * Test
		 */
		identityService.getAccessToken(Scope.VAULT);

		assertEquals(identityService.getAccessToken(Scope.VAULT), accessToken);
		verify(identityService, times(1)).getNewAccessTokenAndCacheIt(Scope.VAULT);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityService#getAccessToken(it.pagopa.swclient.mil.azureservices.identity.bean.Scope)}.
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
		when(identityClient.getAccessToken(Scope.VAULT))
			.thenReturn(
				expAccessToken,
				accessToken);

		/*
		 * Test
		 */
		identityService.getAccessToken(Scope.VAULT);

		assertEquals(identityService.getAccessToken(Scope.VAULT), accessToken);
		verify(identityService, times(2)).getNewAccessTokenAndCacheIt(Scope.VAULT);
	}
}
