package de.uni_passau.fim.se2.sa.readability.utils;


import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.Map;


public class OperandVisitor extends VoidVisitorAdapter<Void> {

    /**
     * Maps operand names to the number of their occurrences in the given code snippet.
     */
    private final Map<String, Integer> operandsPerMethod;

    public OperandVisitor() {
        operandsPerMethod = new HashMap<>();
    }

    public Map<String, Integer> getOperandsPerMethod() {
        return operandsPerMethod;
    }

    private void countOperand(String name) {
        operandsPerMethod.put(name, operandsPerMethod.getOrDefault(name, 0) + 1);
    }

    /**
     * The logic with help (LLM).
     *
     * Reference implementation: OperandVisitorImpl
     */
    @Override
    public void visit(SimpleName n, Void arg) {
        super.visit(n, arg);
        countOperand(n.getIdentifier());
    }

    @Override
    public void visit(StringLiteralExpr n, Void arg) {
        super.visit(n, arg);
        countOperand(n.getValue());
    }

    @Override
    public void visit(IntegerLiteralExpr n, Void arg) {
        super.visit(n, arg);
        countOperand(n.getValue());
    }

    @Override
    public void visit(DoubleLiteralExpr n, Void arg) {
        super.visit(n, arg);
        countOperand(n.getValue());
    }

    @Override
    public void visit(CharLiteralExpr n, Void arg) {
        super.visit(n, arg);
        countOperand(n.getValue());
    }

    @Override
    public void visit(BooleanLiteralExpr n, Void arg) {
        super.visit(n, arg);
        countOperand(String.valueOf(n.getValue()));
    }

    @Override
    public void visit(NullLiteralExpr n, Void arg) {
        super.visit(n, arg);
        countOperand("NULL");
    }

    @Override
    public void visit(LongLiteralExpr n, Void arg) {
        super.visit(n, arg);
        countOperand(n.getValue());
    }
}
