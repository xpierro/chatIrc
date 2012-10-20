package org.fantasme.chatirc.controller;

import java.util.EventListener;

/**
 * Ecoute les évènements de canal.
 */
public interface ChannelListener extends EventListener {
    /**
     * Le topic du canal a changé.
     *
     * @param newTopic Le nouveau topic.
     */
    void topicChanged(String newTopic);
}
