package org.iotdata.constants.dml;

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
			SELECT ?timeStamp ?isConnected ?identifier
			WHERE {
			?subject schema:identifier ?identifier ;
				sosa:hosts ?sensor .
				
			?sensor sosa:madeObservation ?observation .
			
			?observation a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a aiotp2:TagMetadataResult ;
				aiotp2:hasWatchConnected ?isConnected .
				
			FILTER (?isConnected = true)
			}
			""";

	public static final String SELECT_DAYS_WITH_HEART_RATE = """
			SELECT ?timeStamp ?heartRate ?identifier
			WHERE {
			?subject schema:identifier ?identifier ;
				sosa:hosts ?sensor .
				
			?sensor sosa:madeObservation ?observation .
			
			?observation a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a msr:Measure ;
				msr:Unit aiotp2:bpm ;
				msr:hasNumericalValue ?heartRate .
			}
			""";

	public static final String SELECT_DAYS_WITH_WATCH_BATTERY_LEVELS = """
			SELECT ?timeStamp ?batteryLevel ?identifier
			WHERE {
			?subject schema:identifier ?identifier ;
				sosa:hosts ?sensor .
				
			?sensor sosa:madeObservation ?observation .
			
			?observation a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a aiotp2:TagMetadataResult ;
				aiotp2:hasWatchBatteryLevel ?batteryLevel .
			}
			""";

	public static final String SELECT_DAYS_WITH_ALARM = """
			SELECT ?timeStamp ?alarm ?identifier
			WHERE {
			?subject schema:identifier ?identifier ;
				sosa:hosts ?sensor .
				
			?sensor sosa:madeObservation ?observation .
			
			?observation a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a aiotp2:TagMetadataResult ;
				aiotp2:hasAlarm ?alarm .
			
			}
			""";

	public static final String SELECT_DAYS_WITH_LOCATIONS = """
			SELECT ?timeStamp ?xValue ?yValue ?zValue ?accuracyValue ?identifier
			WHERE {
				?subject schema:identifier ?identifier ;
				sosa:hosts ?sensor .
				
			?sensor sosa:madeObservation ?observation .
			
			?observation a sosa:Observation ;
				sosa:resultTime ?timeStamp ;
				sosa:hasResult ?resultNode .
				
			?resultNode a aiotp2:BIMLocation ;
				aiotp2:hasXValue ?xValueNode ;
				aiotp2:hasYValue ?yValueNode ;
				aiotp2:hasZValue ?zValueNode ;
				aiotp2:hasAccuracyValue ?accuracyValueNode .
				
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