package org.iotdata.utils;

import static java.lang.String.format;
import static org.iotdata.utils.TTLReader.readTTLsFromDirectoryToStream;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.pekko.Done;
import org.apache.pekko.NotUsed;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.stream.FlowShape;
import org.apache.pekko.stream.UniformFanInShape;
import org.apache.pekko.stream.UniformFanOutShape;
import org.apache.pekko.stream.javadsl.Balance;
import org.apache.pekko.stream.javadsl.Flow;
import org.apache.pekko.stream.javadsl.GraphDSL;
import org.apache.pekko.stream.javadsl.Merge;
import org.apache.pekko.stream.javadsl.Source;
import org.iotdata.domain.analyzer.AbstractAnalyzer;
import org.iotdata.enums.DatasetType;
import org.slf4j.Logger;

/**
 * Class contains method used to process given RDF stream
 */
public class RDFStreamProcessing {

	private static final Logger logger = getLogger(RDFStreamProcessing.class);

	private final int maxBatchSize;
	private final DatasetType dataType;
	private final AbstractAnalyzer analyzer;
	private final Source<Path, NotUsed> ttlStream;

	/**
	 * Default constructor
	 *
	 * @param dataType     type of data set that is being processed
	 * @param maxBatchSize maximum size of the batch used in RDF stream processing
	 * @param outputPath   path to the file in which the results of analysis are to be stored
	 * @param inputPath    path to the input files
	 * @apiNote data type is used to select appropriate analysis and pre-processing methods
	 */
	public RDFStreamProcessing(final DatasetType dataType, final int maxBatchSize, final String outputPath,
			final String inputPath) {
		this.ttlStream = readTTLsFromDirectoryToStream(dataType.getDirName(), inputPath);
		this.maxBatchSize = maxBatchSize;
		this.dataType = dataType;
		this.analyzer = dataType.getDataAnalyzer().apply(outputPath);
	}

	/**
	 * Method processes a given RDF stream in the batch manner
	 * (i.e. processing consecutive RDF graphs captured within single sliding window)
	 */
	public void processRDFStream() {
		final ActorSystem system = ActorSystem.create(dataType.name());

		final Flow<Path, List<Model>, NotUsed> createModels = Flow.of(Path.class)
				.map(this::createModelFromTTL)
				.sliding(maxBatchSize, 1);

		final Flow<List<Model>, Dataset, NotUsed> createDataSet = Flow.<List<Model>> create()
				.map(this::initializeDataSet);

		final Flow<Dataset, Dataset, NotUsed> processQueriesInParallel =
				Flow.fromGraph(GraphDSL.create(this::initializeQueriesProcessing));

		final CompletionStage<Done> streamFinish = ttlStream
				.via(createModels)
				.via(createDataSet)
				.via(processQueriesInParallel)
				.run(system);

		streamFinish.thenRun(system::terminate);
	}

	private Model createModelFromTTL(final Path path) {
		logger.info("Processing {} file.", path);

		final Model model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(model, path.toUri().getPath());
		return model;
	}

	private Dataset initializeDataSet(final List<Model> models) {
		final Dataset dataset = TDB2Factory.createDataset();
		final AtomicInteger batchSize = new AtomicInteger(0);

		models.forEach(model -> addNextModel(model, dataset, batchSize.incrementAndGet()));
		return dataset;
	}

	private FlowShape<Dataset, Dataset> initializeQueriesProcessing(final GraphDSL.Builder<NotUsed> builder) {
		final List<Flow<Dataset, Dataset, NotUsed>> analysisFlows = analyzer.prepareAnalysisFlows();
		final int queriesNo = analysisFlows.size();

		final UniformFanInShape<Dataset, Dataset> collectQueries = builder.add(Merge.create(queriesNo));
		final UniformFanOutShape<Dataset, Dataset> dispatch = builder.add(Balance.create(queriesNo));

		IntStream.range(0, queriesNo).forEach(idx ->
				builder.from(dispatch.out(idx))
						.via(builder.add(analysisFlows.get(idx)))
						.toInlet(collectQueries.in(idx)));

		return FlowShape.of(dispatch.in(), collectQueries.out());
	}

	private void addNextModel(final Model model, final Dataset dataset, final int currentBatchSize) {
		final String nextModelName = format("%s%d", dataType.name(), currentBatchSize);
		dataset.begin(ReadWrite.WRITE);
		try {
			dataset.addNamedModel(nextModelName, model);
			dataset.commit();
		} finally {
			dataset.end();
		}
	}

}
