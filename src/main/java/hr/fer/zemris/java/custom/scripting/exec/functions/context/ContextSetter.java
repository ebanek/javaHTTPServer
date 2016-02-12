package hr.fer.zemris.java.custom.scripting.exec.functions.context;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.exec.functions.AbstractSmartScriptFunction;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Sets some parameter from some map of given context.
 * 
 * @author Erik Banek
 */
public class ContextSetter extends AbstractSmartScriptFunction {
    /** Specifies how the value is set in context. */
    private IContextSetter setter;

    /**
     * Constructor.
     * 
     * @param setter
     *            setting specificator.
     */
    public ContextSetter(IContextSetter setter) {
        super(2);
        this.setter = setter;
    }

    @Override
    public void apply(ObjectStack stack, RequestContext rc)
            throws IllegalArgumentException, ClassCastException {
        check(stack);

        String key = (String) stack.pop();
        String value = (String) stack.pop();
        setter.set(key, value, rc);
    }
}
