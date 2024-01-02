package org.iotdata.domain.function.cameras;

import static org.apache.jena.sparql.expr.NodeValue.makeInteger;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.iotdata.domain.function.CustomFunction;
import org.iotdata.enums.EventStateType;

/**
 * Function used to aggregate consecutive measurements which indicate that unsafe workers were detected
 */
public class MapEventLength extends FunctionBase1 implements CustomFunction {

	final AtomicInteger eventLength = new AtomicInteger(0);

	public MapEventLength() {
		super();
	}

	public String getName() {
		return "mapEventLength";
	}

	@Override
	public NodeValue exec(final NodeValue eventState) {

		return switch (EventStateType.valueOf(eventState.asString())) {
			case START_EVENT -> makeInteger(1);
			case ONGOING -> makeInteger(eventLength.incrementAndGet());
			case FINISH_EVENT -> finishEvent();
			case NO_EVENT -> makeInteger(0);
		};
	}

	private NodeValue finishEvent() {
		final NodeValue finalLength = makeInteger(eventLength.incrementAndGet());
		eventLength.set(0);
		return finalLength;
	}
}
