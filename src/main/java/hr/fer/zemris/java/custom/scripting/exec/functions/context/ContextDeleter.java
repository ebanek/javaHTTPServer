package hr.fer.zemris.java.custom.scripting.exec.functions.context;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.exec.functions.AbstractSmartScriptFunction;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Deletes some parameter from some map of given context.
 * 
 * @author Erik Banek
 */
public class ContextDeleter extends AbstractSmartScriptFunction {
    /** Specifies the deletion operation. */
    private IContextDeleter deleter;

    /**
     * Constructor.
     * 
     * @param deleter
     *            deletion specificator.
     */
    public ContextDeleter(IContextDeleter deleter) {
        super(1);
        this.deleter = deleter;
    }

    @Override
    public void apply(ObjectStack stack, RequestContext rc)
            throws IllegalArgumentException, ClassCastException {
        check(stack);

        String key = (String) stack.pop();
        deleter.delete(key, rc);
    }
}
