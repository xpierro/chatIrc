package org.fantasme.chatirc.view.space;

import org.fantasme.chatirc.model.space.ServerModel;

/**
 * La vue d'un serveur.
 */
public class ServerView extends AbstractIRCSpaceView {

    /**
     * @param model Le modèle de serveur associé.
     */
    public ServerView(ServerModel model) {
        super(model);
    }
}
