package org.iotdata.domain.function.tags;

import static org.apache.jena.sparql.expr.NodeValue.makeDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase3;
import org.iotdata.domain.function.CustomFunction;

/**
 * Abstract event class used implemented by MapAlarmEvent, MapDisconnectedEvent and MapAbnormalHeartRateEvent
 */
public abstract class MapAbstractEvent extends FunctionBase3 implements CustomFunction {
	final ConcurrentHashMap<String, XMLGregorianCalendar> startTimeMap;
	final AtomicReference<XMLGregorianCalendar> startTime;

	protected MapAbstractEvent(final Object ...params) {
		super();
		this.startTimeMap = (ConcurrentHashMap<String, XMLGregorianCalendar>) params[0];
		this.startTime = (AtomicReference<XMLGregorianCalendar>) params[1];
	}

	@Override
	public CustomFunction constructInitialized() {
		return this;
	}

	@Override
	public NodeValue exec(final NodeValue identifier, final NodeValue timeStamp, final NodeValue status) {
		boolean keyExists = startTimeMap.containsKey(identifier.getString());
		boolean isStatus = status.getBoolean();

		startTime.set(timeStamp.getDateTime());

		if (!keyExists && isStatus) {
			startTimeMap.put(identifier.getString(), timeStamp.getDateTime());
		} else if (keyExists && !isStatus) {
			startTime.set(startTimeMap.get(identifier.getString()));
			startTimeMap.remove(identifier.getString());
		}

		return makeDateTime(startTime.get());
	}
}