package hr.fer.zemris.java.custom.scripting.tokens;

/**
 * Token that represents a constant double value.
 * 
 * @author Erik Banek
 */
public class TokenConstantDouble extends Token {
    /** Token with double value. */
    private double value;

    /**
     * Constructs a read-only token that holds a double.
     * 
     * @param doubleForToken
     *            value that the token will hold.
     */
    public TokenConstantDouble(double doubleForToken) {
        this.value = doubleForToken;
    }

    @Override
    public String asText() {
        return Double.toString(this.value);
    }

    /**
     * Value getter.
     * 
     * @return double value of token.
     */
    public double getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Double.toString(this.value);
    }
}
