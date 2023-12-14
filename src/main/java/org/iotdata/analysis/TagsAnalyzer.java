package org.iotdata.analysis;

import org.apache.jena.query.*;
import org.apache.jena.sparql.core.Prologue;
import org.iotdata.enums.PrefixType;
import org.iotdata.utils.DirectoryModifier;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.iotdata.dml.TagsQueries.*;
import static org.iotdata.enums.PrefixType.*;
import static org.iotdata.utils.QueryConstructor.createQueryFromNamedModels;

/**
 * Class containing methods used to analyse the tags datasets
 */
public class TagsAnalyzer implements AbstractAnalyzer {
	private final AtomicInteger namingIndex = new AtomicInteger(0);

	@Override
	public void performAnalysis(final Dataset dataset) {
		final int currentIndex = namingIndex.incrementAndGet();
		try {
			processResults("results_identifier", selectUniqueIdentifiers(dataset), currentIndex);
			processResults("results_days_watch_connected", selectDaysWithWatchConnected(dataset), currentIndex);
			processResults("results_days_heart_rate", selectDaysWithHeartRate(dataset), currentIndex);
			processResults("results_days_watch_battery_level", selectDaysWithWatchBatteryLevels(dataset), currentIndex);
			processResults("result_days_alarm", selectDaysWithAlarm(dataset), currentIndex);
			processResults("result_days_locations", selectDaysWithLocations(dataset), currentIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processResults(String directoryName, ResultSet resultSet, int currentIndex) throws IOException {
		String basePath = "src/main/resources/results/";
		DirectoryModifier.constructDirectory(basePath, directoryName);
		try (OutputStream output = new FileOutputStream(DirectoryModifier.constructName(basePath, directoryName, currentIndex))) {
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
		return selectData(dataset, SELECT_DAYS_WITH_HEART_RATE, SOSA, AIOT_P2 , MEASURE, SCHEMA);
	}

	/**
	 * Method returns set of timestamps with respective battery levels
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of timestamps with respective battery levels
	 */
	private ResultSet selectDaysWithWatchBatteryLevels(final Dataset dataset) {
		return selectData(dataset, SELECT_DAYS_WITH_WATCH_BATTERY_LEVELS, SOSA, AIOT_P2, SCHEMA);
	}

	/**
	 * Method returns set of timestamps when the alarm was triggered
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of timestamps when the alarm was triggered
	 */
	private ResultSet selectDaysWithAlarm(final Dataset dataset) {
		return selectData(dataset, SELECT_DAYS_WITH_ALARM, SOSA, AIOT_P2, SCHEMA);
	}

	/**
	 * Method returns set of timestamps with respective locations in the BIM model
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of timestamps with respective locations in the BIM model
	 */
	private ResultSet selectDaysWithLocations(final Dataset dataset) {
		return selectData(dataset, SELECT_DAYS_WITH_LOCATIONS, SOSA, AIOT_P2, MEASURE, SCHEMA);
	}

	/**
	 * Method returns set of unique tag identifiers
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of unique tag identifiers
	 */
	private ResultSet selectUniqueIdentifiers(final Dataset dataset) {
		return selectData(dataset, SELECT_UNIQUE_IDENTIFIERS, SCHEMA);
	}

	/**
	 * Method returns number of times when the watch was connected and not connected
	 *
	 * @param dataset dataset on which query is to be executed
	 * @return set of timestamps with the status of connection of the watch
	 */
	private ResultSet selectDaysWithWatchConnected(final Dataset dataset) {
		return selectData(dataset, SELECT_DAYS_WITH_WATCH_CONNECTED, SOSA, AIOT_P2, SCHEMA);
	}

	/**
	 * Method executes the specifies on the dataset
	 *
	 * @param dataset dataset on which query is to be executed
	 * @param query query that will be executed on the dataset
	 * @param prefixes prefixes that will be used in the query
	 * @return output of the query
	 */
	private ResultSet selectData(final Dataset dataset, final String query, final PrefixType... prefixes) {
		final Prologue prologue = createPrefixPrologue(prefixes);
		final QueryExecution queryExecution = createQueryFromNamedModels(dataset, query, prologue);
		return queryExecution.execSelect();
	}
}