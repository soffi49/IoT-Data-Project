package org.iotdata.reader;

import static java.io.File.separator;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.jena.riot.Lang.TTL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import org.apache.jena.graph.Graph;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;
import org.iotdata.exception.InvalidInputDirectoryException;

/**
 * Class with method that allow to parse the resource files
 */
public class FileReader {
	private static final Predicate<Path> isTTL = path -> TTL.getFileExtensions()
			.contains(getExtension(path.toString()));

	/**
	 * Method reads into RDF stream all .ttl files from indicated path in resources
	 *
	 * @param filePath path to the directory in resources
	 * @return stream of tuples
	 */
	public static StreamRDF readTTLsFromDirectoryToStream(final String filePath, final Graph graph) {
		try {
			//TODO add spring here
			final Path directoryPath = Path.of("/path/here");

			final StreamRDF rdfStream = StreamRDFLib.graph(graph);

			Files.walk(directoryPath, FOLLOW_LINKS).filter(Files::isRegularFile).filter(isTTL)
					.forEach(path -> RDFDataMgr.parse(rdfStream, path.toUri().getPath()));
			return rdfStream;

		} catch (IOException | NullPointerException e) {
			throw new InvalidInputDirectoryException(filePath, e);
		}
	}

	/**
	 * Method verifies if the system was started from the JAR or IDE
	 *
	 * @return boolean indicating if the system was started from jar
	 */
	public static boolean isLoadedInJar() {
		final String classPath = FileReader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		return classPath.endsWith(".jar");
	}

	/**
	 * Method builds correct path of the given resource file
	 *
	 * @param pathElements elements of the path
	 * @return String path to the resource
	 */
	public static String buildResourceFilePath(final String... pathElements) {
		if (isLoadedInJar()) {
			return String.join("/", pathElements);
		}
		return String.join(separator, pathElements);
	}
}
