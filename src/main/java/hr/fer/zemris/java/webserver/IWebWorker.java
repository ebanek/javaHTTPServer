package hr.fer.zemris.java.webserver;

import java.io.IOException;

/**
 * Processes the request of the client in some way, using the
 * {@code RequestContext}.
 * 
 * @author Erik Banek
 */
public interface IWebWorker {
    /**
     * Processes the request of client and writes the output through the
     * context.
     * 
     * @param context
     *            through which output to client is handled.
     * @throws IOException
     *             if problem occurs with writing outptu to client.
     */
    void processRequest(RequestContext context) throws IOException;
}
