package ru.nsu.tokarev.expressions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;
import ru.nsu.tokarev.exceptions.*;

class VariableTest {

    private Variable x;
    private Variable y;
    private Variable longName;

    @BeforeEach
    void setUp() throws InvalidInputException {
        x = new Variable("x");
        y = new Variable("y");
        longName = new Variable("variableName");
    }

    @Test
    void testConstructor() {
        assertThrows(InvalidInputException.class, () -> new Variable(null));
        assertThrows(InvalidInputException.class, () -> new Variable(""));
        assertThrows(InvalidInputException.class, () -> new Variable("   "));
    }

    @Test
    void testValidConstructor() throws InvalidInputException {
        Variable validVar = new Variable("  validName  ");
        assertEquals("validName", validVar.getName());
    }

    @Test
    void testEval() throws ExpressionException {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 10.0);
        variables.put("y", 5.0);
        variables.put("variableName", 42.0);

        assertEquals(10.0, x.eval(variables));
        assertEquals(5.0, y.eval(variables));
        assertEquals(42.0, longName.eval(variables));
    }

    @Test
    void testEvalUndefinedVariable() {
        Map<String, Double> variables = new HashMap<>();
        variables.put("y", 5.0);

        assertThrows(VariableNotDefinedException.class, () -> x.eval(variables));
    }

    @Test
    void testEvalWithString() throws ExpressionException {
        assertEquals(10.0, x.eval("x = 10"));
        assertEquals(5.0, y.eval("x = 10; y = 5"));
        assertEquals(42.0, longName.eval("variableName = 42"));
    }

    @Test
    void testEvalWithStringUndefined() {
        assertThrows(VariableNotDefinedException.class, () -> x.eval("y = 5"));
    }

    @Test
    void testDerivative() {
        Expression derivativeX = x.derivative("x");
        assertTrue(derivativeX instanceof Number);
        assertEquals(1.0, ((Number) derivativeX).getValue());

        Expression derivativeY = x.derivative("y");
        assertTrue(derivativeY instanceof Number);
        assertEquals(0.0, ((Number) derivativeY).getValue());

        Expression derivativeY2 = y.derivative("y");
        assertTrue(derivativeY2 instanceof Number);
        assertEquals(1.0, ((Number) derivativeY2).getValue());
    }

    @Test
    void testPrint() {
        assertEquals("x", x.print());
        assertEquals("y", y.print());
        assertEquals("variableName", longName.print());
    }

    @Test
    void testSimplify() throws ExpressionException {
        assertEquals(x, x.simplify());
        assertEquals(y, y.simplify());
        assertEquals(longName, longName.simplify());
    }

    @Test
    void testHasVariables() {
        assertTrue(x.hasVariables());
        assertTrue(y.hasVariables());
        assertTrue(longName.hasVariables());
    }

    @Test
    void testEquals() throws InvalidInputException {
        Variable anotherX = new Variable("x");
        Variable differentVar = new Variable("z");

        assertEquals(x, anotherX);
        assertNotEquals(x, y);
        assertNotEquals(x, differentVar);
    }

    @Test
    void testHashCode() throws InvalidInputException {
        Variable anotherX = new Variable("x");
        assertEquals(x.hashCode(), anotherX.hashCode());
    }

    @Test
    void testGetName() {
        assertEquals("x", x.getName());
        assertEquals("y", y.getName());
        assertEquals("variableName", longName.getName());
    }
}
