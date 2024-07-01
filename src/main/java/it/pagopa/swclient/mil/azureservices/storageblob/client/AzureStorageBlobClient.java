/*
 * AzureStorageBlobClient.java
 *
 * 25 giu 2024
 */
package it.pagopa.swclient.mil.azureservices.storageblob.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.NotBody;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * <p>
 * REST client for Azure Storage Blob.
 * </p>
 * <p>
 * To use this client, the {@code application.properties} must have the definition of the following
 * properties:
 * </p>
 * <ul>
 * <li>{@code quarkus.rest-client.azure-storage-blob.url} must be set with the URL of Azure Storage
 * Blob;</li>
 * <li>{@code azure-storage-blob.api-version} must be {@code 2019-07-07}.</li>
 * </ul>
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-storage-blob")
@ClientHeaderParam(name = "x-ms-version", value = "${azure-storage-blob.version}")
@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
public interface AzureStorageBlobClient {
	/**
	 * <p>
	 * Returns a blob with the given file name.
	 * </p>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param filename    The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	@Path("{filename}")
	@GET
	Response getBlob(
		@NotBody String accessToken,
		@PathParam("filename") String filename);

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param segment1    Segment of the path to file to retrieve.
	 * @param filename    The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	@Path("{segment1}/{filename}")
	@GET
	Response getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("filename") String filename);

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param segment1    Segment of the path to file to retrieve.
	 * @param segment2    Segment of the path to file to retrieve.
	 * @param filename    The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	@Path("{segment1}/{segment2}/{filename}")
	@GET
	Response getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("filename") String filename);

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param segment1    Segment of the path to file to retrieve.
	 * @param segment2    Segment of the path to file to retrieve.
	 * @param segment3    Segment of the path to file to retrieve.
	 * @param filename    The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	@Path("{segment1}/{segment2}/{segment3}/{filename}")
	@GET
	Response getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("segment3") String segment3,
		@PathParam("filename") String filename);

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param segment1    Segment of the path to file to retrieve.
	 * @param segment2    Segment of the path to file to retrieve.
	 * @param segment3    Segment of the path to file to retrieve.
	 * @param segment4    Segment of the path to file to retrieve.
	 * @param filename    The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	@Path("{segment1}/{segment2}/{segment3}/{segment4}/{filename}")
	@GET
	Response getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("segment3") String segment3,
		@PathParam("segment4") String segment4,
		@PathParam("filename") String filename);

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param segment1    Segment of the path to file to retrieve.
	 * @param segment2    Segment of the path to file to retrieve.
	 * @param segment3    Segment of the path to file to retrieve.
	 * @param segment4    Segment of the path to file to retrieve.
	 * @param segment5    Segment of the path to file to retrieve.
	 * @param filename    The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	@Path("{segment1}/{segment2}/{segment3}/{segment4}/{segment5}/{filename}")
	@GET
	Response getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("segment3") String segment3,
		@PathParam("segment4") String segment4,
		@PathParam("segment5") String segment5,
		@PathParam("filename") String filename);

	/**
	 * <p>
	 * Returns a blob with the given file name and path.
	 * </p>
	 * 
	 * @param accessToken The value of access token got by Microsoft Entra ID.
	 * @param segment1    Segment of the path to file to retrieve.
	 * @param segment2    Segment of the path to file to retrieve.
	 * @param segment3    Segment of the path to file to retrieve.
	 * @param segment4    Segment of the path to file to retrieve.
	 * @param segment5    Segment of the path to file to retrieve.
	 * @param segment6    Segment of the path to file to retrieve.
	 * @param filename    The name of file to retrieve.
	 * @return If the file is retrieved successfully, the response body contains it.
	 */
	@Path("{segment1}/{segment2}/{segment3}/{segment4}/{segment5}/{segment6}/{filename}")
	@GET
	Response getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("segment3") String segment3,
		@PathParam("segment4") String segment4,
		@PathParam("segment5") String segment5,
		@PathParam("segment6") String segment6,
		@PathParam("filename") String filename);
}
