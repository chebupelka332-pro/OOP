package ru.nsu.tokarev.parser;

import ru.nsu.tokarev.expressions.*;


public class ExpressionParser {
    public static Expression parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        
        String trimmed = input.trim();
        return parseWithPrecedence(trimmed);
    }

    public static Expression parseWithoutParentheses(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        
        return parseWithPrecedence(input.trim());
    }
    
    private static ParseResult parseExpression(String input, int start) {
        if (start >= input.length()) {
            throw new IllegalArgumentException("Unexpected end of input");
        }
        
        char ch = input.charAt(start);
        
        if (ch == '(') {
            // Parse parenthesized expression
            return parseParenthesizedExpression(input, start);
        } else if (Character.isDigit(ch) || (ch == '-' && start + 1 < input.length() &&
                Character.isDigit(input.charAt(start + 1)))) {
            // Parse number
            return parseNumber(input, start);
        } else if (Character.isLetter(ch)) {
            // Parse variable
            return parseVariable(input, start);
        } else {
            throw new IllegalArgumentException("Unexpected character: " + ch);
        }
    }
    
    private static ParseResult parseParenthesizedExpression(String input, int start) {
        if (input.charAt(start) != '(') {
            throw new IllegalArgumentException("Expected '(' at position " + start);
        }
        
        int pos = start + 1;
        ParseResult leftResult = parseExpression(input, pos);
        pos = leftResult.nextPosition;
        
        if (pos >= input.length()) {
            throw new IllegalArgumentException("Unexpected end of input, expected operator");
        }
        
        char operator = input.charAt(pos);
        pos++;
        
        ParseResult rightResult = parseExpression(input, pos);
        pos = rightResult.nextPosition;
        
        if (pos >= input.length() || input.charAt(pos) != ')') {
            throw new IllegalArgumentException("Expected ')' at position " + pos);
        }
        
        Expression result = createBinaryOperation(operator, leftResult.expression, rightResult.expression);
        return new ParseResult(result, pos + 1);
    }
    
    private static ParseResult parseNumber(String input, int start) {
        StringBuilder sb = new StringBuilder();
        int pos = start;
        
        if (pos < input.length() && input.charAt(pos) == '-') {
            sb.append('-');
            pos++;
        }
        
        while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            sb.append(input.charAt(pos));
            pos++;
        }
        
        try {
            double value = Double.parseDouble(sb.toString());
            return new ParseResult(new ru.nsu.tokarev.expressions.Number(value), pos);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + sb.toString());
        }
    }
    
    private static ParseResult parseVariable(String input, int start) {
        StringBuilder sb = new StringBuilder();
        int pos = start;
        
        while (pos < input.length() && (Character.isLetterOrDigit(input.charAt(pos)))) {
            sb.append(input.charAt(pos));
            pos++;
        }
        
        return new ParseResult(new Variable(sb.toString()), pos);
    }
    
    private static Expression createBinaryOperation(char operator, Expression left, Expression right) {
        switch (operator) {
            case '+':
                return new Add(left, right);
            case '-':
                return new Sub(left, right);
            case '*':
                return new Mul(left, right);
            case '/':
                return new Div(left, right);
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
    
    private static Expression parseWithPrecedence(String input) {
        int[] pos = new int[]{0};
        Expression result = parseAddSub(input.replaceAll("\\s+", ""), pos);

        // Validate that all input was consumed
        if (pos[0] < input.replaceAll("\\s+", "").length()) {
            char unexpectedChar = input.replaceAll("\\s+", "").charAt(pos[0]);
            if (unexpectedChar == ')') {
                throw new IllegalArgumentException("Unexpected closing parenthesis at position " + pos[0]);
            } else {
                throw new IllegalArgumentException("Unexpected character: " + unexpectedChar + " at position " + pos[0]);
            }
        }

        return result;
    }
    
    private static Expression parseAddSub(String input, int[] pos) {
        Expression left = parseMulDiv(input, pos);
        
        while (pos[0] < input.length()) {
            char op = input.charAt(pos[0]);
            if (op == '+' || op == '-') {
                pos[0]++;
                Expression right = parseMulDiv(input, pos);
                left = (op == '+') ? new Add(left, right) : new Sub(left, right);
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
            if (op == '*' || op == '/') {
                pos[0]++;
                Expression right = parseFactor(input, pos);
                left = (op == '*') ? new Mul(left, right) : new Div(left, right);
            } else {
                break;
            }
        }
        
        return left;
    }
    
    private static Expression parseFactor(String input, int[] pos) {
        if (pos[0] >= input.length()) {
            throw new IllegalArgumentException("Unexpected end of input");
        }
        
        char ch = input.charAt(pos[0]);
        
        if (ch == '(') {
            pos[0]++;
            Expression expr = parseAddSub(input, pos);
            if (pos[0] >= input.length() || input.charAt(pos[0]) != ')') {
                throw new IllegalArgumentException("Expected ')' at position " + pos[0]);
            }
            pos[0]++;
            return expr;
        } else if (Character.isDigit(ch) || (ch == '-' && pos[0] + 1 < input.length() && Character.isDigit(input.charAt(pos[0] + 1)))) {
            return parseNumberAtPosition(input, pos);
        } else if (Character.isLetter(ch)) {
            return parseVariableAtPosition(input, pos);
        } else {
            throw new IllegalArgumentException("Unexpected character: " + ch);
        }
    }
    
    private static Expression parseNumberAtPosition(String input, int[] pos) {
        StringBuilder sb = new StringBuilder();
        
        if (pos[0] < input.length() && input.charAt(pos[0]) == '-') {
            sb.append('-');
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
            throw new IllegalArgumentException("Invalid number format: " + sb.toString());
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
    
    private static class ParseResult {
        final Expression expression;
        final int nextPosition;
        
        ParseResult(Expression expression, int nextPosition) {
            this.expression = expression;
            this.nextPosition = nextPosition;
        }
    }
}
