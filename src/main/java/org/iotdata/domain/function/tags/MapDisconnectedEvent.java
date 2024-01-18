package org.iotdata.domain.function.tags;

import org.apache.jena.sparql.expr.NodeValue;

/**
 * Function used to detect disconnected events
 */
public class MapDisconnectedEvent extends TagEventMapper {
	public MapDisconnectedEvent(final Object ...params) {
		super(params);
	}

	@Override
	public String getName() {
		return "mapDisconnectedEventTime";
	}

	@Override
	public boolean isObservationWithinEvent(final NodeValue observationParameter) {
		return observationParameter.getBoolean();
	}
}