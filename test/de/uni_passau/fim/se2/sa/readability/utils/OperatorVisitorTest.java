package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import org.junit.jupiter.api.Test;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;

public class OperatorVisitorTest {

    @Test
    public void testOperandVisitorCollectsCorrectOperands() throws ParseException {
        String code = """
            public class Test {
                public void method() {
                    int a = 5;
                    int b = a + 3;
                    b++;
                    boolean c = (b > 2) ? true : false;
                    boolean d = a instanceof Integer;
                }
            }
            """;

        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);

        OperatorVisitor visitor = new OperatorVisitor();
        bodyDecl.accept(visitor, null);


        Map<OperatorVisitor.OperatorType, Integer> operands = visitor.getOperatorsPerMethod();

        assertEquals(4, operands.getOrDefault(OperatorVisitor.OperatorType.ASSIGNMENT, 0));
        assertEquals(2, operands.getOrDefault(OperatorVisitor.OperatorType.BINARY, 0));
        assertEquals(1, operands.getOrDefault(OperatorVisitor.OperatorType.UNARY, 0));
        assertEquals(1, operands.getOrDefault(OperatorVisitor.OperatorType.CONDITIONAL, 0));
        assertEquals(1, operands.getOrDefault(OperatorVisitor.OperatorType.TYPE_COMPARISON, 0));
    }

    @Test
    public void testEmptyVisitorHasNoOperators() throws ParseException {
        String code = "public class Test {}";
        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        bodyDecl.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertTrue(ops.isEmpty());
    }

    @Test
    public void testAssignmentOperatorsCount() throws ParseException {
        String code = "public class Test { void m() { int a = 1; a = a + 2; } }";
        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        bodyDecl.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertEquals(2, ops.getOrDefault(OperatorVisitor.OperatorType.ASSIGNMENT, 0));
    }

    @Test
    public void testBinaryOperatorsCount() throws ParseException {
        String code = "public class Test { void m() { int x = 1 + 2 - 3 * 4 / 5; } }";
        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        bodyDecl.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertEquals(4, ops.getOrDefault(OperatorVisitor.OperatorType.BINARY, 0));
    }

    @Test
    public void testConditionalExpressionCount() throws ParseException {
        String code = "public class Test { void m() { int x = (1 > 0) ? 10 : 20; } }";
        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        bodyDecl.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertEquals(1, ops.getOrDefault(OperatorVisitor.OperatorType.CONDITIONAL, 0));
    }

    @Test
    public void testInstanceOfCount() throws ParseException {
        String code = "public class Test { void m(Object obj) { boolean b = obj instanceof String; } }";
        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        bodyDecl.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertEquals(1, ops.getOrDefault(OperatorVisitor.OperatorType.TYPE_COMPARISON, 0));
    }

    @Test
    public void testMixedOperatorsCount() throws ParseException {
        String code = "public class Test { void m(Object obj) { int a = 1 + 2; a = a * 3; boolean b = a > 3 ? obj instanceof String : false; } }";
        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        bodyDecl.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertAll("Mixed operators",
                () -> assertEquals(3, ops.getOrDefault(OperatorVisitor.OperatorType.ASSIGNMENT, 0)),
                () -> assertEquals(3, ops.getOrDefault(OperatorVisitor.OperatorType.BINARY, 0)),
                () -> assertEquals(0, ops.getOrDefault(OperatorVisitor.OperatorType.UNARY, 0)),
                () -> assertEquals(1, ops.getOrDefault(OperatorVisitor.OperatorType.CONDITIONAL, 0)),
                () -> assertEquals(1, ops.getOrDefault(OperatorVisitor.OperatorType.TYPE_COMPARISON, 0))
        );
    }

}
