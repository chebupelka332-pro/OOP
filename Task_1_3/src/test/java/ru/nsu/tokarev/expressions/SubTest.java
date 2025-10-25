package ru.nsu.tokarev.expressions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;
import ru.nsu.tokarev.exceptions.*;

class SubTest {
    
    private Sub simpleSub;
    private Sub complexSub;
    private Sub subZero;
    private Variable x;
    private Variable y;
    private Number five;
    private Number three;
    private Number zero;

    @BeforeEach
    void setUp() throws InvalidInputException {
        x = new Variable("x");
        y = new Variable("y");
        five = new Number(5);
        three = new Number(3);
        zero = new Number(0);

        simpleSub = new Sub(five, three);
        complexSub = new Sub(x, new Mul(new Number(2), y));
        subZero = new Sub(x, zero);
    }
    
    @Test
    void testConstructor() {
        assertThrows(InvalidInputException.class, () -> new Sub(null, three));
        assertThrows(InvalidInputException.class, () -> new Sub(five, null));
        assertThrows(InvalidInputException.class, () -> new Sub(null, null));
    }
    
    @Test
    void testEval() throws ExpressionException {
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 10.0);
        variables.put("y", 3.0);
        
        assertEquals(2.0, simpleSub.eval(variables)); // 5 - 3
        assertEquals(4.0, complexSub.eval(variables)); // 10 - (2 * 3)
        assertEquals(10.0, subZero.eval(variables)); // 10 - 0
    }
    
    @Test
    void testEvalWithString() throws ExpressionException {
        assertEquals(2.0, simpleSub.eval(""));
        assertEquals(4.0, complexSub.eval("x = 10; y = 3"));
        assertEquals(10.0, subZero.eval("x = 10"));
    }

    @Test
    void testDerivative() throws ExpressionException {
        Expression derivative = complexSub.derivative("x");
        Map<String, Double> vars = Map.of("x", 10.0, "y", 3.0);
        assertEquals(1.0, derivative.eval(vars)); // d/dx[x - 2*y] = 1
    }
    
    @Test
    void testPrint() {
        assertEquals("(5-3)", simpleSub.print());
        assertEquals("(x-(2*y))", complexSub.print());
        assertEquals("(x-0)", subZero.print());
    }
    
    @Test
    void testSimplify() throws ExpressionException {
        Expression simplified = simpleSub.simplify();
        assertTrue(simplified instanceof Number);
        assertEquals(2.0, ((Number) simplified).getValue());

        Expression simplifiedZero = subZero.simplify();
        assertEquals(x, simplifiedZero);

        Sub sameSub = new Sub(x, x);
        Expression simplifiedSame = sameSub.simplify();
        assertTrue(simplifiedSame instanceof Number);
        assertEquals(0.0, ((Number) simplifiedSame).getValue());
    }
    
    @Test
    void testHasVariables() {
        assertFalse(simpleSub.hasVariables());
        assertTrue(complexSub.hasVariables());
        assertTrue(subZero.hasVariables());
    }

    @Test
    void testEquals() throws InvalidInputException {
        Sub another = new Sub(five, three);
        Sub different = new Sub(three, five);

        assertEquals(simpleSub, another);
        assertNotEquals(simpleSub, different);
        assertNotEquals(simpleSub, complexSub);
    }
    
    @Test
    void testGetOperator() {
        assertEquals('-', Sub.getOperator());
    }
}
