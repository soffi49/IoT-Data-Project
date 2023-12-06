package org.iotdata.exception;

import static java.lang.String.join;

/**
 * Exception thrown when input directory path could not be parsed
 */
public class InvalidInputDirectoryException extends DefaultException {

	public InvalidInputDirectoryException(final String fileName, final Exception originalException) {
		super(join("Could not read directory with name %s.", fileName), originalException);
	}
}
