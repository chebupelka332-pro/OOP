package ru.nsu.tokarev.expressions;

import java.util.Map;
import ru.nsu.tokarev.exceptions.InvalidInputException;
import ru.nsu.tokarev.exceptions.ExpressionException;


public class Mul extends BinaryOperation {

    public Mul(Expression left, Expression right) throws InvalidInputException {
        super(left, right);
    }

    @Override
    public double eval(Map<String, Double> variables) throws ExpressionException {
        return left.eval(variables) * right.eval(variables);
    }

    @Override
    public Expression derivative(String variable) {
        // (u*v)' = u'*v + u*v'
        try {
            return new Add(
                new Mul(left.derivative(variable), right),
                new Mul(left, right.derivative(variable))
            );
        } catch (InvalidInputException e) {
            throw new RuntimeException("Error creating derivative expression", e);
        }
    }

    @Override
    public String print() {
        return "(" + left.print() + "*" + right.print() + ")";
    }

    @Override
    public Expression simplify() throws ExpressionException {
        Expression leftSimp = left.simplify();
        Expression rightSimp = right.simplify();

        // If both operands are numbers, compute the result
        if (!leftSimp.hasVariables() && !rightSimp.hasVariables()) {
            double result = leftSimp.eval(Map.of()) * rightSimp.eval(Map.of());
            return new Number(result);
        }

        // 0 * expr = 0
        if (leftSimp instanceof Number && ((Number) leftSimp).getValue() == 0) {
            return new Number(0);
        }

        // expr * 0 = 0
        if (rightSimp instanceof Number && ((Number) rightSimp).getValue() == 0) {
            return new Number(0);
        }

        // 1 * expr = expr
        if (leftSimp instanceof Number && ((Number) leftSimp).getValue() == 1) {
            return rightSimp;
        }

        // expr * 1 = expr
        if (rightSimp instanceof Number && ((Number) rightSimp).getValue() == 1) {
            return leftSimp;
        }

        // Return new Mul if simplification was performed
        if (!leftSimp.equals(left) || !rightSimp.equals(right)) {
            try {
                return new Mul(leftSimp, rightSimp);
            } catch (InvalidInputException e) {
                throw new ExpressionException("Error creating simplified multiplication", e);
            }
        }

        return this;
    }

    public static char getOperator() {
        return '*';
    }
}
