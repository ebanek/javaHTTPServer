package hr.fer.zemris.java.custom.collections;

/**
 * Exception for use with custom stack. Has basic functionalities of exceptions.
 * 
 * @author Erik Banek
 */
public class EmptyStackException extends RuntimeException {
    /**
     * Generated UID.
     */
    private static final long serialVersionUID = 6307697858263274779L;

    /**
     * Constructs an EmptyStackException with no detail message.
     */
    public EmptyStackException() {
    }

    /**
     * Constructs an EmptyStackException with the specified detail message.
     *
     * @param message
     *            the detail message.
     */
    public EmptyStackException(String message) {
        super(message);
    }
}
