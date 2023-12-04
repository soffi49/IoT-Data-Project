package org.iotdata.analysis;

import org.apache.jena.graph.Graph;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class TagsAnalysisService {
    private final Model model;

    public TagsAnalysisService(Graph inputGraph) {
        model = ModelFactory.createModelForGraph(inputGraph);
    }

    public void performExploratoryAnalysisIdentifier() {
        String queryNoPrefixes = """
                SELECT DISTINCT ?identifier WHERE {?s schema:identifier ?identifier}
                """;

        TagQuery tagQuery = new TagQuery(queryNoPrefixes);
        try (QueryExecution qexec = QueryExecutionFactory.create(tagQuery.query, model)) {
            ResultSet results = qexec.execSelect();
        }
    }

    public void performExploratoryAnalysisWatch() {
        String queryNoPrefixes = """
                SELECT ?isConnected (COUNT(?isConnected) as ?isConnectedCount)
                WHERE {
                    ?s aiot:hasWatchConnected ?isConnected
                }
                GROUP BY ?isConnected
                """;

        TagQuery tagQuery = new TagQuery(queryNoPrefixes);
        try (QueryExecution qexec = QueryExecutionFactory.create(tagQuery.query, model)) {
            ResultSet results = qexec.execSelect();
        }
    }
}