/*
 * WebAppExcUtils.java
 *
 * 22 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.util;

import io.quarkus.logging.Log;
import jakarta.ws.rs.WebApplicationException;

/**
 * <p>
 * Verifies is a failure is due to specific HTTP status code returned by the invoked service.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public class WebAppExcUtils {
	/**
	 * <p>
	 * This class has static methods only.
	 * </p>
	 */
	private WebAppExcUtils() {
	}

	/**
	 * <p>
	 * Verifies if a failure is due to 401 or 403 returned from invoked resource.
	 * </p>
	 * 
	 * @param failure Failure to verify.
	 * @return {@code true} is the failure is due to 401 or 403 returned from invoked resource,
	 *         {@code false} otherwise.
	 */
	public static boolean isUnauthorizedOrForbidden(Throwable failure) {
		Log.debug("Failure inspection");
		if (failure instanceof WebApplicationException webException) {
			return isUnauthorizedOrForbidden(webException);
		} else {
			Log.debugf("Other failure received", failure);
			return false;
		}
	}

	/**
	 * <p>
	 * Verifies if a failure is due to 429 returned from invoked resource.
	 * </p>
	 * 
	 * @param failure Failure to verify.
	 * @return {@code true} is the failure is due to 429 returned from invoked resource, {@code false}
	 *         otherwise.
	 */
	public static boolean isTooManyRequests(Throwable failure) {
		Log.debug("Failure inspection");
		if (failure instanceof WebApplicationException webException) {
			return isTooManyRequests(webException);
		} else {
			Log.debugf("Other failure received", failure);
			return false;
		}
	}

	/**
	 * <p>
	 * Verifies if a {@link jakarta.ws.rs.WebApplicationException WebApplicationException} is due to 401
	 * or 403 returned from invoked resource.
	 * </p>
	 * 
	 * @param webException Exception to verify.
	 * @return {@code true} is the exception is due to 401 or 403 returned from invoked resource,
	 *         {@code false} otherwise.
	 */
	public static boolean isUnauthorizedOrForbidden(WebApplicationException webException) {
		Log.debug("WebApplicationException inspection");
		int status = webException.getResponse().getStatus();
		boolean check = status == 401 || status == 403;
		if (check) {
			Log.debug("Could it be that the access token is invalid?");
			return true;
		} else {
			Log.debugf("HTTP status other than 401 or 403 received: %d", status);
			return false;
		}
	}

	/**
	 * <p>
	 * Verifies if a {@link jakarta.ws.rs.WebApplicationException WebApplicationException} is due to 429
	 * returned from invoked resource.
	 * </p>
	 * 
	 * @param webException Exeption to verify.
	 * @return {@code true} is the exception is due to 429 returned from invoked resource, {@code false}
	 *         otherwise.
	 */
	public static boolean isTooManyRequests(WebApplicationException webException) {
		Log.debug("WebApplicationException inspection");
		int status = webException.getResponse().getStatus();
		boolean check = status == 429;
		if (check) {
			Log.debug("Too many requests!");
			return true;
		} else {
			Log.debugf("HTTP status other than 429 received: %d", status);
			return false;
		}
	}
}
