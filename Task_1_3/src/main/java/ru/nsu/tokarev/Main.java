package ru.nsu.tokarev;

import ru.nsu.tokarev.expressions.Number;
import ru.nsu.tokarev.expressions.Expression;
import ru.nsu.tokarev.expressions.Variable;
import ru.nsu.tokarev.expressions.Add;
import ru.nsu.tokarev.expressions.Sub;
import ru.nsu.tokarev.expressions.Mul;
import ru.nsu.tokarev.expressions.Div;
import ru.nsu.tokarev.parser.ExpressionParser;
import ru.nsu.tokarev.exceptions.*;

public class Main {
    
    public static void main(String[] args) {
        try {
            Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));

            System.out.println(e.print()); // (3+(2*x))

            Expression de = e.derivative("x");
            System.out.println(de.print()); // (0+((0*x)+(2*1)))

            Expression e1 = new Add(new Number(3), new Mul(new Number(2), new Variable("x"))); // (3+(2*x))
            double result = e1.eval("x = 10; y = 13");
            System.out.println(result); // 23.0

            System.out.println(de.simplify().print()); // 2

            Expression e2 = ExpressionParser.parse("3 + 2 * x");
            System.out.println(e2.print()); // (3+(2*x))

            Expression e3 = ExpressionParser.parse("(3 + (2 * x)) / (y - 4)");
            System.out.println(e3.print()); // ((3+(2*x))/(y-4))

            double result1 = e3.eval("x=9;y=7");
            System.out.println(result1);

        } catch (InvalidInputException e) {
            System.err.println("Invalid input: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Parse error: " + e.getMessage());
            if (e.getPosition() != -1) {
                System.err.println("Position: " + e.getPosition());
            }
        } catch (VariableNotDefinedException e) {
            System.err.println("Variable not defined: " + e.getVariableName());
            System.err.println("Message: " + e.getMessage());
        } catch (DivisionByZeroException e) {
            System.err.println("Division by zero error: " + e.getMessage());
        } catch (ExpressionException e) {
            System.err.println("Expression error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
