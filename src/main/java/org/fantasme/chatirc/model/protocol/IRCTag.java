package org.fantasme.chatirc.model.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * L'ensemble des balises de style irc.
 */
public enum IRCTag {
    /**
     * Gras
     */
    BOLD((char) 0x002, "<b>", "</b>"),
    /**
     * Code couleur.
     */
    COLOR((char) 0x003, "<span>", "</span>"),
    //ITALIC((char) 0x016, "<i>", "</i>"),
    /*
     * Non "standard", Virc et mirc7
     */
    ITALIC((char) 0x01D, "<i>", "</i>"),
    /**
     * Remise é noir sur blanc.
     */
    RESET((char) 0x00F),
    /**
     * Inversion du fond et de la couleur de texte.
     */
    REVERSED((char) 0x016),
    /**
     * Souligné.
     */
    UNDERLINED((char) 0x01F, "<u>", "</u>"),
    /**
     * Style inconnu.
     */
    NONE();

    /**
     * Le caractere non imprimable codant la balise de style.
     */
    private final Character code;
    /**
     * La balise d'ouverture html.
     */
    private final String bracketStart;
    /**
     * La balise de fermeture html.
     */
    private final String bracketEnd;

    /**
     * Crée une balise de style IRC.
     *
     * @param code         Le caractére non imprimable codant le style.
     * @param bracketStart La balise ouvrante HTML.
     * @param bracketEnd   La balise fermante HTML.
     */
    IRCTag(Character code, String bracketStart, String bracketEnd) {
        this.code = code;
        this.bracketStart = bracketStart;
        this.bracketEnd = bracketEnd;
    }

    /**
     * Crée une balise de style IRC.
     *
     * @param code Le caractére non imprimable codant le style.
     */
    IRCTag(Character code) {
        this(code, null, null);
    }

    /**
     * Crée une balise de style IRC.
     */
    IRCTag() {
        this(null, null, null);
    }

    /**
     * Renvoie la balise d'ouverture associée au tag irc.
     *
     * @return Une balise d'ouverture html.
     */
    public String getOpeningBracket() {
        return bracketStart;
    }

    /**
     * Renvoie la balise de fermeture associée au tag irc.
     *
     * @return Une balise de fermeture html.
     */
    public String getClosingBracket() {
        return bracketEnd;
    }

    /**
     * Renvoie le caractére associé au tag irc.
     *
     * @return Un caractére non imprimable.
     */
    public Character getCode() {
        return code;
    }

    /**
     * Remplit la table de hachage permettant un acces rapide.
     */
    private static final Map<Character, IRCTag> map;

    static {
        map = new HashMap<Character, IRCTag>();
        for (IRCTag t : values()) {
            map.put(t.getCode(), t);
        }
    }

    /**
     * Renvoie la balise de style IRC associée au caractére.
     *
     * @param code Le caractére codant la balise.
     * @return La balise IRC associée au caractére.
     */
    public static IRCTag getTag(Character code) {
        if (code.compareTo(' ') >= 0 || code.equals((char) 9)) { // Si affichable, pas style.
            return NONE;
        }
        return map.get(code);
    }
}
