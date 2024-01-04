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

	public static final String SELECT_CONSECUTIVE_UNSAFE_WORKERS_EVENTS = """
			SELECT ?eventStart ?eventFinish ?duration ?eventLength ?unsafeWorkersSum ?safeWorkersSum ?maxUnsafeWorkers
			WHERE {
			    {
			      SELECT ?measurementTime
			             ?eventState
			             (func:mapEventLength(?eventState) as ?eventLength)
			             (func:mapEventStartTime(?eventState, ?measurementTime) as ?initialStart)
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

}
