package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Context which simplifies server interaction with client. The context handles
 * header generation an turning server messages into bytes that will be sent to
 * the client.
 * 
 * @author Erik Banek
 */
public class RequestContext {
    /**
     * Represents a cookie that is sent in the http response from the server.
     * 
     * @author Erik Banek
     */
    public static class RCCookie {
        /** Name of cookie. */
        private String name;
        /** Value of cookie. */
        private String value;
        /** Domain in which the cookie is valid. */
        private String domain;
        /** Request path for which the cookie is valid. */
        private String path;
        /** Max age of cookie, currently unused. */
        private Integer maxAge;

        /**
         * Constructor.
         * 
         * @param name
         *            cookie name.
         * @param value
         *            value of cookie.
         * @param maxAge
         *            max age of cookie, currently unused.
         * @param domain
         *            in which the cookie is valid.
         * @param path
         *            for which the cookie is valid.
         */
        public RCCookie(String name, String value, Integer maxAge,
                String domain, String path) {
            if (name == null || value == null) {
                throw new IllegalArgumentException(
                        "Cookie name and value cannot be null!");
            }
            this.name = name;
            this.value = value;
            this.domain = domain;
            this.path = path;
            this.maxAge = maxAge;
        }

        /**
         * Gets domain in which the cookie is valid.
         * 
         * @return cookie domain.
         */
        public String getDomain() {
            return domain;
        }

        /**
         * Gets max age of cookie.
         * 
         * @return cookie max age.
         */
        public Integer getMaxAge() {
            return maxAge;
        }

        /**
         * Gets name of cookie.
         * 
         * @return cookie name.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets path in which the cookie is valid.
         * 
         * @return cookie path.
         */
        public String getPath() {
            return path;
        }

