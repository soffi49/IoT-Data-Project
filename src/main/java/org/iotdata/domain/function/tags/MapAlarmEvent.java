package org.iotdata.domain.function.tags;

/**
 * Function used to detect alarm events
 */
public class MapAlarmEvent extends MapAbstractEvent {
	public MapAlarmEvent(final Object ...params) {
		super(params);
	}

	@Override
	public String getName() {
		return "mapAlarmEvent";
	}
}
