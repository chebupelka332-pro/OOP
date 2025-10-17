package ru.nsu.tokarev.expressions;

import java.util.Map;
import ru.nsu.tokarev.exceptions.DivisionByZeroException;


public class Div extends BinaryOperation {

    public Div(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public double eval(Map<String, Double> variables) {
        double rightValue = right.eval(variables);
        if (rightValue == 0) {
            throw new DivisionByZeroException();
        }
        return left.eval(variables) / rightValue;
    }

    @Override
    public Expression derivative(String variable) {
        // (u/v)' = (u'*v - u*v') / v^2
        return new Div(
            new Sub(
                new Mul(left.derivative(variable), right),
                new Mul(left, right.derivative(variable))
            ),
            new Mul(right, right)
        );
    }

    @Override
    public String print() {
        return "(" + left.print() + "/" + right.print() + ")";
    }

    @Override
    public Expression simplify() {
        Expression leftSimp = left.simplify();
        Expression rightSimp = right.simplify();

        // If both operands are numbers, compute the result
        if (!leftSimp.hasVariables() && !rightSimp.hasVariables()) {
            double rightValue = rightSimp.eval(Map.of());
            if (rightValue == 0) {
                throw new DivisionByZeroException("Division by zero in simplification");
            }
            double result = leftSimp.eval(Map.of()) / rightValue;
            return new Number(result);
        }

        // 0 / expr = 0
        if (leftSimp instanceof Number && ((Number) leftSimp).getValue() == 0) {
            return new Number(0);
        }

        // expr / 1 = expr
        if (rightSimp instanceof Number && ((Number) rightSimp).getValue() == 1) {
            return leftSimp;
        }

        // expr / expr = 1
        if (leftSimp.equals(rightSimp)) {
            return new Number(1);
        }

        return new Div(leftSimp, rightSimp);
    }

    public static char getOperator() {
        return '/';
    }
}
