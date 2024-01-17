package org.iotdata.domain.function.cameras.unsafeevent;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.jena.sparql.expr.NodeValue.makeInteger;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to count the number of workers of specific type detected by the camera
 */
public class CountWorkersPerSafety extends FunctionBase2 implements CameraAggregationFunction {

	final AtomicLong currentWorkersCount;
	final String counterTypeName;

	public CountWorkersPerSafety(final Object... params) {
		super();
		this.currentWorkersCount = (AtomicLong) params[0];
		this.counterTypeName = (String) params[1];
	}

	@Override
	public String getName() {
		return "countWorkers" + capitalize(counterTypeName);
	}

	@Override
	public CustomFunction constructInitialized() {
		return new CountWorkersPerSafety(currentWorkersCount, counterTypeName);
	}

	@Override
	public NodeValue executeForOngoingEvent(final Object... params) {
		return makeInteger(currentWorkersCount.addAndGet((long) params[0]));
	}

	@Override
	public NodeValue executeForNoEvent(final Object... params) {
		return makeInteger(0);
	}

	@Override
	public NodeValue executeForStartEvent(final Object... params) {
		final long workersNumber = (long) params[0];
		currentWorkersCount.set(workersNumber);
		return makeInteger(workersNumber);
	}

	@Override
	public NodeValue executeForFinishEvent(final Object... params) {
		final long workersNumber = (long) params[0];
		final NodeValue finalWorkersCount = makeInteger(currentWorkersCount.addAndGet(workersNumber));
		currentWorkersCount.set(0);
		return finalWorkersCount;
	}

	@Override
	public NodeValue exec(final NodeValue eventState, final NodeValue workersNumberNode) {
		final long workersNumber = workersNumberNode.getInteger().longValue();
		return execute(eventState, workersNumber);
	}
}
