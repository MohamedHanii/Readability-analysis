package de.uni_passau.fim.se2.sa.readability.utils;


import de.uni_passau.fim.se2.sa.readability.features.FeatureMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


import static de.uni_passau.fim.se2.sa.readability.utils.Preprocess.*;
import static org.junit.jupiter.api.Assertions.*;

public class PreprocessTest {

    @TempDir
    Path tempDir;
    File truthFile;

    @BeforeEach
    void setUp() throws IOException {
        Files.writeString(tempDir.resolve("1.jsnp"), "public class A {}", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("2.jsnp"), "public class B {}", StandardCharsets.UTF_8);

        truthFile = tempDir.resolve("truth.csv").toFile();
        try (PrintWriter writer = new PrintWriter(truthFile)) {
            writer.println("id,Snippet1,Snippet2");
            writer.println("mean,4.0,3.0");
        }
    }

    @Test
    void testExtractNumber_variousFilenames() {
        assertEquals(123, extractNumber("File123.java"));
        assertEquals(98, extractNumber("version9_part8.txt"));
        assertEquals(0, extractNumber(""));
        assertEquals(0, extractNumber("no_numbers_here"));
        assertEquals(7, extractNumber("file007.java"));
    }

    @Test
    void testLoadTruthMap_emptyFile_returnsEmptyMap(@TempDir Path dir) throws IOException {
        File empty = dir.resolve("empty.csv").toFile();
        assertTrue(empty.createNewFile());
        Map<String, Double> map = loadTruthMap(empty);
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    void testLoadTruthMap_validMeanLine_parsesCorrectly(@TempDir Path dir) throws IOException {
        Path csv = dir.resolve("truth.csv");
        List<String> lines = List.of(
                "id,Snippet1,Snippet2,SnippetExtra",
                "mean,2.5,4.0,3.6",
                "ignore,0,0,0"
        );
        Files.write(csv, lines, StandardCharsets.UTF_8);

        Map<String, Double> map = loadTruthMap(csv.toFile());
        assertEquals(3, map.size());
        assertEquals(2.5, map.get("1.jsnp"));
        assertEquals(4.0, map.get("2.jsnp"));
        assertEquals(3.6, map.get("Extra.jsnp"));
    }

    @Test
    void testLoadTruthMap_invalidNumber_throwsException(@TempDir Path dir) throws IOException {
        Path csv = dir.resolve("invalid.csv");
        List<String> lines = List.of(
                "id,Snippet1",
                "mean,not_a_number"
        );
        Files.write(csv, lines, StandardCharsets.UTF_8);

        assertThrows(NumberFormatException.class, () -> loadTruthMap(csv.toFile()));
    }

    @Test
    void testLoadTruthMap_fileNotFound_throwsRuntimeException() {
        File nonExistent = Paths.get("nonexistent.csv").toFile();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> loadTruthMap(nonExistent));
        assertNotNull(ex.getCause());
    }

    @Test
    void testCollectCSVBody_producesOrderedCsv() throws IOException {
        FeatureMetric m1 = new FeatureMetric() {
            @Override
            public double computeMetric(String codeSnippet) {
                return 10.0;
            }

            @Override
            public String getIdentifier() {
                return "";
            }
        };
        FeatureMetric m2 = new FeatureMetric() {

            @Override
            public double computeMetric(String codeSnippet) {
                return 1.2345;
            }

            @Override
            public String getIdentifier() {
                return "";
            }
        };
        List<FeatureMetric> metrics = List.of(m1, m2);

        StringBuilder csv = new StringBuilder();
        collectCSVBody(tempDir, truthFile, csv, metrics);

        String[] rows = csv.toString().trim().split("\n");
        assertEquals(3, rows.length);

        String[] cols1 = rows[0].split(",");
        assertEquals("truth.csv", cols1[0]);
        assertEquals("10.00", cols1[1]);
        assertEquals("1.23", cols1[2]);
        assertEquals("N", cols1[3]);

        String[] cols2 = rows[1].split(",");
        assertEquals("1.jsnp", cols2[0]);
        assertEquals("10.00", cols2[1]);
        assertEquals("1.23", cols2[2]);
        assertEquals("Y", cols2[3]);
    }

    @Test
    void testCollectCSVBody_nonexistentSource_throwsRuntimeExceptionFromTruth() {
        Path badSource = Paths.get("does_not_exist");
        File badTruth = Paths.get("bad_truth.csv").toFile();
        StringBuilder csv = new StringBuilder();

        assertThrows(RuntimeException.class, () -> collectCSVBody(badSource, badTruth, csv, Collections.emptyList()));
    }

    @Test
    void testCollectCSVBody_nonexistentSnippetDir_isHandledGracefully() {
        Path badSource = Paths.get("does_not_exist");
        StringBuilder csv = new StringBuilder();

        assertDoesNotThrow(() -> collectCSVBody(badSource, truthFile, csv, Collections.emptyList()));
    }

    @Test
    void testCollectCSVBody_emptySnippetFile_includesEntry() throws IOException {
        Files.writeString(tempDir.resolve("3.jsnp"), "", StandardCharsets.UTF_8);
        StringBuilder csv = new StringBuilder();
        collectCSVBody(tempDir, truthFile, csv, List.of(new FeatureMetric() {
            @Override
            public double computeMetric(String codeSnippet) {
                return 0;
            }

            @Override
            public String getIdentifier() {
                return "";
            }
        }));

        String content = csv.toString();
        assertTrue(content.contains("3.jsnp,0.00,"));
    }

    @Test
    void testCollectCSVBody_thresholdBoundary(@TempDir Path dir) throws IOException {
        Files.writeString(tempDir.resolve("4.jsnp"), "class C {}", StandardCharsets.UTF_8);

        File boundary = dir.resolve("truth2.csv").toFile();
        try (PrintWriter w = new PrintWriter(boundary)) {
            w.println("id,Snippet1,Snippet2,Snippet4");
            w.println("mean,3.6,3.5,3.6");
        }
        StringBuilder csv = new StringBuilder();
        collectCSVBody(tempDir, boundary, csv, List.of(new FeatureMetric() {
            @Override
            public double computeMetric(String codeSnippet) {
                return 1.0;
            }

            @Override
            public String getIdentifier() {
                return "";
            }
        }));
        String out = csv.toString();

        assertTrue(out.contains("1.jsnp,1.00,Y"));
        assertTrue(out.contains("2.jsnp,1.00,N"));
        assertTrue(out.contains("4.jsnp,1.00,Y"));
    }

}
