package org.iotdata;

import static org.iotdata.enums.DatasetType.*;

import org.iotdata.domain.properties.ConfigurationProps;
import org.iotdata.utils.RDFStreamProcessing;

public class Runner {
	public static void main(String[] args) {
		final ConfigurationProps configurator = new ConfigurationProps();
		final RDFStreamProcessing streamProcessor = new RDFStreamProcessing(CAMERAS, configurator.getBatchSize(),
				configurator.getOutputPath(), configurator.getInputPath());
		streamProcessor.processRDFStream();
	}
}