package org.fantasme.chatirc.model.protocol;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Les codes couleurs utilisés par le protocole IRC avec leur équivalent HTML.
 */
public enum IRCColor {
    WHITE(0, "#FFFFFF", "Blanc"),
    BLACK(1, "#000000", "Noir"),
    NAVYBLUE(2, "#23238E", "Bleu marine"),
    GREEN(3, "#008000", "Vert"),
    RED(4, "#FF0000", "Rouge"),
    BROWN(5, "#802A2A", "Marron"),
    PURPLE(6, "#A020F0", "Magenta"),
    OLIVE(7, "#808000", "Olive"),
    YELLOW(8, "#FFFF00", "Jaune"),
    LIMEGREEN(9, "#32CD32", "Vert citron"),
    TEAL(10, "#008080", "Cyan"),
    AQUALIGHT(11, "#00FFFF", "Cyan clair"),
    ROYALBLUE(12, "#3333FF", "Bleu roi"),
    HOTPINK(13, "#FF69B4", "Rose"),
    DARKGRAY(14, "#A9A9A9", "Gris"),
    LIGHTGRAY(15, "#D3D3D3", "Gris clair"),
    WHITE2(16, "#FFFFFF", "Blanc");

    /**
     * Le code numérique associé é la couleur (convention mirc).
     */
    private final int ircCode;
    /**
     * Le nom HTML associé é la couleur.
     */
    private final String htmlName;
    /**
     * Le nom décrivant la couleur en clair.
     */
    private final String fullName;

    /**
     * La table associative permettant un accés rapide aux couleurs.
     */
    private static final Map<Integer, IRCColor> colorMap;

    static {
        colorMap = new HashMap<Integer, IRCColor>();
        for (IRCColor c : IRCColor.values()) {
            colorMap.put(c.ircCode, c);
        }
    }

    /**
     * Crée une nouvelle couleur.
     *
     * @param ircCode  Le code mirc correspondant.
     * @param htmlName Le code html correspondant.
     * @param fullName Le nom en clair correspondant.
     */
    IRCColor(int ircCode, String htmlName, String fullName) {
        this.ircCode = ircCode;
        this.htmlName = htmlName;
        this.fullName = fullName;
    }

    /**
     * Retourne la couleur IRC correspondant au code numérique.
     *
     * @param code Le code dont on cherche la couleur associée.
     * @return La couleur associée au code.
     */
    public static IRCColor getColor(int code) {
        IRCColor color = colorMap.get(code);
        if (color == null) {
            throw new IllegalArgumentException("Couleur inexistante");
        } else {
            return color;
        }
    }

    /**
     * Retourne la description Rouge-Vert-Bleu associée au code HTML de la couleur.
     *
     * @return Un tableau [R, G, B] décrivant la couleur.
     */
    private int[] getRGB() {
        String hexCode = htmlName.substring(1);
        String R = hexCode.substring(0, 2);
        String G = hexCode.substring(2, 4);
        String B = hexCode.substring(4);
        int[] RGB = new int[3];
        RGB[0] = HEXtoDEC(R);
        RGB[1] = HEXtoDEC(G);
        RGB[2] = HEXtoDEC(B);

        return RGB;
    }

    /**
     * Converti une chaine hexadécimale en code décimal.
     *
     * @param hex Le nombre hexadécimal.
     * @return L'entier décimal correspondant.
     */
    private int HEXtoDEC(String hex) {
        return Integer.decode("#" + hex);
    }

    /**
     * Retourne le nom HTML associé é la couleur.
     *
     * @return La couleur en convention HTML
     */
    public String getHtmlName() {
        return htmlName;
    }

    /**
     * Retourne la couleur sous la forme d'un objet java Color.
     *
     * @return L'objet Color associé.
     */
    public Color getJavaColor() {
        int RGB[] = getRGB();
        return new Color(RGB[0], RGB[1], RGB[2]);
    }

    /**
     * Retourne le code irc de la couleur.
     *
     * @return Le code irc de la couleur.
     */
    public int getCode() {
        return ircCode;
    }

    /**
     * Retourne le nom en clair de la couleur.
     *
     * @return Le nom en clair de la couleur.
     */
    public String getName() {
        return fullName;
    }
}
