package de.uni_passau.fim.se2.sa.readability.utils;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CyclomaticComplexityVisitorTest {

    @Test
    public void testGetComplexity() throws ParseException {
        String code = """
                public class TestClass {
                    public void testMethod(int x, int[] arr) {
                        if (x > 0) {
                            System.out.println("if");
                        }
                
                        for (int i = 0; i < 10; i++) {
                            System.out.println("for");
                        }
                
                        for (int a : arr) {
                            System.out.println("foreach");
                        }
                
                        while (x < 5) {
                            x++;
                        }
                
                        do {
                            x--;
                        } while (x > 0);
                
                        switch (x) {
                            case 1:
                                System.out.println("case 1");
                                break;
                            default:
                                System.out.println("default");
                        }
                
                        try {
                            int y = 1 / x;
                        } catch (ArithmeticException e) {
                            System.out.println("caught");
                        }
                
                        String result = (x > 5) ? "big" : "small";
                
                        boolean check = (x > 1 && x < 100) || x == 0;
                    }
                }
                """;

        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);

        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        bodyDecl.accept(visitor, null);


        assertEquals(11, visitor.getComplexity());
    }

    @Test
    void testVisitConditionalExpr() throws ParseException {
        String code = "class T { void m() { int x = (a > b) ? 1 : 2; } }";
        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        bodyDecl.accept(visitor, null);
        assertEquals(2, visitor.getComplexity());
    }

    @Test
    void testVisitNestedConditionalExpr() throws ParseException {
        String code = "class T { void m() { int x = (a > b) ? ((c > d) ? 1 : 2) : 3; } }";
        BodyDeclaration<?> bodyDecl = Parser.parseJavaSnippet(code);

        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        bodyDecl.accept(visitor, null);

        assertEquals(3, visitor.getComplexity());
    }

    @Test
    void testVisitCatchClauseCallsSuper() {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        CatchClause catchClause = new CatchClause(
                new Parameter(new ClassOrInterfaceType(null, "Exception"), "e"),
                new BlockStmt()
        );
        int before = visitor.getComplexity();
        visitor.visit(catchClause, null);
        int after = visitor.getComplexity();

        assertTrue(after > before);
    }

    @Test
    void testVisitConditionalExprCallsSuper() {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        ConditionalExpr expr = new ConditionalExpr(
                new BooleanLiteralExpr(true),
                new BooleanLiteralExpr(false),
                new BooleanLiteralExpr(true)
        );
        int before = visitor.getComplexity();
        visitor.visit(expr, null);
        int after = visitor.getComplexity();
        assertTrue(after > before);
    }

}
