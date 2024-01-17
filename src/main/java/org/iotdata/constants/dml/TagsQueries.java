package org.iotdata.constants.dml;

/**
 * Class containing queries used to analyse tags dataset
 */
public class TagsQueries {
	public static final String SELECT_DISCONNECTED_WATCH_EVENTS  = """
			SELECT ?identifier
					?startTime
					(?timeStamp AS ?finishTime)
					?duration
			WHERE {
				{
					SELECT ?timeStamp
					   	   ?connectionStatus
					       ?identifier
					WHERE {
					?subject schema:identifier ?identifier ;
						sosa:hosts ?sensor .

					?sensor sosa:madeObservation ?observation .

					?observation a sosa:Observation ;
						sosa:resultTime ?timeStamp ;
						sosa:hasResult ?resultNode .

					?resultNode a aiotp2:TagMetadataResult ;
						aiotp2:hasWatchConnected ?connectionStatus .
					}
				}
				BIND(func:mapDisconnectedEventTime(?identifier, ?timeStamp, ?connectionStatus) as ?startTime)
				BIND((?timeStamp - ?startTime) as ?duration)
				FILTER(?startTime != ?timeStamp && day(?duration) < 1 && SUBSTR(STR(?duration), 1, 3) != "-PT")
			}
			""";

	public static final String SELECT_ABNORMAL_HEART_RATE_EVENTS = """
			SELECT ?identifier
					?startTime
					(?timeStamp AS ?finishTime)
					?duration
			WHERE {
				{
					SELECT ?timeStamp
					   	   ?heartRate
					       ?identifier
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
				}
				BIND(func:mapHighHeartRateEventTime(?identifier, ?timeStamp, ?heartRate) as ?startTime)
				BIND((?timeStamp - ?startTime) as ?duration)
				FILTER(?heartRate != 0 && ?startTime != ?timeStamp && day(?duration) < 1 &&
						SUBSTR(STR(?duration), 1, 3) != "-PT")
			}
			""";

	public static final String SELECT_MAX_MIN_HEART_RATE = """
			SELECT ?identifier ?beginInterval ?endInterval ?minHeartRate ?maxHeartRate ?highHeartRateInd ?lowHeartRateInd
			WHERE {
				{
				SELECT ?identifier
						(MIN(?timeStamp) AS ?beginInterval)
						(MAX(?timeStamp) AS ?endInterval)
						(MIN(?heartRate) AS ?minHeartRate)
						(MAX(?heartRate) AS ?maxHeartRate)
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
							
				FILTER (?heartRate != 0)
				}
				GROUP BY ?identifier
				}
			BIND (
			  COALESCE(
				IF(?maxHeartRate > 100, "BAD", 1/0),
			    "GOOD"
			  ) AS ?highHeartRateInd)
			BIND (
			  COALESCE(
				IF(?minHeartRate < 60, "BAD", 1/0),
			    "GOOD"
			  ) AS ?lowHeartRateInd)
			}
			""";

	public static final String SELECT_ALARM_EVENTS  = """
			SELECT ?identifier
					?startTime
					(?timeStamp AS ?finishTime)
					?duration
			WHERE {
				{
					SELECT ?timeStamp
					   	   ?alarmStatus
					       ?identifier
					WHERE {
					?subject schema:identifier ?identifier ;
						sosa:hosts ?sensor .

					?sensor sosa:madeObservation ?observation .

					?observation a sosa:Observation ;
						sosa:resultTime ?timeStamp ;
						sosa:hasResult ?resultNode .
						
					?resultNode a aiotp2:TagMetadataResult ;
						aiotp2:hasAlarm ?alarmStatus .
					}
				}
				BIND(func:mapAlarmEvent(?identifier, ?timeStamp, ?alarmStatus) as ?startTime)
				BIND((?timeStamp - ?startTime) as ?duration)
				FILTER(?startTime != ?timeStamp && day(?duration) < 1 && SUBSTR(STR(?duration), 1, 3) != "-PT")
			}
			""";

	public static final String SELECT_COUNT_ALARM_TRIGGERS = """
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
			
			FILTER(?alarm = true)
			}
			""";
}