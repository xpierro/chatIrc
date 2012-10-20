package org.fantasme.chatirc.view.space.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JTextPane;
import org.fantasme.chatirc.image.ImageFactory;
import org.fantasme.chatirc.tools.properties.GestionnaireFichiers;
import org.fantasme.chatirc.tools.properties.GestionnaireProperties;

/**
 * Un JTextPane avec image de fond.
 */
public class BackgroundTextPane extends JTextPane {
    /**
     * L'image é afficher.
     */
    private static BufferedImage img;

    static {
        GestionnaireFichiers imgFileManager = new GestionnaireFichiers(GestionnaireProperties.getInstance().getPropriete("irc.conf.client.background", "FR"));
        try {
            img = ImageFactory.getImage(imgFileManager.getInputStream());
        } catch (IOException e) {
            img = null;
        } catch (IllegalArgumentException e) {
            img = null;
        }
    }

    public BackgroundTextPane() {
        super();
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
    }

    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (img != null) {
            g.drawImage(img, 0, 0, this);
        }
        super.paintComponent(g);
    }
}
