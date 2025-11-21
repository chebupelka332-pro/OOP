package ru.nsu.tokarev.exceptions;


public class DivisionByZeroException extends ExpressionException {
    
    public DivisionByZeroException() {
        super("Division by zero");
    }
    
    public DivisionByZeroException(String message) {
        super(message);
    }
    
    public DivisionByZeroException(String message, Throwable cause) {
        super(message, cause);
    }
}