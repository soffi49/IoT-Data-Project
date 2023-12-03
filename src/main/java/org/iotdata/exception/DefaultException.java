package org.iotdata.exception;

public class DefaultException extends RuntimeException {

	public DefaultException(final String textToDisplay, final Exception originalException) {
		super(textToDisplay);
		originalException.printStackTrace();
	}
}
