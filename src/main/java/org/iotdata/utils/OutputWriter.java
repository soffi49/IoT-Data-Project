package org.iotdata.utils;

import static fuzzycsv.FuzzyCSVTable.parseCsv;
import static java.io.File.separator;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newBufferedReader;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.jena.query.ResultSetFormatter.outputAsCSV;
import static org.iotdata.utils.DirectoryFactory.constructDirectory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.jena.query.ResultSet;
import org.iotdata.exception.CouldNotCreateOutputCSVException;

import fuzzycsv.FuzzyCSVTable;

/**
 * Class with methods that support construction of .csv output files
 */
public class OutputWriter {

	/**
	 * Method stores partial results in separate output .csv file
	 *
	 * @param directoryName name of the directory in which the results are to be stored
	 * @param resultSet     results obtained after querying the data
	 * @param outputPath    path under which the results are to be stored
	 * @param currentIndex  current index of stored files
	 */
	public static void storeResultsInSeparateFiles(final String directoryName, final ResultSet resultSet,
			final String outputPath, final int currentIndex) {
		if (resultSet.hasNext()) {
			constructDirectory(outputPath, directoryName);
			final String fileName = join(outputPath, directoryName, separator, currentIndex, ".csv");
			storeOutputResult(fileName, resultSet);
		}
	}

	/**
	 * Method stores partial results in a single output .csv file
	 *
	 * @param directoryName name of the directory in which the results are to be stored
	 * @param resultSet     result obtained after querying the data
	 * @param outputPath  path under which the results are to be stored
	 */
	public static void storeResultsInSingleFile(final String directoryName, final ResultSet resultSet,
			final String outputPath) {
		if (resultSet.hasNext()) {
			final boolean doesDirectoryExists = constructDirectory(outputPath, directoryName);
			final String outputFileName = join(outputPath, directoryName, separator, directoryName, ".csv");

			if (!doesDirectoryExists) {
				storeOutputResult(outputFileName, resultSet);
			} else {
				final Path tempOutputPath = storeTemporaryOutputResult(directoryName, resultSet);
				combineAndSaveCSVOutputs(outputFileName, tempOutputPath);
			}

		}
	}

	private static void storeOutputResult(final String outputFileName, final ResultSet resultSet) {
		try {
			final OutputStream output = new FileOutputStream(outputFileName);
			outputAsCSV(output, resultSet);
		} catch (IOException e) {
			throw new CouldNotCreateOutputCSVException(outputFileName, e);
		}
	}

	private static Path storeTemporaryOutputResult(final String directoryName, final ResultSet resultSet) {
		final String tempFileName = String.join("-", directoryName, "temp");
		try {
			final Path tempOutputPath = createTempFile(tempFileName, ".csv");
			final OutputStream tempOutput = new FileOutputStream(tempOutputPath.toFile());
			outputAsCSV(tempOutput, resultSet);
			tempOutput.close();

			return  tempOutputPath;
		} catch (IOException e) {
			throw new CouldNotCreateOutputCSVException(tempFileName, e);
		}
	}

	private static void combineAndSaveCSVOutputs(final String outputFileName, final Path newOutputPath) {
		try {
			final BufferedReader existingCSV = newBufferedReader(Path.of(outputFileName));
			final BufferedReader newCSV = newBufferedReader(newOutputPath);

			final FuzzyCSVTable existingCSVTable = parseCsv(existingCSV, ',');
			final FuzzyCSVTable newCSVTable = parseCsv(newCSV, ',');
			final FuzzyCSVTable combinedTable = existingCSVTable.concatColumns(newCSVTable);

			existingCSV.close();
			newCSV.close();
			combinedTable.export().toCsv().write(outputFileName);
		} catch (IOException e) {
			throw new CouldNotCreateOutputCSVException(outputFileName, e);
		}
	}
}
