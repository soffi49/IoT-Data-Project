package org.iotdata.utils;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.Files.walk;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.jena.riot.Lang.TTL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.iotdata.exception.InvalidInputDirectoryException;

/**
 * Class with method that allow to parse RDF streams
 */
public class TTLReader {

	private static final Predicate<Path> isExtensionTTL = path -> TTL.getFileExtensions()
			.contains(getExtension(path.toString()));

	/**
	 * Method returns stream of .ttl files (i.e. RDF stream) corresponding to the given directory
	 *
	 * @param filePath path to the directory in resources
	 * @return stream of .tll file paths
	 */
	public static Stream<Path> readTTLsFromDirectoryToStream(final String filePath) {
		try {
			final URL url = requireNonNull(TTLReader.class.getClassLoader().getResource(filePath));
			final Path directoryPath = Path.of(url.toURI());
			return walk(directoryPath, FOLLOW_LINKS)
					.filter(Files::isRegularFile)
					.filter(isExtensionTTL);

		} catch (IOException | NullPointerException | URISyntaxException e) {
			throw new InvalidInputDirectoryException(filePath, e);
		}
	}
}
