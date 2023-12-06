package org.iotdata.processing;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.iotdata.utils.TTLReader.readTTLsFromDirectoryToStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb2.TDB2Factory;
import org.iotdata.enums.DatasetType;
import org.iotdata.exception.InvalidPropertyFile;

/**
 * Class contains method used to process given RDF stream
 */
public class RDFStreamProcessing {

	private final int maxBatchSize;
	private final DatasetType dataType;
	private final Stream<Path> ttlStream;

	/**
	 * Default constructor
	 *
	 * @param dataType type of data set that is being processed
	 * @apiNote data type is used to select appropriate analysis and pre-processing methods
	 */
	public RDFStreamProcessing(final DatasetType dataType) {
		this.ttlStream = readTTLsFromDirectoryToStream(dataType.getDirName());
		this.maxBatchSize = getBatchSize();
		this.dataType = dataType;
	}

	/**
	 * Method processes a given RDF stream in the batch manner
	 * (i.e. processing consecutive RDF graphs captured within single sliding window)
	 */
	public void processRDFStream() {
		final AtomicInteger batchSize = new AtomicInteger(0);
		final Dataset dataset = TDB2Factory.createDataset();

		dataset.begin(ReadWrite.WRITE);
		ttlStream.forEach(path -> {
			final int currentBatchSize = batchSize.incrementAndGet();
			addNextModel(path, dataset, currentBatchSize);

			if (currentBatchSize > maxBatchSize) {
				processDataset(dataset, batchSize);
				dataset.commit();
				dataset.end();
				dataset.begin(ReadWrite.WRITE);
			}
		});
	}

	private void addNextModel(final Path path, final Dataset dataset, final int currentBatchSize) {
		final Model nextModel = ModelFactory.createDefaultModel();
		final String nextModelName = format("%s%d", dataType.name(), currentBatchSize);

		RDFDataMgr.read(nextModel, path.toUri().getPath());
		dataset.addNamedModel(nextModelName, nextModel);
	}

	private void processDataset(final Dataset dataset, final AtomicInteger batchSize) {
		dataType.getDataAnalyzer().performAnalysis(dataset);
		batchSize.set(0);
		dataset.listNames().forEachRemaining(dataset::removeNamedModel);
	}

	private int getBatchSize() {
		final String BATCH_CONFIG = "config.properties";

		final Properties props = new Properties();
		try {
			props.load(RDFStreamProcessing.class.getClassLoader().getResourceAsStream(BATCH_CONFIG));
			return parseInt(props.getProperty("batch"));
		} catch (IOException e) {
			throw new InvalidPropertyFile(BATCH_CONFIG, e);
		}
	}
}
