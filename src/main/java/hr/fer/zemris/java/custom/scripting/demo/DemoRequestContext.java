package hr.fer.zemris.java.custom.scripting.demo;

import hr.fer.zemris.java.webserver.RequestContext;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Demo showing the capabilities of the {@code RequestContext} class using the
 * {@code System.out} as the output stream.
 * 
 * @author Erik Banek
 */
public class DemoRequestContext {
    /**
     * Predefined basic program.
     * 
     * @param filePath
     *            path to file into which RequestContext writes.
     * @param encoding
     *            by which the file is encoded, is a String representation of
     *            some charset.
     * @throws IOException
     *             if writing into files fails.
     */
    private static void demo1(String filePath, String encoding)
            throws IOException {
        OutputStream os = Files.newOutputStream(Paths.get(filePath));
        RequestContext rc = new RequestContext(os,
                new HashMap<String, String>(),
                new HashMap<String, String>(),
                new ArrayList<RequestContext.RCCookie>());
        rc.setEncoding(encoding);
        rc.setMimeType("text/plain");
        rc.setStatusCode(205);
        rc.setStatusText("Idemo dalje");
        // Only at this point will header be created and written...
        rc.write("Čevapčići i Šiščevapčići.");
        os.close();
    }

    /**
     * Predefined program that writes something more than the first demo by
     * using {@code RequestContext}.
     * 
     * @param filePath
     *            path to file into which RequestContext writes.
     * @param encoding
     *            by which the file is encoded, is a String representation of
     *            some charset.
     * @throws IOException
     *             if writing into files fails.
     */
    private static void demo2(String filePath, String encoding)
            throws IOException {
        OutputStream os = Files.newOutputStream(Paths.get(filePath));
        RequestContext rc = new RequestContext(os,
                new HashMap<String, String>(),
                new HashMap<String, String>(),
                new ArrayList<RequestContext.RCCookie>());
        rc.setEncoding(encoding);
        rc.setMimeType("text/plain");
        rc.setStatusCode(205);
        rc.setStatusText("Idemo dalje");
        rc.addRCCookie(new RCCookie("korisnik", "perica", 3600, "127.0.0.1",
                "/"));
        rc.addRCCookie(new RCCookie("zgrada", "B4", null, null, "/"));
        // Only at this point will header be created and written...
        rc.write("Čevapčići i Šiščevapčići.");
        os.close();
    }

    /**
     * Runs three demo programs.
     * 
     * @param args
     *            ignored
     * @throws IOException
     *             if writing into files fails.
     */
    public static void main(String[] args) throws IOException {
        demo1("primjer1.txt", "ISO-8859-2");
        demo1("primjer2.txt", "UTF-8");
        demo2("primjer3.txt", "UTF-8");
    }
}
