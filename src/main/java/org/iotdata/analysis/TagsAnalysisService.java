package org.iotdata.analysis;

import org.apache.jena.graph.Graph;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
        try (QueryExecution qexec = QueryExecutionFactory.create(tagQuery.query, model);
             OutputStream output = new FileOutputStream("src/main/resources/results/query1.csv")) {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.outputAsCSV(output, results);
        } catch (IOException e) {
            e.printStackTrace();
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
        try (QueryExecution qexec = QueryExecutionFactory.create(tagQuery.query, model);
             OutputStream output = new FileOutputStream("src/main/resources/results/query2.csv")) {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.outputAsCSV(output, results);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}