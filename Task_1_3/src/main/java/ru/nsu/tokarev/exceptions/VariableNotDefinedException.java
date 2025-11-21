package ru.nsu.tokarev.exceptions;


public class VariableNotDefinedException extends ExpressionException {
    
    private final String variableName;
    
    public VariableNotDefinedException(String variableName) {
        super("Variable '" + variableName + "' is not defined");
        this.variableName = variableName;
    }
    
    public VariableNotDefinedException(String variableName, String message) {
        super(message);
        this.variableName = variableName;
    }
    
    public String getVariableName() {
        return variableName;
    }
}