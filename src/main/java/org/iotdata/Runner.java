package org.iotdata;

import static java.lang.String.format;
import static org.iotdata.enums.DatasetType.CAMERAS;
import static org.iotdata.enums.DatasetType.TAGS;
import static org.iotdata.utils.TTLReader.readTTLsFromDirectoryToStream;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.pekko.Done;
import org.apache.pekko.NotUsed;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.stream.javadsl.Source;
import org.iotdata.analysis.TagsAnalyzer;
import org.iotdata.processing.RDFStreamProcessing;
import org.iotdata.utils.Helper;

import java.nio.file.Path;
import java.time.Instant;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;



public class Runner {
	public static void main(String[] args) {
		Helper.deleteDirectoryContents();
		System.out.println(Instant.now());

		TagsAnalyzer tagsAnalyzer = new TagsAnalyzer();
		final AtomicInteger batchSize = new AtomicInteger(0);
		final ActorSystem system = ActorSystem.create("kg");
		final String filePath = "tags/000/779";

		Source<Path, NotUsed> paths = Source.
				fromJavaStream(() -> readTTLsFromDirectoryToStream(filePath));

		final CompletionStage<Done> done =
				paths.map(path -> {
					Model m = ModelFactory.createDefaultModel();
					RDFDataMgr.read(m, path.toString());
					return m;
				}).runForeach(a -> {
					final int currentBatchSize = batchSize.incrementAndGet();
					final String nextModelName = format("%s%d", "-", currentBatchSize);
					Dataset dataset = TDB2Factory.createDataset();
					dataset.begin(ReadWrite.READ);
					dataset.addNamedModel(nextModelName, a);
					tagsAnalyzer.performAnalysis(dataset);
					dataset.commit();
					dataset.end();
				}, system);;

		done.thenRun(() -> {
			system.terminate();
			System.out.println(Instant.now());
		});


	}
}