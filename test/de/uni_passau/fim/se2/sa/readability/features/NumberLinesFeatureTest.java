package de.uni_passau.fim.se2.sa.readability.features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberLinesFeatureTest {

    @Test
    public void testComputeMetric() {
        NumberLinesFeature feature = new NumberLinesFeature();
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

        double actual = feature.computeMetric(codeSnippet);

        Assertions.assertEquals(39.0, actual, 0.0);
    }

    @Test
    public void testComputeMetricWithEmptyString() {
        NumberLinesFeature feature = new NumberLinesFeature();
        double result = feature.computeMetric("");
        Assertions.assertEquals(1.0, result);
    }

    @Test
    public void testComputeMetricWithSingleLineNoNewline() {
        NumberLinesFeature feature = new NumberLinesFeature();
        String codeSnippet = "int a = 0;";
        double result = feature.computeMetric(codeSnippet);
        Assertions.assertEquals(1.0, result);
    }

    @Test
    public void testGetIdentifier() {
        NumberLinesFeature feature = new NumberLinesFeature();
        Assertions.assertEquals("NumberLines", feature.getIdentifier());
    }
}
