package org.fantasme.chatirc.font;

import java.awt.Font;
import java.util.HashSet;
import java.util.Set;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.fantasme.chatirc.tools.properties.GestionnaireProperties;

/**
 * Se charge de répercuter les changements effectués sur la police de caractères vers tous les documents enregistrés.
 */
public class FontFactory {
    /**
     * La taille minimal autorisée.
     */
    private static final int minSize = Integer.decode(GestionnaireProperties.getInstance().getPropriete("irc.conf.view.font.minSize", "FR"));

    /**
     * La taille maximal autorisée.
     */
    private static final int maxSize = Integer.decode(GestionnaireProperties.getInstance().getPropriete("irc.conf.view.font.maxSize", "FR"));

    /**
     * La taille courante.
     */
    private static int currentSize = Integer.decode(GestionnaireProperties.getInstance().getPropriete("irc.conf.view.font.defaultSize", "FR"));
    /**
     * La police courante.
     */
    private static Font currentFont = new Font(Font.MONOSPACED, Font.PLAIN, currentSize);

    /**
     * L'ensemble des document stylisés é enregistrer.
     */
    private static final Set<StyledDocument> styledDocuments = new HashSet<StyledDocument>();

    /**
     * Retourne la police courante.
     *
     * @return La police de caractére courante.
     */
    public static Font getFont() {
        return currentFont;
    }

    /**
     * Retourne la taille de la police courante.
     *
     * @return La taille de la police courante.
     */
    public static int getSize() {
        return currentSize;
    }

    /**
     * Enregistre un document natif.
     *
     * @param d Le document natif é enregistrer.
     */
    public static void registerStyledDocument(StyledDocument d) {
        styledDocuments.add(d);
        refreshStyledDocuments();
    }

    /**
     * Rafraichit l'ensemble des documents natifs.
     */
    private static void refreshStyledDocuments() {
        for (StyledDocument d : styledDocuments) {
            SimpleAttributeSet set = new SimpleAttributeSet(SimpleAttributeSet.EMPTY);
            StyleConstants.setFontFamily(set, currentFont.getFamily());
            StyleConstants.setFontSize(set, currentSize);
            d.setCharacterAttributes(0, d.getLength(), set, false);
        }
    }

    /**
     * Augmente/Diminue la taille de la police.
     *
     * @param units Le nombre de pas é ajouter/retirer.
     */
    public static void increaseSize(int units) {
        setSize(currentSize + units);
    }

    /**
     * Change la taille de police courante.
     *
     * @param size La nouvelle taille.
     */
    private static void setSize(int size) {
        if (size < maxSize && size > minSize) {
            currentSize = size;
            currentFont = currentFont.deriveFont((float) size);
            refreshStyledDocuments();
        }
    }
}
