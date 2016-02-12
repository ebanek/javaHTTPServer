package hr.fer.zemris.java.custom.scripting.exec.functions;

import hr.fer.zemris.java.custom.scripting.exec.ValueWrapper;

/**
 * Specifies an operation on two {@code ValueWrapper}s.
 * 
 * @author Erik Banek
 */
public interface IBinaryFunction {
    /**
     * Applies the binary function on two {@code ValueWrapper}s.
     * 
     * @param a
     *            first argument.
     * @param b
     *            second argument.
     * @return result of binary operation.
     */
    ValueWrapper operate(ValueWrapper a, ValueWrapper b);
}
