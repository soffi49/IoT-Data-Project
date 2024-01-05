package org.iotdata.domain.analyzer;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.jena.query.Dataset;
import org.apache.pekko.NotUsed;
import org.apache.pekko.stream.javadsl.Flow;
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
	 * Method prepares flow of analysis used later on in streaming.
	 */
	public List<Flow<Dataset, Dataset, NotUsed>> prepareAnalysisFlows() {
		return initializeAnalysisQueries().stream()
				.map(query -> Flow.of(Dataset.class).map(dataset -> {
					query.accept(dataset);
					return dataset;
				}))
				.toList();
	}

	/**
	 * Method returns list of methods accepting data set and performing partial query analysis
	 */
	public List<Consumer<Dataset>> initializeAnalysisQueries() {
		indexOfSingularResultsFile.incrementAndGet();
		return emptyList();
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
