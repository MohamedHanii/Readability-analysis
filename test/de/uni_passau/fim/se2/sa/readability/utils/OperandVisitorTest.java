package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.*;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OperandVisitorTest {

    @Test
    public void testOperandVisitorCollectsCorrectOperands() throws ParseException {
        String code = ""
                + "public class TestClass {\n"
                + "    // SimpleName: class name, method name, var names\n"
                + "    public void testMethod(String param1, int param2) {\n"
                + "        String s = \"hello\";            // StringLiteralExpr\n"
                + "        int x = 42;                     // IntegerLiteralExpr\n"
                + "        double d = 3.14;               // DoubleLiteralExpr\n"
                + "        char c = 'a';                  // CharLiteralExpr\n"
                + "        boolean b = true;              // BooleanLiteralExpr\n"
                + "        long l = 123456789L;           // LongLiteralExpr\n"
                + "        Object o = null;               // NullLiteralExpr\n"
                + "        System.out.println(s + x + d + c + b + l + o + param1 + param2);\n"
                + "    }\n"
                + "}";

        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);

        OperandVisitor visitor = new OperandVisitor();
        bodyDecl.accept(visitor, null);


        Map<String, Integer> operands = visitor.getOperandsPerMethod();


        assertEquals(1, operands.get("TestClass").intValue());
        assertEquals(1, operands.get("testMethod").intValue());
        assertEquals(2, operands.get("s").intValue());
        assertEquals(2, operands.get("x").intValue());
        assertEquals(2, operands.get("d").intValue());
        assertEquals(2, operands.get("c").intValue());
        assertEquals(2, operands.get("b").intValue());
        assertEquals(2, operands.get("l").intValue());
        assertEquals(2, operands.get("o").intValue());
        assertEquals(2, operands.get("param1").intValue());
        assertEquals(2, operands.get("param2").intValue());
        assertEquals(1, operands.get("hello").intValue());
        assertEquals(1, operands.get("42").intValue());
        assertEquals(1, operands.get("3.14").intValue());
        assertEquals(1, operands.get("a").intValue());
        assertEquals(1, operands.get("true").intValue());
        assertEquals(1, operands.get("NULL").intValue());
        assertEquals(1, operands.get("System").intValue());
        assertEquals(1, operands.get("out").intValue());
        assertEquals(1, operands.get("println").intValue());
    }

    @Test
    void testVisitCharLiteralExprCallsSuper() {
        OperandVisitor visitor = new OperandVisitor();
        CharLiteralExpr expr = new CharLiteralExpr('a');
        visitor.visit(expr, null);
        assertContainsOperand(visitor, "a");
    }


    @Test
    void testVisitDoubleLiteralExprCallsSuper() {
        OperandVisitor visitor = new OperandVisitor();
        DoubleLiteralExpr expr = new DoubleLiteralExpr("3.14");
        visitor.visit(expr, null);
        assertContainsOperand(visitor, "3.14");
    }

    @Test
    void testVisitIntegerLiteralExprCallsSuper() {
        OperandVisitor visitor = new OperandVisitor();
        IntegerLiteralExpr expr = new IntegerLiteralExpr("42");
        visitor.visit(expr, null);
        assertContainsOperand(visitor, "42");
    }

    @Test
    void testVisitLongLiteralExprCallsSuper() {
        OperandVisitor visitor = new OperandVisitor();
        LongLiteralExpr expr = new LongLiteralExpr("123L");
        visitor.visit(expr, null);
        assertContainsOperand(visitor, "123L");
    }

    @Test
    void testVisitNullLiteralExprCallsSuper() {
        OperandVisitor visitor = new OperandVisitor();
        NullLiteralExpr expr = new NullLiteralExpr();
        visitor.visit(expr, null);
        assertContainsOperand(visitor, "NULL");
    }

    @Test
    void testVisitSimpleNameCallsSuper() {
        OperandVisitor visitor = new OperandVisitor();
        SimpleName expr = new SimpleName("foo");
        visitor.visit(expr, null);
        assertContainsOperand(visitor, "foo");
    }

    @Test
    void testVisitStringLiteralExprCallsSuper() {
        OperandVisitor visitor = new OperandVisitor();
        StringLiteralExpr expr = new StringLiteralExpr("bar");
        visitor.visit(expr, null);
        assertContainsOperand(visitor, "bar");
    }

    @Test
    void testVisitBooleanLiteralExprTrue() {
        OperandVisitor visitor = new OperandVisitor();
        BooleanLiteralExpr expr = new BooleanLiteralExpr(true);
        visitor.visit(expr, null);
        assertContainsOperand(visitor, "true");
    }

    @Test
    void testVisitBooleanLiteralExprFalse() {
        OperandVisitor visitor = new OperandVisitor();
        BooleanLiteralExpr expr = new BooleanLiteralExpr(false);
        visitor.visit(expr, null);
        assertContainsOperand(visitor, "false");
    }

    private void assertContainsOperand(OperandVisitor visitor, String expected) {
        assertTrue(visitor.getOperandsPerMethod().keySet().stream()
                        .anyMatch(op -> op.equals(expected)),
                "Expected operand '" + expected + "' was not found.");
    }
}
