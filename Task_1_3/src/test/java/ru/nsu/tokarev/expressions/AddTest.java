package ru.nsu.tokarev.expressions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;
import ru.nsu.tokarev.exceptions.*;

class AddTest {
    
    private Add simpleAdd;
    private Add complexAdd;
    private Add addWithZero;
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
        
        simpleAdd = new Add(three, five);
        complexAdd = new Add(x, new Mul(new Number(2), y));
        addWithZero = new Add(x, zero);
    }
    
    @Test
    void testConstructor() {
        assertThrows(InvalidInputException.class, () -> new Add(null, three));
        assertThrows(InvalidInputException.class, () -> new Add(three, null));
        assertThrows(InvalidInputException.class, () -> new Add(null, null));
    }
    
    @Test
    void testEval() {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 10.0);
        variables.put("y", 5.0);
        
        assertEquals(8.0, simpleAdd.eval(variables)); // 3 + 5
        assertEquals(20.0, complexAdd.eval(variables)); // 10 + (2 * 5)
        assertEquals(10.0, addWithZero.eval(variables)); // 10 + 0
    }
    
    @Test
    void testEvalWithString() {
        assertEquals(8.0, simpleAdd.eval(""));
        assertEquals(20.0, complexAdd.eval("x = 10; y = 5"));
        assertEquals(10.0, addWithZero.eval("x = 10"));
    }
    
    @Test
    void testDerivative() {
        Expression derivative = complexAdd.derivative("x");
        // d/dx(x + (2 * y)) = 1 + 0 = 1 + 0
        assertTrue(derivative instanceof Add);
        
        Map<String, Double> vars = new HashMap<>();
        vars.put("x", 5.0);
        vars.put("y", 3.0);
        assertEquals(1.0, derivative.eval(vars));
        
        Expression derivativeY = complexAdd.derivative("y");
        // d/dy(x + (2 * y)) = 0 + 2 = 0 + 2
        assertEquals(2.0, derivativeY.eval(vars));
    }
    
    @Test
    void testPrint() {
        assertEquals("(3+5)", simpleAdd.print());
        assertEquals("(x+(2*y))", complexAdd.print());
        assertEquals("(x+0)", addWithZero.print());
    }
    
    @Test
    void testSimplify() {
        // Constants should be computed
        Expression simplified = simpleAdd.simplify();
        assertTrue(simplified instanceof Number);
        assertEquals(8.0, ((Number) simplified).getValue());
        
        // x + 0 = x
        Expression simplifiedZero = addWithZero.simplify();
        assertTrue(simplifiedZero instanceof Variable);
        assertEquals("x", ((Variable) simplifiedZero).getName());
        
        // 0 + x = x
        Add zeroAdd = new Add(zero, x);
        Expression simplifiedZeroLeft = zeroAdd.simplify();
        assertTrue(simplifiedZeroLeft instanceof Variable);
        assertEquals("x", ((Variable) simplifiedZeroLeft).getName());
        
        // Expression with variables should remain as Add but with simplified operands
        Expression simplifiedComplex = complexAdd.simplify();
        assertTrue(simplifiedComplex instanceof Add);
    }
    
    @Test
    void testHasVariables() {
        assertFalse(simpleAdd.hasVariables());
        assertTrue(complexAdd.hasVariables());
        assertTrue(addWithZero.hasVariables());
    }
    
    @Test
    void testEquals() {
        Add another = new Add(three, five);
        Add different = new Add(five, three);
        
        assertEquals(simpleAdd, another);
        assertNotEquals(simpleAdd, different);
        assertNotEquals(simpleAdd, null);
        assertNotEquals(simpleAdd, "not an add");
        assertEquals(simpleAdd, simpleAdd);
    }
    
    @Test
    void testHashCode() {
        Add another = new Add(three, five);
        assertEquals(simpleAdd.hashCode(), another.hashCode());
    }
    
    @Test
    void testGetOperator() {
        assertEquals('+', Add.getOperator());
    }
    
    @Test
    void testGetLeftRight() {
        assertEquals(three, simpleAdd.getLeft());
        assertEquals(five, simpleAdd.getRight());
    }
}
