package org.iotdata.domain.function.tags;

import static org.apache.jena.sparql.expr.NodeValue.makeDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase3;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to detect disconnected events
 */
public class MapDisconnectedEvent extends FunctionBase3 implements CustomFunction {
	final ConcurrentHashMap<String, XMLGregorianCalendar> disconnectedStartTime;
	final AtomicReference<XMLGregorianCalendar> startTime;

	public MapDisconnectedEvent(final Object ...params) {
		super();
		this.disconnectedStartTime = (ConcurrentHashMap<String, XMLGregorianCalendar>) params[0];
		this.startTime = (AtomicReference<XMLGregorianCalendar>) params[1];
	}

	@Override
	public String getName() {
		return "mapDisconnectedEventTime";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new MapDisconnectedEvent(disconnectedStartTime, startTime);
	}

	@Override
	public NodeValue exec(final NodeValue identifier, final NodeValue timeStamp, final NodeValue connectionStatus) {
		boolean isConnected = connectionStatus.getBoolean();
		boolean keyExists = disconnectedStartTime.containsKey(identifier.getString());

		startTime.set(timeStamp.getDateTime());

		if (!keyExists && !isConnected) {
			disconnectedStartTime.put(identifier.getString(), timeStamp.getDateTime());
		} else if (keyExists && isConnected) {
			startTime.set(disconnectedStartTime.get(identifier.getString()));
			disconnectedStartTime.remove(identifier.getString());
		}

		return makeDateTime(startTime.get());
	}
}
