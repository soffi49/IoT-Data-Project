package org.iotdata.utils;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.tdb2.TDB2;

/**
 * Class with method allowing to construct generic queries used in model processing
 */
public class QueryConstructor {

	/**
	 * Method constructs query execution for given dataset,
	 * taking into account the SPARQL query statement and defined prologue (i.e. prefixes)
	 *
	 * @param dataset        dataset on which query is to be executed
	 * @param queryStatement statement that is to be executed
	 * @param prologue       set of defined prefixes
	 * @return QueryExecution
	 */
	public static QueryExecution createQueryFromNamedModels(final Dataset dataset, final String queryStatement,
			final Prologue prologue) {
		final Query query = QueryFactory.parse(new Query(prologue), queryStatement, null, null);
		return QueryExecution.dataset(dataset)
				.query(query)
				.set(TDB2.symUnionDefaultGraph, true)
				.build();
	}
}
