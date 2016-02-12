package hr.fer.zemris.java.custom.scripting.tokens;

/**
 * Token that represents a constant integer value.
 * 
 * @author Erik Banek
 */
public class TokenConstantInteger extends Token {
    /** Value that holds token integer content. */
    private int value;

    /**
     * Constructs a read-only token with integer.
     * 
     * @param value
     *            of token.
     */
    public TokenConstantInteger(int value) {
        this.value = value;
    }

    @Override
    public String asText() {
        return Integer.toString(this.value);
    }

    /**
     * Token int value getter.
     * 
     * @return integer value of token.
     */
    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
