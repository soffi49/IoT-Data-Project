package org.iotdata.domain.function.cameras;

import static org.apache.jena.sparql.expr.NodeValue.makeInteger;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.iotdata.domain.function.CustomFunction;
import org.iotdata.enums.EventStateType;

/**
 * Function used to count the number of workers of specific type detected by the camera
 */
public class CountWorkers extends FunctionBase2 implements CustomFunction {

	final AtomicLong currentWorkersCount = new AtomicLong(0);

	public CountWorkers() {
		super();
	}

	public String getName() {
		return "countWorkers";
	}

	@Override
	public NodeValue exec(final NodeValue eventState, final NodeValue workersNumberNode) {
		final long workersNumber = workersNumberNode.getInteger().longValue();

		return switch (EventStateType.valueOf(eventState.asString())) {
			case START_EVENT -> startEvent(workersNumber);
			case ONGOING -> makeInteger(currentWorkersCount.addAndGet(workersNumber));
			case FINISH_EVENT -> finishEvent(workersNumber);
			case NO_EVENT -> makeInteger(0);
		};
	}

	private NodeValue startEvent(final long workersNumber) {
		currentWorkersCount.set(workersNumber);
		return makeInteger(workersNumber);
	}

	private NodeValue finishEvent(final long workersNumber) {
		final NodeValue finalWorkersCount = makeInteger(currentWorkersCount.addAndGet(workersNumber));
		currentWorkersCount.set(0);
		return finalWorkersCount;
	}
}
