package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Exception that occurs when a visitor encounters some problem while visiting a
 * node hierarchy.
 * 
 * @author Erik Banek
 */
public class VisitorException extends RuntimeException {
    /**
     * Default UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public VisitorException() {
        super();
    }

    /**
     * Constructor with message.
     * 
     * @param message
     *            of exception
     */
    public VisitorException(String message) {
        super(message);
    }
}
