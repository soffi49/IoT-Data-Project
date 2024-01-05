package org.iotdata.domain.function.cameras.unsafeevent;

import org.apache.jena.sparql.expr.NodeValue;
import org.iotdata.domain.function.CustomFunction;
import org.iotdata.enums.EventStateType;

/**
 * Interface used to define custom ARQ cameras' expression functions
 */
public interface CameraAggregationFunction extends CustomFunction {

	/**
	 * Method executed when the measurement indicating ongoing event is being processed
	 *
	 * @param params input node values
	 * @return node value after processing ongoing event measurement
	 */
	NodeValue executeForOngoingEvent(final Object... params);

	/**
	 * Method executed when the measurement indicating no event is being processed
	 *
	 * @param params input node values
	 * @return node value after processing empty event measurement
	 */
	NodeValue executeForNoEvent(final Object... params);

	/**
	 * Method executed when the measurement indicating started event is being processed
	 *
	 * @param params input node values
	 * @return node value after processing measurement of started event
	 */
	NodeValue executeForStartEvent(final Object... params);

	/**
	 * Method executed when the measurement indicating finished event is being processed
	 *
	 * @param params input node values
	 * @return node value after processing measurement of finished event
	 */
	NodeValue executeForFinishEvent(final Object... params);

	/**
	 * Method executed when the next observation is being processed
	 *
	 * @param eventState current event state
	 * @param params     input node values
	 * @return output node value
	 */
	default NodeValue execute(final NodeValue eventState, final Object... params) {
		return switch (EventStateType.valueOf(eventState.asString())) {
			case START_EVENT -> executeForStartEvent(params);
			case ONGOING -> executeForOngoingEvent(params);
			case FINISH_EVENT -> executeForFinishEvent(params);
			case NO_EVENT -> executeForNoEvent(params);
		};
	}

}
