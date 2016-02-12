package hr.fer.zemris.java.webserver;

import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server that supports the HTTP get method. For each client request makes a new
 * thread that handles that request. Supports remembering the client through the
 * cookie mechanism. It is constructed by supplying a String of a path to a
 * folder containing a file with name server.properties from which the basic
 * properties of the server are read.
 * 
 * <p>
 * The server supports: getting files that are in its folder, executing the
 * scripts in the scripts folder, creating a {@code IWebWorker} which handles a
 * request, or calling a predefined path that will use an already created
 * {@code IWebWorker} that will process the user request.
 * 
 * <p>
 * All requests are logged in the {@code log.txt} file in the root of the server
 * folder.
 * 
 * @author Erik Banek
 */
public class SmartHttpServer {
    /**
     * Worker that handles client requests.
     * 
     * @author Erik Banek
     */
    private class ClientWorker implements Runnable {
        /**
         * Socket that represents the connection to client which this worker
         * handles.
         */
        private Socket csocket;
        /** Stream from which this worker gets the client request. */
        private PushbackInputStream istream;
        /**
         * Stream to which this worker outputs the results of the request to
         * client.
         */
        private OutputStream ostream;
        /**
         * Version of HTTP protocol which the client requested, possibly used
         * later.
         */
        @SuppressWarnings("unused")
        private String version;
        /**
         * Method of HTTP protocol which the client requested, possibly used
         * later.
         */
        @SuppressWarnings("unused")
        private String method;
        /** Parameters of the client http request. */
        private Map<String, String> params = new HashMap<String, String>();
        /** Permanent parameters of the client session/sessions. */
        private Map<String, String> permParams = new ConcurrentHashMap<String, String>();
        /** List of clients cookies. */
        private List<RCCookie> outputCookies = new ArrayList<RequestContext.RCCookie>();
        /**
         * Session id of client, generated if the client is a first timer or
         * forgotten, or got from the cookie list if the client is remembered.
         */
        private String SID;

        /**
         * Constructor.
         * 
         * @param csocket
         *            connection to the client which this worker handles.
         */
        public ClientWorker(Socket csocket) {
            this.csocket = csocket;
        }

        /**
         * Checks if the requested document exists in the server folder.
         * 
         * @param rc
         *            through which the user is served.
         * @param requestPath
         *            which the user requested.
         * @return true iff the requested document exists.
         */
        private boolean checkIfDocumentExists(RequestContext rc,
                String requestPath) {
            Path path = Paths.get(documentRoot + requestPath);
            if (!path.getParent().toFile().exists()) {
                sendError(rc, 403, "Forbidden access " + path);
                return false;
            }
            if (!path.toFile().exists() || !path.toFile().canRead()) {
                sendError(rc, 404, "File does not exist. " + path);
                return false;
            }
            if (path.toFile().getName().split("\\.").length != 2) {
                sendError(rc, 404, "Wrong document format.");
                return false;
            }
            return true;
        }

        /**
         * Checks if the session is remembered. Saves the persistent parameter
         * map if it was.
         * 
         * @param request
         *            from the client, list of header lines.
         * @return true iff the session has been remembered.
         */
        private boolean checkSession(List<String> request) {
            String potentialSID = SmartServerUtility.getCookieSID(request);
            if (potentialSID == null) {
                return false;
            }
            Map<String, String> map = getPermParamMap(potentialSID);
            if (map == null) {
                return false;
            }
            permParams = map;
            return true;
        }

