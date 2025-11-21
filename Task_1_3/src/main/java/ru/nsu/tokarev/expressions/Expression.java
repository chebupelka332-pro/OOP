package ru.nsu.tokarev.expressions;

import java.util.Map;
import ru.nsu.tokarev.exceptions.ExpressionException;

public abstract class Expression {

    public abstract double eval(Map<String, Double> variables) throws ExpressionException;

    public double eval(String variableString) throws ExpressionException {
        Map<String, Double> variables = parseVariables(variableString);
        return eval(variables);
    }

    public abstract Expression derivative(String variable);

    public abstract String print();

    public abstract Expression simplify() throws ExpressionException;

    public abstract boolean hasVariables();

    @Override
    public String toString() {
        return print();
    }

    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

    private Map<String, Double> parseVariables(String variableString) {
        Map<String, Double> variables = new java.util.HashMap<>();
        if (variableString == null || variableString.trim().isEmpty()) {
            return variables;
        }

        String[] assignments = variableString.split(";");
        for (String assignment : assignments) {
            assignment = assignment.trim();
            if (assignment.isEmpty()) continue;

            String[] parts = assignment.split("=");
            if (parts.length == 2) {
                String varName = parts[0].trim();
                double value = Double.parseDouble(parts[1].trim());
                variables.put(varName, value);
            }
        }
        return variables;
    }
}
