package hr.fer.zemris.java.webserver;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Basic server demo that runs the server and makes a big button whose pressing
 * stops it.
 * 
 * @author Erik Banek
 */
public class BasicServerDemo {
    /** Server which is run. */
    private static SmartHttpServer server;
    /**
     * Runs a server with basic settings specified in a file.
     * 
     * @param args
     *            a single argument showing path to a folder containing the
     *            server.config file.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out
            .println("Wrong number of arguments, "
                    +
                    "expected one path to folder containing server.properties.");
            return;
        }
        try {
            server = new SmartHttpServer(args[0]);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        server.start();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setTitle("My first baby server");
            frame.setVisible(true);
            frame.setSize(150, 150);
            frame.setLocation(50, 50);

            JButton butt = new JButton("STOP");
            frame.add(butt);
            butt.addActionListener((e) -> {
                server.stop();
                frame.dispose();
            });
        });
    }
}
