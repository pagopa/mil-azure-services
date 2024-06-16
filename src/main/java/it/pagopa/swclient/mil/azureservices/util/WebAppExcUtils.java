/*
 * WebAppExcUtils.java
 *
 * 22 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.util;

import io.quarkus.logging.Log;
import jakarta.ws.rs.WebApplicationException;

/**
 * 
 * @author Antonio Tarricone
 */
public class WebAppExcUtils {
	/**
	 * 
	 */
	private WebAppExcUtils() {
	}

	/**
	 * 
	 * @param failure
	 * @return
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
	 * 
	 * @param failure
	 * @return
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
	 * 
	 * @param webException
	 * @return
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
	 * 
	 * @param webException
	 * @return
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
