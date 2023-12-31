package org.iotdata.domain.analyzer;

import org.apache.jena.query.Dataset;

/**
 * Abstract class that is to be extended by dataset analyzers
 */
public interface AbstractAnalyzer {

	/**
	 * Method performs full analysis of the selected data set
	 *
	 * @param dataset dataset that is being analysed
	 */
	void performAnalysis(final Dataset dataset, final String outputPath);
}
