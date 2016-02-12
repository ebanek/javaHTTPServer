package hr.fer.zemris.java.custom.scripting.tokens;

/**
 * Token that represents a string value.
 * 
 * @author Erik Banek
 */
public class TokenString extends Token {
    /** Token string value. */
    private String value;

    /**
     * Constructs read-only token with string content.
     * 
     * @param value
     *            String value of token.
     */
    public TokenString(String value) {
        this.value = value;
    }

    @Override
    public String asText() {
        StringBuilder retStringBuilder = new StringBuilder("\"");
        char[] array = this.value.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '\\') {
                retStringBuilder.append("\\\\");// beautiful
            } else if (array[i] == '\"') {
                retStringBuilder.append("\\\"");
            } else if (array[i] == '\n') {
                retStringBuilder.append("\\n");
            } else if (array[i] == '\r') {
                retStringBuilder.append("\\r");
            } else if (array[i] == '\t') {
                retStringBuilder.append("\\t");
            } else {
                retStringBuilder.append(array[i]);
            }
        }
        retStringBuilder.append("\"");
        return retStringBuilder.toString();
    }

    /**
     * Gets string content of token.
     * 
     * @return String value of token.
     */
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
