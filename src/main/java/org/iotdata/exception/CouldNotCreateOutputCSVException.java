package org.iotdata.exception;

import static java.lang.String.join;

/**
 * Exception throw when output CSV file could not have been created.
 */
public class CouldNotCreateOutputCSVException extends DefaultException {

	public CouldNotCreateOutputCSVException(final String fileName, final Exception originalException) {
		super(join("Error has been encountered while trying to save output in {}.", fileName),
				originalException);
	}
}
