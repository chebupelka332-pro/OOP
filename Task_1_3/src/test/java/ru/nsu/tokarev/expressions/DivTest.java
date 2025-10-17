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
    private Div zeroDiv;
    private Div sameDiv;
    private Variable x;
    private Variable y;
    private Number six;
    private Number zero;
    private Number one;
    private Number three;
    
    @BeforeEach
    void setUp() {
        x = new Variable("x");
        y = new Variable("y");
        six = new Number(6);
        zero = new Number(0);
        one = new Number(1);
        three = new Number(3);
        
        simpleDiv = new Div(six, three);
        complexDiv = new Div(x, new Add(y, one));
        divByOne = new Div(x, one);
        zeroDiv = new Div(zero, x);
        sameDiv = new Div(x, x);
    }
    
    @Test
    void testConstructor() {
        assertThrows(InvalidInputException.class, () -> new Div(null, three));
        assertThrows(InvalidInputException.class, () -> new Div(three, null));
    }
    
    @Test
    void testEval() {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 8.0);
        variables.put("y", 3.0);
        
        assertEquals(2.0, simpleDiv.eval(variables));
        assertEquals(2.0, complexDiv.eval(variables));
        assertEquals(8.0, divByOne.eval(variables));
        assertEquals(0.0, zeroDiv.eval(variables));
        assertEquals(1.0, sameDiv.eval(variables));
    }
    
    @Test
    void testEvalDivisionByZero() {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 5.0);
        variables.put("y", 0.0);
        
        Div divByZero = new Div(x, y);
        assertThrows(DivisionByZeroException.class, () -> divByZero.eval(variables));
    }
    
    @Test
    void testDerivative() {
        // (u/v)' = (u'*v - u*v') / v^2
        // d/dx(x / (y + 1)) = (1 * (y + 1) - x * 0) / (y + 1)^2 = (y + 1) / (y + 1)^2
        Expression derivative = complexDiv.derivative("x");
        
        Map<String, Double> vars = new HashMap<>();
        vars.put("x", 8.0);
        vars.put("y", 3.0);
        assertEquals(0.25, derivative.eval(vars), 0.001); // 4 / 16
    }
    
    @Test
    void testPrint() {
        assertEquals("(6/3)", simpleDiv.print());
        assertEquals("(x/(y+1))", complexDiv.print());
        assertEquals("(x/1)", divByOne.print());
    }
    
    @Test
    void testSimplify() {
        // Constants
        Expression simplified = simpleDiv.simplify();
        assertTrue(simplified instanceof Number);
        assertEquals(2.0, ((Number) simplified).getValue());
        
        // x / 1 = x
        Expression simplifiedOne = divByOne.simplify();
        assertTrue(simplifiedOne instanceof Variable);
        assertEquals("x", ((Variable) simplifiedOne).getName());
        
        // 0 / x = 0
        Expression simplifiedZero = zeroDiv.simplify();
        assertTrue(simplifiedZero instanceof Number);
        assertEquals(0.0, ((Number) simplifiedZero).getValue());
        
        // x / x = 1
        Expression simplifiedSame = sameDiv.simplify();
        assertTrue(simplifiedSame instanceof Number);
        assertEquals(1.0, ((Number) simplifiedSame).getValue());
    }
    
    @Test
    void testSimplifyDivisionByZero() {
        Div divByZero = new Div(one, zero);
        assertThrows(DivisionByZeroException.class, () -> divByZero.simplify());
    }
    
    @Test
    void testEquals() {
        Div another = new Div(six, three);
        assertEquals(simpleDiv, another);
        assertNotEquals(simpleDiv, new Div(three, six));
    }
    
    @Test
    void testGetOperator() {
        assertEquals('/', Div.getOperator());
    }
}
