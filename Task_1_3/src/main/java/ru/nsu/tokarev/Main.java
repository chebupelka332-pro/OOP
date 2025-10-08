package ru.nsu.tokarev;

import ru.nsu.tokarev.expressions.Number;
import ru.nsu.tokarev.expressions.Expression;
import ru.nsu.tokarev.expressions.Variable;
import ru.nsu.tokarev.expressions.Add;
import ru.nsu.tokarev.expressions.Sub;
import ru.nsu.tokarev.expressions.Mul;
import ru.nsu.tokarev.expressions.Div;
import ru.nsu.tokarev.parser.ExpressionParser;


public class Main {
    
    public static void main(String[] args) {
        Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));

        System.out.println(e.print()); // (3+(2*x))

        Expression de = e.derivative("x");
        System.out.println(de.print()); // (0+((0*x)+(2*1)))

        Expression e1 = new Add(new Number(3), new Mul(new Number(2), new Variable("x"))); // (3+(2*x))
        double result = e1.eval("x = 10; y = 13");
        System.out.println(result); // 23.0

        System.out.println(de.simplify().print()); // 2

        ExpressionParser parser = new ExpressionParser();
        Expression e2 = parser.parse("3 + 2 * x");
        System.out.println(e2.print()); // (3+(2*x))

        Expression e3 = parser.parse("(3 + (2 * x)) / (y - 4)");
        System.out.println(e3.print()); // ((3+(2*x))/(y-4))

        double result1 = e3.eval("x=9;y=7");
        System.out.println(result1);
    }
}
