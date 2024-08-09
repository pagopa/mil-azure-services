/*
 * AzureWorkloadIdentityClientTest.java
 *
 * 7 ago 2024
 */
package it.pagopa.swclient.mil.azureservices.identity.client.workload;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.MockedStatic;

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;

/**
 * 
 * @author Antonio.tarricone
 */
@QuarkusTest
class AzureWorkloadIdentityClientTest {
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
	void given_requestToGetAccessToken_when_requestIsDone_then_returnAccessToken() {
		/*
		 * Mocking of REST client.
		 */
		Instant now = Instant.now();
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");

		AzureWorkloadIdentityRestClient restClient = mock(AzureWorkloadIdentityRestClient.class);
		when(restClient.getAccessToken(Scope.getForWorkloadIdentity(Scope.STORAGE)))
			.thenReturn(Uni.createFrom()
				.item(accessToken));

		/*
		 * Mocking of QuarkusRestClientBuilder.
		 */
		QuarkusRestClientBuilder clientBuilder = mock(QuarkusRestClientBuilder.class);

		when(clientBuilder.build(AzureWorkloadIdentityRestClient.class))
			.thenReturn(restClient);

		when(clientBuilder.baseUri(any(URI.class)))
			.thenReturn(clientBuilder);

		/*
		 * Mocking of QuarkusRestClientBuilder factory.
		 */
		try (MockedStatic<QuarkusRestClientBuilder> restClientBuilderFactory = mockStatic(QuarkusRestClientBuilder.class)) {
			restClientBuilderFactory.when(() -> QuarkusRestClientBuilder.newBuilder())
				.thenReturn(clientBuilder);

			/*
			 * Test.
			 */
			AzureWorkloadIdentityClient client = new AzureWorkloadIdentityClient(
				Optional.of("https://login.microsoftonline.com/"),
				Optional.of("da795842-fa15-4fd4-b556-f371ac9bafed"));
			client.getAccessToken(Scope.STORAGE)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.awaitItem()
				.assertItem(accessToken);
		}
	}
}
