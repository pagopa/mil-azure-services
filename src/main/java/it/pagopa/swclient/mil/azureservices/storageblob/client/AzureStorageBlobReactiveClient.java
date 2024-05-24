/*
 * AzureStorageBlobReactiveClient.java
 *
 * 21 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.storageblob.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.NotBody;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-storage-blob")
@ClientHeaderParam(name = "x-ms-version", value = "${azure-storage-blob.version}")
@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
public interface AzureStorageBlobReactiveClient {
	/**
	 * 
	 * @param accessToken
	 * @param filename
	 * @return
	 */
	@Path("{filename}")
	@GET
	Uni<Response> getBlob(
		@NotBody String accessToken,
		@PathParam("filename") String filename);

	/**
	 * 
	 * @param accessToken
	 * @param segment1
	 * @param filename
	 * @return
	 */
	@Path("{segment1}/{filename}")
	@GET
	Uni<Response> getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("filename") String filename);

	/**
	 * 
	 * @param accessToken
	 * @param segment1
	 * @param segment2
	 * @param filename
	 * @return
	 */
	@Path("{segment1}/{segment2}/{filename}")
	@GET
	Uni<Response> getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("filename") String filename);

	/**
	 * 
	 * @param accessToken
	 * @param segment1
	 * @param segment2
	 * @param segment3
	 * @param filename
	 * @return
	 */
	@Path("{segment1}/{segment2}/{segment3}/{filename}")
	@GET
	Uni<Response> getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("segment3") String segment3,
		@PathParam("filename") String filename);

	/**
	 * 
	 * @param accessToken
	 * @param segment1
	 * @param segment2
	 * @param segment3
	 * @param segment4
	 * @param filename
	 * @return
	 */
	@Path("{segment1}/{segment2}/{segment3}/{segment4}/{filename}")
	@GET
	Uni<Response> getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("segment3") String segment3,
		@PathParam("segment4") String segment4,
		@PathParam("filename") String filename);

	/**
	 * 
	 * @param accessToken
	 * @param segment1
	 * @param segment2
	 * @param segment3
	 * @param segment4
	 * @param segment5
	 * @param filename
	 * @return
	 */
	@Path("{segment1}/{segment2}/{segment3}/{segment4}/{segment5}/{filename}")
	@GET
	Uni<Response> getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("segment3") String segment3,
		@PathParam("segment4") String segment4,
		@PathParam("segment5") String segment5,
		@PathParam("filename") String filename);

	/**
	 * 
	 * @param accessToken
	 * @param segment1
	 * @param segment2
	 * @param segment3
	 * @param segment4
	 * @param segment5
	 * @param segment6
	 * @param filename
	 * @return
	 */
	@Path("{segment1}/{segment2}/{segment3}/{segment4}/{segment5}/{segment6}/{filename}")
	@GET
	Uni<Response> getBlob(
		@NotBody String accessToken,
		@PathParam("segment1") String segment1,
		@PathParam("segment2") String segment2,
		@PathParam("segment3") String segment3,
		@PathParam("segment4") String segment4,
		@PathParam("segment5") String segment5,
		@PathParam("segment6") String segment6,
		@PathParam("filename") String filename);
}
