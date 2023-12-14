package org.iotdata.analysis;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.core.Prologue;
import org.iotdata.enums.PrefixType;
import org.iotdata.utils.DirectoryModifier;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.iotdata.dml.WeatherQueries.*;
import static org.iotdata.enums.PrefixType.*;
import static org.iotdata.utils.QueryConstructor.createQueryFromNamedModels;

public class WeatherAnalyzer implements AbstractAnalyzer {
    private final AtomicInteger namingIndex = new AtomicInteger(0);

    @Override
    public void performAnalysis(final Dataset dataset) {
        final int currentIndex = namingIndex.incrementAndGet();
        try {
            processResults("results_wind_speed", selectDaysWithWindSpeed(dataset), currentIndex);
            processResults("results_uv_index", selectDaysWithUvIndex(dataset), currentIndex);
            processResults("results_temperature", selectDaysWithTemperature(dataset), currentIndex);
            processResults("results_pressure", selectDaysWithPressure(dataset), currentIndex);
            processResults("results_humidity", selectDaysWithHumidity(dataset), currentIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processResults(String directoryName, ResultSet resultSet, int currentIndex) throws IOException {
        String basePath = "src/main/resources/results/";
        DirectoryModifier.constructDirectory(basePath, directoryName);
        try (OutputStream output = new FileOutputStream(DirectoryModifier.constructName(basePath, directoryName, currentIndex))) {
            ResultSetFormatter.outputAsCSV(output, resultSet);
        }
    }

    private ResultSet selectDaysWithWindSpeed(final Dataset dataset) {
        return selectData(dataset, SELECT_WIND_SPEED, SOSA, AIOT, AIOT_P2, MEASURE);
    }

    private ResultSet selectDaysWithUvIndex(final Dataset dataset) {
        return selectData(dataset, SELECT_UV_INDEX, SOSA, AIOT, AIOT_P2, MEASURE);
    }

    private ResultSet selectDaysWithTemperature(final Dataset dataset) {
        return selectData(dataset, SELECT_TEMPERATURE, SOSA, AIOT, AIOT_P2, MEASURE);
    }

    private ResultSet selectDaysWithPressure(final Dataset dataset) {
        return selectData(dataset, SELECT_PRESSURE, SOSA, AIOT, AIOT_P2, MEASURE);
    }

    private ResultSet selectDaysWithHumidity(final Dataset dataset) {
        return selectData(dataset, SELECT_HUMIDITY, SOSA, AIOT, AIOT_P2, MEASURE);
    }

    /**
     * Method executes the specified query on the dataset
     *
     * @param dataset dataset on which query is to be executed
     * @param query query that will be executed on the dataset
     * @param prefixes prefixes that will be used in the query
     * @return output of the query
     */
    private ResultSet selectData(final Dataset dataset, final String query, final PrefixType... prefixes) {
        final Prologue prologue = createPrefixPrologue(prefixes);
        final QueryExecution queryExecution = createQueryFromNamedModels(dataset, query, prologue);
        return queryExecution.execSelect();
    }
}
