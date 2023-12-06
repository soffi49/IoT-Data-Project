package org.iotdata;

import static org.iotdata.enums.DatasetType.CAMERAS;

import org.iotdata.processing.RDFStreamProcessing;

public class Runner {
	public static void main(String[] args) {
		final RDFStreamProcessing streamProcessor = new RDFStreamProcessing(CAMERAS);
		streamProcessor.processRDFStream();
	}
}