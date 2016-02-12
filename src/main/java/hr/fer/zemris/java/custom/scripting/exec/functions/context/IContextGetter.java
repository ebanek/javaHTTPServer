package hr.fer.zemris.java.custom.scripting.exec.functions.context;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Operation of parameter getting from context.
 * 
 * @author Erik Banek
 */
public interface IContextGetter {
    /**
     * Gets a parameter with given key in context.
     * 
     * @param key
     *            key of parameter to be got.
     * @param rc
     *            from which a parameter is got.
     * @return value to which the key is mapped from context.
     */
    String get(String key, RequestContext rc);
}
