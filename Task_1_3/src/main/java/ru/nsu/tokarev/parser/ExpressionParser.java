package ru.nsu.tokarev.parser;

import ru.nsu.tokarev.expressions.*;
import ru.nsu.tokarev.exceptions.InvalidInputException;
import ru.nsu.tokarev.exceptions.ParseException;


public class ExpressionParser {
    public static Expression parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException("Input cannot be null or empty");
        }
        
        String trimmed = input.trim();
        return parseWithPrecedence(trimmed);
    }

    private static Expression parseWithPrecedence(String input) {
        int[] pos = new int[]{0};
        Expression result = parseAddSub(input.replaceAll("\\s+", ""), pos);

        if (pos[0] < input.replaceAll("\\s+", "").length()) {
            char unexpectedChar = input.replaceAll("\\s+", "").charAt(pos[0]);
            if (unexpectedChar == ')') {
                throw new ParseException("Unexpected closing parenthesis", pos[0]);
            } else {
                throw new ParseException("Unexpected character: " + unexpectedChar, pos[0]);
            }
        }
        
        return result;
    }
    
    private static Expression parseAddSub(String input, int[] pos) {
        Expression left = parseMulDiv(input, pos);
        
        while (pos[0] < input.length()) {
            char op = input.charAt(pos[0]);
            if (op == Add.getOperator() || op == Sub.getOperator()) {
                pos[0]++;
                Expression right = parseMulDiv(input, pos);
                left = (op == Add.getOperator()) ? new Add(left, right) : new Sub(left, right);
            } else {
                break;
            }
        }
        
        return left;
    }
    
    private static Expression parseMulDiv(String input, int[] pos) {
        Expression left = parseFactor(input, pos);
        
        while (pos[0] < input.length()) {
            char op = input.charAt(pos[0]);
            if (op == Mul.getOperator() || op == Div.getOperator()) {
                pos[0]++;
                Expression right = parseFactor(input, pos);
                left = (op == Mul.getOperator()) ? new Mul(left, right) : new Div(left, right);
            } else {
                break;
            }
        }
        
        return left;
    }
    
    private static Expression parseFactor(String input, int[] pos) {
        if (pos[0] >= input.length()) {
            throw new ParseException("Unexpected end of input");
        }
        
        char ch = input.charAt(pos[0]);
        
        if (ch == '(') {
            pos[0]++;
            Expression expr = parseAddSub(input, pos);
            if (pos[0] >= input.length() || input.charAt(pos[0]) != ')') {
                throw new ParseException("Expected ')'", pos[0]);
            }
            pos[0]++;
            return expr;
        } else if (Character.isDigit(ch) || (ch == Sub.getOperator() && pos[0] + 1 < input.length() 
                   && Character.isDigit(input.charAt(pos[0] + 1)))) {
            return parseNumberAtPosition(input, pos);
        } else if (Character.isLetter(ch)) {
            return parseVariableAtPosition(input, pos);
        } else {
            throw new ParseException("Unexpected character: " + ch);
        }
    }
    
    private static Expression parseNumberAtPosition(String input, int[] pos) {
        StringBuilder sb = new StringBuilder();
        
        if (pos[0] < input.length() && input.charAt(pos[0]) == Sub.getOperator()) {
            sb.append(Sub.getOperator());
            pos[0]++;
        }
        
        while (pos[0] < input.length() && (Character.isDigit(input.charAt(pos[0])) || input.charAt(pos[0]) == '.')) {
            sb.append(input.charAt(pos[0]));
            pos[0]++;
        }
        
        try {
            double value = Double.parseDouble(sb.toString());
            return new ru.nsu.tokarev.expressions.Number(value);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid number format: " + sb.toString());
        }
    }
    
    private static Expression parseVariableAtPosition(String input, int[] pos) {
        StringBuilder sb = new StringBuilder();
        
        while (pos[0] < input.length() && Character.isLetterOrDigit(input.charAt(pos[0]))) {
            sb.append(input.charAt(pos[0]));
            pos[0]++;
        }
        
        return new Variable(sb.toString());
    }
}
