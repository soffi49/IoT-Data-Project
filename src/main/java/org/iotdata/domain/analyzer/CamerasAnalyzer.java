package org.iotdata.domain.analyzer;

import static org.iotdata.constants.dml.CamerasQueries.SELECT_DAYS_WITH_UNSAFE_WORKERS;
import static org.iotdata.enums.PrefixType.IOT_ONTO;
import static org.iotdata.enums.PrefixType.SOSA;
import static org.iotdata.utils.QueryExecutor.executeQuery;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ResultSet;

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
		return executeQuery(dataset, SELECT_DAYS_WITH_UNSAFE_WORKERS, SOSA, IOT_ONTO);
	}
}
