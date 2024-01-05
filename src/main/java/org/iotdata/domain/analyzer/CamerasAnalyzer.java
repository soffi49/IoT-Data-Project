package org.iotdata.domain.analyzer;

import static java.util.Map.entry;
import static org.iotdata.constants.OutputDirectoriesConstants.AVERAGE_VALUES;
import static org.iotdata.constants.OutputDirectoriesConstants.AVERAGE_VALUES_NON_ZERO;
import static org.iotdata.constants.OutputDirectoriesConstants.CONFIDENCE_PER_WORKER_TYPE;
import static org.iotdata.constants.OutputDirectoriesConstants.INDETERMINATE_WORKER_EVENTS;
import static org.iotdata.constants.OutputDirectoriesConstants.UNSAFE_WORKER_EVENTS;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_AVERAGE_VALUES;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_AVERAGE_VALUES_NONZERO_CONFIDENCE;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_CONFIDENCE_PER_OBSERVATION_TYPE;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_CONSECUTIVE_INDETERMINATE_WORKERS_EVENTS;
import static org.iotdata.constants.dml.CamerasQueries.SELECT_CONSECUTIVE_UNSAFE_WORKERS_EVENTS;
import static org.iotdata.enums.ArgumentType.INDETERMINATE_WORKERS_EVENT_LENGTH;
import static org.iotdata.enums.ArgumentType.INDETERMINATE_WORKERS_PREV_MEASUREMENT;
import static org.iotdata.enums.ArgumentType.INDETERMINATE_WORKERS_WORKERS_COUNT;
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
import org.iotdata.domain.function.cameras.indeterminatevent.CountIndeterminateWorkers;
import org.iotdata.domain.function.cameras.indeterminatevent.MapIndeterminateEventLength;
import org.iotdata.domain.function.cameras.unsafeevent.CountWorkersPerSafety;
import org.iotdata.domain.function.cameras.unsafeevent.MapUnsafeEventLength;
import org.iotdata.domain.function.cameras.unsafeevent.MapUnsafeEventStartTime;
import org.iotdata.domain.function.cameras.unsafeevent.MapUnsafeEventState;
import org.iotdata.domain.function.cameras.unsafeevent.MaxUnsafeWorkers;
import org.iotdata.enums.ArgumentType;

/**
 * Class containing methods used to analyse the camera datasets
 */
public class CamerasAnalyzer extends AbstractAnalyzer {

	public CamerasAnalyzer(final String outputPath) {
		super(outputPath);

		removeDirectory(outputPath, UNSAFE_WORKER_EVENTS);
		removeDirectory(outputPath, INDETERMINATE_WORKER_EVENTS);
		removeDirectory(outputPath, CONFIDENCE_PER_WORKER_TYPE);
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
				dataset -> storeResultsInSingleFile(CONFIDENCE_PER_WORKER_TYPE,
						selectConfidencePerObservedWorkers(dataset), outputPath),
				dataset -> storeResultsInSingleFile(INDETERMINATE_WORKER_EVENTS,
						detectIndeterminateWorkersEvents(dataset), outputPath),
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
	 * Method computes average confidence with respect to types of detected workers and categorizes confidence
	 * as either HIGH, MEDIUM, LOW or NONE (i.e. when all confidence values are 0)
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return result set of average values
	 */
	private ResultSet selectConfidencePerObservedWorkers(final Dataset dataset) {
		return executeQuery(dataset, SELECT_CONFIDENCE_PER_OBSERVATION_TYPE, SOSA, AIOT_P2);
	}

	/**
	 * Method identifies consecutive measurements which indicate that unsafe workers were recognized by the camera.
	 * It stores the information about:
	 * 1. time frames during which the events were detected
	 * 2. aggregated number of safe and unsafe workers recognized during events
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
				new MapUnsafeEventLength(globalParameters.get(UNSAFE_WORKERS_EVENT_LENGTH)),
				new MapUnsafeEventStartTime(globalParameters.get(UNSAFE_WORKERS_EVENT_START_TIME)),
				new CountWorkersPerSafety(globalParameters.get(UNSAFE_WORKERS_EVENT_UNSAFE_COUNT), "unsafe"),
				new CountWorkersPerSafety(globalParameters.get(UNSAFE_WORKERS_EVENT_SAFE_COUNT), "safe"),
				new MaxUnsafeWorkers(globalParameters.get(UNSAFE_WORKERS_EVENT_UNSAFE_MAX))
		);
		return executeQuery(dataset, SELECT_CONSECUTIVE_UNSAFE_WORKERS_EVENTS, functionsToRegister, SOSA, AIOT_P2, XSD);
	}

	/**
	 * Method identifies consecutive measurements which indicate that unsafe workers were recognized by the camera.
	 * It stores the information about:
	 * 1. time frames during which the events were detected
	 * 2. aggregated number of safe and unsafe workers recognized during events
	 * 3. duration time of the events
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return result set of unsafe workers events
	 */
	private ResultSet detectIndeterminateWorkersEvents(final Dataset dataset) {
		final List<CustomFunction> functionsToRegister = List.of(
				new MapIndeterminateEventLength(
						globalParameters.get(INDETERMINATE_WORKERS_EVENT_LENGTH),
						globalParameters.get(INDETERMINATE_WORKERS_PREV_MEASUREMENT)
				),
				new CountIndeterminateWorkers(globalParameters.get(INDETERMINATE_WORKERS_WORKERS_COUNT))
		);
		return executeQuery(dataset, SELECT_CONSECUTIVE_INDETERMINATE_WORKERS_EVENTS, functionsToRegister,
				SOSA, AIOT_P2, XSD);
	}

	@Override
	protected Map<ArgumentType, Object> initializeGlobalParams() {
		return Map.ofEntries(
				entry(UNSAFE_WORKERS_EVENT_LENGTH, new AtomicInteger(0)),
				entry(UNSAFE_WORKERS_EVENT_INDICATOR, new AtomicInteger(0)),
				entry(UNSAFE_WORKERS_EVENT_UNSAFE_MAX, new AtomicLong(0)),
				entry(UNSAFE_WORKERS_EVENT_UNSAFE_COUNT, new AtomicLong(0)),
				entry(UNSAFE_WORKERS_EVENT_SAFE_COUNT, new AtomicLong(0)),
				entry(UNSAFE_WORKERS_EVENT_START_TIME, new AtomicReference<>(initializeCalendar.get())),
				entry(UNSAFE_WORKERS_EVENT_STATUS, new AtomicReference<>(NO_EVENT)),
				entry(INDETERMINATE_WORKERS_EVENT_LENGTH, new AtomicInteger(0)),
				entry(INDETERMINATE_WORKERS_WORKERS_COUNT, new AtomicLong(0)),
				entry(INDETERMINATE_WORKERS_PREV_MEASUREMENT, new AtomicReference<Instant>(null)),
				entry(PREV_MEASUREMENT_TIME, new AtomicReference<Instant>(null))
		);
	}
}
