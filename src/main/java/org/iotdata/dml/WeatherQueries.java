package org.iotdata.dml;

/**
 * Class containing queries used to analyse tags dataset
 */
public class WeatherQueries {
    public static final String SELECT_WIND_SPEED = """
			SELECT ?timeStamp ?value ?unit ?convertedValue ?convertedUnit
			WHERE {
			?observation a sosa:Observation ;
			    sosa:observedProperty ?property ;
			    sosa:resultTime ?timeStamp .
			    
			?property a sosa:ObservableProperty ;
			    a aiotp2:WindSpeed .
			
			?measure a aiot:MeasureResult ;
			    sosa:isResultOf ?observation ;
			    msr:hasUnit ?unit ;
			    msr:hasNumericalValue ?value .
			 
			BIND(?value * 0.4474 AS ?convertedValue)
			BIND(msr:metrePerSecond-Time as ?convertedUnit)
			}
			""";

	public static final String SELECT_UV_INDEX = """
			SELECT ?timeStamp ?value ?unit
			WHERE {
			?observation a sosa:Observation ;
			    sosa:observedProperty ?property ;
			    sosa:resultTime ?timeStamp .
			    
			?property a sosa:ObservableProperty ;
			    a aiotp2:UvIndex .
			
			?measure a aiot:MeasureResult ;
			    sosa:isResultOf ?observation ;
			    msr:hasUnit ?unit ;
			    msr:hasNumericalValue ?value .
			}
			""";

	public static final String SELECT_TEMPERATURE = """
			SELECT ?timeStamp ?value ?unit ?convertedValue ?convertedUnit
			WHERE {
			?observation a sosa:Observation ;
			    sosa:observedProperty ?property ;
			    sosa:resultTime ?timeStamp .
			    
			?property a sosa:ObservableProperty ;
			    a msr:FahrenheitTemperature .
			
			?measure a aiot:MeasureResult ;
			    sosa:isResultOf ?observation ;
			    msr:hasUnit ?unit ;
			    msr:hasNumericalValue ?value .
					 
			BIND(ROUND((?value - 32) / 1.8) AS ?convertedValue)
			BIND(msr:degreeCelsius as ?convertedUnit)
			}
			""";

	public static final String SELECT_PRESSURE = """
			SELECT ?timeStamp ?value ?unit ?convertedValue
			WHERE {
			?observation a sosa:Observation ;
			    sosa:observedProperty ?property ;
			    sosa:resultTime ?timeStamp .
			    
			?property a sosa:ObservableProperty ;
			    a aiotp2:AtmosphericPressure .
			
			?measure a aiot:MeasureResult ;
			    sosa:isResultOf ?observation ;
			    msr:hasUnit ?unit ;
			    msr:hasNumericalValue ?value .
			    
			BIND(ROUND(?value * 33.86389) AS ?convertedValue)
			}
			""";

	public static final String SELECT_HUMIDITY = """
			SELECT ?timeStamp ?value ?unit
			WHERE {
			?observation a sosa:Observation ;
			    sosa:observedProperty ?property ;
			    sosa:resultTime ?timeStamp .
			    
			?property a sosa:ObservableProperty ;
			    a msr:RelativeHumidity .
			
			?measure a aiot:MeasureResult ;
			    sosa:isResultOf ?observation ;
			    msr:hasUnit ?unit ;
			    msr:hasNumericalValue ?value .
			}
			""";
}