        /**
         * Closes the streams that served for communicating with client.
         * 
         * @return true iff streams were successfully closed.
         */
        private boolean close() {
            try {
                ostream.close();
                istream.close();
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        /**
         * Initializes the parameters the user has given in the request path.
         * 
         * @param requestPath
         *            that the user sent.
         */
        private void initializeParameters(String requestPath) {
            String[] split = requestPath.split("\\?");
            if (split.length < 2) {
                return;
            }
            String[] paramPairs = split[1].split("&");
            for (String ppair : paramPairs) {
                params.put(ppair.split("=")[0], ppair.split("=")[1]);
            }
        }

        /**
         * Initializes the request context through which the client is sent data
         * that he requested.
         * 
         * @return request context.
         */
        private RequestContext initializeRequestContext() {
            RequestContext rc =
                    new RequestContext(ostream, params, permParams,
                            outputCookies);
            rc.setStatusCode(200);
            return rc;
        }

        /**
         * Initializes basic variables of the request.
         * 
         * @param firstLine
         *            of http header request.
         * @return request path of the request.
         */
        private String initializeVariables(String firstLine) {
            String[] split = firstLine.split(" ");
            method = split[0];
            version = split[2];
            return split[1];
        }

        /**
         * Loads the file from server root folder and sends it to the user
         * through the {@code RequestContext}.
         * 
         * @param rc
         *            through which the user is served.
         * @param requestPath
         *            which the user requested.
         */
        private void loadFile(RequestContext rc, String requestPath) {
            if (!checkIfDocumentExists(rc, requestPath)) {
                return;
            }
            Path path = Paths.get(documentRoot + requestPath);
            String extension = path.toFile().toString().split("\\.")[1];
            try {
                rc.setMimeType(mimeTypes.getOrDefault(extension,
                        Config.DEFAULT_MIME_TYPE));
                rc.write(Files.readAllBytes(path));
            } catch (IOException e) {
                SmartServerUtility.log("Error reading requested file.", bw);
            }
        }

        /**
         * Opens the streams that serve for communicating with client.
         * 
         * @return true iff streams were successfully opened.
         */
        private boolean open() {
            try {
                ostream = csocket.getOutputStream();
                istream = new PushbackInputStream(csocket.getInputStream());
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        /**
         * Reads the whole request header from the bytes the client sends
         * through connection stream.
         * 
         * @return String containing the whole header of the client request.
         */
        private String readWholeHeader() {
            try {
                byte[] lastFour = new byte[] { -1, -1, -1, -1 };
                byte[] lastTwo = new byte[] { -1, -1 };
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while (true) {
                    byte b = Integer.valueOf(istream.read()).byteValue();
                    if (b == -1) {
                        return null;
                    }
                    if (b != 13) {
                        bos.write(b);
                    }

                    lastTwo[0] = lastTwo[1];
                    lastTwo[1] = b;
                    for (int i = 0; i < 3; i++) {
                        lastFour[i] = lastFour[i + 1];
                    }
                    lastFour[3] = b;
                    if (SmartServerUtility.isTerminating(lastTwo) ||
                            SmartServerUtility.isTerminating(lastFour)) {
                        byte[] array = bos.toByteArray();
                        return new String(array, Config.HEADER_ENCODING);
                    }
                }
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        public void run() {
            if (!open()) {
                SmartServerUtility.log("Error opening connection to client.",
                        bw);
                return;
            }
            String unparsedRequest = readWholeHeader();
            if (unparsedRequest == null) {
                SmartServerUtility.log("Error getting header.",
                        bw);
                sendError(400, "Illegal request");
                return;
            }
            List<String> request = SmartServerUtility
                    .parseIntoHeaderLines(unparsedRequest);

            if (request.size() < 1
                    || !SmartServerUtility
                    .checkRequest(request.get(0),
                            SUPPORTED_METHODS,
                            SUPPORTED_VERSIONS)) {
                SmartServerUtility.log("Erroneus request header.",
                        bw);
                sendError(400, "Illegal request");
                return;
            }

            boolean sessionSaved = checkSession(request);
            if (!sessionSaved) {
                setCookie(SID = getRandomString());
            }

            String requestPath = initializeVariables(request.get(0));
            initializeParameters(requestPath);

            RequestContext rc = initializeRequestContext();
            serve(rc, requestPath);

            if (!sessionSaved) {
                saveSession();
            }

            if (!close()) {
                SmartServerUtility.log("Error closing connection to client "
                        + SID +
                        "\n", bw);
            }
        }

        /** Saves user session. */
        private void saveSession() {
            makeNewSession(SID, permParams);
        }

        /**
         * Executes a script that the user requested.
         * 
         * @param rc
         *            through which the user is served.
         * @param requestPath
         *            which the user requested.
         */
        private void script(RequestContext rc, String requestPath) {
            if (!checkIfDocumentExists(rc, requestPath.split("\\?")[0])) {
                return;
            }
            Path path = Paths.get(documentRoot + requestPath.split("\\?")[0]);
            try {
                SmartServerUtility.executeScript(rc, path);
            } catch (IOException e) {
                SmartServerUtility.log("Error writing to client", bw);
            }
        }

        /**
         * Sends an error result to user.
         * 
         * @param statusCode
         *            code of error.
         * @param message
         *            error message.
         */
        private void sendError(int statusCode, String message) {
            sendError(new RequestContext(ostream, params, permParams,
                    outputCookies), statusCode, message);
        }

        /**
         * Sends an error result through given request context.
         * 
         * @param rc
         *            output to user.
         * @param statusCode
         *            code of error.
         * @param message
         *            message of error.
         */
        private void sendError(RequestContext rc, int statusCode, String message) {
            rc.setStatusCode(statusCode);
            rc.setStatusText(message);
            try {
                rc.write(statusCode + " " + message);
                SmartServerUtility.log(statusCode + " " + message, bw);
            } catch (IOException e) {
                SmartServerUtility.log("Error writing to client.", bw);
            }
        }

        /**
         * Serves the client request.
         * 
         * @param rc
         *            {@code RequestContext} through which the client is served.
         * @param requestPath
         *            path which the client has requested in the request
         *            headers.
         */
        private void serve(RequestContext rc, String requestPath) {
            if (requestPath.startsWith(Config.WORKERS_PATH_START)) {
                SmartServerUtility.log("Worker by convention", bw);
                workerConvent(rc, requestPath);
            } else if (workersMap.containsKey(requestPath.split("\\?")[0])) {
                SmartServerUtility.log("Worker by configuration", bw);
                workerConfig(rc, requestPath);
            } else if (requestPath.split("\\?")[0]
                    .endsWith(Config.SCRIPT_EXTENSION)) {
                SmartServerUtility.log("Script execution.", bw);
                script(rc, requestPath);
            } else {
                SmartServerUtility.log("Loading file.", bw);
                loadFile(rc, requestPath);
            }
        }

        /**
         * Puts a new cookie in the cookie array using the given random String.
         * 
         * @param randString
         *            String representing the sid.
         */
        private void setCookie(String randString) {
            outputCookies.add(new RCCookie("sid", randString, null, address,
                    "/"));
        }

        /**
         * Serves client request by way of workers that have predefined paths.
         * 
         * @param rc
         *            through which the user is served.
         * @param requestPath
         *            which the user requested.
         */
        private void workerConfig(RequestContext rc, String requestPath) {
            IWebWorker iww = workersMap.get(requestPath.split("\\?")[0]);
            try {
                iww.processRequest(rc);
            } catch (IOException e) {
                SmartServerUtility.log("Problem with worker output.", bw);
            }
        }

        /**
         * Servers user by way of instantiating a worker and delegating the
         * request processsing to the worker.
         * 
         * @param rc
         *            through which the worker will output the result of request
         *            to client.
         * @param requestPath
         *            which the user requested in the http header.
         */
        private void workerConvent(RequestContext rc, String requestPath) {
            String s = requestPath
                    .substring(Config.WORKERS_PATH_START.length());
            String fqcn = Config.WORKER_PACKAGE + "." + s.split("\\?")[0];
            IWebWorker iww = null;
            try {
                iww = getWorker(fqcn);
            } catch (ClassNotFoundException | InstantiationException
                    | IllegalAccessException e) {
                sendError(rc, 400, "Error creating an instance of worker");
                return;
            }
            try {
                iww.processRequest(rc);
            } catch (IOException e) {
                SmartServerUtility.log("Problem with worker output.", bw);
            }
        }

    }
    /**
     * Main server thread, opens the port and listens for client requests.
     * 
     * @author Erik Banek
     */
    protected class ServerThread extends Thread {
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(address, port));
                serverSocket.setSoTimeout(Config.SERVER_TIMEOUT);
            } catch (IOException e) {
                System.err.println("Error creating access point, try again.");
                return;
            }

            while (true) {
                if (serverShutdown) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        System.err.println("Error closing access point.");
                    }
                    break;
                }

                Socket client = null;
                try {
                    client = serverSocket.accept();
                } catch (IOException e) {
                    continue;
                }
                ClientWorker cw = new ClientWorker(client);
                threadPool.submit(cw);
                // no future getting, they are in the pool awaiting
            }
        }
    }
    /**
     * Class which represents a session with a client.
     * 
     * @author Erik Banek
     */
    private static class SessionMapEntry {
        /** Time by which session entry is valid. */
        long validUntil;
        /** Persistent parameters map saved from last session. */
        Map<String, String> map;

