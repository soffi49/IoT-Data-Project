package org.iotdata.domain.function.tags;

import static org.apache.jena.sparql.expr.NodeValue.makeDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase3;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to detect abnormal heart rate events (< 60 and > 100)
 */
public class MapAbnormalHeartRateEvent extends FunctionBase3 implements CustomFunction {
	final ConcurrentHashMap<String, XMLGregorianCalendar> abnormalHeartRateStartTime;
	final AtomicReference<XMLGregorianCalendar> startTime;
	final Integer low = 60;
	final Integer high = 100;

	public MapAbnormalHeartRateEvent(final Object ...params) {
		super();
		this.abnormalHeartRateStartTime = (ConcurrentHashMap<String, XMLGregorianCalendar>) params[0];
		this.startTime = (AtomicReference<XMLGregorianCalendar>) params[1];
	}

	@Override
	public String getName() {
		return "mapHighHeartRateEventTime";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new MapAbnormalHeartRateEvent(abnormalHeartRateStartTime, startTime);
	}

	@Override
	public NodeValue exec(final NodeValue identifier, final NodeValue timeStamp, final NodeValue heartRate) {
		boolean keyExists = abnormalHeartRateStartTime.containsKey(identifier.getString());
		boolean isAbnormalHeartRate = (heartRate.getInteger().intValue() > high) ||
				(heartRate.getInteger().intValue() < low);

		startTime.set(timeStamp.getDateTime());

		if (!keyExists && isAbnormalHeartRate) {
			abnormalHeartRateStartTime.put(identifier.getString(), timeStamp.getDateTime());
		} else if (keyExists && !isAbnormalHeartRate) {
			startTime.set(abnormalHeartRateStartTime.get(identifier.getString()));
			abnormalHeartRateStartTime.remove(identifier.getString());
		}

		return makeDateTime(startTime.get());
	}
}
