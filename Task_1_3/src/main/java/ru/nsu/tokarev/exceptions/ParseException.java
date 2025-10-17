package ru.nsu.tokarev.exceptions;


public class ParseException extends ExpressionException {
    
    private final int position;
    
    public ParseException(String message) {
        super(message);
        this.position = -1;
    }
    
    public ParseException(String message, int position) {
        super(message + " at position " + position);
        this.position = position;
    }
    
    public ParseException(String message, Throwable cause) {
        super(message, cause);
        this.position = -1;
    }
    
    public int getPosition() {
        return position;
    }
}