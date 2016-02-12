package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.IOException;

/**
 * Sends the client a html containing all parameter names and their values from
 * the path the client sent.
 * 
 * @author Erik Banek
 */
public class EchoParams implements IWebWorker {

    @Override
    public void processRequest(RequestContext context) throws IOException {
        context.setMimeType("text/html");

        context.write("<html><body>");
        context.write("<h3>Here are all parameter names and values:</h3>");
        for (String key : context.getParameterNames()) {
            String value = context.getParameter(key);
            context.write("<p>" + key + ": " + value + "<p>");
        }
        context.write("</body></html>");

    }

}
