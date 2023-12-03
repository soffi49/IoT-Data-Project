package org.iotdata.exception;

/**
 * Default exception extended by custom exception classes
 */
public class DefaultException extends RuntimeException {

	public DefaultException(final String textToDisplay, final Exception originalException) {
		super(textToDisplay);
		originalException.printStackTrace();
	}
}
