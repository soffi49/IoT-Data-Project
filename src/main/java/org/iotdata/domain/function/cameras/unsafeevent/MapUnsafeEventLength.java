package org.iotdata.domain.function.cameras.unsafeevent;

import static org.apache.jena.sparql.expr.NodeValue.makeInteger;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to aggregate the number of consecutive measurements indicating that series of unsafe workers were detected
 */
public class MapUnsafeEventLength extends FunctionBase1 implements CameraAggregationFunction {

	final AtomicInteger eventLength;

	public MapUnsafeEventLength(final Object... params) {
		super();
		this.eventLength = (AtomicInteger) params[0];
	}

	@Override
	public String getName() {
		return "mapUnsafeEventLength";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new MapUnsafeEventLength(eventLength);
	}

	@Override
	public NodeValue executeForOngoingEvent(final Object... params) {
		return makeInteger(eventLength.incrementAndGet());
	}

	@Override
	public NodeValue executeForNoEvent(final Object... params) {
		return makeInteger(0);
	}

	@Override
	public NodeValue executeForStartEvent(final Object... params) {
		return makeInteger(1);
	}

	@Override
	public NodeValue executeForFinishEvent(final Object... params) {
		final NodeValue finalLength = makeInteger(eventLength.incrementAndGet());
		eventLength.set(0);
		return finalLength;
	}

	@Override
	public NodeValue exec(final NodeValue eventState) {
		return execute(eventState);
	}
}
