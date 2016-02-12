package hr.fer.zemris.java.custom.scripting.tokens;

/**
 * Token that represents a function. The function is represented as a String of
 * the functions name.
 * 
 * @author Erik Banek
 */
public class TokenFunction extends Token {
    /** Token function name. */
    private String name;

    /**
     * Constructs a read-only token with function as String.
     * 
     * @param name
     *            name of function.
     */
    public TokenFunction(String name) {
        this.name = name;
    }

    @Override
    public String asText() {
        return ("@" + this.name);
    }

    /**
     * Gets function name in token.
     * 
     * @return name of function of token.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
