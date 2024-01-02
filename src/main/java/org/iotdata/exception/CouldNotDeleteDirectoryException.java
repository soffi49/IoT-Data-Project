package org.iotdata.exception;

import static java.lang.String.join;

/**
 * Exception throw when directory could not be removed.
 */
public class CouldNotDeleteDirectoryException extends DefaultException {

	public CouldNotDeleteDirectoryException(final String directoryName, final Exception originalException) {
		super(join("Error has been encountered while trying to delete directory {}.", directoryName),
				originalException);
	}
}
