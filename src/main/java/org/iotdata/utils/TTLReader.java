package org.iotdata.utils;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.Files.walk;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.jena.riot.Lang.TTL;
import static org.apache.pekko.stream.javadsl.Source.fromJavaStream;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.pekko.NotUsed;
import org.apache.pekko.japi.function.Predicate;
import org.apache.pekko.stream.javadsl.Source;
import org.iotdata.exception.InvalidInputDirectoryException;

/**
 * Class with method that allow to parse RDF streams
 */
public class TTLReader {

	private static final Predicate<Path> isExtensionTTL = path ->
			TTL.getFileExtensions().contains(getExtension(path.toString()));

	/**
	 * Method returns stream of .ttl files (i.e. RDF stream) corresponding to the given directory
	 *
	 * @param filePath  path to the directory in resources
	 * @param inputPath path to the input files
	 * @return stream of .tll file paths
	 */
	public static Source<Path, NotUsed> readTTLsFromDirectoryToStream(final String filePath, final String inputPath) {
		try {
			final String path = join(inputPath, filePath);
			final URL url = Paths.get(path).toAbsolutePath().toUri().toURL();
			final Path directoryPath = Path.of(url.toURI());
			final Stream<Path> directoryFiles = walk(directoryPath, FOLLOW_LINKS);
			return fromJavaStream(() -> directoryFiles)
					.filter(Files::isRegularFile)
					.filter(isExtensionTTL);

		} catch (IOException | NullPointerException | URISyntaxException e) {
			throw new InvalidInputDirectoryException(filePath, e);
		}
	}
}
