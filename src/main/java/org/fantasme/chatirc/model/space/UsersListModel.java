package org.fantasme.chatirc.model.space;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;
import org.fantasme.chatirc.model.protocol.User;

/**
 * La liste d'utilisateurs d'un channel.
 */
public class UsersListModel extends AbstractListModel {

    /**
     * Les données de la liste.
     */
    private final List<User> users;

    public UsersListModel() {
        users = new LinkedList<User>();
    }

    public User getElementAt(int i) {
        return users.get(i);
    }

    public int getSize() {
        return users.size();
    }

    /**
     * Retourne l'utilisateur correspondant au pseudonyme.
     *
     * @param nickname Le pseudonyme é rechercher dans la liste.
     * @return L'utilisateur correspondant au pseudonyme.
     */
    public User getUser(String nickname) {
        for (User u : users) {
            if (u.getNick().equals(nickname)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Ajoute un utilisateur é la liste.
     *
     * @param user L'utilisateur é ajouter.
     */
    public void addUser(User user) {
        if (user == null) {
            throw new NullPointerException("Utilisateur vide.");
        }
        if (!users.contains(user)) {
            users.add(user);
            int size = getSize();
            fireIntervalAdded(this, size - 1, size - 1); // Pas utile ?
            resort();
        } else {
            users.remove(user);
            users.add(user);
        }
    }

    /**
     * Supprime l'utilisateur de la liste.
     *
     * @param user L'utilisateur é supprimer.
     */
    public void delUser(User user) {
        if (user == null) {
            throw new NullPointerException("Utilisateur vide.");
        }
        if (users.contains(user)) {
            int size = getSize();
            users.remove(user);
            fireIntervalRemoved(this, size - 1, size - 1); // Pas utile ?
            resort();
        }
    }

    /**
     * Ajoute un statut é l'utilisateur.
     *
     * @param user   L'utilisateur auquel ajouter le statut.
     * @param status Le statut é ajouter é l'utilisateur.
     */
    public void addStatus(User user, User.Status status) {
        if (users.contains(user)) {
            user = getUser(user.getNick());
            user.addStatus(status);
            resort();
        }
    }

    /**
     * Supprime un status é un utilisateur.
     *
     * @param user   L'utilisateur auquel supprimer un statut.
     * @param status Le statut é supprimer.
     */
    public void removeStatus(User user, User.Status status) {
        if (users.contains(user)) {
            user = getUser(user.getNick());
            user.removeStatus(status);
            resort();
        }
    }

    /**
     * Teste si l'utilisateur appartient é la liste.
     *
     * @param user L'utilisateur é tester.
     * @return Vrai si l'utilisateur appartient é la liste.
     */
    public boolean contains(User user) {
        if (user == null) {
            throw new NullPointerException("Utilisateur vide.");
        }
        return users.contains(user);
    }

    /**
     * Retrie la liste des utilisateurs.
     */
    private void resort() {
        Collections.sort(users);
        fireContentsChanged(this, 0, getSize() - 1);
    }
}
