package org.fantasme.chatirc.controller;

import java.util.EventListener;
import org.fantasme.chatirc.model.space.ChannelModel;
import org.fantasme.chatirc.model.space.PrivateMessageModel;

/**
 * Ecoute les événements serveur.
 */
public interface ServerListener extends EventListener {
    /**
     * Un canal a été créé
     *
     * @param model Le modéle du nouveau canal.
     */
    void chanCreated(ChannelModel model);

    /**
     * Un message privé a été créé.
     *
     * @param model      Le modéle du nouveau message.
     * @param setCurrent Veut-on l'afficher immédiatement ?
     */
    void pvCreated(PrivateMessageModel model, boolean setCurrent);

    /**
     * Un serveur a été créé.
     *
     * @param serverAddress L'adresse du nouveau serveur.
     */
    void serverCreated(String serverAddress);

    /**
     * Le pseudonyme a changé.
     *
     * @param nickname Le nouveau pseudonyme.
     */
    void nicknameChanged(String nickname);

    /**
     * Une nouvelle liste de canaux a été reéue.
     */
    void channelListStarted();
}
