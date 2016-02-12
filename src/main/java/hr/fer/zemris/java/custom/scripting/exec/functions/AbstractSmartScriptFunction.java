package hr.fer.zemris.java.custom.scripting.exec.functions;

import hr.fer.zemris.java.custom.collections.ObjectStack;

/**
 * Functions which has a check for number of arguments needed for its execution.
 * 
 * @author Erik Banek
 */
public abstract class AbstractSmartScriptFunction implements
        ISmartScriptFunction {
    /** Args needed for execution. */
    private int argumentsNeeded;

    /**
     * Constructor.
     * 
     * @param argumentsNeeded
     *            for execution of fucntion.
     */
    public AbstractSmartScriptFunction(int argumentsNeeded) {
        this.argumentsNeeded = argumentsNeeded;
    }

    /**
     * Checks if stack has equal or more arguments than needed for execution.
     * 
     * @param stack
     *            whose size is checked.
     * @throws IllegalArgumentException
     *             if stack is not big enough.
     */
    protected void check(ObjectStack stack) throws IllegalArgumentException {
        if (stack.size() < argumentsNeeded) {
            throw new IllegalArgumentException(
                    "Not enough arguments for operation: " +
                            this.getClass().getSimpleName());
        }
    }
}
