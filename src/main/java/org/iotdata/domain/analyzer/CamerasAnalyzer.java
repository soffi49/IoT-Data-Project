package org.iotdata.domain.analyzer;

import static org.iotdata.constants.OutputDirectoriesConstants.AVERAGE_VALUES;
import static org.iotdata.constants.OutputDirectoriesConstants.AVERAGE_VALUES_NON_ZERO;
import static org.iotdata.constants.OutputDirectoriesConstants.UNSAFE_WORKER_EVENTS;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_AVERAGE_VALUES;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_AVERAGE_VALUES_NONZERO_CONFIDENCE;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_CONSECUTIVE_UNSAFE_WORKERS_EVENTS;
import static org.iotdata.enums.ArgumentType.PREV_MEASUREMENT_TIME;
import static org.iotdata.enums.ArgumentType.UNSAFE_WORKERS_EVENT_INDICATOR;
import static org.iotdata.enums.ArgumentType.UNSAFE_WORKERS_EVENT_LENGTH;
import static org.iotdata.enums.ArgumentType.UNSAFE_WORKERS_EVENT_SAFE_COUNT;
import static org.iotdata.enums.ArgumentType.UNSAFE_WORKERS_EVENT_START_TIME;
import static org.iotdata.enums.ArgumentType.UNSAFE_WORKERS_EVENT_STATUS;
import static org.iotdata.enums.ArgumentType.UNSAFE_WORKERS_EVENT_UNSAFE_COUNT;
import static org.iotdata.enums.ArgumentType.UNSAFE_WORKERS_EVENT_UNSAFE_MAX;
import static org.iotdata.enums.EventStateType.NO_EVENT;
import static org.iotdata.enums.PrefixType.AIOT_P2;
import static org.iotdata.enums.PrefixType.SOSA;
import static org.iotdata.enums.PrefixType.XSD;
import static org.iotdata.utils.CalendarInitializer.initializeCalendar;
import static org.iotdata.utils.DirectoryFactory.removeDirectory;
import static org.iotdata.utils.OutputWriter.storeResultsInSingleFile;
import static org.iotdata.utils.QueryExecutor.executeQuery;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ResultSet;
import org.iotdata.domain.function.CustomFunction;
import org.iotdata.domain.function.cameras.CountWorkers;
import org.iotdata.domain.function.cameras.MapEventLength;
import org.iotdata.domain.function.cameras.MapEventStartTime;
import org.iotdata.domain.function.cameras.MapUnsafeEventState;
import org.iotdata.domain.function.cameras.MaxUnsafeWorkers;
import org.iotdata.enums.ArgumentType;

/**
 * Class containing methods used to analyse the camera datasets
 */
public class CamerasAnalyzer extends AbstractAnalyzer {

	public CamerasAnalyzer(final String outputPath) {
		super(outputPath);

		removeDirectory(outputPath, UNSAFE_WORKER_EVENTS);
		removeDirectory(outputPath, AVERAGE_VALUES);
		removeDirectory(outputPath, AVERAGE_VALUES_NON_ZERO);
	}

	@Override
	public List<Consumer<Dataset>> initializeAnalysisQueries() {
		super.initializeAnalysisQueries();

		return List.of(
				dataset -> storeResultsInSingleFile(AVERAGE_VALUES,
						selectAvgValues(dataset), outputPath),
				dataset -> storeResultsInSingleFile(AVERAGE_VALUES_NON_ZERO,
						selectAvgConfidenceWithoutZeros(dataset), outputPath),
				dataset -> storeResultsInSingleFile(UNSAFE_WORKER_EVENTS,
						detectUnsafeWorkersEvents(dataset), outputPath)
		);
	}

	/**
	 * Method computes average values of given measurements window
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return result set of average values
	 */
	private ResultSet selectAvgValues(final Dataset dataset) {
		return executeQuery(dataset, SELECT_AVERAGE_VALUES, SOSA, AIOT_P2);
	}

	/**
	 * Method computes average values of given measurements window where at leas one measurement value
	 * is different from 0.
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return result set of average values different than zero
	 */
	private ResultSet selectAvgConfidenceWithoutZeros(final Dataset dataset) {

		return executeQuery(dataset, SELECT_AVERAGE_VALUES_NONZERO_CONFIDENCE, SOSA, AIOT_P2);
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
				new MapUnsafeEventState(
						globalParameters.get(UNSAFE_WORKERS_EVENT_INDICATOR),
						globalParameters.get(UNSAFE_WORKERS_EVENT_STATUS),
						globalParameters.get(PREV_MEASUREMENT_TIME)
				),
				new MapEventLength(globalParameters.get(UNSAFE_WORKERS_EVENT_LENGTH)),
				new MapEventStartTime(globalParameters.get(UNSAFE_WORKERS_EVENT_START_TIME)),
				new CountWorkers(globalParameters.get(UNSAFE_WORKERS_EVENT_UNSAFE_COUNT), "unsafe"),
				new CountWorkers(globalParameters.get(UNSAFE_WORKERS_EVENT_SAFE_COUNT), "safe"),
				new MaxUnsafeWorkers(globalParameters.get(UNSAFE_WORKERS_EVENT_UNSAFE_MAX))
		);
		return executeQuery(dataset, SELECT_CONSECUTIVE_UNSAFE_WORKERS_EVENTS, functionsToRegister, SOSA, AIOT_P2, XSD);
	}

	@Override
	protected Map<ArgumentType, Object> initializeGlobalParams() {
		return Map.of(
				UNSAFE_WORKERS_EVENT_LENGTH, new AtomicInteger(0),
				UNSAFE_WORKERS_EVENT_INDICATOR, new AtomicInteger(0),
				UNSAFE_WORKERS_EVENT_UNSAFE_MAX, new AtomicLong(0),
				UNSAFE_WORKERS_EVENT_UNSAFE_COUNT, new AtomicLong(0),
				UNSAFE_WORKERS_EVENT_SAFE_COUNT, new AtomicLong(0),
				UNSAFE_WORKERS_EVENT_START_TIME, new AtomicReference<>(initializeCalendar.get()),
				UNSAFE_WORKERS_EVENT_STATUS, new AtomicReference<>(NO_EVENT),
				PREV_MEASUREMENT_TIME, new AtomicReference<Instant>(null)
		);
	}
}
