package org.iotdata.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

/**
 * Class containing helper methods to manipulate the directories where the results of the analysis are stored
 */
public class DirectoryModifier {
    /**
     * Method constructs the name of the .csv file where the query results are stored
     *
     * @param basePath base directory path
     * @param name name of the directory
     * @param index name of the file
     * @return
     */
    public static String constructName(String basePath, String name, Integer index) {
        return basePath + name  + "/" + index.toString() + ".csv";
    }

    /**
     * Method constructs the name of the directory where .csv files are stored
     *
     * @param basePath base directory path
     * @param name name of the directory
     */
    public static void constructDirectory(String basePath, String name) {
        String directoryPath = basePath + name;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Method deletes contents of the directory
     *
     * @param directoryPath path of the directory to be deleted
     */
    public static void deleteDirectoryContents(String directoryPath) {
        Path dir = Paths.get(directoryPath);
        try {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .filter(path -> !path.equals(dir))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
