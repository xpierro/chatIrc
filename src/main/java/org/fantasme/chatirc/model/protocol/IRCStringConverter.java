package org.fantasme.chatirc.model.protocol;

import java.util.Deque;
import java.util.LinkedList;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.fantasme.chatirc.font.FontFactory;
import org.fantasme.chatirc.model.text.ExtendedStyledDocument;

/**
 * Convertie les chaines formatées IRC en chaines formatées pour le client.
 */
public class IRCStringConverter {

    /**
     * Transforme une chaine formatée pour l'IRC en une chaine HTML.
     *
     * @param ircString La chaine é transformer.
     * @return La chaine HTML transformée.
     */
    // TODO: faire un vrai automate a états(waitingColor, waitingBackground, etc) notamment pour les couleurs (incrémentation non vérifée de pos)
    public static String toHTML(String ircString) {
        StringBuffer out = new StringBuffer();
        Deque<IRCTag> stack = new LinkedList<IRCTag>();
        IRCColor front = null;
        IRCColor back = null;

        IRCColor lastFront = IRCColor.BLACK;
        //IRCColor lastBack = IRCColor.WHITE;
        /**
         * Parse caractére é caractére la chaine brute et ajoute les balises
         * HTML correspondantes dans <code>out</code> si besoin.
         */
        for (int pos = 0; pos < ircString.length(); pos++) {
            Character c = ircString.charAt(pos);
            IRCTag t = IRCTag.getTag(c); // Le tag rencontré.
            switch (t) {
                case NONE: // Pas de traitement.
                    out.append(c);
                    break;
                case COLOR: // Code couleur: "front,back"
                    if (front != null) { // Il y a deja une couleur: on réinitialise
                        closeTagUntil(out, IRCTag.COLOR, stack);
                        front = null;
                    }
                    pos += 1; // On commence é lire les codes couleurs
                    if (pos < ircString.length()) {

                        String frontCode = "";
                        if (ircString.charAt(pos) == ',') {
                            front = lastFront; // Ancienne couleur si immediatement fond
                        } else {
                            while (pos < ircString.length() && ircString.charAt(pos) >= '0' && ircString.charAt(pos) <= '9') {
                                frontCode += ircString.charAt(pos);
                                pos += 1;
                            }
                            // On peut utiliser le code COLOR comme un reset couleur.
                            if (frontCode.equals("")) {
                                closeTagUntil(out, IRCTag.COLOR, stack);
                                pos -= 1;
                                break;
                            }
                            front = IRCColor.getColor(Integer.parseInt(frontCode));
                        }


                        if (pos < ircString.length() && ircString.charAt(pos) == ',') { // Couleur de fond
                            String backCode = "";
                            pos += 1;
                            while (pos < ircString.length() && ircString.charAt(pos) >= '0' && ircString.charAt(pos) <= '9') {
                                backCode += ircString.charAt(pos);
                                pos += 1;
                            }
                            try {
                                back = IRCColor.getColor(Integer.parseInt(backCode));
                            } catch (NumberFormatException e) {
                                back = IRCColor.BLACK;
                            }
                        }
                        // On crée le code HTML
                        out.append("<span style=\"color:");
                        out.append(front.getHtmlName());
                        out.append(";");
                        out.append(back == null ? "" : "background-color: " + back.getHtmlName() + ";");
                        out.append("\">");
                        stack.push(IRCTag.COLOR);
                        pos -= 1; // On se replace au dernier caractére lu.
                        lastFront = front;
                        //lastBack = back;
                    }
                    break;
                case RESET: // On efface tous les styles.
                    closeAllTags(out, stack);
                    front = null;
                    back = null;
                    lastFront = IRCColor.BLACK;
                    //lastBack = IRCColor.WHITE;
                    break;
                case REVERSED: // On inverse les couleurs /!\ Ambigue avec italique.
                    if (front == null || back == null) {
                        front = IRCColor.BLACK;
                        back = IRCColor.WHITE;
                    }
                    if (stack.contains(IRCTag.COLOR)) {
                        closeTagUntil(out, IRCTag.COLOR, stack);
                    }
                    out.append("<span style=\"color:" + back.getHtmlName() + ";"
                            + "background-color:" + front.getHtmlName() + ";"
                            + "\">");
                    IRCColor temp = front;
                    front = back;
                    back = temp;
                    stack.push(IRCTag.COLOR);
                    break;
                default: // On a rencontré un style de base (souligné, etc.).
                    if (!stack.contains(t)) {
                        openTag(out, t);
                        stack.push(t);
                    } else {
                        closeTagUntil(out, t, stack);
                    }
            }
        }
        closeAllTags(out, stack);
        return out.toString();
    }

    /**
     * Ouvre le tag HTML correspondant.
     *
     * @param out La chiane dans laquelle est ajoutée la balise ouvrante.
     * @param t   La balise IRC é ouvrir.
     */
    private static void openTag(StringBuffer out, IRCTag t) {
        out.append(t.getOpeningBracket());
    }

    /**
     * Ferme toutes les balises ouvertes dans la pile jusqu'é la balise
     * spécifiée puis réouvre toutes les balises fermées sauf la balise
     * spécifiée.
     * Utile quand plusieures balises sont encastrées.
     *
     * @param out La chaine dans laquelle sont ajoutées les balises.
     * @param t   La balise irc é fermer.
     * @param s   La pile des balises ouvertes.
     */
    private static void closeTagUntil(StringBuffer out, IRCTag t, Deque<IRCTag> s) {
        Deque<IRCTag> tempStack = new LinkedList<IRCTag>();
        while (!s.isEmpty() && s.peek() != t) {
            out.append(s.peek().getClosingBracket());
            tempStack.push(s.pop());
        }

        if (!s.isEmpty()) {
            out.append(s.pop().getClosingBracket());
        }

        while (!tempStack.isEmpty()) {
            out.append(tempStack.peek().getOpeningBracket());
            s.push(tempStack.pop());
        }
    }

