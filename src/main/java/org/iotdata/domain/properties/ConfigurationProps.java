package org.iotdata.domain.properties;

import static java.lang.Integer.parseInt;
import static org.iotdata.constants.ConfigurationConstants.BATCH_SIZE;
import static org.iotdata.constants.ConfigurationConstants.CONFIG_FILE;
import static org.iotdata.constants.ConfigurationConstants.INOUT_PATH;
import static org.iotdata.constants.ConfigurationConstants.OUTPUT_PATH;

import java.io.IOException;
import java.util.Properties;

import org.iotdata.exception.InvalidPropertyFileException;
import org.iotdata.utils.RDFStreamProcessing;

import lombok.Getter;

/**
 * Class responsible for reading user configuration
 */
@Getter
public class ConfigurationProps {

	private final Properties properties;

	/**
	 * Default constructor
	 */
	public ConfigurationProps() {
		this.properties = new Properties();
		try {
			properties.load(RDFStreamProcessing.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
		} catch (IOException e) {
			throw new InvalidPropertyFileException(CONFIG_FILE, e);
		}
	}

	/**
	 * Method retrieves property of RDF stream batch size
	 *
	 * @return batch size
	 */
	public int getBatchSize() {
		return parseInt(properties.getProperty(BATCH_SIZE));
	}

	/**
	 * Method retrieves property indicating path to the directory in which the outputs are to be stored
	 *
	 * @return output path
	 */
	public String getOutputPath() {
		return properties.getProperty(OUTPUT_PATH);
	}

	/**
	 * Method retrieves property indicating path to the directory in which the inputs are stored
	 *
	 * @return input path
	 */
	public String getInputPath() {
		return properties.getProperty(INOUT_PATH);
	}
}
