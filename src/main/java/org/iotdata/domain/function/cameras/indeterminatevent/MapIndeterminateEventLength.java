package org.iotdata.domain.function.cameras.indeterminatevent;

import static java.lang.Math.abs;
import static java.time.Duration.between;
import static java.util.Objects.nonNull;
import static org.apache.jena.sparql.expr.NodeValue.makeInteger;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to aggregate indeterminate workers' event duration length.
 * It returns a node that contains length indicating also the state of the event:
 *
 * <p> -2 -> no event is currently ongoing </p>
 * <p> -1 -> event has started </p>
 * <p> 0 -> event is ongoing </p>
 * <p> [event length value] -> event has finished </p>
 */
public class MapIndeterminateEventLength extends FunctionBase2 implements CustomFunction {

	final AtomicInteger eventLength;
	final AtomicReference<Instant> previousMeasurementTime;

	public MapIndeterminateEventLength(final Object... params) {
		super();
		this.eventLength = (AtomicInteger) params[0];
		this.previousMeasurementTime = (AtomicReference<Instant>) params[1];
	}

	@Override
	public String getName() {
		return "mapIndeterminateWorkersEventLength";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new MapIndeterminateEventLength(eventLength, previousMeasurementTime);
	}

	@Override
	public NodeValue exec(final NodeValue numberOfIndeterminateWorkers, final NodeValue time) {
		final boolean isIndeterminate = numberOfIndeterminateWorkers.getInteger().intValue() > 0;
		final NodeValue node;

		validateMeasurementTime(time);

		if (isIndeterminate) {
			node = makeInteger(eventLength.get() == 0 ? -1 : 0);
			eventLength.incrementAndGet();
		} else {
			node = makeInteger(eventLength.get() != 0 ? eventLength.get() : -2);
			eventLength.set(0);
		}
		return node;
	}

	private void validateMeasurementTime(final NodeValue time) {
		final Instant measurementTime = time.getDateTime().toGregorianCalendar().getTime().toInstant();
		final boolean isMeasurementObsolete = nonNull(previousMeasurementTime.get())
				&& abs(between(measurementTime, previousMeasurementTime.get()).toHours()) > 1;

		// check if time difference between measurements is too large to be taken into account within the same series
		if (isMeasurementObsolete) {
			eventLength.set(0);
		}
		previousMeasurementTime.set(measurementTime);
	}
}
