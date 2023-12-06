package org.iotdata.dml;

/**
 * Class containing queries used to analyse camera dataset
 */
public class CamerasQueries {

	public static final String SELECT_DAYS_WITH_UNSAFE_WORKERS = """
			SELECT ?timeStamp ?unsafeWorkersNum
			WHERE {
			?sub sosa:resultTime ?timeStamp ;
			     sosa:hasResult ?resultNode.
		
			?resultNode iot:unsafeWorkersNum ?unsafeWorkersNum.
			
			FILTER(?unsafeWorkersNum > 0)
			}
			ORDER BY DESC(?unsafeWorkersNum)
			""";
}
