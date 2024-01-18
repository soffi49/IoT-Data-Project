package org.iotdata.domain.function.tags;

import org.apache.jena.sparql.expr.NodeValue;

/**
 * Function used to detect abnormal heart rate events (< 60 and > 100)
 */
public class MapAbnormalHeartRateEvent extends TagEventMapper {
	static final Integer LOW = 60;
	static final Integer HIGH = 100;

	public MapAbnormalHeartRateEvent(final Object ...params) {
		super(params);
	}

	@Override
	public String getName() {
		return "mapHighHeartRateEventTime";
	}

	@Override
	public boolean isObservationWithinEvent(final NodeValue observationParameter) {
		return (observationParameter.getInteger().intValue() > HIGH) ||
						(observationParameter.getInteger().intValue() < LOW);
	}
}