        /**
         * Constructor.
         * 
         * @param validUntil
         *            time until which last user session is still valid.
         * @param map
         *            persistent parameters map saved from last session.
         */
        public SessionMapEntry(long validUntil,
                Map<String, String> map) {
            this.validUntil = validUntil;
            this.map = map;
        }
    }
    /** Supported methods of the server. */
    private static Set<String> SUPPORTED_METHODS = new HashSet<String>();
    /** Supported http protocol versions of the server. */
    private static Set<String> SUPPORTED_VERSIONS = new HashSet<String>();
    static {
        SUPPORTED_METHODS.add("GET");
        SUPPORTED_VERSIONS.add("HTTP/1.1");
        SUPPORTED_VERSIONS.add("HTTP/1.0");
    }
    /** String of the address on which the server listens. */
    private String address;
    /** Port on which the server listens. */
    private int port;
    /** Number of threads that handle client request. */
    private int workerThreads;
    /**
     * Time period inside which the server remembers the client from its last
     * request.
     */
    private int sessionTimeout;
    /** Mime types mapped to their extensions of folders. */
    private Map<String, String> mimeTypes = new HashMap<>();;
    /** Main server thread which listens for connections. */
    private ServerThread serverThread;
    /** Thread pool which handles worker threads that handle client requests. */
    private ExecutorService threadPool;
    /** Path do root of server folder. */
    private Path documentRoot;
    /** Paths that are specially mapped to workers. */
    private Map<String, IWebWorker> workersMap = new HashMap<>();
    /** Variable that tells the server if it should shutdown. */
    private volatile boolean serverShutdown = false;

