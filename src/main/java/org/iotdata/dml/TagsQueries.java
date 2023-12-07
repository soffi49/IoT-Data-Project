package org.iotdata.dml;

/**
 * Class containing queries used to analyse tags dataset
 */
public class TagsQueries {
	public static final String SELECT_UNIQUE_IDENTIFIERS = """
			SELECT DISTINCT ?identifier
			WHERE
			{?subject schema:identifier ?identifier}
			""";

	public static final String SELECT_DAYS_WITH_WATCH_CONNECTED = """
			SELECT ?timeStamp ?isConnected
			WHERE {
			?subject a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a iot:TagMetadataResult ;
				iot:hasWatchConnected ?isConnected .
			}
			""";

	public static final String SELECT_DAYS_WITH_HEART_RATE = """
			SELECT ?timeStamp ?heartRate
			WHERE {
			?subject a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a msr:Measure ;
				msr:Unit iot:bpm ;
				msr:hasNumericalValue ?heartRate .
			}
			""";

	public static final String SELECT_DAYS_WITH_WATCH_BATTERY_LEVELS = """
			SELECT ?timeStamp ?batteryLevel
			WHERE {
			?subject a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a iot:TagMetadataResult ;
				iot:hasWatchBatteryLevel ?batteryLevel .
			}
			""";

	public static final String SELECT_DAYS_WITH_ALARM = """
			SELECT ?timeStamp ?alarm
			WHERE {
			?subject a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a iot:TagMetadataResult ;
				iot:hasAlarm ?alarm .
			
			FILTER (?alarm = true)
			}
			""";

	public static final String SELECT_DAYS_WITH_LOCATIONS = """
			SELECT ?timeStamp ?xValue ?yValue ?zValue ?accuracyValue
			WHERE {
			?subject a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a iot:BIMLocation ;
				iot:hasXValue ?xValueNode ;
				iot:hasYValue ?yValueNode ;
				iot:hasZValue ?zValueNode ;
				iot:hasAccuracyValue ?accuracyValueNode .
				
			?xValueNode a msr:Measure ;
				msr:Unit msr:millimetre ;
				msr:hasNumericalValue ?xValue .
				
			?yValueNode a msr:Measure ;
				msr:Unit msr:millimetre ;
				msr:hasNumericalValue ?yValue .
				
			?zValueNode a msr:Measure ;
				msr:Unit msr:millimetre ;
				msr:hasNumericalValue ?zValue .
				
			?accuracyValueNode a msr:Measure ;
				msr:Unit msr:millimetre ;
				msr:hasNumericalValue ?accuracyValue .
			}
			""";
}
