package ru.nsu.tokarev.exceptions;

/**
 * Base exception class for all expression-related errors.
 */
public class ExpressionException extends Exception {

    public ExpressionException(String message) {
        super(message);
    }
    
    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}