package ru.nsu.tokarev.expressions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;

class SubTest {
    
    private Sub simpleSub;
    private Sub complexSub;
    private Sub subWithZero;
    private Sub sameSub;
    private Variable x;
    private Variable y;
    private Number three;
    private Number zero;
    private Number five;
    
    @BeforeEach
    void setUp() {
        x = new Variable("x");
        y = new Variable("y");
        three = new Number(3);
        zero = new Number(0);
        five = new Number(5);
        
        simpleSub = new Sub(five, three);
        complexSub = new Sub(x, new Mul(new Number(2), y));
        subWithZero = new Sub(x, zero);
        sameSub = new Sub(x, x);
    }
    
    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Sub(null, three));
        assertThrows(IllegalArgumentException.class, () -> new Sub(three, null));
    }
    
    @Test
    void testEval() {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 10.0);
        variables.put("y", 3.0);
        
        assertEquals(2.0, simpleSub.eval(variables));
        assertEquals(4.0, complexSub.eval(variables));
        assertEquals(10.0, subWithZero.eval(variables));
        assertEquals(0.0, sameSub.eval(variables));
    }
    
    @Test
    void testDerivative() {
        Expression derivative = complexSub.derivative("x");
        Map<String, Double> vars = new HashMap<>();
        vars.put("x", 5.0);
        vars.put("y", 3.0);
        assertEquals(1.0, derivative.eval(vars)); // d/dx(x - (2*y)) = 1 - 0
    }
    
    @Test
    void testPrint() {
        assertEquals("(5-3)", simpleSub.print());
        assertEquals("(x-(2*y))", complexSub.print());
        assertEquals("(x-0)", subWithZero.print());
    }
    
    @Test
    void testSimplify() {
        // Constants
        Expression simplified = simpleSub.simplify();
        assertTrue(simplified instanceof Number);
        assertEquals(2.0, ((Number) simplified).getValue());
        
        // x - 0 = x
        Expression simplifiedZero = subWithZero.simplify();
        assertTrue(simplifiedZero instanceof Variable);
        assertEquals("x", ((Variable) simplifiedZero).getName());
        
        // x - x = 0
        Expression simplifiedSame = sameSub.simplify();
        assertTrue(simplifiedSame instanceof Number);
        assertEquals(0.0, ((Number) simplifiedSame).getValue());
    }
    
    @Test
    void testEquals() {
        Sub another = new Sub(five, three);
        assertEquals(simpleSub, another);
        assertNotEquals(simpleSub, new Sub(three, five));
    }
    
    @Test
    void testGetOperator() {
        assertEquals("-", simpleSub.getOperator());
    }
}
