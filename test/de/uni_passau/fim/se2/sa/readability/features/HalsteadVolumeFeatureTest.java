package de.uni_passau.fim.se2.sa.readability.features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HalsteadVolumeFeatureTest {

    @Test
    public void testComputeMetrics() {
        String codeSnippet = "protected synchronized String evalToString(\n" +
                "            Object self,\n" +
                "            String expr,\n" +
                "            String sep)\n" +
                "    \tthrows ExpansionException {\n" +
                "\n" +
                "        _scratchBindings.put(\"self\", self);\n" +
                "        java.util.List values = eval(_scratchBindings, expr);\n" +
                "        _strBuf.setLength(0);\n" +
                "        Iterator iter = values.iterator();\n" +
                "        while (iter.hasNext()) {\n" +
                "            Object v = iter.next();\n" +
                "            if (Model.getFacade().isAModelElement(v)) {\n" +
                "                v = Model.getFacade().getName(v);\n" +
                "                if (\"\".equals(v)) {\n" +
                "                    v = Translator.localize(\"misc.name.anon\");\n" +
                "                }\n" +
                "            }\n" +
                "            if (Model.getFacade().isAExpression(v)) {\n" +
                "                v = Model.getFacade().getBody(v);\n" +
                "                if (\"\".equals(v)) {\n" +
                "                    v = \"(unspecified)\";\n" +
                "                }\n" +
                "            }\n" +
                "            if (!\"\".equals(v)) {\n" +
                "                _strBuf.append(v);\n" +
                "                if (iter.hasNext()) {\n" +
                "                    _strBuf.append(sep);\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return _strBuf.toString();\n" +
                "    }";

        double result = new HalsteadVolumeFeature().computeMetric(codeSnippet);

        Assertions.assertEquals(443.97, result, 0.5);
    }

    @Test
    void testComputeMetricThrowsRuntimeExceptionOnParseException() {
        String invalidCodeSnippet = "String[] parseOptions(String[] args) {\n" +
                "        for (int i = 0; i != args.length; ++i) {\n" +
                "            String arg = args[i];\n"
                ;

        assertThrows(RuntimeException.class, () -> new HalsteadVolumeFeature().computeMetric(invalidCodeSnippet));
    }

    @Test
    public void testComputeMetricForSimpleMethod() {
        String simpleMethod = "void foo() { int a = 0; }";
        double result = new HalsteadVolumeFeature().computeMetric(simpleMethod);
        Assertions.assertTrue(result > 0);
    }

    @Test
    public void testComputeMetricThrowsOnEmptyString() {
        assertThrows(RuntimeException.class, () -> new HalsteadVolumeFeature().computeMetric(""));
    }
    @Test
    public void testGetIdentifier() {
        Assertions.assertEquals("HalsteadVolume", new HalsteadVolumeFeature().getIdentifier());
    }

    @Test
    public void testComputeMetricWithNoOperatorsOrOperands() {
        String codeSnippet = "class Empty {}";
        double result = new HalsteadVolumeFeature().computeMetric(codeSnippet);
        Assertions.assertTrue(result >= 0);
    }


}
