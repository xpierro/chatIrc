// TODO : trouver un moyen de mieux abstraire, en ayant pas trop de cloison entre le CTCPreq reéues, les CTCPreq envoyées, les CTCPrep reéus et les CTCPrep envoyées...

package org.fantasme.chatirc.model.protocol.io;

import java.awt.Color;
import org.fantasme.chatirc.model.space.ServerModel;
import org.fantasme.chatirc.model.text.MessageInjecter;

/**
 * Abstraction du protocole annexe CTCP (Client To Client Protocol).
 * Contrairement à ce qu'indique son nom, ce protocole passe encore par le
 * biais du serveur, contrairement au DCC (Direct Client to Client).
 * <p/>
 * Les messages CTCP reéuus du serveur sont encapsulés dans un PRIVMSG
 * ou une NOTICE:
 */
public enum ICTCPRequest {
    /**
     * Le code CTCP pour le "/me".
     */
    ACTION("ACTION") {
        public void execute(String o, String d, String arg, ServerModel m) {
            MessageInjecter.injectPlainMessage("*", o + " " + arg, m.getSpaces().get(d), Color.gray);
        }
    },
    VERSION("VERSION") {
        public void execute(String o, String d, String arg, ServerModel m) {
            m.sendCTCPReply("VERSION", o, "yanicJAVA:1.0:noarch");
            System.out.println("STUB : VERSION");
        }
    };

    /**
     * Le code litéral de la commande.
     */
    private final String command;

    /**
     * Créé une nouvelle requéte.
     *
     * @param req
     */
    ICTCPRequest(String req) {
        command = req;
    }

    /**
     * Execute le comportement associé é une requéte CTCP
     *
     * @param origin L'origine du message
     * @param dest   La destination du message.
     * @param arg    Les arguments de la commande.
     * @param model  Le modéle de serveur associé.
     */
    public abstract void execute(String origin, String dest, String arg, ServerModel model);

    /**
     * Retourne la requéte CTCP associée é la description de la commande.
     *
     * @param command La description de la commande.
     * @return L'objet correspondant é la description de la commande.
     */
    public static ICTCPRequest getCTCP(String command) {
        if (command == null) {
            return null;
        }
        for (ICTCPRequest c : values()) {
            if (command.equals(c.command)) {
                return c;
            }
        }
        return null;
    }
}

