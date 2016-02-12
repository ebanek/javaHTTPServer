package hr.fer.zemris.java.custom.scripting.exec.functions.context;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Operation of parameter deletion in context.
 * 
 * @author Erik Banek
 */
public interface IContextDeleter {
    /**
     * Deletes a parameter with given key in context.
     * 
     * @param key
     *            key of parameter to be deleted.
     * @param rc
     *            from which a parameter is deleted.
     */
    void delete(String key, RequestContext rc);
}
