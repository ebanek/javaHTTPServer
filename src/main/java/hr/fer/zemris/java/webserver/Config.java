package hr.fer.zemris.java.webserver;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Contains all default setting and values of server. Declutters the main server
 * class.
 * 
 * @author Erik Banek
 */
public interface Config {
    /**
     * Timeout in which the server waits for client request. After timeout
     * passes the server checks if it should still run and stops, or repeats
     * waiting for client request.
     */
    int SERVER_TIMEOUT = 4000;
    /**
     * Charset which is used to store messages in the log file.
     */
    Charset LOG_CHARSET = StandardCharsets.UTF_8;
    /** Charset which is used for reading server config folder. */
    Charset CONFIG_CHARSET = StandardCharsets.UTF_8;
    /** Default name of server properties file. */
    String SERVER_PROP_FILE = "server.properties";
    /**
     * Default name of server properties file that contains supported mime
     * files.
     */
    String DEFAULT_MIME_FILE = "mime.properties";
    /**
     * Default name of server properties file that contains worker packages.
     */
    String DEFAULT_WORKERS_FILE = "worker.properties";
    /** Default server address. */
    String DEFAULT_ADDRESS = "127.0.0.1";
    /** Default number of supported threads in the server. */
    String DEFAULT_WORKER_NUM = "4";
    /** Default port on which the server listens. */
    String DEFAULT_PORT = "80";
    /**
     * Default time in minutes in which a single session with a client is
     * remembered.
     */
    String DEFAULT_SESSION_TIMEOUT = "10";
    /**
     * Default start path which indicates that a worker with some name is
     * wanted.
     */
    String WORKERS_PATH_START = "/ext/";
    /** Extension which indicates that a script execution from server is wanted. */
    String SCRIPT_EXTENSION = ".smscr";
    /** Default encoding/decoding charset of headers. */
    Charset HEADER_ENCODING = StandardCharsets.US_ASCII;
    /** Byte array whose encounter signals header end. */
    byte[] HEADER_TERMINATING_BYTES_LEN2 = new byte[] { 10, 10 };
    /** Byte array whose encounter signals header end. */
    byte[] HEADER_TERMINATING_BYTES_LEN4 = new byte[] { 13, 10,
            13, 10 };
    /** Package inside which worker classes are located. */
    String WORKER_PACKAGE = "hr.fer.zemris.java.webserver.workers";
    /** If no mime type is found, this is the default one. */
    String DEFAULT_MIME_TYPE = "application/octet-stream";
    /** Milliseconds in minute for calculations. */
    int MILLISECONDS_IN_MINUTE = 1000 * 60;
    /** How much the clean waits before cleaning session map. */
    int CLEANER_MINUTES_TO_SLEEP = 5;
    /** Alphabet for generating random String. */
    String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /** Length of random String. */
    int RANDOM_STRING_LENGTH = 20;
}
