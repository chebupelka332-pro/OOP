package ru.nsu.tokarev.expressions;

import java.util.Map;


public class Add extends BinaryOperation {

    public Add(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public double eval(Map<String, Double> variables) {
        return left.eval(variables) + right.eval(variables);
    }

    @Override
    public Expression derivative(String variable) {
        return new Add(left.derivative(variable), right.derivative(variable));
    }

    @Override
    public String print() {
        return "(" + left.print() + "+" + right.print() + ")";
    }

    @Override
    public Expression simplify() {
        Expression leftSimp = left.simplify();
        Expression rightSimp = right.simplify();

        // If both operands are numbers, compute the result
        if (!leftSimp.hasVariables() && !rightSimp.hasVariables()) {
            double result = leftSimp.eval(Map.of()) + rightSimp.eval(Map.of());
            return new Number(result);
        }

        // 0 + expr = expr
        if (leftSimp instanceof Number && ((Number) leftSimp).getValue() == 0) {
            return rightSimp;
        }

        // expr + 0 = expr
        if (rightSimp instanceof Number && ((Number) rightSimp).getValue() == 0) {
            return leftSimp;
        }

        return new Add(leftSimp, rightSimp);
    }

    public static char getOperator() {
        return '+';
    }
}
