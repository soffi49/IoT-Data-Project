package org.iotdata.analysis;

import static org.iotdata.dml.CamerasQueries.SELECT_DAYS_WITH_UNSAFE_WORKERS;
import static org.iotdata.enums.PrefixType.*;
import static org.iotdata.utils.QueryConstructor.createQueryFromNamedModels;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.core.Prologue;

/**
 * Class containing methods used to analyse the camera datasets
 */
public class CamerasAnalyzer implements AbstractAnalyzer {

	@Override
	public void performAnalysis(final Dataset dataset) {
		selectDaysWithUnsafeWorkers(dataset);
	}

	/**
	 * Method selects days when unsafe workers were registered
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return result set of unsafe workers
	 */
	private ResultSet selectDaysWithUnsafeWorkers(final Dataset dataset) {
		final Prologue prologue = createPrefixPrologue(SOSA, AIOT_P2);
		final QueryExecution queryExecution =
				createQueryFromNamedModels(dataset, SELECT_DAYS_WITH_UNSAFE_WORKERS, prologue);
		return queryExecution.execSelect();
	}
}
