package org.iotdata.domain.analyzer;

import static org.iotdata.constants.OutputDirectoriesConstants.UNSAFE_WORKERS_DAYS;
import static org.iotdata.constants.OutputDirectoriesConstants.UNSAFE_WORKER_EVENTS;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_CONSECUTIVE_UNSAFE_WORKERS_EVENTS;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_DAYS_WITH_UNSAFE_WORKERS;
import static org.iotdata.enums.PrefixType.AIOT_P2;
import static org.iotdata.enums.PrefixType.SOSA;
import static org.iotdata.enums.PrefixType.XSD;
import static org.iotdata.utils.DirectoryFactory.removeDirectory;
import static org.iotdata.utils.OutputWriter.storeResultsInSingleFile;
import static org.iotdata.utils.QueryExecutor.executeQuery;
import static org.iotdata.utils.OutputWriter.storeResultsInSeparateFiles;

import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ResultSet;
import org.iotdata.domain.function.CustomFunction;
import org.iotdata.domain.function.cameras.CountWorkers;
import org.iotdata.domain.function.cameras.MapEventLength;
import org.iotdata.domain.function.cameras.MapEventStartTime;
import org.iotdata.domain.function.cameras.MapUnsafeEventState;

/**
 * Class containing methods used to analyse the camera datasets
 */
public class CamerasAnalyzer extends AbstractAnalyzer {

	public CamerasAnalyzer(final String outputPath) {
		super(outputPath);

		removeDirectory(outputPath, UNSAFE_WORKER_EVENTS);
		removeDirectory(outputPath, UNSAFE_WORKERS_DAYS);
	}

	@Override
	public void performAnalysis(final Dataset dataset) {
		super.performAnalysis(dataset);
		final int index = indexOfSingularResultsFile.get();

		storeResultsInSeparateFiles(UNSAFE_WORKERS_DAYS, selectDaysWithUnsafeWorkers(dataset), outputPath, index);
		storeResultsInSingleFile(UNSAFE_WORKER_EVENTS, detectUnsafeWorkersEvents(dataset), outputPath);
	}

	/**
	 * Method selects days when unsafe workers were registered
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return result set of unsafe workers
	 */
	private ResultSet selectDaysWithUnsafeWorkers(final Dataset dataset) {
		return executeQuery(dataset, SELECT_DAYS_WITH_UNSAFE_WORKERS, SOSA, AIOT_P2);
	}

	/**
	 * Method identifies consecutive measurements which indicate that unsafe workers were recognized by the camera.
	 * It stores the information about:
	 * 1. time frames during which the events were detected
	 * 2. aggregated number of safe, unsafe and indeterminate workers recognized during events
	 * 3. duration time of the events
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return result set of unsafe workers events
	 */
	private ResultSet detectUnsafeWorkersEvents(final Dataset dataset) {
		final List<CustomFunction> functionsToRegister = List.of(
				new MapUnsafeEventState(),
				new MapEventLength(),
				new MapEventStartTime(),
				new CountWorkers()
		);
		return executeQuery(dataset, SELECT_CONSECUTIVE_UNSAFE_WORKERS_EVENTS, functionsToRegister, SOSA, AIOT_P2, XSD);
	}
}
