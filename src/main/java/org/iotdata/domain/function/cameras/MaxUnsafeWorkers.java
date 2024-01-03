package org.iotdata.domain.function.cameras;

import static java.lang.Math.max;
import static org.apache.jena.sparql.expr.NodeValue.makeInteger;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to count the number of workers of specific type detected by the camera
 */
public class MaxUnsafeWorkers extends FunctionBase2 implements CameraAggregationFunction {

	final AtomicLong currentMaxValue;

	public MaxUnsafeWorkers(final Object... params) {
		super();
		this.currentMaxValue = (AtomicLong) params[0];
	}

	@Override
	public String getName() {
		return "maxUnsafeWorkers";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new MaxUnsafeWorkers(currentMaxValue);
	}

	@Override
	public NodeValue executeForOngoingEvent(final Object... params) {
		final long workersNumber = (long) params[0];
		return makeInteger(currentMaxValue.updateAndGet(currVal -> max(currVal, workersNumber)));
	}

	@Override
	public NodeValue executeForNoEvent(final Object... params) {
		return makeInteger(0);
	}

	@Override
	public NodeValue executeForStartEvent(final Object... params) {
		final long workersNumber = (long) params[0];
		currentMaxValue.set(workersNumber);
		return makeInteger(workersNumber);
	}

	@Override
	public NodeValue executeForFinishEvent(final Object... params) {
		final long workersNumber = (long) params[0];
		final long newMaxValue = currentMaxValue.updateAndGet(currVal -> max(currVal, workersNumber));

		final NodeValue finalWorkersCount = makeInteger(newMaxValue);
		currentMaxValue.set(0);
		return finalWorkersCount;
	}

	@Override
	public NodeValue exec(final NodeValue eventState, final NodeValue workersNumberNode) {
		final long workersNumber = workersNumberNode.getInteger().longValue();
		return execute(eventState, workersNumber);
	}
}
