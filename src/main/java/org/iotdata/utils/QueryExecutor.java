package org.iotdata.utils;

import static org.iotdata.enums.PrefixType.createPrefixPrologue;
import static org.iotdata.utils.QueryConstructor.createQueryFromNamedModels;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.core.Prologue;
import org.iotdata.enums.PrefixType;

/**
 * Class containing methods used to execute common queries
 */
public class QueryExecutor {

	/**
	 * Method constructs and executes given query
	 *
	 * @param dataset  dataset on which query is to be executed
	 * @param query    content of the query
	 * @param prefixes prefixes applied within the query
	 * @return results obtained after query execution
	 */
	public static ResultSet executeQuery(final Dataset dataset, final String query, final PrefixType... prefixes) {
		final Prologue prologue = createPrefixPrologue(prefixes);
		try (QueryExecution queryExecution = createQueryFromNamedModels(dataset, query, prologue)) {
			return queryExecution.execSelect();
		}
	}
}
