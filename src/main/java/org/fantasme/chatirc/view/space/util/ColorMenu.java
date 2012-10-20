package org.fantasme.chatirc.view.space.util;

import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.fantasme.chatirc.model.protocol.IRCColor;

/**
 * Un menu de selection de couleur.
 */
public class ColorMenu extends JMenu {
    /**
     * L'ensemble des choix possibles.
     */
    private JMenuItem[] items;
    /**
     * La table associative faisant le lien entre un objet du menu et une couleur.
     */
    private Map<JMenuItem, IRCColor> colorMap;

    /**
     * @param title Le titre du menu.
     */
    public ColorMenu(String title) {
        super(title);
        createView();
        placeComponents();
    }

    /**
     * Crée la vue.
     */
    private void createView() {
        colorMap = new HashMap<JMenuItem, IRCColor>();
        // On créé les différents items de couleurs
        List<JMenuItem> items = new LinkedList<JMenuItem>();
        for (IRCColor c : IRCColor.values()) {
            JMenuItem i = new JMenuItem(c.getName(), createColorIcon(c));
            items.add(i);
            colorMap.put(i, c);
        }
        this.items = items.toArray(new JMenuItem[items.size()]);
    }

    /**
     * Place les composants.
     */
    private void placeComponents() {
        for (JMenuItem i : items) {
            add(i);
        }
    }

    /**
     * Retourne la couleur associée é l'objet du menu.
     *
     * @param item L'objet du menu.
     * @return La couleur associée é l'objet du menu.
     */
    public IRCColor getColor(JMenuItem item) {
        return colorMap.get(item);
    }

    /**
     * Ajoute un écouteur d'action é tous les objets de la liste.
     *
     * @param l L'écouteur é ajouter.
     */
    public void addItemActionListener(ActionListener l) {
        for (JMenuItem i : items) {
            i.addActionListener(l);
        }
    }

    /**
     * Supprime un écouteur d'action de tous les objets de la liste.
     *
     * @param l L'écouteur é supprimer.
     */
    public void removeItemActionListener(ActionListener l) {
        for (JMenuItem i : items) {
            i.removeActionListener(l);
        }
    }

    /**
     * Crée l'icone de couleur associée é une couleur irc.
     *
     * @param color La couleur dont on veut une icéne.
     * @return L'icéne correspondant é la couleur.
     */
    private ImageIcon createColorIcon(IRCColor color) {
        int w = 20;
        int h = 20;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(color.getJavaColor());
        g.fillRect(0, 0, w, h);
        return new ImageIcon(img);
    }
}
