package ru.nsu.tokarev.expressions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;
import ru.nsu.tokarev.exceptions.*;

class MulTest {
    
    private Mul simpleMul;
    private Mul complexMul;
    private Mul mulByZero;
    private Mul mulByOne;
    private Variable x;
    private Variable y;
    private Number three;
    private Number four;
    private Number zero;
    private Number one;

    @BeforeEach
    void setUp() throws InvalidInputException {
        x = new Variable("x");
        y = new Variable("y");
        three = new Number(3);
        four = new Number(4);
        zero = new Number(0);
        one = new Number(1);

        simpleMul = new Mul(three, four);
        complexMul = new Mul(x, new Add(y, one));
        mulByZero = new Mul(x, zero);
        mulByOne = new Mul(x, one);
    }
    
    @Test
    void testConstructor() {
        assertThrows(InvalidInputException.class, () -> new Mul(null, three));
        assertThrows(InvalidInputException.class, () -> new Mul(three, null));
        assertThrows(InvalidInputException.class, () -> new Mul(null, null));
    }
    
    @Test
    void testEval() throws ExpressionException {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 5.0);
        variables.put("y", 2.0);
        
        assertEquals(12.0, simpleMul.eval(variables)); // 3 * 4
        assertEquals(15.0, complexMul.eval(variables)); // 5 * (2 + 1)
        assertEquals(0.0, mulByZero.eval(variables)); // 5 * 0
        assertEquals(5.0, mulByOne.eval(variables)); // 5 * 1
    }

    @Test
    void testEvalWithString() throws ExpressionException {
        assertEquals(12.0, simpleMul.eval(""));
        assertEquals(15.0, complexMul.eval("x = 5; y = 2"));
        assertEquals(0.0, mulByZero.eval("x = 5"));
        assertEquals(5.0, mulByOne.eval("x = 5"));
    }
    
    @Test
    void testDerivative() throws ExpressionException {
        Expression derivative = complexMul.derivative("x");
        Map<String, Double> vars = Map.of("x", 5.0, "y", 2.0);
        assertEquals(3.0, derivative.eval(vars)); // d/dx[x*(y+1)] = (y+1)
    }
    
    @Test
    void testPrint() {
        assertEquals("(3*4)", simpleMul.print());
        assertEquals("(x*(y+1))", complexMul.print());
        assertEquals("(x*0)", mulByZero.print());
        assertEquals("(x*1)", mulByOne.print());
    }
    
    @Test
    void testSimplify() throws ExpressionException {
        Expression simplified = simpleMul.simplify();
        assertTrue(simplified instanceof Number);
        assertEquals(12.0, ((Number) simplified).getValue());

        Expression simplifiedZero = mulByZero.simplify();
        assertTrue(simplifiedZero instanceof Number);
        assertEquals(0.0, ((Number) simplifiedZero).getValue());

        Expression simplifiedOne = mulByOne.simplify();
        assertEquals(x, simplifiedOne);

        Mul zeroMul = new Mul(zero, x);
        Expression simplifiedZeroLeft = zeroMul.simplify();
        assertTrue(simplifiedZeroLeft instanceof Number);
        assertEquals(0.0, ((Number) simplifiedZeroLeft).getValue());

        Mul oneMul = new Mul(one, x);
        Expression simplifiedOneLeft = oneMul.simplify();
        assertEquals(x, simplifiedOneLeft);
    }
    
    @Test
    void testHasVariables() {
        assertFalse(simpleMul.hasVariables());
        assertTrue(complexMul.hasVariables());
        assertTrue(mulByZero.hasVariables());
        assertTrue(mulByOne.hasVariables());
    }

    @Test
    void testEquals() throws InvalidInputException {
        Mul another = new Mul(three, four);
        Mul different = new Mul(four, three);

        assertEquals(simpleMul, another);
        assertNotEquals(simpleMul, different);
        assertNotEquals(simpleMul, complexMul);
    }
    
    @Test
    void testGetOperator() {
        assertEquals('*', Mul.getOperator());
    }
}
