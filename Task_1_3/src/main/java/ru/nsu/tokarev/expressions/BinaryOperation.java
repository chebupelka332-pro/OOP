package ru.nsu.tokarev.expressions;

import java.util.Map;
import java.util.Objects;


public abstract class BinaryOperation extends Expression {
    protected final Expression left;
    protected final Expression right;

    public BinaryOperation(Expression left, Expression right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean hasVariables() {
        return left.hasVariables() || right.hasVariables();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        BinaryOperation that = (BinaryOperation) other;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), left, right);
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    protected abstract String getOperator();
}
