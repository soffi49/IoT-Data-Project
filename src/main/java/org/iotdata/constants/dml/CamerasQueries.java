package org.iotdata.constants.dml;

/**
 * Class containing queries used to analyse camera dataset
 */
public class CamerasQueries {

	public static final String SELECT_AVERAGE_VALUES = """
			SELECT (MIN(?timeStamp) as ?startTime)
			       (MAX(?timeStamp) as ?endTime)
			       (AVG(?unsafeWorkers) as ?unsafeWorkersAvg)
			       (AVG(?safeWorkers) as ?safeWorkersAvg)
			       (AVG(?indeterminateWorkers) as ?indeterminateWorkersAvg)
			       (AVG(?confidence) as ?confidenceAvg)
			WHERE {
			?sub sosa:resultTime ?timeStamp ;
			     sosa:hasResult ?resultNode.

			?resultNode aiotp2:hasConfidence ?confidence;
			            aiotp2:unsafeWorkersNum ?unsafeWorkers;
			            aiotp2:safeWorkersNum ?safeWorkers;
			            aiotp2:indeterminateWorkersNum ?indeterminateWorkers.
			}
			""";

	public static final String SELECT_AVERAGE_VALUES_NONZERO_CONFIDENCE = """
			SELECT (MIN(?timeStamp) as ?startTime)
			       (MAX(?timeStamp) as ?endTime)
			       (AVG(?confidence) as ?confidenceAvg)
			WHERE {
			?sub sosa:resultTime ?timeStamp ;
			     sosa:hasResult ?resultNode.

			?resultNode aiotp2:hasConfidence ?confidence;
			            aiotp2:unsafeWorkersNum ?unsafeWorkers;
			            aiotp2:safeWorkersNum ?safeWorkers;
			            aiotp2:indeterminateWorkersNum ?indeterminateWorkers.
			            
			FILTER(?confidence != 0 || 
			       ?unsafeWorkers != 0 || 
			       ?safeWorkers != 0 || 
			       ?indeterminateWorkers != 0)
			}
			""";

	public static final String SELECT_CONFIDENCE_PER_OBSERVATION_TYPE = """
			SELECT ?confidenceAvg
			       ?confidenceForUnsafeWorkers
			       ?confidenceSafeWorkers
			       ?confidenceIndeterminateWorkers
			       ?confidenceNoneObserved
			       ?confidenceLevel
			       ?time
			WHERE {
			    {
			        SELECT (AVG(?confidence) as ?confidenceAvg)
			               (SAMPLE(?resultNode) as ?node)
			               (MIN(?timeStamp) as ?time)
			        WHERE {
			        ?sub sosa:resultTime ?timeStamp ;
			             sosa:hasResult ?resultNode.
			        
			        ?resultNode aiotp2:hasConfidence ?confidence.
			        }
			    }
			?node aiotp2:unsafeWorkersNum ?unsafeWorkers;
			      aiotp2:safeWorkersNum ?safeWorkers;
			      aiotp2:indeterminateWorkersNum ?indeterminateWorkers.
			            
			BIND(IF(?unsafeWorkers > 0, 1, 0) as ?confidenceForUnsafeWorkers)
			BIND(IF(?safeWorkers > 0, 1, 0) as ?confidenceSafeWorkers)
			BIND(IF(?indeterminateWorkers > 0, 1, 0) as ?confidenceIndeterminateWorkers)
			BIND(IF(?unsafeWorkers = 0 && ?safeWorkers = 0 && ?indeterminateWorkers = 0, 1, 0) as ?confidenceNoneObserved)
			BIND (
			  COALESCE(
			    IF(?confidenceAvg >= 85, "HIGH", 1/0),
			    IF(?confidenceAvg >= 50, "MEDIUM", 1/0),
			    IF(?confidenceAvg != 0, "LOW", 1/0),
			    "NONE"
			  ) AS ?confidenceLevel
			)
			}
			""";

	public static final String SELECT_CONSECUTIVE_UNSAFE_WORKERS_EVENTS = """
			SELECT ?eventStart ?eventFinish ?duration ?eventLength ?unsafeWorkersSum ?safeWorkersSum ?maxUnsafeWorkers
			WHERE {
			    {
			      SELECT ?measurementTime
			             ?eventState
			             (func:mapUnsafeEventLength(?eventState) as ?eventLength)
			             (func:mapUnsafeEventStartTime(?eventState, ?measurementTime) as ?initialStart)
			             (func:maxUnsafeWorkers(?eventState, ?unsafeWorkers) as ?maxUnsafeWorkers)
			             (func:countWorkersUnsafe(?eventState, ?unsafeWorkers) as ?unsafeWorkersSum)
			             (func:countWorkersSafe(?eventState, ?safeWorkers) as ?safeWorkersSum)
			      WHERE {
			       ?sub sosa:resultTime ?measurementTime ;
			            sosa:hasResult ?resultNode.
			     
			       ?resultNode aiotp2:hasSafetyStatus ?status ;
			                   aiotp2:unsafeWorkersNum ?unsafeWorkers ;
			                   aiotp2:safeWorkersNum ?safeWorkers.
			                   
			       BIND(func:mapUnsafeWorkersEventState(?unsafeWorkers, ?measurementTime) as ?eventState)
			       }
			       ORDER BY ?measurementTime
			    }
			    FILTER(STR(?eventState)="FINISH_EVENT")
			    FILTER(?eventLength > 3)
			    
			    BIND(IF(?measurementTime < ?initialStart, ?initialStart, ?measurementTime) as ?eventFinish)
			    BIND(IF(?measurementTime < ?initialStart, ?measurementTime, ?initialStart) as ?eventStart)
			    BIND((?eventFinish - ?eventStart) as ?duration)
			}
			""";

	public static final String SELECT_CONSECUTIVE_INDETERMINATE_WORKERS_EVENTS = """
			SELECT ?measurementTime ?indeterminateWorkersCount ?eventLength
			WHERE {
			    {
			      SELECT ?measurementTime
			             ?eventLength
			             (func:countIndeterminateWorkers(?eventLength, ?indeterminateWorkers) as ?indeterminateWorkersCount)
			      WHERE {
			       ?sub sosa:resultTime ?measurementTime ;
			            sosa:hasResult ?resultNode.
			            		     
			       ?resultNode aiotp2:indeterminateWorkersNum ?indeterminateWorkers.
			                   
			       BIND(func:mapIndeterminateWorkersEventLength(?indeterminateWorkers, ?measurementTime) as ?eventLength)
			       }
			       ORDER BY ?measurementTime
			    }
			    FILTER(?eventLength > 3)
			}
			""";

}
