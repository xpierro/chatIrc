package org.fantasme.chatirc.error;

import org.fantasme.chatirc.model.space.IRCSpaceModel;

/**
 * Gestionnaire d'exception.
 */
public class ExceptionHandler {
    /**
     * Gére les exceptions non bloquantes.
     *
     * @param e L'exception é gérer.
     */
    public static void handleNonBlockingException(Exception e) {
        e.printStackTrace();
    }

    /**
     * Gére les exceptions du package javax.swing.text.
     *
     * @param e L'exception é gérer.
     */
    public static void handleTextException(Exception e) {
        e.printStackTrace();
    }

    /**
     * Gère les messages affichables sur un espace dans le client.
     *
     * @param e           L'exception à gérer.
     * @param display     L'espace ou afficher le message.
     * @param description La description é afficher.
     */
    public static void handleDisplayableException(Exception e, IRCSpaceModel display, String description) {

    }
}
