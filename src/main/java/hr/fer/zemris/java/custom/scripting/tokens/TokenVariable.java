package hr.fer.zemris.java.custom.scripting.tokens;

/**
 * Token class that represents a single variable by its name.
 * 
 * @author Erik Banek
 */
public class TokenVariable extends Token {
    /** Name of variable in token. */
    private String name;

    /**
     * Constructs a read-only token, that holds a variable name.
     * 
     * @param name
     *            name of variable.
     */
    public TokenVariable(String name) {
        this.name = name;
    }

    @Override
    public String asText() {
        return this.name;
    }

    /**
     * Token variable name getter.
     * 
     * @return name of this token variable.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