    /**
     * Ferme tous les tags HTML ouverts dans la pile.
     *
     * @param out   La chaine dans laquelle sont ajoutées les balises fermantes.
     * @param stack La pile des balises ouvertes.
     */
    private static void closeAllTags(StringBuffer out, Deque<IRCTag> stack) {
        while (!stack.isEmpty()) {
            out.append(stack.pop().getClosingBracket());
        }
    }

    /**
     * Initialise l'ensemble des styles.
     *
     * @param set L'ensemble des styles a initialiser.
     */
    private static void initSet(SimpleAttributeSet set) {
        // TODO: gestion dynamique du front/back
        //StyleConstants.setBackground(set, IRCColor.WHITE.getJavaColor());
        //StyleConstants.setForeground(set, IRCColor.BLACK.getJavaColor());
        StyleConstants.setBold(set, false);
        StyleConstants.setUnderline(set, false);
        StyleConstants.setFontFamily(set, FontFactory.getFont().getFamily());
        StyleConstants.setFontSize(set, FontFactory.getSize());
    }

    public static void insertIntoStyledDocument(ExtendedStyledDocument sdoc, String ircString) {
        IRCColor front = null;
        IRCColor back = null;

        IRCColor lastFront = IRCColor.BLACK;
        //IRCColor lastBack = IRCColor.WHITE;

        SimpleAttributeSet currentAttributeSet = new SimpleAttributeSet(SimpleAttributeSet.EMPTY);
        initSet(currentAttributeSet);
        /**
         * Parse caractére é caractére la chaine brute et ajoute les balises
         */
        for (int pos = 0; pos < ircString.length(); pos++) {
            Character c = ircString.charAt(pos);
            IRCTag t = IRCTag.getTag(c); // Le tag rencontré.
            switch (t) {
                case NONE: // Pas de traitement.
                    sdoc.appendChar(c, new SimpleAttributeSet(currentAttributeSet));
                    break;
                case COLOR: // Code couleur: "front,back"
                    if (front != null) { // Il y a deja une couleur: on réinitialise
                        StyleConstants.setForeground(currentAttributeSet, IRCColor.BLACK.getJavaColor());
                        front = null;
                    }
                    pos += 1; // On commence é lire les codes couleurs
                    if (pos < ircString.length()) {
                        String frontCode = "";
                        if (ircString.charAt(pos) == ',') {
                            front = lastFront; // Ancienne couleur si immediatement fond
                        } else {
                            while (pos < ircString.length() && ircString.charAt(pos) >= '0' && ircString.charAt(pos) <= '9') {
                                frontCode += ircString.charAt(pos);
                                pos += 1;
                            }
                            // On peut utiliser le code COLOR comme un reset couleur.
                            if (frontCode.equals("")) {
                                StyleConstants.setForeground(currentAttributeSet, IRCColor.BLACK.getJavaColor());
                                StyleConstants.setBackground(currentAttributeSet, IRCColor.WHITE.getJavaColor());
                                pos -= 1;
                                break;
                            }
                            front = IRCColor.getColor(Integer.parseInt(frontCode));
                        }


                        if (pos < ircString.length() && ircString.charAt(pos) == ',') { // Couleur de fond
                            String backCode = "";
                            pos += 1;
                            while (pos < ircString.length() && ircString.charAt(pos) >= '0' && ircString.charAt(pos) <= '9') {
                                backCode += ircString.charAt(pos);
                                pos += 1;
                            }
                            try {
                                back = IRCColor.getColor(Integer.parseInt(backCode));
                            } catch (NumberFormatException e) {
                                back = IRCColor.BLACK;
                            }
                        }
                        if (front != null) StyleConstants.setForeground(currentAttributeSet, front.getJavaColor());
                        if (back != null) StyleConstants.setBackground(currentAttributeSet, back.getJavaColor());
                        pos -= 1; // On se replace au dernier caractére lu.
                        lastFront = front;
                        //lastBack = back;
                    }
                    break;
                case RESET: // On efface tous les styles.
                    currentAttributeSet = new SimpleAttributeSet(SimpleAttributeSet.EMPTY);
                    front = null;
                    back = null;
                    lastFront = IRCColor.BLACK;
                    //lastBack = IRCColor.WHITE;
                    break;
                case REVERSED: // On inverse les couleurs /!\ Ambigue avec italique.
                    if (front == null || back == null) {
                        front = IRCColor.BLACK;
                        back = IRCColor.WHITE;
                    }
                    StyleConstants.setForeground(currentAttributeSet, back.getJavaColor());
                    StyleConstants.setBackground(currentAttributeSet, front.getJavaColor());
                    IRCColor temp = front;
                    front = back;
                    back = temp;
                    break;
                case BOLD:
                    StyleConstants.setBold(currentAttributeSet, !StyleConstants.isBold(currentAttributeSet));
                    break;
                case UNDERLINED:
                    StyleConstants.setUnderline(currentAttributeSet, !StyleConstants.isUnderline(currentAttributeSet));
                    break;
                default:
                    break;
            }
        }
        sdoc.appendChar('\n', null);
    }

}
