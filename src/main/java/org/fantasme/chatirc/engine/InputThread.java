package org.fantasme.chatirc.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import org.fantasme.chatirc.controller.EngineListener;

/**
 * Thread d'écoute des messages du serveur.
 * Lorsqu'un message est reéu, un événement est envoyé dans l'event dispatch
 * thread avec la chaine brute reéue.
 * <p/>
 * Note pour la maintenabilité :
 * Certains problémes d'accés concurrents pourraient étre rencontrés.
 * Le fait d'utiliser l'EDT les résoud puisqu'il met les événements dans la
 * file d'événements, mais impossible d'étre sér que l'ordre est respecté.
 */
public class InputThread extends Thread {
    /**
     * Le lecteur d'entrée.
     */
    private final BufferedReader in;

    /**
     * Le verrou d'entrée.
     */
    private final Boolean charsetLock;

    /**
     * Le nom du charset en cours.
     */
    private String charsetName;

    /**
     * Le nom du charset d'entrée.
     */
    private final String inputCharsetName;

    private final EventListenerList eventListeners;

    /**
     * Initialisation.
     *
     * @param inputStream Le flux d'entrée
     * @param charset     Le charset par défaut
     */
    public InputThread(InputStream inputStream, Charset charset) {
        in = new BufferedReader(new InputStreamReader(inputStream, charset));
        eventListeners = new EventListenerList();
        charsetLock = Boolean.TRUE;
        charsetName = charset.toString();
        inputCharsetName = charset.toString();
    }

    /**
     * Corps du Thread. Lit une chaine puis la propage dans un événement EDT.
     */
    public void run() {
        boolean finished = false;
        while (!finished) {
            String message;
            try {
                message = in.readLine();
            } catch (IOException e) {
                message = null;
            }

            // Si null est renvoyée, le flux est fini.
            if (message == null) {
                finished = true;
            } else {
                synchronized (charsetLock) {
                    try {
                        byte[] bytes = message.getBytes(inputCharsetName);
                        message = new String(bytes, charsetName);
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("Encodage inconnu.");
                    }
                }
                final String finalMessage = message;
                System.out.println(finalMessage);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fireMessageReceived(finalMessage);
                    }
                });
            }
        }
    }

    /**
     * Change le charset de l'entrée.
     *
     * @param charset Le nouveau charset
     */
    void changeEncoding(Charset charset) {
        synchronized (charsetLock) {
            charsetName = charset.toString();
        }
    }

    /**
     * Ajoute un écouteur de moteur.
     *
     * @param listener Le nouvel écouteur de moteur.
     */
    public void addEngineListener(EngineListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener inajoutable (null)");
        }
        eventListeners.add(EngineListener.class, listener);
    }

    /**
     * Supprime un écouteur de moteur.
     *
     * @param listener L'écouteur é supprimer.
     */
    public void removeEngineListener(EngineListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener insupprimable (null)");
        }
        eventListeners.remove(EngineListener.class, listener);
    }

    /**
     * Retourne l'ensemble des écouteurs de moteur.
     *
     * @return L'ensemble des écouteurs de moteur.
     */
    public EngineListener[] getEngineListeners() {
        return eventListeners.getListeners(EngineListener.class);
    }

    /**
     * Signale qu'un nouveau message a été reéu.
     *
     * @param message Le nouveau message reéu.
     */
    protected void fireMessageReceived(String message) {
        for (EngineListener l : getEngineListeners()) {
            l.messageReceived(message);
        }
    }
}
