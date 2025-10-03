package ru.nsu.tokarev.expressions;

import java.util.Map;
import java.util.Objects;


public class Number extends Expression {
    private final double value;

    public Number(double value) {
        this.value = value;
    }

    @Override
    public double eval(Map<String, Double> variables) {
        return value;
    }

    @Override
    public Expression derivative(String variable) {
        return new Number(0);
    }

    @Override
    public String print() {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
    }

    @Override
    public Expression simplify() {
        return this; // Numbers are already simplified
    }

    @Override
    public boolean hasVariables() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Number)) return false;
        Number number = (Number) other;
        return Double.compare(number.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public double getValue() {
        return value;
    }
}
