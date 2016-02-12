package hr.fer.zemris.java.custom.scripting.exec.functions;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.exec.ValueWrapper;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Binary function which does some calculation with two {@code ValueWrapper}s.
 * 
 * @author Erik Banek
 */
public class BinarySmartFunction extends AbstractSmartScriptFunction {
    /** Function which does the actual calculation. */
    private IBinaryFunction function;

    /**
     * Constructor.
     * 
     * @param function
     *            the calculation part.
     */
    public BinarySmartFunction(IBinaryFunction function) {
        super(2);
        this.function = function;
    }

    @Override
    public void apply(ObjectStack stack, RequestContext rc)
            throws IllegalArgumentException, ClassCastException {
        check(stack);
        ValueWrapper a = new ValueWrapper(stack.pop());
        ValueWrapper b = new ValueWrapper(stack.pop());
        stack.push(function.operate(a, b).toString());
    }

}
