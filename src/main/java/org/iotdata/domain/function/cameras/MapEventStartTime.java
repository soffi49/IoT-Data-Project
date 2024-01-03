package org.iotdata.domain.function.cameras;

import static org.apache.jena.sparql.expr.NodeValue.makeDateTime;
import static org.iotdata.utils.CalendarInitializer.initializeCalendar;

import java.util.concurrent.atomic.AtomicReference;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to map the time of first measurement after which consecutive measurements
 * indicating series of unsafe workers were detected
 */
public class MapEventStartTime extends FunctionBase2 implements CameraAggregationFunction {

	final AtomicReference<XMLGregorianCalendar> startTime;

	public MapEventStartTime(final Object... params) {
		super();
		this.startTime = (AtomicReference<XMLGregorianCalendar>) params[0];
	}

	@Override
	public String getName() {
		return "mapEventStartTime";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new MapEventStartTime(startTime);
	}

	@Override
	public NodeValue executeForOngoingEvent(final Object... params) {
		return makeDateTime(startTime.get());
	}

	@Override
	public NodeValue executeForNoEvent(final Object... params) {
		return makeDateTime((XMLGregorianCalendar) params[0]);
	}

	@Override
	public NodeValue executeForStartEvent(final Object... params) {
		final XMLGregorianCalendar measurementTime = (XMLGregorianCalendar) params[0];
		startTime.set(measurementTime);
		return makeDateTime(measurementTime);
	}

	@Override
	public NodeValue executeForFinishEvent(final Object... params) {
		final NodeValue eventStart = makeDateTime(startTime.get());
		startTime.set(initializeCalendar.get());
		return eventStart;
	}

	@Override
	public NodeValue exec(final NodeValue eventState, final NodeValue measurementTime) {
		final XMLGregorianCalendar time = measurementTime.getDateTime();
		return execute(eventState, time);
	}
}
