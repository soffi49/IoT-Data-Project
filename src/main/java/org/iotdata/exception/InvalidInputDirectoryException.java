package org.iotdata.exception;

import static java.lang.String.join;

public class InvalidInputDirectoryException extends DefaultException {

	public InvalidInputDirectoryException(final String fileName, final Exception originalException) {
		super(join("Could not read directory with name %s.", fileName), originalException);
	}
}
