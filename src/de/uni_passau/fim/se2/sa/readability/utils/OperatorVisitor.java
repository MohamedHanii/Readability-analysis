package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.Map;

public class OperatorVisitor extends VoidVisitorAdapter<Void> {

    public enum OperatorType {
        ASSIGNMENT,         // x=y
        BINARY,             // x+y
        UNARY,              // -x, ++x
        CONDITIONAL,        // ?
        TYPE_COMPARISON,    // instanceof
    }

    /**
     * Maps operator types to the number of their occurrences in the given code snippet.
     */
    private final Map<OperatorType, Integer> operatorsPerMethod;

    public OperatorVisitor() {
        operatorsPerMethod = new HashMap<>();
    }

    public Map<OperatorType, Integer> getOperatorsPerMethod() {
        return operatorsPerMethod;
    }

    private void countOperators(OperatorType type) {
        operatorsPerMethod.put(type, operatorsPerMethod.getOrDefault(type, 0) + 1);
    }

    @Override
    public void visit(VariableDeclarator n, Void arg) {
        super.visit(n, arg);
        countOperators(OperatorType.ASSIGNMENT);
    }

    @Override
    public void visit(AssignExpr n, Void arg) {
        super.visit(n, arg);
        countOperators(OperatorType.ASSIGNMENT);
    }

    @Override
    public void visit(BinaryExpr n, Void arg) {
        super.visit(n, arg);
        countOperators(OperatorType.BINARY);
    }

    @Override
    public void visit(UnaryExpr n, Void arg) {
        super.visit(n, arg);
        countOperators(OperatorType.UNARY);
    }

    @Override
    public void visit(ConditionalExpr n, Void arg) {
        super.visit(n, arg);
        countOperators(OperatorType.CONDITIONAL);
    }

    @Override
    public void visit(InstanceOfExpr n, Void arg) {
        super.visit(n, arg);
        countOperators(OperatorType.TYPE_COMPARISON);
    }

}
