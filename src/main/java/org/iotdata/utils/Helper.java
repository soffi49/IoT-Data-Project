package org.iotdata.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;


public class Helper {
    public static String constructName(String basePath, String name, Integer index) {
        return basePath + name  + "/" + index.toString() + ".csv";
    }

    public static void constructDirectory(String basePath, String name) {
        String directoryPath = basePath + name;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static void deleteDirectoryContents() {
        Path dir = Paths.get("src/main/resources/results");
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
