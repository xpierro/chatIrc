package org.fantasme.chatirc.model.space;

import org.fantasme.chatirc.model.text.MessageInjecter;

/**
 * Le modèle d'un message privé.
 */
public class PrivateMessageModel extends AbstractIRCSpaceModel {
    /**
     * @param from   L'origine du message privé.
     * @param server Le modèle du serveur associé.
     */
    public PrivateMessageModel(String from, ServerModel server) {
        super(from, server);
    }

    /**
     * Retourne le seul pseudonyme visible, le correspondant.
     *
     * @return Le pseudonyme du correspondant.
     */
    public String[] getVisibleNicknames() {
        return new String[]{getName()};
    }

    /**
     * Modifie le nom du correspondant.
     *
     * @param name Le nouveau nom du correspondant.
     */
    public void setName(String name) {
        String oldName = getName();
        super.setName(name);
        // On affiche le message de changement de pseudo
        MessageInjecter.injectClientMessage(oldName + " s'appelle maintenant " + name, this);
    }
}
