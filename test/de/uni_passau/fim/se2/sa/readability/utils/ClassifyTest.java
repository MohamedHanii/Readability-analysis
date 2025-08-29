package de.uni_passau.fim.se2.sa.readability.utils;

import org.junit.jupiter.api.Test;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ClassifyTest {

    @Test
    public void testLoadDataset() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("feature1,feature2,class\n");
            writer.write("1.0,2.0,A\n");
            writer.write("3.0,4.0,B\n");
        }

        Instances dataset = Classify.loadDataset(tempFile);

        assertNotNull(dataset);
        assertEquals(2, dataset.numInstances());
        assertEquals(3, dataset.numAttributes());
        assertEquals("class", dataset.classAttribute().name());
    }

    @Test
    public void testTrainAndEvaluate() throws Exception {
        File tempFile = File.createTempFile("test_dataset", ".csv");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("feature1,feature2,class\n");
            writer.write("1.0,2.0,A\n");
            writer.write("1.5,2.5,A\n");
            writer.write("2.0,3.0,B\n");
            writer.write("2.5,3.5,B\n");
            writer.write("3.0,4.0,A\n");
            writer.write("3.5,4.5,B\n");
            writer.write("4.0,5.0,A\n");
            writer.write("4.5,5.5,B\n");
            writer.write("5.0,6.0,A\n");
            writer.write("5.5,6.5,B\n");
        }

        Instances dataset = Classify.loadDataset(tempFile);

        Evaluation evaluation = Classify.trainAndEvaluate(dataset);

        assertNotNull(evaluation);
        assertEquals(10, evaluation.numInstances());
        assertTrue(evaluation.correct() + evaluation.incorrect() == 10);
    }

    @Test
    public void testLoadDataset_noClassAttribute() throws IOException {
        File tempFile = File.createTempFile("no_class", ".csv");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("feature1,feature2\n");
            writer.write("1.0,2.0\n");
            writer.write("3.0,4.0\n");
        }

        Instances dataset = Classify.loadDataset(tempFile);
        assertEquals(2, dataset.numAttributes());
    }

    @Test
    public void testTrainAndEvaluate_tooFewInstances() throws IOException {
        File tempFile = File.createTempFile("few_instances", ".csv");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("feature1,feature2,class\n");
            writer.write("1.0,2.0,A\n");
            writer.write("3.0,4.0,B\n");
        }

        Instances dataset = Classify.loadDataset(tempFile);

        Exception exception = assertThrows(Exception.class, () -> {
            Classify.trainAndEvaluate(dataset);
        });

        assertTrue(exception.getMessage().contains("folds") || exception instanceof IllegalArgumentException);
    }

    @Test
    public void testClassifierPredictionOnTrainingSet() throws Exception {
        File tempFile = File.createTempFile("prediction_check", ".csv");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("feature1,feature2,class\n");
            writer.write("1.0,2.0,A\n");
            writer.write("2.0,3.0,B\n");
            writer.write("3.0,4.0,A\n");
            writer.write("4.0,5.0,B\n");
        }

        Instances dataset = Classify.loadDataset(tempFile);

        Logistic logistic = new Logistic();
        logistic.buildClassifier(dataset);

        double prediction = logistic.classifyInstance(dataset.instance(0));
        assertTrue(prediction == 0.0 || prediction == 1.0);
    }

    @Test
    public void testSingleClassDataset() throws IOException {
        File tempFile = File.createTempFile("one_class", ".csv");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("feature1,feature2,class\n");
            writer.write("1.0,2.0,A\n");
            writer.write("1.5,2.5,A\n");
            writer.write("2.0,3.0,A\n");
            writer.write("2.5,3.5,A\n");
            writer.write("3.0,4.0,A\n");
            writer.write("3.5,4.5,A\n");
        }

        Instances dataset = Classify.loadDataset(tempFile);

        assertEquals(1, dataset.classAttribute().numValues());

        Exception exception = assertThrows(Exception.class, () -> {
            Classify.trainAndEvaluate(dataset);
        });

        System.out.println("Caught exception: " + exception.getMessage());

        assertNotNull(exception.getMessage());
    }

}
