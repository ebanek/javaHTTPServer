package hr.fer.zemris.java.custom.scripting.tokens;

/**
 * Basic token that represents an atomic expression. All other tokens are
 * derived from this class.
 * 
 * @author Erik Banek
 */
public class Token {

    /**
     * Returns String representation of token content as it should be in the
     * custom language for parsing.
     * 
     * @return text representation of token content.
     */
    public String asText() {
        return "";
    }
}
