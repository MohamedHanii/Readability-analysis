package de.uni_passau.fim.se2.sa.readability.utils;

import de.uni_passau.fim.se2.sa.readability.features.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implemented this class with help of LLM
 * FileName: PreprocessHelper.md
 * */
public class Preprocess {

    /**
     * A value of 3.6 splits the Scalabrino Dataset into almost evenly balanced binary classes.
     */
    private static final double TRUTH_THRESHOLD = 3.6;

    /**
     * Traverses through each java snippet in the specified source directory and computes the specified list of feature metrics.
     * Each snippet is then saved together with its extracted feature values and the truth score as one row in the csv, resulting
     * in the scheme [File,NumberLines,TokenEntropy,HalsteadVolume,Truth].
     * <p>
     * The File column value corresponds to the respective file name.
     * All feature values are rounded to two decimal places.
     * The truth value corresponds to a String that is set to the value "Y" if the mean rater score of a given snippet is greater or equal
     * than the TRUTH_THRESHOLD. Otherwise, if the mean score is lower than the TRUTH_THRESHOLD the truth value String is set to "N".
     *
     * @param sourceDir      the directory containing java snippet (.jsnp) files.
     * @param truth          the ground truth csv file containing the human readability ratings of the code snippets.                       `
     * @param csv            the builder for the csv.
     * @param featureMetrics the list of specified features via the cli.
     * @throws IOException if the source directory or the truth file does not exist.
     */
    public static void collectCSVBody(Path sourceDir, File truth, StringBuilder csv, List<FeatureMetric> featureMetrics) throws IOException {
        try {
            Map<String, Double> truthMap = loadTruthMap(truth);

            Files.walk(sourceDir)
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparingInt(path -> extractNumber(path.getFileName().toString())))
                    .forEach(path -> {
                        try {
                            String codeString = Files.readString(path, StandardCharsets.UTF_8);

                            StringBuilder line = new StringBuilder();
                            String fileName = path.getFileName().toString();
                            line.append(fileName);
                            for (FeatureMetric metric : featureMetrics) {
                                double metricValue = metric.computeMetric(codeString);
                                line.append(",").append(String.format("%.2f", metricValue));
                            }
                            Double truthValue = truthMap.get(fileName);
                            String truthLabel = (truthValue != null && truthValue >= TRUTH_THRESHOLD) ? "Y" : "N";
                            line.append(",").append(truthLabel);
                            csv.append(line);
                            csv.append("\n");
                        } catch (Exception e) {
                            System.err.println("Failed to read file: " + path + " -> " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
    }

    public static Map<String, Double> loadTruthMap(File truthFile) {
        Map<String, Double> truthMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(truthFile))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return truthMap;

            String[] headers = headerLine.split(",");
            int fileCount = headers.length - 1;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equalsIgnoreCase("mean")) {
                    for (int i = 1; i <= fileCount; i++) {
                        String fileName = headers[i].trim().replace("Snippet", "") + ".jsnp";
                        double meanValue = Double.parseDouble(parts[i].trim());
                        truthMap.put(fileName, meanValue);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return truthMap;
    }

    public static int extractNumber(String filename) {
        String number = filename.replaceAll("\\D+", "");
        return number.isEmpty() ? 0 : Integer.parseInt(number);
    }


}
