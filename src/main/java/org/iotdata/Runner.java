package org.iotdata;

import static org.iotdata.reader.FileReader.readTTLsFromDirectoryToStream;

import org.apache.jena.sparql.graph.GraphFactory;
import org.iotdata.analysis.TagsAnalysisService;

public class Runner {
    public static void main(String[] args) {
        var graph = GraphFactory.createDefaultGraph();
        readTTLsFromDirectoryToStream("src/main/resources/tags/000/779", graph);

        var tagAnalysisService = new TagsAnalysisService(graph);
        tagAnalysisService.performExploratoryAnalysisIdentifier();
        tagAnalysisService.performExploratoryAnalysisBattery();
    }
}