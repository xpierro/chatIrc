package org.fantasme.chatirc.model.space;

import java.awt.Color;
import javax.swing.event.EventListenerList;
import org.fantasme.chatirc.controller.SpaceListener;
import org.fantasme.chatirc.model.text.ExtendedStyledDocument;
import org.fantasme.chatirc.model.text.MessageInjecter;
import org.fantasme.chatirc.model.text.MultiColumnDocument;

/**
 * Implémentation partielle des modéles d'espace IRC.
 */
public abstract class AbstractIRCSpaceModel implements IRCSpaceModel {
    /**
     * Le document de l'espace.
     */
    private final ExtendedStyledDocument doc;

    /**
     * Le nom identifiant l'espace.
     */
    private String name;

    /**
     * Le modéle du serveur associé.
     */
    private ServerModel server;

    /**
     * La liste des écouteur d'événement d'espace.
     */
    private final EventListenerList spaceListeners;

    /**
     * Crée un espace irc.
     *
     * @param name   Le nom de l'espace.
     * @param server Le serveur associé é l'espace.
     */
    protected AbstractIRCSpaceModel(String name, ServerModel server) {
        this(name);
        this.server = server;
    }

    /**
     * Crée un espace irc.
     *
     * @param name Le nom de l'espace.
     */
    protected AbstractIRCSpaceModel(String name) {
        doc = new MultiColumnDocument();
        this.name = name;
        spaceListeners = new EventListenerList();
    }

    public ExtendedStyledDocument getDocument() {
        return doc;
    }

    public String getName() {
        return name;
    }

    public ServerModel getServerModel() {
        return server;
    }

    /*public void injectMessage(String message) {
        doc.append(message);
        fireSpaceUpdated(message); TODO: remettre en place
    }*/

    public void postMessage(String user, String mess) {
        MessageInjecter.injectPrivateMessage(user, mess, this);
    }

    public void setName(String name) {
        this.name = name;
        fireSpaceNameChanged(name);
    }

    public void quit(String nickname, String reason) {
        MessageInjecter.injectPlainMessage("<--", nickname + " est parti (" + reason + ")", this, Color.gray);
    }

    public void close() {
        server.getSpaces().remove(name);
        fireSpaceClosed();
    }

    public void addSpaceListener(SpaceListener l) {
        spaceListeners.add(SpaceListener.class, l);
    }

    public void removeSpaceListener(SpaceListener l) {
        spaceListeners.remove(SpaceListener.class, l);
    }

    private SpaceListener[] getSpaceListeners() {
        return spaceListeners.getListeners(SpaceListener.class);
    }

    private void fireSpaceUpdated(String message) {
        for (SpaceListener l : getSpaceListeners()) {
            l.spaceUpdated(message);
        }
    }

    private void fireSpaceNameChanged(String name) {
        for (SpaceListener l : getSpaceListeners()) {
            l.spaceNameChanged(name);
        }
    }

    protected void fireSpaceClosed() {
        for (SpaceListener l : getSpaceListeners()) {
            l.spaceClosed();
        }
    }
}