        /**
         * Gets value of cookie.
         * 
         * @return cookie value.
         */
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(name + ":\"" + value + "\"");
            if (domain != null) {
                sb.append("; Domain=" + domain);
            }
            if (path != null) {
                sb.append("; Path=" + path);
            }
            if (maxAge != null) {
                sb.append("; Max-Age=" + maxAge);
            }
            sb.append("; Http-Only");
            return sb.toString();
        }
    }
    /** Protocol which is used. */
    private final static String PROTOCOL = "HTTP/1.1";
    /** Default encoding of the header of http response. */
    private final static Charset HEADER_ENCODING = StandardCharsets.US_ASCII;
    /** Stream to which the context sends bytes of data. */
    private OutputStream outputStream;
    /**
     * Charset which the context uses for turning messages from server in String
     * format into byte format.
     */
    private Charset charset;
    /**
     * Encoding which the context tells the client to use for the text file to
     * be sent.
     */
    private String encoding = "UTF-8";
    /** Status code of the http response which is sent to client. */
    private int statusCode = 200;
    /** Status code of the http response which is sent to client. */
    private String statusText = "OK";
    /** Mime type of file which is sent to client. */
    private String mimeType = "text/html";
    /** Parameters of client http request. */
    private Map<String, String> parameters;
    /**
     * Temporary parameters for storage in calculating the result that will be
     * sent to user in some form.
     */
    private Map<String, String> temporaryParameters;
    /**
     * Persistent parameters which are stored through multiple client-server
     * interactions.
     */
    private Map<String, String> persistentParameters;
    /** Cookies that will be given to the client. */
    private List<RCCookie> outputCookies;

    /**
     * True if the header part of http response has been sent. Some request
     * properties cannot be changed after the header has been generated.
     */
    private boolean headerGenerated = false;

    /**
     * Constructor.
     * 
     * @param outputStream
     *            on which the request writes data to client.
     * @param parameters
     *            map which the request will use.
     * @param persistentParameters
     *            map which the request will use, and server remember.
     * @param outputCookies
     *            which the request will output to client.
     */
    public RequestContext(OutputStream outputStream,
            Map<String, String> parameters,
            Map<String, String> persistentParameters,
            List<RCCookie> outputCookies) {
        if (outputStream == null) {
            throw new IllegalArgumentException("Stream cannot be null!");
        }
        this.outputStream = outputStream;
        this.parameters = (parameters == null) ? new HashMap<String, String>()
                : parameters;
        this.persistentParameters = (persistentParameters == null) ? new HashMap<String, String>()
                : persistentParameters;
        this.outputCookies = (outputCookies == null) ? new ArrayList<RCCookie>()
                : outputCookies;
        this.temporaryParameters = new HashMap<>();
    }

    /**
     * Adds a cookie to the context.
     * 
     * @param rcCookie
     *            to be added.
     */
    public void addRCCookie(RCCookie rcCookie) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        outputCookies.add(rcCookie);
    }

    /**
     * Retrieves the value from parameters map with the given name.
     * 
     * @param name
     *            of value to be retrieved.
     * @return wanted value.
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Retrieves names of all parameters in parameters map, the given map is
     * read-only.
     * 
     * @return read-only parameters map.
     */
    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(parameters.keySet());
    }

    /**
     * Retrieves the value from persistent parameters map with the given name.
     * 
     * @param name
     *            of value to be retrieved.
     * @return wanted value.
     */
    public String getPersistentParameter(String name) {
        return persistentParameters.get(name);
    }

    /**
     * Retrieves names of all parameters in persistent parameters map, the given
     * map is read-only.
     * 
     * @return read-only persistent parameters map.
     */
    public Set<String> getPersistentParameterNames() {
        return Collections.unmodifiableSet(persistentParameters.keySet());
    }

    /**
     * Retrieves the value from temporary parameters map with the given name.
     * 
     * @param name
     *            of value to be retrieved.
     * @return wanted value.
     */
    public String getTemporaryParameter(String name) {
        return temporaryParameters.get(name);
    }

    /**
     * Retrieves names of all parameters in temporary parameters map, the given
     * map is read-only.
     * 
     * @return read-only temporary parameters map.
     */
    public Set<String> getTemporaryParameterNames() {
        return Collections.unmodifiableSet(temporaryParameters.keySet());
    }

    /**
     * Removes a value from temporaryParameters map.
     * 
     * @param name
     *            of value to be removed.
     */
    public void removePersistentParameter(String name) {
        persistentParameters.remove(name);
    }

    /**
     * Removes a value from temporaryParameters map.
     * 
     * @param name
     *            of value to be removed.
     */
    public void removeTemporaryParameter(String name) {
        temporaryParameters.remove(name);
    }

    /**
     * Sets the encoding which the client will be told to use for interpreting
     * textual data.
     * 
     * @param encoding
     *            to use for textual data.
     */
    public void setEncoding(String encoding) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        if (encoding == null) {
            this.encoding = "UTF-8";
            return;
        }
        this.encoding = encoding;
    }

    /**
     * Sets the mime type so the client can correctly interpret the incoming
     * bytes.
     * 
     * @param mimeType
     *            of bytes that will be sent to client.
     */
    public void setMimeType(String mimeType) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        if (mimeType == null) {
            this.mimeType = "text/html";
            return;
        }
        this.mimeType = mimeType;
    }

    /**
     * Stores a value in persistentParameters map.
     * 
     * @param name
     *            of value to be stored.
     * @param value
     *            to be stored.
     */
    public void setPersistentParameter(String name, String value) {
        persistentParameters.put(name, value);
    }

    /**
     * Sets status code of http response.
     * 
     * @param statusCode
     *            of response.
     */
    public void setStatusCode(int statusCode) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        this.statusCode = statusCode;
    }

    /**
     * Sets status text of http response.
     * 
     * @param statusText
     *            of response.
     */
    public void setStatusText(String statusText) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        if (statusText == null) {
            statusText = "OK";
            return;
        }
        this.statusText = statusText;
    }

    /**
     * Stores a value in temporaryParameters map.
     * 
     * @param name
     *            of value to be stored.
     * @param value
     *            to be stored.
     */
    public void setTemporaryParameter(String name, String value) {
        temporaryParameters.put(name, value);
    }

    /**
     * Writes byte data to user.
     * 
     * @param data
     *            to be sent to user.
     * @return this.
     * @throws IOException
     *             if a problem occurs with writing data to user.
     */
    public RequestContext write(byte[] data) throws IOException {
        if (!headerGenerated) {
            writeHeader();
        }
        outputStream.write(data, 0, data.length);
        return this;
    }

    /**
     * Writes a String to the user, generates the header if it was not
     * generated.
     * 
     * @param text
     *            that the user will be sent.
     * @return this.
     * @throws IOException
     *             if a problem occurs with writing text to user.
     */
    public RequestContext write(String text) throws IOException {
        if (!headerGenerated) {
            writeHeader();
        }
        writeString(text, charset);
        return this;
    }

    /**
     * Writes the header that contains all current information contained in the
     * context that a typical header needs.
     * 
     * @throws IOException
     *             if a problem occurs with writing the header to user.
     */
    private void writeHeader() throws IOException {
        headerGenerated = true;
        charset = Charset.forName(encoding);
        outputCookies = Collections.unmodifiableList(outputCookies);

        writeString(PROTOCOL + " " + statusCode + " " + statusText + "\n",
                HEADER_ENCODING);

        String type = mimeType.startsWith("text/") ? (mimeType + "; charset=" + encoding)
                : mimeType;
        writeString("Content-Type: " + type + "\n", HEADER_ENCODING);

        for (RCCookie cookie : outputCookies) {
            String cookieString = cookie.toString();
            writeString("Set-cookie: " + cookieString + "\n", HEADER_ENCODING);
        }
        writeString("\n", HEADER_ENCODING);
    }

    /**
     * Writes a String to the client.
     * 
     * @param s
     *            String to be written.
     * @param charset
     *            which is used to turn the String into bytes.
     * @throws IOException
     *             if something wrong occurs with writing String to client.
     */
    private void writeString(String s, Charset charset) throws IOException {
        byte[] toWrite = s.getBytes(charset);
        outputStream.write(toWrite, 0, toWrite.length);
        outputStream.flush();
    }
}
