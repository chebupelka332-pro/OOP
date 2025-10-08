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
        Expression expr = ExpressionParser.parse("3 + 2 * x");
        assertEquals(23.0, expr.eval("x = 10"));
        assertEquals("(3+(2*x))", expr.print());
    }

    @Test
    void testParseWithoutParenthesesPrecedence() {
        Expression expr1 = ExpressionParser.parse("2 + 3 * 4");
        assertEquals(14.0, expr1.eval(""));

        Expression expr2 = ExpressionParser.parse("12 / 3 + 2");
        assertEquals(6.0, expr2.eval(""));

        Expression expr3 = ExpressionParser.parse("10 - 3 - 2");
        assertEquals(5.0, expr3.eval(""));
    }

    @Test
    void testParseWithoutParenthesesWithParens() {
        Expression expr = ExpressionParser.parse("(2 + 3) * 4");
        assertEquals(20.0, expr.eval(""));
    }

    @Test
    void testParseWithoutParenthesesComplex() {
        Expression expr = ExpressionParser.parse("x * y + z / 2");
        assertEquals(23.0, expr.eval("x = 5; y = 4; z = 6"));
        assertEquals("((x*y)+(z/2))", expr.print());
    }

    @Test
    void testParseErrors() {
        // Null input
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(null));
        
        // Empty input
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(""));
        
        // Missing closing parenthesis
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(3+5"));
        
        // Missing opening parenthesis
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3+5)"));
        
        // Invalid operator
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(3%5)"));
        
        // Unexpected end of input
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(3+"));
        
        // Empty parentheses
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("()"));
    }

    @Test
    void testParseWithoutParenthesesErrors() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(null));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3 +"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(3 + 5"));
    }

    @Test
    void testParseNegativeNumbers() {
        Expression expr1 = ExpressionParser.parse("(-3+5)");
        assertEquals(2.0, expr1.eval(""));

        Expression expr2 = ExpressionParser.parse("-3 + 5");
        assertEquals(2.0, expr2.eval(""));
    }

    @Test
    void testParseWhitespace() {
        Expression expr = ExpressionParser.parse("  3  +  2  *  x  ");
        assertEquals(23.0, expr.eval("x = 10"));
    }

    @Test
    void testParseDecimalNumbers() {
        Expression expr1 = ExpressionParser.parse("3.5 + 2.7");
        assertEquals(6.2, expr1.eval(""), 0.001);

        Expression expr2 = ExpressionParser.parse("(3.14*2.0)");
        assertEquals(6.28, expr2.eval(""), 0.001);

        Expression expr3 = ExpressionParser.parse("-3.5 + 2");
        assertEquals(-1.5, expr3.eval(""), 0.001);
    }

    @Test
    void testParseInvalidNumbers() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3..5"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3.5.2"));
    }

    @Test
    void testParseComplexWhitespaceHandling() {
        Expression expr1 = ExpressionParser.parse("   (3+5)   ");
        assertEquals(8.0, expr1.eval(""));

        Expression expr2 = ExpressionParser.parse("   x + y   ");
        assertEquals(7.0, expr2.eval("x = 3; y = 4"));
    }

    @Test
    void testParseUnexpectedCharacterErrors() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3 @ 5"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x # y"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3 $ 5"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(3@5)"));
    }

    @Test
    void testParseEmptyParenthesesInWithoutParentheses() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("()"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3 + ()"));
    }

    @Test
    void testParseMultipleConsecutiveOperators() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3 ++ 5"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3 +* 5"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3 */ 5"));
    }

    @Test
    void testParseOperatorAtEnd() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3 * 5 +"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x -"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("3 /"));
    }

    @Test
    void testParseVariableNames() {
        Expression expr1 = ExpressionParser.parse("var123 + x1");
        assertEquals(15.0, expr1.eval("var123 = 10; x1 = 5"));

        Expression expr2 = ExpressionParser.parse("(abc+def)");
        assertEquals(9.0, expr2.eval("abc = 4; def = 5"));
    }

    @Test
    void testParseComplexNegativeNumbers() {
        Expression expr1 = ExpressionParser.parse("-5 * -3");
        assertEquals(15.0, expr1.eval(""));

        Expression expr2 = ExpressionParser.parse("x + -2.5");
        assertEquals(2.5, expr2.eval("x = 5"));
    }

    @Test
    void testParseUnmatchedParentheses() {
        // Test various unmatched parentheses scenarios
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("((3 + 5)"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(3 + 5))"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(")(3 + 5)"));
    }

    @Test
    void testParseLeftAssociativity() {
        Expression expr1 = ExpressionParser.parse("10 - 3 - 2");
        assertEquals(5.0, expr1.eval(""));

        Expression expr2 = ExpressionParser.parse("12 / 3 / 2");
        assertEquals(2.0, expr2.eval(""));
    }

    @Test
    void testParseSingleCharacterExpressions() {
        Expression num = ExpressionParser.parse("5");
        assertEquals(5.0, num.eval(""));

        Expression var = ExpressionParser.parse("x");
        assertEquals(10.0, var.eval("x = 10"));
    }

    @Test
    void testParseWhitespaceOnlyInput() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("   "));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("   "));
    }
}
