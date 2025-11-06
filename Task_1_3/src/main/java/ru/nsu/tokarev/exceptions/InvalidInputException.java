package ru.nsu.tokarev.exceptions;


public class InvalidInputException extends ExpressionException {
    
    public InvalidInputException(String message) {
        super(message);
    }
    
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}