package de.uni_passau.fim.se2.sa.readability.features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CyclomaticComplexityFeatureTest {

    @Test
    public void testComputeMetric() {
        String codeSnippet = "String[] parseOptions(String[] args) {\n" +
                "        for (int i = 0; i != args.length; ++i) {\n" +
                "            String arg = args[i];\n" +
                "\n" +
                "            try {\n" +
                "                if (arg.equals(\"--\")) {\n" +
                "                    return copyArray(args, i + 1, args.length);\n" +
                "                } else if (arg.startsWith(\"--\")) {\n" +
                "                    if (arg.startsWith(\"--filter=\") || arg.equals(\"--filter\")) {\n" +
                "                        String filterSpec;\n" +
                "                        if (arg.equals(\"--filter\")) {\n" +
                "                            ++i;\n" +
                "\n" +
                "                            if (i < args.length) {\n" +
                "                                filterSpec = args[i];\n" +
                "                            } else {\n" +
                "                                parserErrors.add(new CommandLineParserError(arg + \" value not specified\"));\n" +
                "\n" +
                "                                break;\n" +
                "                            }\n" +
                "                        } else {\n" +
                "                            filterSpec = arg.substring(arg.indexOf('=') + 1);\n" +
                "                        }\n" +
                "\n" +
                "                        filter = filter.intersect(FilterFactories.createFilterFromFilterSpec(\n" +
                "                                createSuiteDescription(arg), filterSpec));\n" +
                "                    } else {\n" +
                "                        parserErrors.add(new CommandLineParserError(\"JUnit knows nothing about the \" + arg + \" option\"));\n" +
                "                    }\n" +
                "                } else {\n" +
                "                    return copyArray(args, i, args.length);\n" +
                "                }\n" +
                "            } catch (FilterFactory.FilterNotCreatedException e) {\n" +
                "                parserErrors.add(e);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        return new String[]{};\n" +
                "    }";

        double result = new CyclomaticComplexityFeature().computeMetric(codeSnippet);

        Assertions.assertEquals(9.0, result, 0.2);
    }

    @Test
    void testComputeMetricThrowsRuntimeExceptionOnParseException() {
        String invalidCodeSnippet = "String[] parseOptions(String[] args) {\n" +
                "        for (int i = 0; i != args.length; ++i) {\n" +
                "            String arg = args[i];\n"
                ;

        assertThrows(RuntimeException.class, () -> new CyclomaticComplexityFeature().computeMetric(invalidCodeSnippet));
    }

    @Test
    public void testComputeMetricForSimpleMethod() {
        String simpleMethod = "void foo() { int a = 0; }";
        double result = new CyclomaticComplexityFeature().computeMetric(simpleMethod);
        Assertions.assertEquals(1.0, result, 0.1);
    }

    @Test
    public void testGetIdentifier() {
        Assertions.assertEquals("CyclomaticComplexity", new CyclomaticComplexityFeature().getIdentifier());
    }

    @Test
    public void testComputeMetricWithEmptyStringThrows() {
        assertThrows(RuntimeException.class, () -> new CyclomaticComplexityFeature().computeMetric(""));
    }

}
