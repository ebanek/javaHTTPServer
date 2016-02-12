package hr.fer.zemris.java.custom.scripting.exec.functions.context;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.exec.functions.AbstractSmartScriptFunction;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Gets some parameter from some map of given context.
 * 
 * @author Erik Banek
 */
public class ContextGetter extends AbstractSmartScriptFunction {
    /** Specifies getting operation . */
    private IContextGetter getter;

    /**
     * Constructor.
     * 
     * @param getter
     *            getting specificator.
     */
    public ContextGetter(IContextGetter getter) {
        super(2);
        this.getter = getter;
    }

    @Override
    public void apply(ObjectStack stack, RequestContext rc)
            throws IllegalArgumentException, ClassCastException {
        check(stack);

        Object defaultValue = stack.pop();
        String key = (String) stack.pop();
        String value = getter.get(key, rc);

        stack.push(value == null ? defaultValue : value);
    }

}
