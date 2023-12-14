package org.iotdata.utils;

import static java.lang.String.format;
import static org.iotdata.utils.TTLReader.readTTLsFromDirectoryToStream;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.pekko.Done;
import org.apache.pekko.NotUsed;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.stream.javadsl.Flow;
import org.apache.pekko.stream.javadsl.Source;
import org.iotdata.enums.DatasetType;

/**
 * Class contains method used to process given RDF stream
 */
public class RDFStreamProcessing {

	private final int maxBatchSize;
	private final DatasetType dataType;
	private final Source<Path, NotUsed> ttlStream;

	/**
	 * Default constructor
	 *
	 * @param dataType     type of data set that is being processed
	 * @param maxBatchSize maximum size of the batch used in RDF stream processing
	 * @apiNote data type is used to select appropriate analysis and pre-processing methods
	 */
	public RDFStreamProcessing(final DatasetType dataType, final int maxBatchSize) {
		this.ttlStream = readTTLsFromDirectoryToStream(dataType.getDirName());
		this.maxBatchSize = maxBatchSize;
		this.dataType = dataType;
	}

	/**
	 * Method processes a given RDF stream in the batch manner
	 * (i.e. processing consecutive RDF graphs captured within single sliding window)
	 */
	public void processRDFStream() {
		final AtomicInteger batchSize = new AtomicInteger(0);
		final Dataset dataset = TDB2Factory.createDataset();
		final ActorSystem system = ActorSystem.create(dataType.getDirName());

		final Flow<Path, List<Model>, NotUsed> createModels = Flow.of(Path.class)
				.map(this::createModelFromTTL)
				.grouped(maxBatchSize);

		final CompletionStage<Done> streamFinish =
				ttlStream.via(createModels.async())
						.runForeach(model -> processDataset(model, dataset, batchSize), system);

		streamFinish.thenRun(system::terminate);

	}

	private Model createModelFromTTL(final Path path) {
		final Model model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(model, path.toUri().getPath());
		return model;
	}

	private void processDataset(final List<Model> models, final Dataset dataset, final AtomicInteger batchSize) {
		models.forEach(model -> addNextModel(model, dataset, batchSize.incrementAndGet()));
		dataset.begin(ReadWrite.WRITE);
		try {
			dataType.getDataAnalyzer().performAnalysis(dataset);
			dataset.listModelNames().forEachRemaining(dataset::removeNamedModel);
			dataset.commit();
		} finally {
			dataset.end();
		}
		batchSize.set(0);
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
