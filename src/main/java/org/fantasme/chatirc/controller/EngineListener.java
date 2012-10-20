package org.fantasme.chatirc.controller;

import java.util.EventListener;

/**
 * Ecoute les événements du moteur.
 */
public interface EngineListener extends EventListener {

    /**
     * Un message a été reçu.
     *
     * @param mess Le message reçu.
     */
    void messageReceived(String mess);
}
