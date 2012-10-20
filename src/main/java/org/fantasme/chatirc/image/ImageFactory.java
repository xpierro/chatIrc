package org.fantasme.chatirc.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Librairie de fabrication d'images.
 */
public class ImageFactory {
    /**
     * Retourne une image à partir d'un flux d'entrée.
     *
     * @param i Le flux d'entrée.
     * @return L'image correspondant au flux.
     */
    public static BufferedImage getImage(InputStream i) throws IOException {
        return ImageIO.read(i);
    }
}
