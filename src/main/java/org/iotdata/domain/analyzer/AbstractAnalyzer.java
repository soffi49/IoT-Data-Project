package org.iotdata.domain.analyzer;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.query.Dataset;

/**
 * Abstract class that is to be extended by dataset analyzers
 */
public abstract class AbstractAnalyzer {

	protected final AtomicInteger indexOfSingularResultsFile = new AtomicInteger(0);
	protected final String outputPath;

	/**
	 * Default constructor
	 *
	 * @param outputPath path under which the results are to be stored
	 */
	protected AbstractAnalyzer(final String outputPath) {
		this.outputPath = outputPath;
	}

	/**
	 * Method performs full analysis of the selected data set
	 *
	 * @param dataset dataset that is being analysed
	 */
	public void performAnalysis(final Dataset dataset) {
		indexOfSingularResultsFile.incrementAndGet();
	}
}
