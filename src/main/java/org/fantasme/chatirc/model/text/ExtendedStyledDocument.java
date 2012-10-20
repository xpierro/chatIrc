package org.fantasme.chatirc.model.text;

import javax.naming.directory.BasicAttribute;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyledDocument;

/**
 * Document stylisé étendu.
 */
public interface ExtendedStyledDocument extends StyledDocument {
    /**
     * L'ensemble des attributs personnalisé appliquables au document.
     */
    class Attribute {
        /**
         * Pseudonyme clickable.
         */
        public static BasicAttribute CLICKABLE_NICKNAME = new BasicAttribute("CLICKABLE_NICKNAME");
    }

    /**
     * Ajoute un caractére é la fin du document.
     *
     * @param c      Le caractére é ajouter.
     * @param styles Les styles é associer.
     */
    void appendChar(char c, AttributeSet styles);

    /**
     * Ajoute un message irc é la fin du document.
     * @param message Le message irc.
     */
    //void append(String message);

    /**
     * Prépare le document é l'insertion d'un nouveau message.
     */
    void startMessage();

    /**
     * Insére un identifiant é la fin du document
     *
     * @param id     L'identifiant é insérer.
     * @param styles Les styles é appliquer..
     */
    void insertId(String id, MutableAttributeSet styles);

    /**
     * Insére une chaine monolithique dans la partie contenu du message en cours d'insertion.
     *
     * @param contentString La chaine monolithique é insérer.
     * @param styles        Le style de toute la chaine.
     */
    void insertContentString(String contentString, AttributeSet styles);

    /**
     * Fermer le message en cours d'insertion
     */
    void endMessage();

    /**
     * Applique toutes les opération post-insertion.
     *
     * @param originalString La chaine original
     */
    void applyPostOperations(String originalString);

}
