package org.iotdata.domain.function.cameras.indeterminatevent;

import static org.apache.jena.sparql.expr.NodeValue.makeInteger;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to count the number of indeterminate workers during event
 */
public class CountIndeterminateWorkers extends FunctionBase2 implements CustomFunction {

	final AtomicLong currentWorkersCount;

	public CountIndeterminateWorkers(final Object... params) {
		super();
		this.currentWorkersCount = (AtomicLong) params[0];
	}

	@Override
	public String getName() {
		return "countIndeterminateWorkers";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new CountIndeterminateWorkers(currentWorkersCount);
	}

	@Override
	public NodeValue exec(final NodeValue eventLength, final NodeValue workersNumberNode) {
		final long workersNumber = workersNumberNode.getInteger().longValue();
		final long length = eventLength.getInteger().intValue();

		if (length > -1) {
			currentWorkersCount.addAndGet(workersNumber);
			return makeInteger(length == 0 ? 0 : currentWorkersCount.get());
		} else {
			currentWorkersCount.set(0);
			return makeInteger(0);
		}
	}
}
