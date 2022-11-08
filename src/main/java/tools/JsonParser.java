package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class JsonParser {
    private File file;
    private Scanner reader;
    public static final boolean DEBUG = true;

    private static class Validator {
        private final boolean valid;
        private String fault;

        public Validator(boolean isValid, String fault) {
            this.valid = isValid;
            this.fault = fault;
        }

        public Validator(boolean isValid) {
            this.valid = isValid;
        }

        public boolean isValid() {
            return valid;
        }

        @Override
        public String toString() {
            return "Validator {%s, %s}".formatted(valid, fault);
        }
    }

    public JsonParser(String filePath) throws FileNotFoundException {
        this.file = new File(filePath);
        this.reader = new Scanner(this.file);
    }

    private boolean tokenIsString(String token) {
        return token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"';
    }

    @Unfinished({"Does not support nested objects nor arrays."})
    private Validator validJson() {
        boolean openedCurlyBrace = false;
        boolean closedCurlyBrace = false;
        boolean nextTokenIsClosedCurlyBrace = false;
        while (this.reader.hasNext()) {
            String currentLine = this.reader.nextLine();
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
                    if (DEBUG) {
                        System.out.printf("\t* Setting nextTokenIsClosedCurlyBrace=true where currentToken=`%s` *\n", currentToken);
                    }
                    nextTokenIsClosedCurlyBrace = true;
                }

                lineTokenCount++;
            }
        }

        if (openedCurlyBrace && closedCurlyBrace) {
            return new Validator(true);
        }
        else if (openedCurlyBrace) {
            return new Validator(false, "Expected `}`, found EOF.");
        }
        else {
            return new Validator(false, "Expected `{`.");
        }
    }

    public <K, V> void readInto(Map<K, V> output) {
        // TODO: Implement me

        if (DEBUG) {
            // Calls the validJson() method to check if the file is valid JSON
            System.out.printf("\t-> Output validJson(): %s\n", this.validJson());
        }
    }
}
