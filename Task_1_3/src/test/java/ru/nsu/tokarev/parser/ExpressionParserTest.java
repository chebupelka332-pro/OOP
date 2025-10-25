package ru.nsu.tokarev.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.nsu.tokarev.expressions.*;
import ru.nsu.tokarev.exceptions.*;

class ExpressionParserTest {

    @Test
    void testParseSimpleNumber() throws ParseException, InvalidInputException {
        Expression expr = ExpressionParser.parse("42");
        assertTrue(expr instanceof ru.nsu.tokarev.expressions.Number);
        assertEquals(42.0, ((ru.nsu.tokarev.expressions.Number) expr).getValue());
    }

    @Test
    void testParseNegativeNumber() throws ParseException, InvalidInputException {
        Expression expr = ExpressionParser.parse("-5");
        assertTrue(expr instanceof ru.nsu.tokarev.expressions.Number);
        assertEquals(-5.0, ((ru.nsu.tokarev.expressions.Number) expr).getValue());
    }

    @Test
    void testParseDecimalNumber() throws ParseException, InvalidInputException {
        Expression expr = ExpressionParser.parse("3.14");
        assertTrue(expr instanceof ru.nsu.tokarev.expressions.Number);
        assertEquals(3.14, ((ru.nsu.tokarev.expressions.Number) expr).getValue());
    }

    @Test
    void testParseVariable() throws ParseException, InvalidInputException {
        Expression expr = ExpressionParser.parse("x");
        assertTrue(expr instanceof Variable);
        assertEquals("x", ((Variable) expr).getName());
    }

    @Test
    void testParseSimpleAddition() throws ParseException, InvalidInputException, ExpressionException {
        Expression expr = ExpressionParser.parse("3 + 5");
        assertEquals("(3+5)", expr.print());
        assertEquals(8.0, expr.eval(""));
    }

    @Test
    void testParseSimpleSubtraction() throws ParseException, InvalidInputException, ExpressionException {
        Expression expr = ExpressionParser.parse("10 - 4");
        assertEquals("(10-4)", expr.print());
        assertEquals(6.0, expr.eval(""));
    }

    @Test
    void testParseSimpleMultiplication() throws ParseException, InvalidInputException, ExpressionException {
        Expression expr = ExpressionParser.parse("3 * 4");
        assertEquals("(3*4)", expr.print());
        assertEquals(12.0, expr.eval(""));
    }

    @Test
    void testParseSimpleDivision() throws ParseException, InvalidInputException, ExpressionException {
        Expression expr = ExpressionParser.parse("8 / 2");
        assertEquals("(8/2)", expr.print());
        assertEquals(4.0, expr.eval(""));
    }

    @Test
    void testParsePrecedence() throws ParseException, InvalidInputException {
        Expression expr = ExpressionParser.parse("3 + 2 * 4");
        assertEquals("(3+(2*4))", expr.print());

        Expression expr2 = ExpressionParser.parse("2 * 4 + 3");
        assertEquals("((2*4)+3)", expr2.print());
    }

    @Test
    void testParseParentheses() throws ParseException, InvalidInputException {
        Expression expr = ExpressionParser.parse("(3 + 2) * 4");
        assertEquals("((3+2)*4)", expr.print());
    }

    @Test
    void testParseComplexExpression() throws ParseException, InvalidInputException, ExpressionException {
        Expression expr = ExpressionParser.parse("(3 + 2 * x) / (y - 4)");
        assertEquals("((3+(2*x))/(y-4))", expr.print());

        double result = expr.eval("x = 5; y = 7");
        assertEquals(13.0/3.0, result, 0.001);
    }

    @Test
    void testParseWithWhitespace() throws ParseException, InvalidInputException {
        Expression expr = ExpressionParser.parse("  3   +   2   *   x  ");
        assertEquals("(3+(2*x))", expr.print());
    }

    @Test
    void testParseInvalidInput() {
        assertThrows(InvalidInputException.class, () -> ExpressionParser.parse(null));
        assertThrows(InvalidInputException.class, () -> ExpressionParser.parse(""));
        assertThrows(InvalidInputException.class, () -> ExpressionParser.parse("   "));
    }

    @Test
    void testParseInvalidSyntax() {
        assertThrows(ParseException.class, () -> ExpressionParser.parse("3 +"));
        assertThrows(ParseException.class, () -> ExpressionParser.parse("+ 3"));
        assertThrows(ParseException.class, () -> ExpressionParser.parse("3 + + 4"));
        assertThrows(ParseException.class, () -> ExpressionParser.parse("(3 + 2"));
        assertThrows(ParseException.class, () -> ExpressionParser.parse("3 + 2)"));
        assertThrows(ParseException.class, () -> ExpressionParser.parse("3 @ 4"));
    }

    @Test
    void testParseInvalidNumber() {
        assertThrows(ParseException.class, () -> ExpressionParser.parse("3.14.15"));
        assertThrows(ParseException.class, () -> ExpressionParser.parse("3..14"));
    }

    @Test
    void testOperatorMethods() {
        assertEquals('+', Add.getOperator());
        assertEquals('-', Sub.getOperator());
        assertEquals('*', Mul.getOperator());
        assertEquals('/', Div.getOperator());
    }
}
