package org.iotdata.analysis;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

public class TagQuery {
    private String queryString;
    public final Query query;

    private void addPrefixes(String queryNoPrefixes) {
        String prefixes = """
        PREFIX schema: <http://schema.org/>
        PREFIX aiot: <https://assist-iot.eu/ontologies/aiot_p2#>
        """;
        queryString = prefixes + queryNoPrefixes;
    }

    public TagQuery(String queryNoPrefixes) {
        addPrefixes(queryNoPrefixes);
        query = QueryFactory.create(queryString);
    }
}
