package ru.nsu.tokarev.expressions;

import java.util.Map;


public class Mul extends BinaryOperation {

    public Mul(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public double eval(Map<String, Double> variables) {
        return left.eval(variables) * right.eval(variables);
    }

    @Override
    public Expression derivative(String variable) {
        // (u*v)' = u'*v + u*v'
        return new Add(
            new Mul(left.derivative(variable), right),
            new Mul(left, right.derivative(variable))
        );
    }

    @Override
    public String print() {
        return "(" + left.print() + "*" + right.print() + ")";
    }

    @Override
    public Expression simplify() {
        Expression leftSimp = left.simplify();
        Expression rightSimp = right.simplify();

        // If both operands are numbers, compute the result
        if (!leftSimp.hasVariables() && !rightSimp.hasVariables()) {
            double result = leftSimp.eval(Map.of()) * rightSimp.eval(Map.of());
            return new Number(result);
        }

        // expr * 0 = 0
        if ((leftSimp instanceof Number && ((Number) leftSimp).getValue() == 0) ||
            (rightSimp instanceof Number && ((Number) rightSimp).getValue() == 0)) {
            return new Number(0);
        }

        // expr * 1 = expr
        if (leftSimp instanceof Number && ((Number) leftSimp).getValue() == 1) {
            return rightSimp;
        }

        // 1 * expr = expr
        if (rightSimp instanceof Number && ((Number) rightSimp).getValue() == 1) {
            return leftSimp;
        }

        return new Mul(leftSimp, rightSimp);
    }

    @Override
    protected String getOperator() {
        return "*";
    }
}
