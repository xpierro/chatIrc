package org.fantasme.chatirc.controller;

import java.util.EventListener;

/**
 * Ecoute les événements générés par les espaces IRC.
 */
public interface SpaceListener extends EventListener {
    /**
     * Lorsqu'un espace a un nouveau message injecté, il le signale.
     *
     * @param message Le message injecté.
     */
    void spaceUpdated(String message);

    /**
     * Lorsqu'un espace change de nom.
     *
     * @param name Le nouveau nom.
     */
    void spaceNameChanged(String name);

    /**
     * L'espace a été fermé.
     */
    void spaceClosed();
}