    /** Saved sessions of clients. */
    private volatile Map<String, SessionMapEntry> sessions =
            new HashMap<String, SmartHttpServer.SessionMapEntry>();

    /** RNG for creating session id's. */
    private Random sessionRandom = new Random();

    /** Path to file in which logs are written. */
    private Path logFile;

    /** Writer which is used for writing into the log file. */
    private volatile BufferedWriter bw;

    /**
     * Constructs the server.
     * 
     * @param configFileFolderPath
     *            String of path to folder containing server properties.
     * @throws IllegalArgumentException
     *             if the properties file is faulty, or if the wrong path is
     *             provided.
     */
    public SmartHttpServer(String configFileFolderPath)
            throws IllegalArgumentException {
        try {
            initProperties(configFileFolderPath);
            createLogFile();
            bw = Files.newBufferedWriter(logFile, Config.LOG_CHARSET);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Problem with initializing server configuration, please try again.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Problem with parsing properties information.");
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            throw new IllegalArgumentException("Unsupported IWebWorker class!");
        }
        startCleanerThread();
        serverThread = new ServerThread();
    }

    /**
     * Work of cleaning the sessions map. It is used by the cleaner thread.
     */
    public synchronized void clean() {
        Iterator<Entry<String, SessionMapEntry>> it = sessions.entrySet()
                .iterator();
        while (it.hasNext()) {
            Entry<String, SessionMapEntry> entry = it.next();
            Long time = System.currentTimeMillis();
            if (time > entry.getValue().validUntil) {
                it.remove();
            }
        }
    }

