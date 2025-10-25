package ru.nsu.tokarev.expressions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;
import ru.nsu.tokarev.exceptions.*;

class DivTest {
    
    private Div simpleDiv;
    private Div complexDiv;
    private Div divByOne;
    private Variable x;
    private Variable y;
    private Number six;
    private Number three;
    private Number one;
    private Number zero;

    @BeforeEach
    void setUp() throws InvalidInputException {
        x = new Variable("x");
        y = new Variable("y");
        six = new Number(6);
        three = new Number(3);
        one = new Number(1);
        zero = new Number(0);

        simpleDiv = new Div(six, three);
        complexDiv = new Div(x, new Add(y, one));
        divByOne = new Div(x, one);
    }
    
    @Test
    void testConstructor() {
        assertThrows(InvalidInputException.class, () -> new Div(null, three));
        assertThrows(InvalidInputException.class, () -> new Div(six, null));
        assertThrows(InvalidInputException.class, () -> new Div(null, null));
    }
    
    @Test
    void testEval() throws ExpressionException {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 12.0);
        variables.put("y", 2.0);

        assertEquals(2.0, simpleDiv.eval(variables)); // 6 / 3
        assertEquals(4.0, complexDiv.eval(variables)); // 12 / (2 + 1)
        assertEquals(12.0, divByOne.eval(variables)); // 12 / 1
    }
    
    @Test
    void testEvalWithDivisionByZero() throws InvalidInputException {
        Div divByZero = new Div(six, zero);
        Map<String, Double> variables = new HashMap<>();

        assertThrows(DivisionByZeroException.class, () -> divByZero.eval(variables));
    }
    
    @Test
    void testEvalWithString() throws ExpressionException {
        assertEquals(2.0, simpleDiv.eval(""));
        assertEquals(4.0, complexDiv.eval("x = 12; y = 2"));
        assertEquals(12.0, divByOne.eval("x = 12"));
    }

    @Test
    void testDerivative() throws ExpressionException {
        Expression derivative = complexDiv.derivative("x");
        Map<String, Double> vars = Map.of("x", 12.0, "y", 2.0);
        assertEquals(1.0/3.0, derivative.eval(vars), 0.001);
    }
    
    @Test
    void testPrint() {
        assertEquals("(6/3)", simpleDiv.print());
        assertEquals("(x/(y+1))", complexDiv.print());
        assertEquals("(x/1)", divByOne.print());
    }
    
    @Test
    void testSimplify() throws ExpressionException {
        Expression simplified = simpleDiv.simplify();
        assertTrue(simplified instanceof Number);
        assertEquals(2.0, ((Number) simplified).getValue());

        Expression simplifiedOne = divByOne.simplify();
        assertEquals(x, simplifiedOne);

        Div zeroDiv = new Div(zero, three);
        Expression simplifiedZero = zeroDiv.simplify();
        assertTrue(simplifiedZero instanceof Number);
        assertEquals(0.0, ((Number) simplifiedZero).getValue());

        Div divByZero = new Div(six, zero);
        assertThrows(DivisionByZeroException.class, () -> divByZero.simplify());
    }
    
    @Test
    void testHasVariables() {
        assertFalse(simpleDiv.hasVariables());
        assertTrue(complexDiv.hasVariables());
        assertTrue(divByOne.hasVariables());
    }

    @Test
    void testEquals() throws InvalidInputException {
        Div another = new Div(six, three);
        Div different = new Div(three, six);

        assertEquals(simpleDiv, another);
        assertNotEquals(simpleDiv, different);
        assertNotEquals(simpleDiv, complexDiv);
    }
    
    @Test
    void testGetOperator() {
        assertEquals('/', Div.getOperator());
    }
}
