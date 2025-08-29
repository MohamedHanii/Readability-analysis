package de.uni_passau.fim.se2.sa.readability.features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenEntropyFeatureTest {

    @Test
    public void testTokenEntropyFeature() {
        String codeSnippet = "@Override\n" +
                "    public void runTest(final Test test, final TestResult result) {\n" +
                "        Thread t = new Thread() {\n" +
                "            @Override\n" +
                "            public void run() {\n" +
                "                try {\n" +
                "                    // inlined due to limitation in VA/Java\n" +
                "                    //ActiveTestSuite.super.runTest(test, result);\n" +
                "                    test.run(result);\n" +
                "                } finally {\n" +
                "                    ActiveTestSuite.this.runFinished();\n" +
                "                }\n" +
                "            }\n" +
                "        };\n" +
                "        t.start();\n" +
                "    }";
        double result = new TokenEntropyFeature().computeMetric(codeSnippet);

        Assertions.assertEquals(2.027, result, 0.2);
    }

    @Test
    void testComputeMetricThrowsRuntimeExceptionOnParseException() {
        String invalidCodeSnippet = "String[] parseOptions(String[] args) {\n" +
                "        for (int i = 0; i != args.length; ++i) {\n" +
                "            String arg = args[i];\n"
                ;

        assertThrows(RuntimeException.class, () -> new TokenEntropyFeature().computeMetric(invalidCodeSnippet));
    }

    @Test
    public void testGetIdentifier() {
        TokenEntropyFeature feature = new TokenEntropyFeature();
        Assertions.assertEquals("TokenEntropy", feature.getIdentifier());
    }
}
