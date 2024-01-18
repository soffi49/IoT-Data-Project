package org.iotdata.domain.function.tags;

import org.apache.jena.sparql.expr.NodeValue;

/**
 * Function used to detect alarm events
 */
public class MapAlarmEvent extends TagEventMapper {
	public MapAlarmEvent(final Object ...params) {
		super(params);
	}

	@Override
	public String getName() {
		return "mapAlarmEvent";
	}

	@Override
	public boolean isObservationWithinEvent(final NodeValue observationParameter) {
		return observationParameter.getBoolean();
	}
}
