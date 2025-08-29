package de.uni_passau.fim.se2.sa.readability.features;

import com.github.javaparser.ParseException;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.body.BodyDeclaration;
import de.uni_passau.fim.se2.sa.readability.utils.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TokenEntropyFeature extends FeatureMetric {

    /**
     * Computes the entropy metric based on the tokens of the given code snippet.
     * Since we are interested in the readability of code as perceived by a human, tokens also include whitespaces and the like.
     *
     * @return token entropy of the given code snippet.
     */
    @Override
    public double computeMetric(String codeSnippet) {
        try {
            Map<String, Integer> freq = new HashMap<>();
            BodyDeclaration<?> parser = Parser.parseJavaSnippet(codeSnippet);
            Optional<TokenRange> tokenRange = parser.getTokenRange();
            tokenRange.get().forEach(token -> {
                String tokenText = token.getText();
                freq.put(tokenText, freq.getOrDefault(tokenText, 0) + 1);
            });

            int totalTokens = freq.values().stream().mapToInt(Integer::intValue).sum();
            double entropy = 0.0;

            for (int count : freq.values()) {
                double p = (double) count / totalTokens;
                entropy += p * (Math.log(p) / Math.log(2));
            }

            return -entropy;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getIdentifier() {
        return "TokenEntropy";
    }
}
