package org.iotdata.enums;

import org.iotdata.domain.analyzer.AbstractAnalyzer;
import org.iotdata.domain.analyzer.CamerasAnalyzer;
import org.iotdata.domain.analyzer.TagsAnalyzer;

import lombok.Getter;

/**
 * Types of considered datasets
 */
@Getter
public enum DatasetType {

	CAMERAS("cameras", new CamerasAnalyzer()),
	TAGS("tags", new TagsAnalyzer()),
	WEATHER("weather", null);


	final String dirName;
	final AbstractAnalyzer dataAnalyzer;

	DatasetType(final String dirName, final AbstractAnalyzer dataAnalyzer) {
		this.dirName = dirName;
		this.dataAnalyzer = dataAnalyzer;
	}
}
