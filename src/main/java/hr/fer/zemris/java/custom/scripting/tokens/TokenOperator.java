package hr.fer.zemris.java.custom.scripting.tokens;

/**
 * Token that represents an operator. It represents it by a string which
 * contains that operator.
 * 
 * @author Erik Banek
 */
public class TokenOperator extends Token {
    /** String containing operator symbol. */
    private String symbol;

    /**
     * Constructs a read-only token containing an operator in a string.
     * 
     * @param symbol
     *            with symbol that this operator represents.
     */
    public TokenOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String asText() {
        return this.symbol;
    }

    /**
     * Gets string representation of operator that the token represents.
     * 
     * @return String with symbol that this operator represents.
     */
    public String getSymbol() {
        return this.symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }
}
