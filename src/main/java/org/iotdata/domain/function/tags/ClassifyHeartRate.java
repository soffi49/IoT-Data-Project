package org.iotdata.domain.function.tags;

import static org.apache.jena.sparql.expr.NodeValue.makeString;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.iotdata.domain.function.CustomFunction;

/**
 * Function used to classify heart rate to 'HIGH' (> 100), 'LOW' (< 60), 'GOOD' (<= 60 <=100)
 */
public class ClassifyHeartRate extends FunctionBase1 implements CustomFunction {

	public ClassifyHeartRate() {
		super();
	}

	@Override
	public String getName() {
		return "classifyHeartRate";
	}

	@Override
	public CustomFunction constructInitialized() {
		return new ClassifyHeartRate();
	}

	@Override
	public NodeValue exec(final NodeValue heartRate) {
		Integer heartRateValue = heartRate.getInteger().intValue();
		String status;
		if (heartRateValue > 100) {
			status = "HIGH";
		} else if (heartRateValue < 60) {
			status = "LOW";
		} else {
			status = "GOOD";
		}

		return makeString(status);
	}
}