    /**
     * Creates a log file in which log messages are written.
     * 
     * @throws IOException
     *             if a problem occurs while creating log file.
     */
    private void createLogFile() throws IOException {
        String fileName = documentRoot + "/log.txt";
        Path path = Paths.get(fileName);
        path.toFile().createNewFile();
        logFile = path;
    }

    /**
     * Gets the persistent parameters map stored in session map to which some
     * sid is mapped.
     * 
     * @param sid
     *            which is possibly mapped to some persistent map stored in
     *            sessions map.
     * @return null if the session expired or if there is no such sid present,
     *         or map otherwise.
     */
    protected synchronized Map<String, String> getPermParamMap(String sid) {
        SessionMapEntry entry = sessions.get(sid);
        if (entry == null) {
            return null;
        }
        Long currTime = System.currentTimeMillis();
        if (entry.validUntil < currTime) {
            sessions.remove(sid);
            return null;
        }
        entry.validUntil = currTime + sessionTimeout
                * Config.MILLISECONDS_IN_MINUTE;
        return entry.map;
    }

    /**
     * Returns a generated random String that contains some number of random
     * upper english letters that serves as the session id for clients.
     * 
     * @return random generated String.
     */
    protected synchronized String getRandomString() {
        StringBuilder sb = new StringBuilder(Config.RANDOM_STRING_LENGTH);
        for (int i = 0; i < Config.RANDOM_STRING_LENGTH; i++) {
            sb.append(Config.ALPHABET.charAt(
                    sessionRandom.nextInt(Config.ALPHABET.length())));
        }
        String ret = sb.toString();
        return ret;
    }

    /**
     * Instantiates a worker by its fully qualified class name.
     * 
     * @param fqcn
     *            of the worker.
     * @return worker which is defined by its fqcn.
     * @throws ClassNotFoundException
     *             if an unexisting worker class is requested in properties
     *             file.
     * @throws InstantiationException
     *             if a problem occurs with worker instantiation.
     * @throws IllegalAccessException
     *             if a problem occurs with worker instantiation.
     */
    private IWebWorker getWorker(String fqcn) throws ClassNotFoundException,
    InstantiationException, IllegalAccessException {
        Object newObject = null;

        Class<?> referenceToClass = this.getClass()
                .getClassLoader()
                .loadClass(fqcn);
        newObject = referenceToClass.newInstance();
        return (IWebWorker) newObject;
    }

    /**
     * Initializes mime types that are mapped to their file extensionds.
     * 
     * @param path
     *            to file which contains mime properties.
     * @throws IOException
     *             if a problem occurs with file reading.
     */
    private void initMimeTypes(Path path) throws IOException {
        if (path.toFile().isFile() && path.toFile().canRead()) {
            Properties mimes = new Properties();
            mimes.load(Files.newInputStream(path));
            for (String key : mimes.stringPropertyNames()) {
                mimeTypes.put(key, mimes.getProperty(key));
            }
        }
    }

