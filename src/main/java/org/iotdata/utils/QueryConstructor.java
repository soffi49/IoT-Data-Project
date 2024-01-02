package org.iotdata.utils;

import static org.iotdata.enums.PrefixType.FUNC;

import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.tdb2.TDB2;
import org.iotdata.domain.function.CustomFunction;

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
		return getDatasetQueryExecution(query, dataset);
	}

	/**
	 * Method constructs query execution for given dataset,
	 * taking into account the SPARQL query statement, defined prologue (i.e. prefixes) and custom ARQ functions.
	 *
	 * @param dataset        dataset on which query is to be executed
	 * @param queryStatement statement that is to be executed
	 * @param prologue       set of defined prefixes
	 * @param functions      list of custom functions that are to be used in the query
	 * @return QueryExecution
	 */
	public static QueryExecution createQueryFromNamedModels(final Dataset dataset, final String queryStatement,
			final Prologue prologue, final List<CustomFunction> functions) {
		prologue.setPrefix(FUNC.getPrefix(), FUNC.getUri());
		final Query query = QueryFactory.parse(new Query(prologue), queryStatement, null, null);
		appendFunctionsToQuery(functions);
		return getDatasetQueryExecution(query, dataset);
	}

	private static void appendFunctionsToQuery(final List<CustomFunction> functions) {
		final FunctionRegistry functionRegistry = FunctionRegistry.get();
		functions.forEach(func -> functionRegistry.put(func.getUri(), func.getClass()));
	}

	private static QueryExecution getDatasetQueryExecution(final Query query, final Dataset dataset) {
		return QueryExecution.dataset(dataset)
				.query(query)
				.set(TDB2.symUnionDefaultGraph, true)
				.build();
	}
}
