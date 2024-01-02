package org.iotdata.domain.function;

import static org.apache.commons.lang3.StringUtils.join;
import static org.iotdata.enums.PrefixType.FUNC;

/**
 * Interface used to define custom ARQ expression functions
 */
public interface CustomFunction {

	/**
	 * @return name of custom function
	 */
	String getName();

	/**
	 * @return full URI of custom function
	 */
	default String getUri() {
		return join(FUNC.getUri(), getName());
	}
}
