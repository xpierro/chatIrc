package org.fantasme.chatirc.controller;

import org.fantasme.chatirc.model.space.ChannelModel;
import org.fantasme.chatirc.model.space.PrivateMessageModel;

/**
 * Implémentation abstraite du ServerListener.
 */
public class ServerAdapter implements ServerListener {
    public void chanCreated(ChannelModel model) {
    }

    public void pvCreated(PrivateMessageModel model, boolean setCurrent) {
    }

    public void serverCreated(String serverAddress) {
    }

    public void nicknameChanged(String nickname) {
    }

    public void channelListStarted() {
    }
}
