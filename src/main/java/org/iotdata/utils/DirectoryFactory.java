package org.iotdata.utils;

import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;
import java.io.IOException;

import org.iotdata.exception.CouldNotDeleteDirectoryException;

/**
 * Class containing helper methods to manipulate the directories where the results of the analysis are stored
 */
public class DirectoryFactory {

	/**
	 * Method constructs the directory
	 *
	 * @param basePath base directory path
	 * @param name     name of the directory
	 * @return flag indicated if directory has existed before
	 */
	public static boolean constructDirectory(String basePath, String name) {
		final String directoryPath = basePath + name;
		final File directory = new File(directoryPath);
		final boolean directoryExists = directory.exists();

		if (!directoryExists) {
			directory.mkdirs();
		}
		return directoryExists;
	}

	/**
	 * Method removes the directory
	 *
	 * @param basePath base directory path
	 * @param name     name of the directory
	 */
	public static void removeDirectory(String basePath, String name) {
		final String directoryPath = basePath + name;
		final File directory = new File(directoryPath);

		if (directory.exists()) {
			try {
				deleteDirectory(directory);
			} catch (IOException e) {
				throw new CouldNotDeleteDirectoryException(directoryPath, e);
			}
		}
	}
}
