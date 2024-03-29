package org.iotdata.enums;

/**
 * Types of global parameters used during the analysis of individual data sets
 */
public enum ArgumentType {

	//	GLOBAL PARAMETERS OF CAMERAS ANALYSIS
	UNSAFE_WORKERS_EVENT_LENGTH,
	UNSAFE_WORKERS_EVENT_START_TIME,
	UNSAFE_WORKERS_EVENT_INDICATOR,
	UNSAFE_WORKERS_EVENT_STATUS,
	UNSAFE_WORKERS_EVENT_UNSAFE_MAX,
	UNSAFE_WORKERS_EVENT_UNSAFE_COUNT,
	UNSAFE_WORKERS_EVENT_SAFE_COUNT,
  INDETERMINATE_WORKERS_EVENT_LENGTH,
	INDETERMINATE_WORKERS_PREV_MEASUREMENT,
	INDETERMINATE_WORKERS_WORKERS_COUNT,
	PREV_MEASUREMENT_TIME,

	DISCONNECTED_WATCHES,
	DISCONNECTED_WATCHES_START_TIME,
	ABNORMAL_HEART_RATE,
	ABNORMAL_HEART_RATE_START_TIME,
	ALARM_TRIGGER,
	ALARM_TRIGGER_START_TIME
}
