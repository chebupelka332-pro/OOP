package ru.nsu.tokarev.expressions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;
import ru.nsu.tokarev.exceptions.*;

class NumberTest {
    
    private Number positiveNumber;
    private Number negativeNumber;
    private Number zero;
    private Number decimalNumber;

    @BeforeEach
    void setUp() {
        positiveNumber = new Number(5.0);
        negativeNumber = new Number(-3.0);
        zero = new Number(0.0);
        decimalNumber = new Number(3.14);
    }

    @Test
    void testConstructor() {
        assertEquals(5.0, positiveNumber.getValue());
        assertEquals(-3.0, negativeNumber.getValue());
        assertEquals(0.0, zero.getValue());
        assertEquals(3.14, decimalNumber.getValue());
    }
    
    @Test
    void testEval() throws ExpressionException {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 10.0);

        assertEquals(5.0, positiveNumber.eval(variables));
        assertEquals(-3.0, negativeNumber.eval(variables));
        assertEquals(0.0, zero.eval(variables));
        assertEquals(3.14, decimalNumber.eval(variables));
    }
    
    @Test
    void testEvalWithString() throws ExpressionException {
        assertEquals(5.0, positiveNumber.eval("x = 10"));
        assertEquals(-3.0, negativeNumber.eval(""));
        assertEquals(0.0, zero.eval("x = 5; y = 3"));
        assertEquals(3.14, decimalNumber.eval(""));
    }
    
    @Test
    void testDerivative() {
        Expression derivative = positiveNumber.derivative("x");
        assertTrue(derivative instanceof Number);
        assertEquals(0.0, ((Number) derivative).getValue());

        Expression derivative2 = negativeNumber.derivative("y");
        assertTrue(derivative2 instanceof Number);
        assertEquals(0.0, ((Number) derivative2).getValue());
    }
    
    @Test
    void testPrint() {
        assertEquals("5", positiveNumber.print());
        assertEquals("-3", negativeNumber.print());
        assertEquals("0", zero.print());
        assertEquals("3.14", decimalNumber.print());
    }
    
    @Test
    void testSimplify() throws ExpressionException {
        assertEquals(positiveNumber, positiveNumber.simplify());
        assertEquals(negativeNumber, negativeNumber.simplify());
        assertEquals(zero, zero.simplify());
        assertEquals(decimalNumber, decimalNumber.simplify());
    }
    
    @Test
    void testHasVariables() {
        assertFalse(positiveNumber.hasVariables());
        assertFalse(negativeNumber.hasVariables());
        assertFalse(zero.hasVariables());
        assertFalse(decimalNumber.hasVariables());
    }
    
    @Test
    void testEquals() {
        Number another5 = new Number(5.0);
        Number different = new Number(6.0);

        assertEquals(positiveNumber, another5);
        assertNotEquals(positiveNumber, different);
        assertNotEquals(positiveNumber, negativeNumber);
    }
    
    @Test
    void testHashCode() {
        Number another5 = new Number(5.0);
        assertEquals(positiveNumber.hashCode(), another5.hashCode());
    }
}
