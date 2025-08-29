package de.uni_passau.fim.se2.sa.readability.features;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import de.uni_passau.fim.se2.sa.readability.utils.OperandVisitor;
import de.uni_passau.fim.se2.sa.readability.utils.OperatorVisitor;
import de.uni_passau.fim.se2.sa.readability.utils.Parser;

import java.util.Map;

public class HalsteadVolumeFeature extends FeatureMetric {

    /**
     * Computes the Halstead Volume metric based on the given code snippet.
     *
     * @return Halstead Volume of the given code snippet.
     */
    @Override
    public double computeMetric(String codeSnippet) {
        try {
            BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(codeSnippet);
            OperandVisitor operandVisitor = new OperandVisitor();
            OperatorVisitor operatorVisitor = new OperatorVisitor();


            bodyDecl.accept(operatorVisitor, null);
            Map<OperatorVisitor.OperatorType, Integer> operatorsMap = operatorVisitor.getOperatorsPerMethod();

            int N1 = operatorsMap.values().stream().mapToInt(Integer::intValue).sum();
            int n1 = operatorsMap.size();

            bodyDecl.accept(operandVisitor, null);
            Map<String, Integer> operandsMap = operandVisitor.getOperandsPerMethod();

            int N2 = operandsMap.values().stream().mapToInt(Integer::intValue).sum();
            int n2 = operandsMap.size();

            int N = N1 + N2;
            int n = n1 + n2;

            return N * (Math.log(n) / Math.log(2));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getIdentifier() {
        return "HalsteadVolume";
    }
}
