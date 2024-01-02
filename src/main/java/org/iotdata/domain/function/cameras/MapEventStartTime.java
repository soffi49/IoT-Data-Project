package org.iotdata.domain.function.cameras;

import static org.apache.jena.sparql.expr.NodeValue.makeDateTime;
import static org.apache.jena.sparql.expr.NodeValue.xmlDatatypeFactory;

import java.util.concurrent.atomic.AtomicReference;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.iotdata.domain.function.CustomFunction;
import org.iotdata.enums.EventStateType;

/**
 * Function used to map the time of first measurement after which consecutive measurements
 * indicating unsafe workers were detected
 */
public class MapEventStartTime extends FunctionBase2 implements CustomFunction {

	private static final AtomicReference<XMLGregorianCalendar> startTime = new AtomicReference<>(
			xmlDatatypeFactory.newXMLGregorianCalendar());

	public MapEventStartTime() {
		super();
	}

	public String getName() {
		return "mapEventStartTime";
	}

	@Override
	public NodeValue exec(final NodeValue eventState, final NodeValue measurementTime) {
		final XMLGregorianCalendar time = measurementTime.getDateTime();

		return switch (EventStateType.valueOf(eventState.asString())) {
			case START_EVENT -> startEvent(time);
			case ONGOING -> makeDateTime(startTime.get());
			case FINISH_EVENT -> finishEvent();
			case NO_EVENT -> makeDateTime(time);
		};
	}

	private NodeValue startEvent(final XMLGregorianCalendar measurementTime) {
		startTime.set(measurementTime);
		return makeDateTime(measurementTime);
	}

	private NodeValue finishEvent() {
		final NodeValue eventStart = makeDateTime(startTime.get());
		startTime.set(xmlDatatypeFactory.newXMLGregorianCalendar());
		return eventStart;
	}
}
