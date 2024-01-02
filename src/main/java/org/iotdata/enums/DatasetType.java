package org.iotdata.enums;

import java.util.function.Function;

import org.iotdata.domain.analyzer.AbstractAnalyzer;
import org.iotdata.domain.analyzer.CamerasAnalyzer;
import org.iotdata.domain.analyzer.TagsAnalyzer;

import lombok.Getter;

/**
 * Types of considered datasets
 */
@Getter
public enum DatasetType {

	CAMERAS("cameras", CamerasAnalyzer::new),
	TAGS("tags", TagsAnalyzer::new),
	WEATHER("weather", null);

	final String dirName;
	final Function<String, AbstractAnalyzer> dataAnalyzer;

	DatasetType(final String dirName, final Function<String, AbstractAnalyzer> dataAnalyzer) {
		this.dirName = dirName;
		this.dataAnalyzer = dataAnalyzer;
	}
}
