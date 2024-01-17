package org.iotdata.domain.function.tags;

/**
 * Function used to detect disconnected events
 */
public class MapDisconnectedEvent extends MapAbstractEvent {
	public MapDisconnectedEvent(final Object ...params) {
		super(params);
	}

	@Override
	public String getName() {
		return "mapDisconnectedEventTime";
	}
}