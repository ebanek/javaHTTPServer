package hr.fer.zemris.java.custom.scripting.exec.functions.context;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Operation of parameter setting in context.
 * 
 * @author Erik Banek
 */
public interface IContextSetter {
    /**
     * Sets some parameter whose key is specified in context.
     * 
     * @param key
     *            of parameter to be set.
     * @param value
     *            to which the parameter will be set.
     * @param rc
     *            context in which the parameters are set.
     */
    void set(String key, String value, RequestContext rc);
}
