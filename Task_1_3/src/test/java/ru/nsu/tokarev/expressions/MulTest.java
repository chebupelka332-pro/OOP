package ru.nsu.tokarev.expressions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;

class MulTest {
    
    private Mul simpleMul;
    private Mul complexMul;
    private Mul mulByZero;
    private Mul mulByOne;
    private Variable x;
    private Variable y;
    private Number three;
    private Number zero;
    private Number one;
    private Number five;
    
    @BeforeEach
    void setUp() {
        x = new Variable("x");
        y = new Variable("y");
        three = new Number(3);
        zero = new Number(0);
        one = new Number(1);
        five = new Number(5);
        
        simpleMul = new Mul(three, five);
        complexMul = new Mul(x, new Add(y, three));
        mulByZero = new Mul(x, zero);
        mulByOne = new Mul(x, one);
    }
    
    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Mul(null, three));
        assertThrows(IllegalArgumentException.class, () -> new Mul(three, null));
    }
    
    @Test
    void testEval() {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 4.0);
        variables.put("y", 2.0);
        
        assertEquals(15.0, simpleMul.eval(variables));
        assertEquals(20.0, complexMul.eval(variables));
        assertEquals(0.0, mulByZero.eval(variables));
        assertEquals(4.0, mulByOne.eval(variables));
    }
    
    @Test
    void testDerivative() {
        // (u*v)' = u'*v + u*v'
        // d/dx(x * (y + 3)) = 1 * (y + 3) + x * 0 = (y + 3)
        Expression derivative = complexMul.derivative("x");
        
        Map<String, Double> vars = new HashMap<>();
        vars.put("x", 4.0);
        vars.put("y", 2.0);
        assertEquals(5.0, derivative.eval(vars));
        
        Expression derivativeY = complexMul.derivative("y");
        // d/dy(x * (y + 3)) = 0 * (y + 3) + x * 1 = x
        assertEquals(4.0, derivativeY.eval(vars));
    }
    
    @Test
    void testPrint() {
        assertEquals("(3*5)", simpleMul.print());
        assertEquals("(x*(y+3))", complexMul.print());
        assertEquals("(x*0)", mulByZero.print());
    }
    
    @Test
    void testSimplify() {
        // Constants
        Expression simplified = simpleMul.simplify();
        assertTrue(simplified instanceof Number);
        assertEquals(15.0, ((Number) simplified).getValue());
        
        // x * 0 = 0
        Expression simplifiedZero = mulByZero.simplify();
        assertTrue(simplifiedZero instanceof Number);
        assertEquals(0.0, ((Number) simplifiedZero).getValue());
        
        // 0 * x = 0
        Mul zeroMul = new Mul(zero, x);
        Expression simplifiedZeroLeft = zeroMul.simplify();
        assertTrue(simplifiedZeroLeft instanceof Number);
        assertEquals(0.0, ((Number) simplifiedZeroLeft).getValue());
        
        // x * 1 = x
        Expression simplifiedOne = mulByOne.simplify();
        assertTrue(simplifiedOne instanceof Variable);
        assertEquals("x", ((Variable) simplifiedOne).getName());
        
        // 1 * x = x
        Mul oneMul = new Mul(one, x);
        Expression simplifiedOneLeft = oneMul.simplify();
        assertTrue(simplifiedOneLeft instanceof Variable);
        assertEquals("x", ((Variable) simplifiedOneLeft).getName());
    }
    
    @Test
    void testEquals() {
        Mul another = new Mul(three, five);
        assertEquals(simpleMul, another);
        assertNotEquals(simpleMul, new Mul(five, three));
    }
    
    @Test
    void testGetOperator() {
        assertEquals("*", simpleMul.getOperator());
    }
}
