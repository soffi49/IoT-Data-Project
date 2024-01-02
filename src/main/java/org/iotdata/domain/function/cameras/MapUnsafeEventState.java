package org.iotdata.domain.function.cameras;

import static java.time.Duration.between;
import static java.util.Objects.nonNull;
import static org.apache.jena.sparql.expr.NodeValue.makeString;
import static org.iotdata.enums.EventStateType.FINISH_EVENT;
import static org.iotdata.enums.EventStateType.NO_EVENT;
import static org.iotdata.enums.EventStateType.ONGOING;
import static org.iotdata.enums.EventStateType.START_EVENT;
import static org.iotdata.enums.PrefixType.AIOT_P2;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.iotdata.domain.function.CustomFunction;
import org.iotdata.enums.EventStateType;

/**
 * Function used to define the event state of measurements indicating that unsafe workers were detected.
 *
 * <p> NO_EVENT - measurement is not a part of event of interest. </p>
 * <p> START_EVENT - measurement with "status_unsafe" that begins a series of consecutive measurements within event of interest. </p>
 * <p> ONGOING - measurement being in the middle of a series of consecutive measurements within event of interest. </p>
 * <p> FINISH_EVENT - last measurement in a series of consecutive measurements within event of interest </p>
 */
public class MapUnsafeEventState extends FunctionBase2 implements CustomFunction {

	private static final AtomicInteger cumulativeSum = new AtomicInteger(0);
	private static final AtomicReference<EventStateType> eventStatus = new AtomicReference<>(NO_EVENT);
	private static final AtomicReference<Instant> previousMeasurementTime = new AtomicReference<>(null);
	private static final BooleanSupplier noOngoingEvent = () -> cumulativeSum.get() == 0;

	public MapUnsafeEventState() {
		super();
	}

	public String getName() {
		return "mapUnsafeWorkersEventState";
	}

	@Override
	public NodeValue exec(final NodeValue status, final NodeValue time) {
		final boolean isUnsafe = status.getNode().getURI().equals(AIOT_P2.getUri() + "status_unsafe");

		validateMeasurementTime(time);
		if (isUnsafe) {
			processUnsafeWorkerDetected();
		} else if (!noOngoingEvent.getAsBoolean()) {
			processOngoingEvent();
		} else {
			eventStatus.set(NO_EVENT);
		}

		return makeString(eventStatus.get().name());
	}

	private void validateMeasurementTime(final NodeValue time) {
		final Instant measurementTime = time.getDateTime().toGregorianCalendar().getTime().toInstant();
		final boolean isMeasurementObsolete = nonNull(previousMeasurementTime.get())
				&& between(measurementTime, previousMeasurementTime.get()).toHours() > 3;

		// check if time difference between measurements is too large to be taken into account within the same series
		if (isMeasurementObsolete) {
			cumulativeSum.set(0);
			eventStatus.set(NO_EVENT);
		}
		previousMeasurementTime.set(measurementTime);
	}

	private void processUnsafeWorkerDetected() {
		// check if measurement indicates start of a new series
		if (noOngoingEvent.getAsBoolean()) {
			eventStatus.set(START_EVENT);
		}
		cumulativeSum.set(2);
	}

	private void processOngoingEvent() {
		cumulativeSum.decrementAndGet();
		eventStatus.set(ONGOING);

		// check if current measurement finishes the series
		if (noOngoingEvent.getAsBoolean()) {
			eventStatus.set(FINISH_EVENT);
		}
	}
}
