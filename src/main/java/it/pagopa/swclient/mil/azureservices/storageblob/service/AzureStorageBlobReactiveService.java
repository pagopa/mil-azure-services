/*
 * AzureStorageBlobReactiveService.java
 *
 * 21 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.storageblob.service;

import java.lang.reflect.Method;
import java.time.Instant;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.AccessToken;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.client.AzureIdentityReactiveClient;
import it.pagopa.swclient.mil.azureservices.storageblob.client.AzureStorageBlobReactiveClient;
import it.pagopa.swclient.mil.azureservices.util.NoAround;
import it.pagopa.swclient.mil.azureservices.util.WebAppExcUtils;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

/**
 * 
 * @author Antonio Tarricone
 */
public class AzureStorageBlobReactiveService<T> {
	/*
	 * 
	 */
	private AzureIdentityReactiveClient identityClient;

	/*
	 * 
	 */
	private AzureStorageBlobReactiveClient<T> blobClient;

	/*
	 * 
	 */
	private AccessToken accessToken;

	/**
	 * 
	 * @param identityClient
	 * @param blobClient
	 */
	AzureStorageBlobReactiveService(
		@RestClient AzureIdentityReactiveClient identityClient,
		@RestClient AzureStorageBlobReactiveClient<T> blobClient) {
		this.identityClient = identityClient;
		this.blobClient = blobClient;
	}

	/**
	 * 
	 * @return
	 */
	private Uni<Void> getNewAccessTokenAndCacheIt() {
		Log.debug("Get new access token");
		return identityClient.getAccessToken(Scope.VAULT)
			.invoke(newAccessToken -> {
				Log.trace("Caching access token");
				accessToken = newAccessToken;
			})
			.replaceWithVoid();
	}

	/**
	 * 
	 * @return
	 */
	private Uni<Void> getAccessToken() {
		if (accessToken != null && accessToken.getExpiresOn() > Instant.now().getEpochSecond()) {
			Log.trace("Cached access token is going to be used");
			return Uni.createFrom().voidItem();
		} else {
			Log.debug("There's no cached access token or it is expired");
			return getNewAccessTokenAndCacheIt();
		}
	}

	/**
	 * 
	 */
	@NoAround
	void resetCachedAccessToken() {
		accessToken = null;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Uni<Object> proceed(InvocationContext context) {
		Log.trace("Proceed");
		try {
			return (Uni<Object>) (context.proceed());
		} catch (Exception e) {
			throw new RuntimeException(e); // NOSONAR
		}
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	@AroundInvoke
	Object authenticate(InvocationContext context) {
		Method method = context.getMethod();
		NoAround noAround = method.getAnnotation(NoAround.class);
		if (noAround == null) {
			Log.tracef("Around invoke: %s.%s", context.getTarget().getClass().getSimpleName(), method.getName());
			return getAccessToken()
				.chain(() -> proceed(context))
				.onFailure(WebAppExcUtils::isUnauthorizedOrForbidden) // On 401 or 403...
				.recoverWithUni(f -> {
					Log.debug("Recovering");
					return getNewAccessTokenAndCacheIt() // ...get a new access token...
						.chain(() -> proceed(context));
				}); // ...and retry!
		} else {
			Log.tracef("No around: %s.%s", context.getTarget().getClass().getSimpleName(), method.getName());
			return proceed(context);
		}
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public Uni<T> getBlob(String fileName) {
		return blobClient.getBlob(accessToken.getValue(), fileName);
	}

	/**
	 * 
	 * @param segment1
	 * @param fileName
	 * @return
	 */
	public Uni<T> getBlob(String segment1, String fileName) {
		return blobClient.getBlob(accessToken.getValue(), segment1, fileName);
	}

	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @param fileName
	 * @return
	 */
	public Uni<T> getBlob(String segment1, String segment2, String fileName) {
		return blobClient.getBlob(accessToken.getValue(), segment1, segment2, fileName);
	}

	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @param segment3
	 * @param fileName
	 * @return
	 */
	public Uni<T> getBlob(String segment1, String segment2, String segment3, String fileName) {
		return blobClient.getBlob(accessToken.getValue(), segment1, segment2, segment3, fileName);
	}

	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @param segment3
	 * @param segment4
	 * @param fileName
	 * @return
	 */
	public Uni<T> getBlob(String segment1, String segment2, String segment3, String segment4, String fileName) {
		return blobClient.getBlob(accessToken.getValue(), segment1, segment2, segment3, segment4, fileName);
	}

	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @param segment3
	 * @param segment4
	 * @param segment5
	 * @param fileName
	 * @return
	 */
	public Uni<T> getBlob(String segment1, String segment2, String segment3, String segment4, String segment5, String fileName) {
		return blobClient.getBlob(accessToken.getValue(), segment1, segment2, segment3, segment4, segment5, fileName);
	}

	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @param segment3
	 * @param segment4
	 * @param segment5
	 * @param segment6
	 * @param fileName
	 * @return
	 */
	public Uni<T> getBlob(String segment1, String segment2, String segment3, String segment4, String segment5, String segment6, String fileName) {
		return blobClient.getBlob(accessToken.getValue(), segment1, segment2, segment3, segment4, segment5, segment6, fileName);
	}
}
