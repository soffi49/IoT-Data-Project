package org.iotdata.enums;

import org.iotdata.analysis.AbstractAnalyzer;
import org.iotdata.analysis.CamerasAnalyzer;
import org.iotdata.analysis.TagsAnalyzer;

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
