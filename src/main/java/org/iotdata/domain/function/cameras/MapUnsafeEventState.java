package org.iotdata.domain.function.cameras;

import static java.lang.Math.abs;
import static java.time.Duration.between;
import static java.util.Objects.nonNull;
import static org.apache.jena.sparql.expr.NodeValue.makeString;
import static org.iotdata.enums.EventStateType.FINISH_EVENT;
import static org.iotdata.enums.EventStateType.NO_EVENT;
import static org.iotdata.enums.EventStateType.ONGOING;
import static org.iotdata.enums.EventStateType.START_EVENT;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntPredicate;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.iotdata.domain.function.CustomFunction;
import org.iotdata.enums.EventStateType;

/**
 * Function used to define the event state of measurements indicating that unsafe workers were detected.
 *
 * <p> NO_EVENT - measurement is not a part of event of interest. </p>
 * <p> START_EVENT - measurement detecting some unsafe workers that begins a series of consecutive measurements within event of interest. </p>
 * <p> ONGOING - measurement being in the middle of a series of consecutive measurements within event of interest. </p>
 * <p> FINISH_EVENT - last measurement in a series of consecutive measurements within event of interest. </p>
 */
public class MapUnsafeEventState extends FunctionBase2 implements CustomFunction {

	static final IntPredicate noOngoingEvent = indicator -> indicator == 0;

	final AtomicInteger eventIndicator;
	final AtomicReference<EventStateType> eventStatus;
	final AtomicReference<Instant> previousMeasurementTime;

	public MapUnsafeEventState(final Object... params) {
		super();
		this.eventIndicator = (AtomicInteger) params[0];
		this.eventStatus = (AtomicReference<EventStateType>) params[1];
		this.previousMeasurementTime = (AtomicReference<Instant>) params[2];
	}

	@Override
	public String getName() {
		return "mapUnsafeWorkersEventState";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new MapUnsafeEventState(eventIndicator, eventStatus, previousMeasurementTime);
	}

	@Override
	public NodeValue exec(final NodeValue numberOfUnsafeWorkers, final NodeValue time) {
		final boolean isUnsafe = numberOfUnsafeWorkers.getInteger().intValue() > 0;

		validateMeasurementTime(time);
		if (isUnsafe) {
			processUnsafeWorkerDetected();
		} else if (!noOngoingEvent.test(eventIndicator.get())) {
			processOngoingEvent();
		} else {
			eventStatus.set(NO_EVENT);
		}

		return makeString(eventStatus.get().name());
	}

	private void validateMeasurementTime(final NodeValue time) {
		final Instant measurementTime = time.getDateTime().toGregorianCalendar().getTime().toInstant();
		final boolean isMeasurementObsolete = nonNull(previousMeasurementTime.get())
				&& abs(between(measurementTime, previousMeasurementTime.get()).toHours()) > 3;

		// check if time difference between measurements is too large to be taken into account within the same series
		if (isMeasurementObsolete) {
			eventIndicator.set(0);
			eventStatus.set(NO_EVENT);
		}
		previousMeasurementTime.set(measurementTime);
	}

	private void processUnsafeWorkerDetected() {
		// check if measurement indicates start of a new series
		if (noOngoingEvent.test(eventIndicator.get())) {
			eventStatus.set(START_EVENT);
		}
		eventIndicator.set(2);
	}

	private void processOngoingEvent() {
		eventIndicator.decrementAndGet();
		eventStatus.set(ONGOING);

		// check if current measurement finishes the series
		if (noOngoingEvent.test(eventIndicator.get())) {
			eventStatus.set(FINISH_EVENT);
		}
	}
}
