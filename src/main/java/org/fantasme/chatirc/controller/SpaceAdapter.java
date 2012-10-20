package org.fantasme.chatirc.controller;

/**
 * Classe vide permettant de n'utiliser qu'une partie du Listener.
 */
public class SpaceAdapter implements SpaceListener {
    public void spaceUpdated(String message) {
    }

    public void spaceNameChanged(String name) {
    }

    public void spaceClosed() {
    }
}
