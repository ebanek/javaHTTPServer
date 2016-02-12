package hr.fer.zemris.java.custom.scripting.parser;

/**
 * Used for signaling that a parsing error has happened. Exception for use with
 * custom parser.
 * 
 * @author Erik Banek
 */
public class SmartScriptParserException extends RuntimeException {
    /**
     * Default UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a parser exception.
     */
    public SmartScriptParserException() {

    }

    /**
     * Constructs a custom parser exception with message.
     * 
     * @param message
     *            of problem that caused the exception.
     */
    public SmartScriptParserException(String message) {
        super(message);
    }
}
