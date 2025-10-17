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
    void setUp() {
        x = new Variable("x");
        y = new Variable("y");
        longName = new Variable("variable1");
    }

    @Test
    void testConstructor() {
        assertThrows(InvalidInputException.class, () -> new Variable(null));
        assertThrows(InvalidInputException.class, () -> new Variable(""));
        assertThrows(InvalidInputException.class, () -> new Variable("   "));

        Variable trimmed = new Variable("  x  ");
        assertEquals("x", trimmed.getName());
    }

    @Test
    void testEval() {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 10.0);
        variables.put("y", 5.5);
        variables.put("variable1", -2.0);

        assertEquals(10.0, x.eval(variables));
        assertEquals(5.5, y.eval(variables));
        assertEquals(-2.0, longName.eval(variables));
    }

    @Test
    void testEvalMissingVariable() {
        Map<String, Double> variables = new HashMap<>();
        variables.put("y", 5.0);

        assertThrows(VariableNotDefinedException.class, () -> x.eval(variables));
    }

    @Test
    void testEvalWithString() {
        assertEquals(10.0, x.eval("x = 10"));
        assertEquals(5.5, y.eval("y = 5.5"));
        assertEquals(-2.0, longName.eval("variable1 = -2"));

        // Multiple variables
        assertEquals(10.0, x.eval("x = 10; y = 5"));

        assertThrows(VariableNotDefinedException.class, () -> x.eval("y = 10"));
    }

    @Test
    void testDerivative() {
        Expression dx_dx = x.derivative("x");
        Expression dx_dy = x.derivative("y");

        assertTrue(dx_dx instanceof Number);
        assertTrue(dx_dy instanceof Number);

        assertEquals(1.0, ((Number) dx_dx).getValue());
        assertEquals(0.0, ((Number) dx_dy).getValue());
    }

    @Test
    void testPrint() {
        assertEquals("x", x.print());
        assertEquals("y", y.print());
        assertEquals("variable1", longName.print());
    }

    @Test
    void testSimplify() {
        Expression simplified = x.simplify();
        assertSame(x, simplified); // Should return same object
    }

    @Test
    void testHasVariables() {
        assertTrue(x.hasVariables());
        assertTrue(y.hasVariables());
        assertTrue(longName.hasVariables());
    }

    @Test
    void testEquals() {
        Variable anotherX = new Variable("x");
        Variable differentVar = new Variable("z");

        assertEquals(x, anotherX);
        assertNotEquals(x, differentVar);
        assertNotEquals(x, null);
        assertNotEquals(x, "not a variable");
        assertEquals(x, x);
    }

    @Test
    void testHashCode() {
        Variable anotherX = new Variable("x");
        assertEquals(x.hashCode(), anotherX.hashCode());
    }

    @Test
    void testGetName() {
        assertEquals("x", x.getName());
        assertEquals("y", y.getName());
        assertEquals("variable1", longName.getName());
    }
}
