package org.iotdata.utils;

import static java.time.Instant.now;
import static org.apache.jena.sparql.expr.NodeValue.xmlDatatypeFactory;

import java.util.function.Supplier;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Default calendar initializer
 */
public class CalendarInitializer {

	/**
	 * Method initialize default calendar to current date
	 */
	public static final Supplier<XMLGregorianCalendar> initializeCalendar =
			() -> xmlDatatypeFactory.newXMLGregorianCalendar(now().toString());
}
