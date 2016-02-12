package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.scripting.tokens.TokenConstantDouble;
import hr.fer.zemris.java.custom.scripting.tokens.TokenConstantInteger;
import hr.fer.zemris.java.custom.scripting.tokens.TokenFunction;
import hr.fer.zemris.java.custom.scripting.tokens.TokenOperator;
import hr.fer.zemris.java.custom.scripting.tokens.TokenString;
import hr.fer.zemris.java.custom.scripting.tokens.TokenVariable;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines all token creators.
 * 
 * @author Erik Banek
 */
public class TokenCreators {
    /**
     * Creator of {@code TokenFunction}.
     * 
     * @author Erik Banek
     */
    public static class FunctionTokenCreator extends ATokenCreator {
        /** TokenVariable creator to whom work is delegated. */
        private VariableTokenCreator variableCreator;

        /**
         * Constructor.
         * 
         * @param docString
         *            containing all text of script.
         * @param fromIndex
         *            index of start of token in docString.
         * @throws IllegalArgumentException
         *             if a faulty document String is passed.
         */
        public FunctionTokenCreator(String docString, int fromIndex)
                throws IllegalArgumentException {
            super(docString, fromIndex);
            variableCreator = new VariableTokenCreator(docString, fromIndex + 1);
            end = variableCreator.end;
            token = new TokenFunction(variableCreator.getString());
        }
    }

    /**
     * Creator of token which is a number, {@code TokenConstantDouble} or
     * {@code TokenConstantInteger}.
     * 
     * @author Erik Banek
     */
    public static class NumberTokenCreator extends ATokenCreator {
        /**
         * Constructor.
         * 
         * @param docString
         *            containing all text of script.
         * @param fromIndex
         *            index of start of token in docString.
         * @throws IllegalArgumentException
         *             if a faulty document String is passed.
         */
        public NumberTokenCreator(String docString, int fromIndex)
                throws IllegalArgumentException {

            super(docString, fromIndex);
            boolean dotAppeared = false;
            int currentIndex = fromIndex;

            while (currentIndex < docString.length()) {
                Character c = docString.charAt(currentIndex);
                if (Character.isDigit(c) || c == '.') {
                    if (c == '.') {
                        dotAppeared = true;
                    }
                    currentIndex++;
                    continue;
                }
                end = currentIndex;

                try {
                    String num = docString.substring(fromIndex, end);
                    if (dotAppeared) {
                        double d = Double.parseDouble(num);
                        token = new TokenConstantDouble(d);
                        return;
                    } else {
                        int i = Integer.parseInt(num);
                        token = new TokenConstantInteger(i);
                        return;
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cannot parse number!");
                }
            }
            throw new IllegalArgumentException(
                    "Number extends to end of document String!");
        }
    }

    /**
     * Creator of {@code TokenOperator}.
     * 
     * @author Erik Banek
     */
    public static class OperatorTokenCreator extends ATokenCreator {
        /**
         * Constructor.
         * 
         * @param docString
         *            containing all text of script.
         * @param fromIndex
         *            index of start of token in docString.
         */
        public OperatorTokenCreator(String docString, int fromIndex) {
            super(docString, fromIndex);
            token = new TokenOperator(docString.substring(fromIndex,
                    fromIndex + 1));
            end = fromIndex + 1;
        }
    }

    /**
     * Creator of {@code TokenString}.
     * 
     * @author Erik Banek
     */
    public static class StringTokenCreator extends ATokenCreator {
        /**
         * Converts string in tag to TokenString. Escaping is handled and
         * converted here.
         * 
         * @param toConvert
         *            String to be parsed into token String.
         * @return parsed token String
         */
        private static TokenString stringToTokenString(String toConvert) {
            // get rid of "", and convert to array
            char[] arrayToConvert = toConvert.substring(1,
                    toConvert.length() - 1).toCharArray();
            StringBuilder convertBuilder = new StringBuilder();

            for (int i = 0; i < arrayToConvert.length; i++) {
                if (arrayToConvert[i] == '\\'
                        && i != (arrayToConvert.length - 1)
                        && (arrayToConvert[i + 1] == '\\'
                                || arrayToConvert[i + 1] == 'n'
                                || arrayToConvert[i + 1] == '\"'
                                || arrayToConvert[i + 1] == 't'
                                || arrayToConvert[i + 1] == 'r')) {
                    char toAppend = 0;
                    switch (arrayToConvert[i + 1]) {
                    case '\\':
                        toAppend = '\\';
                        break;
                    case '\"':
                        toAppend = '\"';
                        break;
                    case 'n':
                        toAppend = '\n';
                        break;
                    case 't':
                        toAppend = '\t';
                        break;
                    case 'r':
                        toAppend = '\r';
                        break;
                    }

                    convertBuilder.append(toAppend);
                    i++;
                    continue;
                }
                // if there is no escape, standard append
                convertBuilder.append(arrayToConvert[i]);
            }
            return new TokenString(convertBuilder.toString());
        }

