package org.iotdata.exception;

import static java.lang.String.join;

/**
 * Exception throw when the property file could not be read
 */
public class InvalidPropertyFile extends RuntimeException {

	public InvalidPropertyFile(final String file, final Exception originalException) {
		super(join("The property configuration file %s could not be loaded.", file), originalException);
	}
}
