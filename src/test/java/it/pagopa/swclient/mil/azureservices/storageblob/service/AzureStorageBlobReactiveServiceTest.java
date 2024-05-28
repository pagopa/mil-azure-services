/*
 * AzureStorageBlobReactiveServiceTest.java
 *
 * 23 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.storageblob.service;

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
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService;
import it.pagopa.swclient.mil.azureservices.storageblob.client.AzureStorageBlobReactiveClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class AzureStorageBlobReactiveServiceTest {
	/*
	 * 
	 */
	@InjectMock
	AzureIdentityReactiveService identityService;

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureStorageBlobReactiveClient blobClient;

	/*
	 * 
	 */
	@Inject
	AzureStorageBlobReactiveService blobService;

	/*
	 * 
	 */
	private Instant now;

	/*
	 * 
	 */
	private Response response;

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
		now = Instant.now();
		response = Response.ok(new Sample()
			.setField1("field_1")
			.setField2("field_2")).build();
		Mockito.reset(blobClient);
		AccessToken accessToken = new AccessToken()
			.setExpiresOn(now.plus(5, ChronoUnit.MINUTES).getEpochSecond())
			.setValue("access_token_string");
		when(identityService.getAccessToken(Scope.STORAGE))
			.thenReturn(Uni.createFrom().item(accessToken));
		when(identityService.getNewAccessTokenAndCacheIt(Scope.STORAGE))
			.thenReturn(Uni.createFrom().item(accessToken));
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	void given_getBlobRequest_when_blobClientReturns401_then_getNewAccessTokenAndRetry() {
		/*
		 * Setup.
		 */
		when(blobClient.getBlob("access_token_string", "file_name"))
			.thenReturn(
				Uni.createFrom().failure(new WebApplicationException(401)),
				Uni.createFrom().item(response));

		/*
		 * Test.
		 */
		blobService.getBlob("file_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(response);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	void given_getBlobRequest_when_blobClientReturns403_then_getNewAccessTokenAndRetry() {
		/*
		 * Setup.
		 */
		when(blobClient.getBlob("access_token_string", "segment_1", "file_name"))
			.thenReturn(
				Uni.createFrom().failure(new WebApplicationException(403)),
				Uni.createFrom().item(response));

		/*
		 * Test.
		 */
		blobService.getBlob("segment_1", "file_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(response);
	}

	/**
	 * 
	 */
	@Test
	void given_getBlobRequest_when_blobClientReturns404_then_getFailure() {
		/*
		 * Setup.
		 */
		when(blobClient.getBlob("access_token_string", "segment_1", "segment_2", "file_name"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(404)));

		/*
		 * Test.
		 */
		blobService.getBlob("segment_1", "segment_2", "file_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailed();
	}

	/**
	 * 
	 */
	@Test
	void given_getBlobRequest_when_blobClientReturnsFailure_then_getFailure() {
		/*
		 * Setup.
		 */
		when(blobClient.getBlob("access_token_string", "segment_1", "segment_2", "segment_3", "file_name"))
			.thenReturn(Uni.createFrom().failure(new Exception("other_failure")));

		/*
		 * Test.
		 */
		blobService.getBlob("segment_1", "segment_2", "segment_3", "file_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailed();
	}

	/**
	 * 
	 */
	@Test
	void given_getBlobRequest_when_blobClientThrowsException_then_getFailure() {
		/*
		 * Setup.
		 */
		when(blobClient.getBlob("access_token_string", "segment_1", "segment_2", "segment_3", "segment_4", "file_name"))
			.thenThrow(new RuntimeException("exception_while_proceeding"));

		/*
		 * Test.
		 */
		blobService.getBlob("segment_1", "segment_2", "segment_3", "segment_4", "file_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailed();
	}

	/**
	 * 
	 */
	@Test
	void given_getBlobRequestWith6Seg_when_blobClientReturnsBlob_then_returnIt() {
		/*
		 * Setup.
		 */
		when(blobClient.getBlob("access_token_string", "segment_1", "segment_2", "segment_3", "segment_4", "segment_5", "segment_6", "file_name"))
			.thenReturn(Uni.createFrom().item(response));

		/*
		 * Test.
		 */
		blobService.getBlob("segment_1", "segment_2", "segment_3", "segment_4", "segment_5", "segment_6", "file_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(response);
	}

	/**
	 * 
	 */
	@Test
	void given_getBlobRequestWith5Seg_when_blobClientReturnsBlob_then_returnIt() {
		/*
		 * Setup.
		 */
		when(blobClient.getBlob("access_token_string", "segment_1", "segment_2", "segment_3", "segment_4", "segment_5", "file_name"))
			.thenReturn(Uni.createFrom().item(response));

		/*
		 * Test.
		 */
		blobService.getBlob("segment_1", "segment_2", "segment_3", "segment_4", "segment_5", "file_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(response);
	}

	/**
	 * 
	 */
	@Test
	void given_getBlobRequestWith4Seg_when_blobClientReturnsBlob_then_returnIt() {
		/*
		 * Setup.
		 */
		when(blobClient.getBlob("access_token_string", "segment_1", "segment_2", "segment_3", "segment_4", "file_name"))
			.thenReturn(Uni.createFrom().item(response));

		/*
		 * Test.
		 */
		blobService.getBlob("segment_1", "segment_2", "segment_3", "segment_4", "file_name")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(response);
	}
}
