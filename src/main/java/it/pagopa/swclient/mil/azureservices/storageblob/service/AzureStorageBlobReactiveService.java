/*
 * AzureStorageBlobReactiveService.java
 *
 * 21 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.storageblob.service;

import java.lang.reflect.Method;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.azureservices.identity.bean.Scope;
import it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService;
import it.pagopa.swclient.mil.azureservices.storageblob.client.AzureStorageBlobReactiveClient;
import it.pagopa.swclient.mil.azureservices.util.WebAppExcUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Response;

/**
 * <p>
 * This service is a kind of wrapper of
 * {@link it.pagopa.swclient.mil.azureservices.storageblob.client.AzureStorageBlobReactiveClient
 * AzureStorageBlobReactiveClient} which implements:
 * </p>
 * <ul>
 * <li>the retrieving, caching (done by means of
 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService
 * AzureIdentityReactiveService}) and renewal (when it expires or when used the resource API returns
 * 401 or 403) of the access token from Microsoft Entra ID that will be used with Azure Storage Blob
 * (by means of
 * {@link it.pagopa.swclient.mil.azureservices.storageblob.client.AzureStorageBlobReactiveClient
 * AzureStorageBlobReactiveClient}).</li>
 * </ul>
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureStorageBlobReactiveService {
	/**
	 * <p>
	 * Service to retrieve the access token from Microsoft Entra ID.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService
	 *      AzureIdentityReactiveService
	 */
	private AzureIdentityReactiveService identityService;

	/**
	 * <p>
	 * REST client to use Azure Storage Blob.
	 * </p>
	 * 
	 * @see it.pagopa.swclient.mil.azureservices.storageblob.client.AzureStorageBlobReactiveClient
	 *      AzureStorageBlobReactiveClient
	 */
	@RestClient
	AzureStorageBlobReactiveClient blobClient;

	/**
	 * <p>
	 * Cached access token.
	 * </p>
	 */
	private String accessTokenValue;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 * 
	 * @param identityService {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService
	 *                        AzureIdentityReactiveService}
	 */
	@Inject
	AzureStorageBlobReactiveService(AzureIdentityReactiveService identityService) {
		this.identityService = identityService;
	}

	/**
	 * <p>
	 * Transforms {@link java.lang.Exception Exception} in {@link java.lang.RuntimeException
	 * RuntimeException} to allow handling with Mutiny.
	 * </p>
	 * 
	 * @param context {@link jakarta.interceptor.InvocationContext InvocationContext}
	 * @return Object returned by the target method.
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
	 * <p>
	 * This method decorates the others:
	 * </p>
	 * <ul>
	 * <li>retrieving the access token from Microsoft Entra ID by means of
	 * {@link it.pagopa.swclient.mil.azureservices.identity.service.AzureIdentityReactiveService
	 * AzureIdentityReactiveService};</li>
	 * <li>renewing of the access token from Microsoft Entra ID the invoked target API returns 401 or
	 * 403.</li>
	 * </ul>
	 * 
	 * @param context {@link jakarta.interceptor.InvocationContext InvocationContext}
	 * @return Object returned by the target method.
	 */
	@AroundInvoke
	Object authenticate(InvocationContext context) {
		Method method = context.getMethod();
		Log.tracef("Around invoke: %s.%s", context.getTarget().getClass().getSimpleName(), method.getName());
		return identityService.getAccessToken(Scope.STORAGE)
			.invoke(accessToken -> accessTokenValue = accessToken.getValue())
			.chain(() -> proceed(context))
			.onFailure(WebAppExcUtils::isUnauthorizedOrForbidden) // On 401 or 403...
			.recoverWithUni(f -> {
				Log.debug("Recovering");
				return identityService.getNewAccessTokenAndCacheIt(Scope.STORAGE) // ...get a new access token...
					.invoke(accessToken -> accessTokenValue = accessToken.getValue())
					.chain(() -> proceed(context));
			}); // ...and retry!
	}

	/**
	 * <p>
	 * Returns a blob with the given file name.
	 * </p>
	 * 
	 * @param filename The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	public Uni<Response> getBlob(String filename) {
		return blobClient.getBlob(accessTokenValue, filename);
	}

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param segment1 Segment of the path to file to retrieve.
	 * @param filename The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	public Uni<Response> getBlob(String segment1, String filename) {
		return blobClient.getBlob(accessTokenValue, segment1, filename);
	}

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param segment1 Segment of the path to file to retrieve.
	 * @param segment2 Segment of the path to file to retrieve.
	 * @param filename The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	public Uni<Response> getBlob(String segment1, String segment2, String filename) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, filename);
	}

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param segment1 Segment of the path to file to retrieve.
	 * @param segment2 Segment of the path to file to retrieve.
	 * @param segment3 Segment of the path to file to retrieve.
	 * @param filename The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	public Uni<Response> getBlob(String segment1, String segment2, String segment3, String filename) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, segment3, filename);
	}

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param segment1 Segment of the path to file to retrieve.
	 * @param segment2 Segment of the path to file to retrieve.
	 * @param segment3 Segment of the path to file to retrieve.
	 * @param segment4 Segment of the path to file to retrieve.
	 * @param filename The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	public Uni<Response> getBlob(String segment1, String segment2, String segment3, String segment4, String filename) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, segment3, segment4, filename);
	}

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param segment1 Segment of the path to file to retrieve.
	 * @param segment2 Segment of the path to file to retrieve.
	 * @param segment3 Segment of the path to file to retrieve.
	 * @param segment4 Segment of the path to file to retrieve.
	 * @param segment5 Segment of the path to file to retrieve.
	 * @param filename The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	public Uni<Response> getBlob(String segment1, String segment2, String segment3, String segment4, String segment5, String filename) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, segment3, segment4, segment5, filename);
	}

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param segment1 Segment of the path to file to retrieve.
	 * @param segment2 Segment of the path to file to retrieve.
	 * @param segment3 Segment of the path to file to retrieve.
	 * @param segment4 Segment of the path to file to retrieve.
	 * @param segment5 Segment of the path to file to retrieve.
	 * @param segment6 Segment of the path to file to retrieve.
	 * @param filename The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	public Uni<Response> getBlob(String segment1, String segment2, String segment3, String segment4, String segment5, String segment6, String filename) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, segment3, segment4, segment5, segment6, filename);
	}
}
