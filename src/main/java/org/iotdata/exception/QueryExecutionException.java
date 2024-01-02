package org.iotdata.exception;

import static java.lang.String.join;

/**
 * Exception throw when some error was detected during query execution
 */
public class QueryExecutionException extends DefaultException {

	public QueryExecutionException(final String query, final Exception originalException) {
		super(join("Error within execution of query {} was detected.", query), originalException);
	}
}
