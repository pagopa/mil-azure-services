/*
 * NoAround.java
 *
 * 20 mag 2024
 */
package it.pagopa.swclient.mil.azureservices.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author antonio.tarricone
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface NoAround {
}
