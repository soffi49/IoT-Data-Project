package org.iotdata.domain.function.tags;

import static org.apache.jena.sparql.expr.NodeValue.makeDateTime;
import org.apache.jena.sparql.expr.NodeValue;

/**
 * Function used to detect abnormal heart rate events (< 60 and > 100)
 */
public class MapAbnormalHeartRateEvent extends MapAbstractEvent {
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
	public NodeValue exec(final NodeValue identifier, final NodeValue timeStamp, final NodeValue heartRate) {
		boolean keyExists = startTimeMap.containsKey(identifier.getString());
		boolean isAbnormalHeartRate = (heartRate.getInteger().intValue() > HIGH) ||
				(heartRate.getInteger().intValue() < LOW);

		startTime.set(timeStamp.getDateTime());

		if (!keyExists && isAbnormalHeartRate) {
			startTimeMap.put(identifier.getString(), timeStamp.getDateTime());
		} else if (keyExists && !isAbnormalHeartRate) {
			startTime.set(startTimeMap.get(identifier.getString()));
			startTimeMap.remove(identifier.getString());
		}

		return makeDateTime(startTime.get());
	}
}
