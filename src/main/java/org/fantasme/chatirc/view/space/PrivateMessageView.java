package org.fantasme.chatirc.view.space;

import org.fantasme.chatirc.model.space.PrivateMessageModel;

/**
 * La vue d'un message privé.
 */
public class PrivateMessageView extends AbstractIRCSpaceView {

    /**
     * @param model Le modéle de message privé associé.
     */
    public PrivateMessageView(PrivateMessageModel model) {
        super(model);
    }
}
