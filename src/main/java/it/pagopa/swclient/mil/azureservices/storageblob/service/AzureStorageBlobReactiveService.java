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
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureStorageBlobReactiveService {
	/*
	 * 
	 */
	private AzureIdentityReactiveService identityService;

	/*
	 * 
	 */
	private AzureStorageBlobReactiveClient blobClient;

	/*
	 * 
	 */
	private String accessTokenValue;

	/**
	 * 
	 * @param identityClient
	 * @param blobClient
	 */
	@Inject
	AzureStorageBlobReactiveService(
		AzureIdentityReactiveService identityService,
		@RestClient AzureStorageBlobReactiveClient blobClient) {
		this.identityService = identityService;
		this.blobClient = blobClient;
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
	 * 
	 * @param fileName
	 * @return
	 */
	public Uni<Response> getBlob(String fileName) {
		return blobClient.getBlob(accessTokenValue, fileName);
	}

	/**
	 * 
	 * @param segment1
	 * @param fileName
	 * @return
	 */
	public Uni<Response> getBlob(String segment1, String fileName) {
		return blobClient.getBlob(accessTokenValue, segment1, fileName);
	}

	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @param fileName
	 * @return
	 */
	public Uni<Response> getBlob(String segment1, String segment2, String fileName) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, fileName);
	}

	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @param segment3
	 * @param fileName
	 * @return
	 */
	public Uni<Response> getBlob(String segment1, String segment2, String segment3, String fileName) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, segment3, fileName);
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
	public Uni<Response> getBlob(String segment1, String segment2, String segment3, String segment4, String fileName) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, segment3, segment4, fileName);
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
	public Uni<Response> getBlob(String segment1, String segment2, String segment3, String segment4, String segment5, String fileName) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, segment3, segment4, segment5, fileName);
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
	public Uni<Response> getBlob(String segment1, String segment2, String segment3, String segment4, String segment5, String segment6, String fileName) {
		return blobClient.getBlob(accessTokenValue, segment1, segment2, segment3, segment4, segment5, segment6, fileName);
	}
}
