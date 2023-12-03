package org.iotdata;

import static org.iotdata.reader.FileReader.readTTLsFromDirectoryToStream;

import org.apache.jena.sparql.graph.GraphFactory;

public class Runner {
	public static void main(String[] args) {
		var graph = GraphFactory.createDefaultGraph();
		readTTLsFromDirectoryToStream("/cameras", graph);
	}
}