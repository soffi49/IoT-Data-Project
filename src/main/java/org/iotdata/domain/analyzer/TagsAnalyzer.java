package org.iotdata.domain.analyzer;

import static org.iotdata.constants.OutputDirectoriesConstants.ALARM_TRIGGER_EVENTS;
import static org.iotdata.constants.OutputDirectoriesConstants.COUNT_ALARM_EVENTS;
import static org.iotdata.constants.OutputDirectoriesConstants.DISCONNECTED_WATCH_EVENTS;
import static org.iotdata.constants.OutputDirectoriesConstants.HIGH_HEART_RATE_EVENTS;
import static org.iotdata.constants.OutputDirectoriesConstants.MAX_MIN_HEART_RATE;
import static org.iotdata.constants.dml.TagsQueries.SELECT_ALARM_EVENTS;
import static org.iotdata.constants.dml.TagsQueries.SELECT_COUNT_ALARM_TRIGGERS;
import static org.iotdata.constants.dml.TagsQueries.SELECT_DISCONNECTED_WATCH_EVENTS;
import static org.iotdata.constants.dml.TagsQueries.SELECT_ABNORMAL_HEART_RATE_EVENTS;
import static org.iotdata.constants.dml.TagsQueries.SELECT_MAX_MIN_HEART_RATE;
import static org.iotdata.enums.ArgumentType.ALARM_TRIGGER;
import static org.iotdata.enums.ArgumentType.ALARM_TRIGGER_START_TIME;
import static org.iotdata.enums.ArgumentType.DISCONNECTED_WATCHES_START_TIME;
import static org.iotdata.enums.ArgumentType.DISCONNECTED_WATCHES;
import static org.iotdata.enums.ArgumentType.ABNORMAL_HEART_RATE;
import static org.iotdata.enums.ArgumentType.ABNORMAL_HEART_RATE_START_TIME;
import static org.iotdata.enums.PrefixType.AIOT_P2;
import static org.iotdata.enums.PrefixType.MEASURE;
import static org.iotdata.enums.PrefixType.SCHEMA;
import static org.iotdata.enums.PrefixType.SOSA;
import static org.iotdata.utils.CalendarInitializer.initializeCalendar;
import static org.iotdata.utils.DirectoryFactory.removeDirectory;
import static org.iotdata.utils.OutputWriter.storeResultsInSingleFile;
import static org.iotdata.utils.QueryExecutor.executeQuery;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ResultSet;
import org.iotdata.domain.function.CustomFunction;
import org.iotdata.domain.function.tags.ClassifyHeartRate;
import org.iotdata.domain.function.tags.MapAlarmEvent;
import org.iotdata.domain.function.tags.MapDisconnectedEvent;
import org.iotdata.domain.function.tags.MapAbnormalHeartRateEvent;
import org.iotdata.enums.ArgumentType;

/**
 * Class containing methods used to analyse the tags datasets
 */
public class TagsAnalyzer extends AbstractAnalyzer {

	public TagsAnalyzer(final String outputPath) {
		super(outputPath);

		removeDirectory(outputPath, DISCONNECTED_WATCH_EVENTS);
		removeDirectory(outputPath, HIGH_HEART_RATE_EVENTS);
		removeDirectory(outputPath, MAX_MIN_HEART_RATE);
		removeDirectory(outputPath, ALARM_TRIGGER_EVENTS);
		removeDirectory(outputPath, COUNT_ALARM_EVENTS);
	}

	@Override
	public List<Consumer<Dataset>> initializeAnalysisQueries() {
		super.initializeAnalysisQueries();

		return List.of(
				dataset -> storeResultsInSingleFile(DISCONNECTED_WATCH_EVENTS,
						detectDisconnectedWatch(dataset), outputPath),
				dataset -> storeResultsInSingleFile(HIGH_HEART_RATE_EVENTS,
						detectAbnormalHeartRate(dataset), outputPath),
				dataset -> storeResultsInSingleFile(MAX_MIN_HEART_RATE,
						selectMaxMinHeartRate(dataset), outputPath),
				dataset -> storeResultsInSingleFile(ALARM_TRIGGER_EVENTS,
						detectAlarmTrigger(dataset), outputPath),
				dataset -> storeResultsInSingleFile(COUNT_ALARM_EVENTS,
						selectAlarmTrigger(dataset), outputPath)
		);
	}

	/**
	 * Method returns disconnected watch events
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of all events when the watch was disconnected together with duration
	 */
	private ResultSet detectDisconnectedWatch(final Dataset dataset) {
		final List<CustomFunction> functionsToRegister = List.of(
				new MapDisconnectedEvent(
						globalParameters.get(DISCONNECTED_WATCHES),
						globalParameters.get(DISCONNECTED_WATCHES_START_TIME)
				)
		);
		return executeQuery(dataset, SELECT_DISCONNECTED_WATCH_EVENTS, functionsToRegister, SOSA, AIOT_P2, SCHEMA);
	}

	/**
	 * Method returns abnormal heart rate (less than 60 or greater than 100) events
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of all events when the heart rate is in abnormal range together with duration
	 */
	private ResultSet detectAbnormalHeartRate(final Dataset dataset) {
		final List<CustomFunction> functionsToRegister = List.of(
				new MapAbnormalHeartRateEvent(
						globalParameters.get(ABNORMAL_HEART_RATE),
						globalParameters.get(ABNORMAL_HEART_RATE_START_TIME)
				),
				new ClassifyHeartRate()
		);
		return executeQuery(dataset, SELECT_ABNORMAL_HEART_RATE_EVENTS, functionsToRegister, SOSA, AIOT_P2, MEASURE, SCHEMA);
	}

	/**
	 * Method returns max and min heart rate for the worker in the given processing window
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return max and min heart rate in the processing window
	 */
	private ResultSet selectMaxMinHeartRate(final Dataset dataset) {
		return executeQuery(dataset, SELECT_MAX_MIN_HEART_RATE, SOSA, AIOT_P2, MEASURE, SCHEMA);
	}

	/**
	 * Method returns alarm duration events
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of all events when alarm was triggered together with duration
	 */
	private ResultSet detectAlarmTrigger(final Dataset dataset) {
		final List<CustomFunction> functionsToRegister = List.of(
				new MapAlarmEvent(
						globalParameters.get(ALARM_TRIGGER),
						globalParameters.get(ALARM_TRIGGER_START_TIME)
				)
		);
		return executeQuery(dataset, SELECT_ALARM_EVENTS, functionsToRegister, SOSA, AIOT_P2, SCHEMA);
	}

	/**
	 * Method returns alarm trigger timestamps
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return list of timestamps when the alarm was triggered
	 */
	private ResultSet selectAlarmTrigger(final Dataset dataset) {
		return executeQuery(dataset, SELECT_COUNT_ALARM_TRIGGERS, SOSA, AIOT_P2, SCHEMA);
	}

	@Override
	protected Map<ArgumentType, Object> initializeGlobalParams() {
		return Map.of(
				DISCONNECTED_WATCHES, new ConcurrentHashMap<>(),
				DISCONNECTED_WATCHES_START_TIME, new AtomicReference<>(initializeCalendar.get()),
				ABNORMAL_HEART_RATE, new ConcurrentHashMap<>(),
				ABNORMAL_HEART_RATE_START_TIME, new AtomicReference<>(initializeCalendar.get()),
				ALARM_TRIGGER, new ConcurrentHashMap<>(),
				ALARM_TRIGGER_START_TIME, new AtomicReference<>(initializeCalendar.get())
		);
	}
}