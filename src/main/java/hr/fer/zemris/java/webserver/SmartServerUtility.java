package hr.fer.zemris.java.webserver;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Some utility functions which mainly serve for decluttering the main server
 * class. Also makes some typical http operations reusable.
 * 
 * @author Erik Banek
 */
public class SmartServerUtility {
    /** Date format in which logs to log file are written. */
    private static final DateFormat dateFormat = new SimpleDateFormat(
            "yyyy/MM/dd HH:mm:ss");

    /**
     * Checks if parameters list from wanted path is in correct format.
     * 
     * @param parameters
     *            parameters String.
     * @return true iff the parameters part of path is in correct format.
     */
    public static boolean checkParameters(String parameters) {
        if (parameters.isEmpty()) {
            return true;
        }
        String[] paramPairs = parameters.split("&");
        for (String s : paramPairs) {
            if (s.split("=").length != 2) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the first line of a http request is valid, given server
     * supported http versions and methods.
     * 
     * @param firstLine
     *            first line of client sent http request.
     * @param methods
     *            which the server supports.
     * @param versions
     *            which the server supports
     * @return true iff the request is valid.
     */
    public static boolean checkRequest(String firstLine, Set<String> methods,
            Set<String> versions) {
        String[] split = firstLine.split(" ");
        if (split.length != 3) {
            return false;
        }
        String method = split[0];
        String requestedPath = split[1];
        String version = split[2];
        if (!methods.contains(method) || !versions.contains(version)) {
            return false;
        }
        String[] splitPath = requestedPath.split("\\?");
        if (splitPath.length == 1) {
            return true;
        } else if (splitPath.length > 2) {
            return false;
        } else {
            return checkParameters(splitPath[1]);
        }

    }

    /**
     * Executes the script from document described by the path to it. Handles
     * output throught the given context.
     * 
     * @param rc
     *            context through which output is handled.
     * @param pathToScript
     *            containing the script to be executed.
     * @throws IOException
     *             if a problem occurs with reading the script or writing the
     *             output of script to given context.
     */
    public static void executeScript(RequestContext rc, Path pathToScript)
            throws IOException {
        // create engine and execute it
        String documentBody = SmartServerUtility.getDocumentText(
                pathToScript.toString());
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                rc).execute();
    }

    /**
     * Checks if the client has cookies. Gets the cookie value whose name is
     * 'sid', if it exists.
     * 
     * @param request
     *            all lines from the http request of the client.
     * @return String value of cookie which name is sid, or null otherwise.
     */
    public static String getCookieSID(List<String> request) {
        for (String line : request) {
            if (!line.startsWith("Cookie: ")) {
                continue;
            }
            String[] split = line.split("\\s");
            String cookieNamePair = split[1].trim();

            String[] cookieNameSplit = cookieNamePair.split(":|=");
            String cookieName = cookieNameSplit[0].trim();

            String cookieValue = cookieNameSplit[1].trim().substring(1);
            cookieValue = cookieValue.substring(0, cookieValue.length() - 1);

            if (cookieName.equals("sid")) {
                return cookieValue;
            }
        }
        return null;
    }

    /**
     * Gets the text from document in String format.
     * 
     * @param path
     *            of document from which text is wanted.
     * @return String of text from document.
     * @throws IOException
     *             if problem occurs with reading text from document.
     */
    public static String getDocumentText(String path) throws IOException {
        byte[] bytes;
        bytes = Files.readAllBytes(Paths.get(path));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Checks if given byte array is a header terminating byte array.
     * 
     * @param array
     *            which checked for termination.
     * @return true iff the given array signals header ending.
     */
    public static boolean isTerminating(byte[] array) {
        if (array.length == 2) {
            return array[0] == Config.HEADER_TERMINATING_BYTES_LEN2[0] &&
                    array[1] == Config.HEADER_TERMINATING_BYTES_LEN2[1];
        } else if (array.length == 4) {
            return array[0] == Config.HEADER_TERMINATING_BYTES_LEN4[0] &&
                    array[1] == Config.HEADER_TERMINATING_BYTES_LEN4[1] &&
                    array[2] == Config.HEADER_TERMINATING_BYTES_LEN4[2] &&
                    array[3] == Config.HEADER_TERMINATING_BYTES_LEN4[3];
        }
        return false;
    }

    /**
     * Writes wanted message to the log file specified by the buffered writer.
     * 
     * @param messageToLog
     *            message whose text is wanted to be remembered.
     * @param bw
     *            through which writing to log file is done.
     */
    public synchronized static void log(String messageToLog, BufferedWriter bw) {
        try {
            String threadInfo = Thread.currentThread().toString();
            Date date = new Date();
            bw.write(threadInfo + "\n" + messageToLog + "    "
                    + dateFormat.format(date)
                    + "\n");
            bw.flush();
        } catch (IOException ignorable) {
        }
    }

    /**
     * Parses a header String into header lines.
     * 
     * @param header
     *            single String containing whole request header.
     * @return header lines.
     */
    public static List<String> parseIntoHeaderLines(String header) {
        List<String> headerLines = new ArrayList<String>();
        String currentLine = null;
        for (String s : header.split("\n")) {
            if (s.isEmpty())
                break;
            char c = s.charAt(0);
            if (c == 9 || c == 32) {
                currentLine += s;
            } else {
                if (currentLine != null) {
                    headerLines.add(currentLine);
                }
                currentLine = s;
            }
        }
        if (!currentLine.isEmpty()) {
            headerLines.add(currentLine);
        }
        return headerLines;
    }
}
