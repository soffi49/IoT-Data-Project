package org.iotdata.constants.dml;

/**
 * Class containing queries used to analyse camera dataset
 */
public class CamerasQueries {

	public static final String SELECT_DAYS_WITH_UNSAFE_WORKERS = """
			SELECT ?timeStamp ?unsafeWorkersNum
			WHERE {
			?sub sosa:resultTime ?timeStamp ;
			     sosa:hasResult ?resultNode.

			?resultNode aiotp2:unsafeWorkersNum ?unsafeWorkersNum.

			FILTER(?unsafeWorkersNum > 0)
			}
			ORDER BY DESC(?unsafeWorkersNum)
			""";

	public static final String SELECT_CONSECUTIVE_UNSAFE_WORKERS_EVENTS = """
			SELECT ?eventStart ?eventFinish ?duration ?unsafeWorkersSum ?safeWorkersSum ?indeterminateWorkersSum
			WHERE {
			    {
			      SELECT ?measurementTime
			             ?eventState
			             (func:mapEventLength(?eventState) as ?eventLength)
			             (func:mapEventStartTime(?eventState, ?measurementTime) as ?eventStart)
			             (func:countWorkers(?eventState, ?unsafeWorkers) as ?unsafeWorkersSum)
			             (func:countWorkers(?eventState, ?safeWorkers) as ?safeWorkersSum)
			             (func:countWorkers(?eventState, ?indeterminateWorkers) as ?indeterminateWorkersSum)
			      WHERE {
			       ?sub sosa:resultTime ?measurementTime ;
			            sosa:hasResult ?resultNode.
			     
			       ?resultNode aiotp2:hasSafetyStatus ?status ;
			                   aiotp2:unsafeWorkersNum ?unsafeWorkers ;
			                   aiotp2:safeWorkersNum ?safeWorkers ;
			                   aiotp2:indeterminateWorkersNum ?indeterminateWorkers.
			                   
			       BIND(func:mapUnsafeWorkersEventState(?status, ?measurementTime) as ?eventState)
			       }
			       ORDER BY ?timeStamp
			    }
			    FILTER(STR(?eventState)="FINISH_EVENT")
			    FILTER(?eventLength > 3)
			    
			    BIND(?measurementTime as ?eventFinish)
			    BIND((?eventFinish - ?eventStart) as ?duration)
			}
			""";

}
