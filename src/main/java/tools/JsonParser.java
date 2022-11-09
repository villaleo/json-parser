package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class JsonParser {
    private File file;
    public static final boolean DEBUG = false;

    private static class Validator {
        private final boolean valid;
        private String message;

        public static final String UNKNOWN_TYPE = "Unknown";
        public static final String BOOLEAN_TYPE = "Boolean";
        public static final String NUMBER_TYPE = "Number";
        public static final String STRING_TYPE = "String";

        public Validator(boolean isValid, String message) {
            this.valid = isValid;
            this.message = message;
        }

        public Validator(boolean isValid) {
            this.valid = isValid;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Validator {%s, %s}".formatted(valid, message);
        }
    }

    public JsonParser(String filePath) throws FileNotFoundException {
        this.file = new File(filePath);
    }

    private boolean tokenIsString(String token) {
        return token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"';
    }

    private Validator tokenIsValidType(String token) {
        // remove trailing comma, if present
        if (token.charAt(token.length() - 1) == ',') {
            token = token.substring(0, token.length() - 1);
        }

        if (tokenIsString(token)) {
            return new Validator(true, Validator.STRING_TYPE);
        }

        // check if token is a number
        boolean isNumber = false;
        try {
            Double.parseDouble(token);
            isNumber = true;
        } catch (NumberFormatException err) {
            // Do nothing
        }
        try {
            Integer.parseInt(token);
            isNumber = true;
        } catch (NumberFormatException err) {
            // Do nothing
        }
        if (isNumber) {
            return new Validator(true, Validator.NUMBER_TYPE);
        }

        // check if token is a boolean
        if (token.equals("true") || token.equals("false")) {
            return new Validator(true, Validator.BOOLEAN_TYPE);
        }
        return new Validator(false, Validator.UNKNOWN_TYPE);
    }

    private String removeQuotes(String token) {
        if (tokenIsString(token)) {
            return token.substring(1, token.length() - 1);
        }
        return token;
    }

    private Object valueMapper(String value) {
        Validator validator = tokenIsValidType(value);
        if (!validator.isValid()) {
            return null;
        }

        switch (validator.getMessage()) {
            case Validator.STRING_TYPE:
                if (DEBUG) {
                    System.out.printf("\t*Found string: %s\n", value);
                }
                return removeQuotes(value);
            case Validator.NUMBER_TYPE:
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException err) {
                    return Double.parseDouble(value);
                }
            case Validator.BOOLEAN_TYPE:
                return Boolean.parseBoolean(value);
            default:
                return null;
        }
    }

    @Unfinished({"Does not support nested objects nor arrays."})
    private Validator validJson() {
        Scanner reader;
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException err) {
            System.out.printf("File not found: %s", err.getMessage());
            return new Validator(false, "File not found.");
        }

        boolean openedCurlyBrace = false;
        boolean closedCurlyBrace = false;
        boolean nextTokenIsClosedCurlyBrace = false;

        while (reader.hasNext()) {
            String currentLine = reader.nextLine();
            // Skip the tokenization if curly brace found
            if (currentLine.trim().equals("{")) {
                openedCurlyBrace = true;
                continue;
            } else if (currentLine.trim().equals("}")) {
                closedCurlyBrace = true;
                continue;
            }

            var tokens = new StringTokenizer(currentLine, ":");

            var iter = tokens.asIterator();
            int lineTokenCount = 0;
            while (iter.hasNext()) {
                var currentToken = iter.next().toString().trim();

                // Check that the current token is `}` if nextTokenIsClosedCurlyBrace is true
                if (nextTokenIsClosedCurlyBrace && !currentToken.equals("}")) {
                    return new Validator(false, "Expected `,` or `}`, found `%s`.".formatted(currentToken));
                }
                // Make sure the key is a string
                if (lineTokenCount == 0 && !tokenIsString(currentToken)) {
                    return new Validator(false, "Key `%s` must be a string.".formatted(currentToken));
                }
                // If no trailing comma, then the next line should be the `}`
                if (!tokens.hasMoreTokens() && currentToken.charAt(currentToken.length() - 1) != ',') {
                    nextTokenIsClosedCurlyBrace = true;
                }
                // Check that the right side of the colon is a valid value
                if (lineTokenCount == 1 && !tokenIsValidType(currentToken).isValid()) {
                    return new Validator(false, "Expected number, string, boolean. Got `%s`.".formatted(currentToken));
                }

                lineTokenCount++;
            }
        }
        reader.close();

        if (openedCurlyBrace && closedCurlyBrace) {
            return new Validator(true);
        } else if (openedCurlyBrace) {
            return new Validator(false, "Expected `}`, found EOF.");
        } else {
            return new Validator(false, "Expected `{`.");
        }
    }

    public HashMap<String, Object> parseJsonToMap() {
        var output = new HashMap<String, Object>();

        if (!validJson().isValid()) {
            System.out.printf("Invalid JSON: %s\n", validJson().getMessage());
            return null;
        }

        Scanner reader;
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException err) {
            System.out.printf("File not found: %s", err.getMessage());
            return null;
        }

        while (reader.hasNext()) {
            String currentLine = reader.nextLine();
            // Skip the tokenization if curly brace found
            if (currentLine.trim().equals("{")) {
                continue;
            } else if (currentLine.trim().equals("}")) {
                continue;
            }

            var tokens = new StringTokenizer(currentLine, ":");
            // Remove quotes from key
            String key = removeQuotes(tokens.nextToken().trim());

            // Remove trailing comma, if present
            String value = tokens.nextToken().trim();
            if (value.contains(",")) {
                value = value.substring(0, value.length() - 1);
            }

            // Add to output
            output.put(key, valueMapper(value));

            if (DEBUG) {
                System.out.printf("\t* Adding key=`%s`, value=`%s`\n", key, value);
            }
        }
        reader.close();

        return output;
    }
}
