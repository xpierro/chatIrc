package org.fantasme.chatirc.model.space;

import java.awt.Color;
import javax.swing.event.EventListenerList;
import org.fantasme.chatirc.controller.ChannelListener;
import org.fantasme.chatirc.model.protocol.User;
import org.fantasme.chatirc.model.text.MessageInjecter;

/**
 * Le modèle d'un canal. Spécificités : la liste d'utilisateurs et le topic.
 */
public class ChannelModel extends AbstractIRCSpaceModel {
    /**
     * Le modèle de la liste d'utilisateurs.
     */
    private final UsersListModel usersListModel;

    /**
     * Le sujet du canal.
     */
    private String topic;

    /**
     * La listes des écouteurs de canaux.
     */
    private final EventListenerList listeners;

    /**
     * @param name   Le nom du canal.
     * @param server Le serveur associé.
     */
    public ChannelModel(String name, ServerModel server) {
        super(name, server);
        usersListModel = new UsersListModel();
        topic = null;
        listeners = new EventListenerList();
    }

    public UsersListModel getUsersListModel() {
        return usersListModel;
    }

    public String getTopic() {
        return topic;
    }

    /**
     * La liste d'utilisateurs.
     *
     * @return La liste d'utilisateurs du canal.
     */
    public String[] getVisibleNicknames() {
        String[] visibleNicknames = new String[usersListModel.getSize()];
        for (int i = 0; i < usersListModel.getSize(); i++) {
            visibleNicknames[i] = usersListModel.getElementAt(i).getNick();
        }
        return visibleNicknames;
    }

    /**
     * L'utilisateur est-il sur le canal ?
     *
     * @param nick L'utilisateur à chercher.
     * @return Le resultat de la recherche de l'utilisateur sur le canal.
     */
    public boolean containsNickname(String nick) {
        return usersListModel.contains(new User(nick));
    }

    /**
     * Ajoute un utilisateur au canal.
     *
     * @param nick Le pseudonyme de l'utilisateur.
     */
    public void addNickname(String nick) {
        usersListModel.addUser(new User(nick));
    }

    /**
     * Enléve un utilisateur du canal.
     *
     * @param nick Le pseudonyme de l'utilisateur.
     */
    public void removeNickname(String nick) {
        usersListModel.delUser(new User(nick));
    }

    /**
     * Change le pseudonyme d'un utilisateur.
     *
     * @param oldNick L'ancien pseudonyme.
     * @param newNick Le nouveau pseudonyme.
     */
    public void changeNickname(String oldNick, String newNick) {
        User u = usersListModel.getUser(oldNick);
        usersListModel.delUser(u);
        usersListModel.addUser(new User(u.getStatus().getStatusLetter() + newNick));
    }

    /**
     * Ajoute un status à la pile de status de l'utilisateur.
     *
     * @param newStatus Le status à ajouter.
     * @param nickname  Le pseudonyme de l'utilisateur ayant obtenu un nouveau status.
     */
    public void addUserStatus(User.Status newStatus, String nickname) {
        usersListModel.addStatus(new User(nickname), newStatus);
    }

    /**
     * Enlève un status de la pile de status de l'utilisateur.
     *
     * @param oldStatus Le status à enlever.
     * @param nickname  Le pseudonyme de l'utilisateur ayant perdu un status.
     */
    public void removeUserStatus(User.Status oldStatus, String nickname) {
        usersListModel.removeStatus(new User(nickname), oldStatus);
    }

    /**
     * Affiche le message de l'utilisateur ayant quitté le canal.
     *
     * @param nickname Le pseudonyme de l'utilisateur parti du canal.
     * @param reason   La raison du départ de l'utilisateur.
     */
    public void part(String nickname, String reason) {
        removeNickname(nickname);
        MessageInjecter.injectPlainMessage("<--", nickname + " a quitté " + getName() + " (" + reason + ")", this, Color.gray);
    }

    /**
     * Change le sujet du canal.
     * Le changement doit être repercuté au dessus.
     *
     * @param topic Le nouveau sujet.
     */
    public void setTopic(String topic) {
        this.topic = topic;
        fireTopicChanged(topic);
    }

    /**
     * Gère le message d'arrivée sur le canal.
     *
     * @param nickname Le pseudo de la personne qui est entrée.
     */
    public void join(String nickname) {
        if (!nickname.equals(getServerModel().getNickname())) {
            MessageInjecter.injectPlainMessage("-->", nickname + " a rejoint " + getName(), this, Color.gray);
        } else {
            MessageInjecter.injectClientMessage("Vous parlez maintenant sur " + getName(), this);
        }
    }

    public void quit(String nickname, String reason) {
        super.quit(nickname, reason);
        removeNickname(nickname);
    }

    /**
     * Gère le départ du canal avec raison.
     *
     * @param reason La raison du départ.
     */
    public void close(String reason) {
        getServerModel().sendPlainMessage("PART " + getName() + " " + reason);
        super.close();
    }

    public void close() {
        close("");
    }

    public void addChannelListener(ChannelListener l) {
        listeners.add(ChannelListener.class, l);
    }

    public void removeChannelListener(ChannelListener l) {
        listeners.remove(ChannelListener.class, l);
    }

    public ChannelListener[] getChannelListeners() {
        return listeners.getListeners(ChannelListener.class);
    }

    private void fireTopicChanged(String newTopic) {
        for (ChannelListener l : getChannelListeners()) {
            l.topicChanged(newTopic);
        }
    }
}
