package ru.nsu.tokarev.expressions;

import java.util.Map;
import java.util.Objects;
import ru.nsu.tokarev.exceptions.InvalidInputException;
import ru.nsu.tokarev.exceptions.VariableNotDefinedException;


public class Variable extends Expression {
    private final String name;

    public Variable(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Variable name cannot be null or empty");
        }
        this.name = name.trim();
    }

    @Override
    public double eval(Map<String, Double> variables) {
        if (!variables.containsKey(name)) {
            throw new VariableNotDefinedException(name);
        }
        return variables.get(name);
    }

    @Override
    public Expression derivative(String variable) {
        return name.equals(variable) ? new Number(1) : new Number(0);
    }

    @Override
    public String print() {
        return name;
    }

    @Override
    public Expression simplify() {
        return this; // Variables cannot be simplified without values
    }

    @Override
    public boolean hasVariables() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Variable)) return false;
        Variable variable = (Variable) other;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }
}