    /**
     * Initializes server properties.
     * 
     * @param propFilePath
     *            path to file which contains server properties.
     * @throws IOException
     *             if a problem occurs with file reading.
     * @throws NumberFormatException
     *             if something is not correctly written in the properties file.
     * @throws ClassNotFoundException
     *             if an unexisting worker class is presents in properties file.
     * @throws InstantiationException
     *             if a problem occurs with worker instantiation.
     * @throws IllegalAccessException
     *             if a problem occurs with worker instantiation.
     */
    private void initProperties(String propFilePath) throws IOException,
    NumberFormatException,
    ClassNotFoundException, InstantiationException,
    IllegalAccessException {
        Properties properties = new Properties();
        BufferedReader reader = Files.newBufferedReader(
                Paths.get(propFilePath + Config.SERVER_PROP_FILE),
                Config.CONFIG_CHARSET);
        properties.load(reader);

        address = properties.getProperty("server.address",
                Config.DEFAULT_ADDRESS);
        workerThreads = Integer.parseInt(
                properties.getProperty("server.workerThreads",
                        Config.DEFAULT_WORKER_NUM));
        port = Integer.parseInt(properties.getProperty("server.port",
                Config.DEFAULT_PORT));
        documentRoot = Paths.get(properties.getProperty("server.documentRoot"));
        if (!documentRoot.toFile().isDirectory()) {
            throw new IllegalArgumentException("Root document is not a folder!");
        }

        sessionTimeout = Integer.parseInt(properties.getProperty(
                "session.timeout", Config.DEFAULT_SESSION_TIMEOUT));

        initMimeTypes(Paths.get(properties.getProperty(
                "server.mimeConfig",
                propFilePath + Config.DEFAULT_MIME_FILE)));
        initWorkersConfig(Paths.get(properties.getProperty(
                "server.workers",
                propFilePath + Config.DEFAULT_WORKERS_FILE)));

    }

    /**
     * Initializes workers.
     * 
     * @param path
     *            to file which contains worker properties.
     * @throws IOException
     *             if a problem occurs with file reading.
     * @throws ClassNotFoundException
     *             if an unexisting worker class is presents in properties file.
     * @throws InstantiationException
     *             if a problem occurs with worker instantiation.
     * @throws IllegalAccessException
     *             if a problem occurs with worker instantiation.
     */
    private void initWorkersConfig(Path path) throws IOException,
    ClassNotFoundException, InstantiationException,
    IllegalAccessException {
        if (path.toFile().isFile() && path.toFile().canRead()) {
            Properties workerProp = new Properties();
            workerProp.load(Files.newInputStream(path));
            for (String workerPath : workerProp.stringPropertyNames()) {
                String fqcn = workerProp.getProperty(workerPath);
                workersMap.put(workerPath, getWorker(fqcn));
            }
        }
    }

    /**
     * Stores a session with given sid and map.
     * 
     * @param sid
     *            which is mapped to the session entry.
     * @param map
     *            persistent parameter mapped which is store for next session
     *            with same client.
     */
    protected synchronized void makeNewSession(String sid,
            Map<String, String> map) {
        Long time = System.currentTimeMillis() +
                sessionTimeout * Config.MILLISECONDS_IN_MINUTE;
        sessions.put(sid, new SessionMapEntry(time, map));
    }

    /**
     * Starts the server thread that listens for client request on the address
     * provided in some way from the constructor.
     */
    protected synchronized void start() {
        threadPool = Executors.newFixedThreadPool(workerThreads);
        serverThread.start();
    }

    /**
     * Initializes the cleaner thread that is daemonic, and which periodically
     * cleans the sessions map.
     */
    private void startCleanerThread() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    wait(Config.CLEANER_MINUTES_TO_SLEEP
                            * Config.MILLISECONDS_IN_MINUTE);
                } catch (Exception e) {
                }
                clean();
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    /**
     * Stops the server thread by way of changing the variable that the server
     * periodically checks.
     */
    protected synchronized void stop() {
        serverShutdown = true;
        threadPool.shutdown();
        try {
            bw.close();
        } catch (IOException e) {
        }
    }
}