        /** Array of characters in document, equivalent to docString. */
        private char[] docArray;

        /**
         * Constructor.
         * 
         * @param docString
         *            containing all text of script.
         * @param fromIndex
         *            index of start of token in docString.
         * @throws IllegalArgumentException
         *             if a faulty document String is passed.
         */
        public StringTokenCreator(String docString, int fromIndex)
                throws IllegalArgumentException {
            super(docString, fromIndex);
            int currentIndex = fromIndex;
            docArray = docString.toCharArray();
            while (currentIndex < docString.length()) {
                int possibleEnd = docString.indexOf("\"", currentIndex + 1);
                if (!isEscaped(possibleEnd)) {
                    end = possibleEnd + 1;
                    token = stringToTokenString(docString.substring(fromIndex,
                            end));
                    return;
                }
                currentIndex = possibleEnd;

            }
            throw new IllegalArgumentException(
                    "String token extends to end of document!");
        }

        /**
         * Checks if current char at given position is escaped.
         * 
         * @param currentIndex
         *            position of char whose escaping is checked.
         * @return true iff the char is escaped.
         */
        private boolean isEscaped(int currentIndex) {
            int numberOfEscapes = 0;
            while (currentIndex > 0 && docArray[currentIndex - 1] == '\\') {
                currentIndex--;
                numberOfEscapes++;
            }
            return numberOfEscapes % 2 == 1;
        }
    }

    /**
     * Creator of {@code TokenVariable}.
     * 
     * @author Erik Banek
     */
    public static class VariableTokenCreator extends ATokenCreator {
        /**
         * Tests if char can be in variable name.
         * 
         * @param c
         *            char to be tested.
         * @return true if char can be in variable name.
         */
        private static boolean isVariableChar(char c) {
            return Character.isLetterOrDigit(c) || c == '_';
        }

        /**
         * Constructor.
         * 
         * @param docString
         *            containing all text of script.
         * @param fromIndex
         *            index of start of token in docString.
         * @throws IllegalArgumentException
         *             if a faulty document String is passed.
         */
        public VariableTokenCreator(String docString, int fromIndex)
                throws IllegalArgumentException {
            super(docString, fromIndex);
            int currentIndex = fromIndex;
            while (currentIndex < docString.length()) {
                Character c = docString.charAt(currentIndex);
                if (!isVariableChar(c)) {
                    end = currentIndex;
                    token = new TokenVariable(docString.substring(fromIndex,
                            end));
                    return;
                }
                currentIndex++;
            }
            throw new IllegalArgumentException(
                    "Variable extends to end of document String!");
        }
    }

    /** List of operators supported by the SmartScript language. */
    private static Set<Character> operators;

    static {
        Set<Character> set = new HashSet<>();
        set.add('+');
        set.add('-');
        set.add('*');
        set.add('/');
        set.add('=');
        operators = set;
    }

    /**
     * Gets the token creator created from the document String and starting
     * index of token. Helpful fact: which token is created is known from the
     * first character of token.
     * 
     * @param docString
     *            containing all text of script.
     * @param fromIndex
     *            index of start of token in docString.
     * @return token creator.
     */
    public static ATokenCreator getCreator(String docString, int fromIndex) {
        String tokenStringStart = docString.substring(fromIndex);
        Character start = tokenStringStart.charAt(0);

        if (Character.isLetter(start)) {
            return new VariableTokenCreator(docString, fromIndex);
        } else if (start == '\"') {
            return new StringTokenCreator(docString, fromIndex);
        } else if (Character.isDigit(start)) {
            return new NumberTokenCreator(docString, fromIndex);
        } else if (start == '@') {
            return new FunctionTokenCreator(docString, fromIndex);
        } else if (operators.contains(start)) {
            return new OperatorTokenCreator(docString, fromIndex);
        } else {
            throw new IllegalArgumentException(
                    "Unkown token starting character!");
        }
    }
}
