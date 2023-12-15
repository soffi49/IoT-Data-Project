package org.iotdata.domain.analyzer;

import org.apache.jena.query.*;
import org.iotdata.utils.DirectoryModifier;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.iotdata.constants.dml.TagsQueries.*;
import static org.iotdata.enums.PrefixType.*;
import static org.iotdata.utils.QueryExecutor.executeQuery;

/**
 * Class containing methods used to analyse the tags datasets
 */
public class TagsAnalyzer implements AbstractAnalyzer {
	private final AtomicInteger namingIndex = new AtomicInteger(0);

	@Override
	public void performAnalysis(final Dataset dataset, final String outputPath) {
		final int currentIndex = namingIndex.incrementAndGet();
		try {
			processResults("results_identifier", selectUniqueIdentifiers(dataset), currentIndex, outputPath);
			processResults("results_days_watch_connected", selectDaysWithWatchConnected(dataset), currentIndex,
					outputPath);
			processResults("results_days_heart_rate", selectDaysWithHeartRate(dataset), currentIndex, outputPath);
			processResults("results_days_watch_battery_level", selectDaysWithWatchBatteryLevels(dataset), currentIndex,
					outputPath);
			processResults("result_days_alarm", selectDaysWithAlarm(dataset), currentIndex, outputPath);
			processResults("result_days_locations", selectDaysWithLocations(dataset), currentIndex, outputPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processResults(String directoryName, ResultSet resultSet, int currentIndex, String outputPath)
			throws IOException {
		DirectoryModifier.constructDirectory(outputPath, directoryName);
		try (OutputStream output = new FileOutputStream(
				DirectoryModifier.constructName(outputPath, directoryName, currentIndex))) {
			ResultSetFormatter.outputAsCSV(output, resultSet);
		}
	}

	/**
	 * Method returns set of timestamps with respective heart rates
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of timestamps with respective heart rates
	 */
	private ResultSet selectDaysWithHeartRate(final Dataset dataset) {
		return executeQuery(dataset, SELECT_DAYS_WITH_HEART_RATE, SOSA, AIOT_P2, MEASURE, SCHEMA);
	}

	/**
	 * Method returns set of timestamps with respective battery levels
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of timestamps with respective battery levels
	 */
	private ResultSet selectDaysWithWatchBatteryLevels(final Dataset dataset) {
		return executeQuery(dataset, SELECT_DAYS_WITH_WATCH_BATTERY_LEVELS, SOSA, AIOT_P2, SCHEMA);
	}

	/**
	 * Method returns set of timestamps when the alarm was triggered
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of timestamps when the alarm was triggered
	 */
	private ResultSet selectDaysWithAlarm(final Dataset dataset) {
		return executeQuery(dataset, SELECT_DAYS_WITH_ALARM, SOSA, AIOT_P2, SCHEMA);
	}

	/**
	 * Method returns set of timestamps with respective locations in the BIM model
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of timestamps with respective locations in the BIM model
	 */
	private ResultSet selectDaysWithLocations(final Dataset dataset) {
		return executeQuery(dataset, SELECT_DAYS_WITH_LOCATIONS, SOSA, AIOT_P2, MEASURE, SCHEMA);
	}

	/**
	 * Method returns set of unique tag identifiers
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of unique tag identifiers
	 */
	private ResultSet selectUniqueIdentifiers(final Dataset dataset) {
		return executeQuery(dataset, SELECT_UNIQUE_IDENTIFIERS, SCHEMA);
	}

	/**
	 * Method returns number of times when the watch was connected and not connected
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of timestamps with the status of connection of the watch
	 */
	private ResultSet selectDaysWithWatchConnected(final Dataset dataset) {
		return executeQuery(dataset, SELECT_DAYS_WITH_WATCH_CONNECTED, SOSA, AIOT_P2, SCHEMA);
	}
}