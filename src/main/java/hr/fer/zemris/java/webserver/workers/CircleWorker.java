package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Draws a circle and sends the corresponding byte array got from the image to
 * the {@code RequestContext} to write.
 * 
 * @author Erik Banek
 */
public class CircleWorker implements IWebWorker {
    /** Radius of the circle to draw. */
    private final static int CIRCLE_RADIUS = 200;

    @Override
    public void processRequest(RequestContext context) throws IOException {
        BufferedImage bim = new BufferedImage(200, 200,
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = bim.createGraphics();
        g2d.setBackground(Color.CYAN);
        g2d.drawOval(0, 0, CIRCLE_RADIUS, CIRCLE_RADIUS);
        g2d.setColor(Color.YELLOW);
        g2d.dispose();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ImageIO.write(bim, "png", bos);
        context.setMimeType("image/png");
        context.write(bos.toByteArray());

    }

}
