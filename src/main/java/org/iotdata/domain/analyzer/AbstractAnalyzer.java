package org.iotdata.domain.analyzer;

import static java.util.Collections.emptyMap;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.query.Dataset;
import org.iotdata.enums.ArgumentType;

/**
 * Abstract class that is to be extended by dataset analyzers
 */
public abstract class AbstractAnalyzer {

	protected final AtomicInteger indexOfSingularResultsFile = new AtomicInteger(0);
	protected final String outputPath;
	protected final Map<ArgumentType, Object> globalParameters;

	/**
	 * Default constructor
	 *
	 * @param outputPath path under which the results are to be stored
	 */
	protected AbstractAnalyzer(final String outputPath) {
		this.outputPath = outputPath;
		this.globalParameters = initializeGlobalParams();
	}

	/**
	 * Method performs full analysis of the selected data set
	 *
	 * @param dataset dataset that is being analysed
	 */
	public void performAnalysis(final Dataset dataset) {
		indexOfSingularResultsFile.incrementAndGet();
	}

	/**
	 * Method that can be overwritten to initialize parameters used globally within the analysis.
	 *
	 * @return initialized parameters map
	 */
	protected Map<ArgumentType, Object> initializeGlobalParams() {
		return emptyMap();
	}
}
