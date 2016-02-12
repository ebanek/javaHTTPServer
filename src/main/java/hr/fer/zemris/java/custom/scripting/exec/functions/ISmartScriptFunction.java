package hr.fer.zemris.java.custom.scripting.exec.functions;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Function which does something on the given stack, and possibly gets/sets the
 * values on the {@code RequestContext}.
 * 
 * @author Erik Banek
 */
public interface ISmartScriptFunction {
    /**
     * Applies the function.
     * 
     * @param stack
     *            from which arguments are popped/pushed.
     * @param rc
     *            from which arguments are got/set.
     * @throws IllegalArgumentException
     *             if operation cannot be applied.
     * @throws ClassCastException
     *             if a problem occurs with expected value format.
     */
    void apply(ObjectStack stack, RequestContext rc)
            throws IllegalArgumentException, ClassCastException;
}
