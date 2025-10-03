package ru.nsu.tokarev.expressions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;

class NumberTest {
    
    private ru.nsu.tokarev.expressions.Number number;
    private ru.nsu.tokarev.expressions.Number zero;
    private ru.nsu.tokarev.expressions.Number negativeNumber;
    private ru.nsu.tokarev.expressions.Number decimal;
    
    @BeforeEach
    void setUp() {
        number = new ru.nsu.tokarev.expressions.Number(5);
        zero = new ru.nsu.tokarev.expressions.Number(0);
        negativeNumber = new ru.nsu.tokarev.expressions.Number(-3);
        decimal = new ru.nsu.tokarev.expressions.Number(3.14);
    }
    
    @Test
    void testEval() {
        Map<String, Double> variables = new HashMap<>();
        assertEquals(5.0, number.eval(variables));
        assertEquals(0.0, zero.eval(variables));
        assertEquals(-3.0, negativeNumber.eval(variables));
        assertEquals(3.14, decimal.eval(variables), 0.001);
    }
    
    @Test
    void testEvalWithString() {
        assertEquals(5.0, number.eval("x = 10"));
        assertEquals(5.0, number.eval(""));
        Map<String, Double> emptyMap = new HashMap<>();
        assertEquals(5.0, number.eval(emptyMap));
    }
    
    @Test
    void testDerivative() {
        Expression derivative = number.derivative("x");
        assertTrue(derivative instanceof ru.nsu.tokarev.expressions.Number);
        assertEquals(0.0, ((ru.nsu.tokarev.expressions.Number) derivative).getValue());

        derivative = number.derivative("y");
        assertEquals(0.0, ((ru.nsu.tokarev.expressions.Number) derivative).getValue());
    }
    
    @Test
    void testPrint() {
        assertEquals("5", number.print());
        assertEquals("0", zero.print());
        assertEquals("-3", negativeNumber.print());
        assertEquals("3.14", decimal.print());
    }
    
    @Test
    void testToString() {
        assertEquals("5", number.toString());
        assertEquals("0", zero.toString());
    }
    
    @Test
    void testSimplify() {
        Expression simplified = number.simplify();
        assertSame(number, simplified); // Should return same object
    }
    
    @Test
    void testHasVariables() {
        assertFalse(number.hasVariables());
        assertFalse(zero.hasVariables());
        assertFalse(negativeNumber.hasVariables());
    }
    
    @Test
    void testEquals() {
        ru.nsu.tokarev.expressions.Number another5 = new ru.nsu.tokarev.expressions.Number(5);
        ru.nsu.tokarev.expressions.Number different = new ru.nsu.tokarev.expressions.Number(10);
        
        assertEquals(number, another5);
        assertNotEquals(number, different);
        assertNotEquals(number, null);
        assertNotEquals(number, "not a number");
        assertEquals(number, number);
    }
    
    @Test
    void testHashCode() {
        ru.nsu.tokarev.expressions.Number another5 = new ru.nsu.tokarev.expressions.Number(5);
        assertEquals(number.hashCode(), another5.hashCode());
    }
    
    @Test
    void testGetValue() {
        assertEquals(5.0, number.getValue());
        assertEquals(0.0, zero.getValue());
        assertEquals(-3.0, negativeNumber.getValue());
    }
}
