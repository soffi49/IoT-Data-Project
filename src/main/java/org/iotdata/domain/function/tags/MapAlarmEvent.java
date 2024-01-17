package org.iotdata.domain.function.tags;

import static org.apache.jena.sparql.expr.NodeValue.makeDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase3;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to detect alarm events
 */
public class MapAlarmEvent extends FunctionBase3 implements CustomFunction {
	final ConcurrentHashMap<String, XMLGregorianCalendar> alarmStartTime;
	final AtomicReference<XMLGregorianCalendar> startTime;

	public MapAlarmEvent(final Object ...params) {
		super();
		this.alarmStartTime = (ConcurrentHashMap<String, XMLGregorianCalendar>) params[0];
		this.startTime = (AtomicReference<XMLGregorianCalendar>) params[1];
	}

	@Override
	public String getName() {
		return "mapAlarmEvent";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new MapAlarmEvent(alarmStartTime, startTime);
	}

	@Override
	public NodeValue exec(final NodeValue identifier, final NodeValue timeStamp, final NodeValue alarmStatus) {
		boolean keyExists = alarmStartTime.containsKey(identifier.getString());
		boolean isAlarm = alarmStatus.getBoolean();

		startTime.set(timeStamp.getDateTime());

		if (!keyExists && isAlarm) {
			alarmStartTime.put(identifier.getString(), timeStamp.getDateTime());
		} else if (keyExists && !isAlarm) {
			startTime.set(alarmStartTime.get(identifier.getString()));
			alarmStartTime.remove(identifier.getString());
		}

		return makeDateTime(startTime.get());
	}
}
