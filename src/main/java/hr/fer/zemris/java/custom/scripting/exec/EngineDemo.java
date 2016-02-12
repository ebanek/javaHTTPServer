package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo that shows the capabilities of the script executor. Which demo is shown
 * depends on the number that is entered through the command line as the
 * argument.
 * 
 * @author Erik Banek
 */
public class EngineDemo {

    /**
     * Default demo.
     * 
     * @throws IOException
     *             if something wrong occurs with the script or the
     *             {@code RequestContext}.
     */
    private static void defaultDemo() throws IOException {
        String documentBody = getDocumentText("webroot/scripts/longTime.smscr");
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        // create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters,
                        persistentParameters, cookies)).execute();

    }

    /**
     * Basic for loop script demo.
     * 
     * @throws IOException
     *             if something wrong occurs with the script or the
     *             {@code RequestContext}.
     */
    private static void demo1() throws IOException {
        String documentBody = getDocumentText("webroot/scripts/osnovni.smscr");
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();

        // create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters,
                        persistentParameters, cookies)).execute();
    }

    /**
     * Parameter from the url path (artificial) script demo.
     * 
     * @throws IOException
     *             if something wrong occurs with the script or the
     *             {@code RequestContext}.
     */
    private static void demo2() throws IOException {
        String documentBody = getDocumentText("webroot/scripts/zbrajanje.smscr");
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        parameters.put("a", "4");
        parameters.put("b", "2");

        // create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters,
                        persistentParameters, cookies)).execute();

    }

    /**
     * Call number demo. Always shows one because of offline testing.
     * 
     * @throws IOException
     *             if something wrong occurs with the script or the
     *             {@code RequestContext}.
     */
    private static void demo3() throws IOException {
        String documentBody = getDocumentText("webroot/scripts/brojPoziva.smscr");
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        persistentParameters.put("brojPoziva", "3");
        RequestContext rc = new RequestContext(System.out, parameters,
                persistentParameters, cookies);
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(), rc)
                .execute();
        System.out.println("Vrijednost u mapi: "
                + rc.getPersistentParameter("brojPoziva"));

    }

    /**
     * Fibonacci script demo.
     * 
     * @throws IOException
     *             if something wrong occurs with the script or the
     *             {@code RequestContext}.
     */
    private static void demo4() throws IOException {
        String documentBody = getDocumentText("webroot/scripts/fibonacci.smscr");
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        // create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters,
                        persistentParameters, cookies)).execute();

    }

    /**
     * Gets the text inside the document from the given path formed as a String.
     * 
     * @param path
     *            to the document from which the content decoded with UTF_8 is
     *            wanted.
     * @return String content of the document.
     */
    private static String getDocumentText(String path) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths
                    .get(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Main class deciding which demo is to be run.
     * 
     * @param args
     *            no arguments if the default demo is selected, or 1 to 4
     *            depending on which demo is wanted.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            try {
                defaultDemo();
            } catch (IOException e) {
            }
            return;
        }
        int num = Integer.parseInt(args[0]);

        try {
            if (num == 1) {
                demo1();
            } else if (num == 2) {
                demo2();
            } else if (num == 3) {
                demo3();
            } else {
                demo4();
            }
        } catch (IOException ignorable) {
        }
    }

}
