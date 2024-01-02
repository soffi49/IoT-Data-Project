package org.iotdata.domain.analyzer;

import static org.iotdata.constants.dml.TagsQueries.SELECT_DAYS_WITH_ALARM;
import static org.iotdata.constants.dml.TagsQueries.SELECT_DAYS_WITH_HEART_RATE;
import static org.iotdata.constants.dml.TagsQueries.SELECT_DAYS_WITH_LOCATIONS;
import static org.iotdata.constants.dml.TagsQueries.SELECT_DAYS_WITH_WATCH_BATTERY_LEVELS;
import static org.iotdata.constants.dml.TagsQueries.SELECT_DAYS_WITH_WATCH_CONNECTED;
import static org.iotdata.constants.dml.TagsQueries.SELECT_UNIQUE_IDENTIFIERS;
import static org.iotdata.enums.PrefixType.AIOT_P2;
import static org.iotdata.enums.PrefixType.MEASURE;
import static org.iotdata.enums.PrefixType.SCHEMA;
import static org.iotdata.enums.PrefixType.SOSA;
import static org.iotdata.utils.QueryExecutor.executeQuery;
import static org.iotdata.utils.OutputWriter.storeResultsInSeparateFiles;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ResultSet;

/**
 * Class containing methods used to analyse the tags datasets
 */
public class TagsAnalyzer extends AbstractAnalyzer {

	public TagsAnalyzer(final String outputPath) {
		super(outputPath);
	}

	@Override
	public void performAnalysis(final Dataset dataset) {
		super.performAnalysis(dataset);

		final int index = indexOfSingularResultsFile.get();
		storeResultsInSeparateFiles("results_identifier", selectUniqueIdentifiers(dataset), outputPath, index);
		storeResultsInSeparateFiles("results_days_watch_connected", selectDaysWithWatchConnected(dataset), outputPath,
				index);
		storeResultsInSeparateFiles("results_days_heart_rate", selectDaysWithHeartRate(dataset), outputPath, index);
		storeResultsInSeparateFiles("results_days_watch_battery_level", selectDaysWithWatchBatteryLevels(dataset),
				outputPath, index);
		storeResultsInSeparateFiles("result_days_alarm", selectDaysWithAlarm(dataset), outputPath, index);
		storeResultsInSeparateFiles("result_days_locations", selectDaysWithLocations(dataset), outputPath, index);
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