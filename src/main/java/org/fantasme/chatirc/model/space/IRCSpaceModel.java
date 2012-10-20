package org.fantasme.chatirc.model.space;

import org.fantasme.chatirc.controller.SpaceListener;
import org.fantasme.chatirc.model.text.ExtendedStyledDocument;

/**
 * Les "espaces" ou "piéces" visitables.
 */
public interface IRCSpaceModel {
    /**
     * Retourne le document associé.
     *
     * @return Le document associé.
     */
    ExtendedStyledDocument getDocument();

    /**
     * Retourne le nom de l'espace.
     *
     * @return Le nom de cet espace.
     */
    String getName();

    /**
     * Retourne l'ensemble des utilisateurs visibles sur l'espace.
     *
     * @return Le tableau des utilisateurs visibles.
     */
    String[] getVisibleNicknames();

    /**
     * Retourne le modéle de serveur
     *
     * @return Le modéle du serveur associé.
     */
    ServerModel getServerModel();


    /**
     * Affiche un message typique <utilisateur> message
     *
     * @param user L'utilisateur ayant écrit le message.
     * @param mess Le contenu du message.
     */
    public void postMessage(String user, String mess);

    /**
     * Change le nom de l'espace.
     *
     * @param name Le nouveau nom.
     */
    public void setName(String name);

    /**
     * Affiche le message associé l'action quit.
     *
     * @param nickname Le pseudo
     * @param reason   La raison du quit
     */
    void quit(String nickname, String reason);

    /**
     * Ferme l'espace.
     */
    void close();

    /**
     * Ajoute un écouteur d'espace.
     *
     * @param l L'écouteur d'espace é ajouter.
     */
    void addSpaceListener(SpaceListener l);

    /**
     * Supprime un écouteur d'espace.
     *
     * @param l L'écouteur d'espace é supprimer.
     */
    void removeSpaceListener(SpaceListener l);
}
