package ru.nsu.tokarev.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.nsu.tokarev.expressions.*;

class ExpressionParserTest {

    @Test
    void testParseNumber() {
        Expression expr = ExpressionParser.parse("5");
        assertTrue(expr instanceof ru.nsu.tokarev.expressions.Number);
        assertEquals(5.0, ((ru.nsu.tokarev.expressions.Number) expr).getValue());

        Expression negative = ExpressionParser.parse("-3");
        assertTrue(negative instanceof ru.nsu.tokarev.expressions.Number);
        assertEquals(-3.0, ((ru.nsu.tokarev.expressions.Number) negative).getValue());

        Expression decimal = ExpressionParser.parse("3.14");
        assertTrue(decimal instanceof ru.nsu.tokarev.expressions.Number);
        assertEquals(3.14, ((ru.nsu.tokarev.expressions.Number) decimal).getValue());
    }

    @Test
    void testParseVariable() {
        Expression expr = ExpressionParser.parse("x");
        assertTrue(expr instanceof Variable);
        assertEquals("x", ((Variable) expr).getName());

        Expression longVar = ExpressionParser.parse("variable1");
        assertTrue(longVar instanceof Variable);
        assertEquals("variable1", ((Variable) longVar).getName());
    }

    @Test
    void testParseSimpleAddition() {
        Expression expr = ExpressionParser.parse("(3+5)");
        assertTrue(expr instanceof Add);
        assertEquals(8.0, expr.eval(""));
        assertEquals("(3+5)", expr.print());
    }

    @Test
    void testParseSimpleSubtraction() {
        Expression expr = ExpressionParser.parse("(5-3)");
        assertTrue(expr instanceof Sub);
        assertEquals(2.0, expr.eval(""));
        assertEquals("(5-3)", expr.print());
    }

    @Test
    void testParseSimpleMultiplication() {
        Expression expr = ExpressionParser.parse("(3*4)");
        assertTrue(expr instanceof Mul);
        assertEquals(12.0, expr.eval(""));
        assertEquals("(3*4)", expr.print());
    }

    @Test
    void testParseSimpleDivision() {
        Expression expr = ExpressionParser.parse("(8/2)");
        assertTrue(expr instanceof Div);
        assertEquals(4.0, expr.eval(""));
        assertEquals("(8/2)", expr.print());
    }

    @Test
    void testParseWithVariables() {
        Expression expr = ExpressionParser.parse("(x+y)");
        assertTrue(expr instanceof Add);
        assertEquals(15.0, expr.eval("x = 10; y = 5"));
        assertEquals("(x+y)", expr.print());
    }

    @Test
    void testParseComplexExpression() {
        Expression expr = ExpressionParser.parse("(3+(2*x))");
        assertTrue(expr instanceof Add);
        assertEquals(23.0, expr.eval("x = 10"));
        assertEquals("(3+(2*x))", expr.print());
    }

    @Test
    void testParseNestedExpressions() {
        Expression expr = ExpressionParser.parse("((x+y)*(z-w))");
        assertTrue(expr instanceof Mul);
        // (x+y) * (z-w) = (2+3) * (7-3) = 5 * 4 = 20
        assertEquals(20.0, expr.eval("x = 2; y = 3; z = 7; w = 3"));
        assertEquals("((x+y)*(z-w))", expr.print());
    }

    @Test
    void testParseWithoutParentheses() {
        Expression expr = ExpressionParser.parseWithoutParentheses("3 + 2 * x");
        assertEquals(23.0, expr.eval("x = 10"));
        assertEquals("(3+(2*x))", expr.print());
    }

    @Test
    void testParseWithoutParenthesesPrecedence() {
        Expression expr1 = ExpressionParser.parseWithoutParentheses("2 + 3 * 4");
        assertEquals(14.0, expr1.eval(""));

        Expression expr2 = ExpressionParser.parseWithoutParentheses("12 / 3 + 2");
        assertEquals(6.0, expr2.eval(""));

        Expression expr3 = ExpressionParser.parseWithoutParentheses("10 - 3 - 2");
        assertEquals(5.0, expr3.eval(""));
    }

    @Test
    void testParseWithoutParenthesesWithParens() {
        Expression expr = ExpressionParser.parseWithoutParentheses("(2 + 3) * 4");
        assertEquals(20.0, expr.eval(""));
    }

    @Test
    void testParseWithoutParenthesesComplex() {
        Expression expr = ExpressionParser.parseWithoutParentheses("x * y + z / 2");
        assertEquals(23.0, expr.eval("x = 5; y = 4; z = 6"));
        assertEquals("((x*y)+(z/2))", expr.print());
    }

    @Test
    void testParseErrors() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(null));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(3%5)"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(3+"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("()"));
    }

    @Test
    void testParseWithoutParenthesesErrors() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parseWithoutParentheses(null));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parseWithoutParentheses(""));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parseWithoutParentheses("3 +"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parseWithoutParentheses("(3 + 5"));
    }

    @Test
    void testParseNegativeNumbers() {
        Expression expr1 = ExpressionParser.parse("(-3+5)");
        assertEquals(2.0, expr1.eval(""));

        Expression expr2 = ExpressionParser.parseWithoutParentheses("-3 + 5");
        assertEquals(2.0, expr2.eval(""));
    }

    @Test
    void testParseWhitespace() {
        Expression expr = ExpressionParser.parseWithoutParentheses("  3  +  2  *  x  ");
        assertEquals(23.0, expr.eval("x = 10"));
    }
}
