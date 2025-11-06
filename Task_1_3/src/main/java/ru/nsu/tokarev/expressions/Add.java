package ru.nsu.tokarev.expressions;

import java.util.Map;
import ru.nsu.tokarev.exceptions.InvalidInputException;
import ru.nsu.tokarev.exceptions.ExpressionException;


public class Add extends BinaryOperation {

    public Add(Expression left, Expression right) throws InvalidInputException {
        super(left, right);
    }

    @Override
    public double eval(Map<String, Double> variables) throws ExpressionException {
        return left.eval(variables) + right.eval(variables);
    }

    @Override
    public Expression derivative(String variable) {
        try {
            return new Add(left.derivative(variable), right.derivative(variable));
        } catch (InvalidInputException e) {
            throw new RuntimeException("Error creating derivative expression", e);
        }
    }

    @Override
    public String print() {
        return "(" + left.print() + "+" + right.print() + ")";
    }

    @Override
    public Expression simplify() throws ExpressionException {
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

        // Return new Add if simplification was performed
        if (!leftSimp.equals(left) || !rightSimp.equals(right)) {
            try {
                return new Add(leftSimp, rightSimp);
            } catch (InvalidInputException e) {
                throw new ExpressionException("Error creating simplified addition", e);
            }
        }

        return this;
    }

    public static char getOperator() {
        return '+';
    }
}
