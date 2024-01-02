package org.iotdata.exception;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

/**
 * Default exception extended by custom exception classes
 */
public class DefaultException extends RuntimeException {

	private static final Logger logger = getLogger(DefaultException.class);

	public DefaultException(final String textToDisplay, final Exception originalException) {
		super(textToDisplay);
		logger.error(textToDisplay);
		originalException.printStackTrace();
	}
}
