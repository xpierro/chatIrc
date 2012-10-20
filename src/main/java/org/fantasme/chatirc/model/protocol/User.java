package org.fantasme.chatirc.model.protocol;

import java.util.Set;
import java.util.TreeSet;

/**
 * Représentation d'un utilisateur.
 */
public class User implements Comparable<User> {
    /**
     * Le status d'un utilisateur en fonction de la lettre-clef.
     */
    public enum Status {
        OWNER("~"),
        ADMIN("&"),
        OPERATOR("@"),
        HALFOP("%"),
        VOICE("+"),
        USER("");

        /**
         * Lettre clef codant le status.
         */
        private final String letter;

        /**
         * Crée un nouveau statut en fonction de la lettre-clef.
         *
         * @param s La lettre-clef du statut.
         */
        Status(String s) {
            letter = s;
        }

        public String getStatusLetter() {
            return letter;
        }

        /**
         * Retourne le statut en fonctionne de la lettre-clef.
         *
         * @param letter Lettre-clef dont on veut le statut.
         * @return Le statut correspondant é la lettre-clef.
         */
        public static Status getStatus(String letter) {
            for (Status s : Status.values()) {
                if (s.letter.equals(letter)) {
                    return s;
                }
            }
            return USER;
        }
    }

    /**
     * Le pseudo de l'utilisateur.
     */
    private final String nick;

    /**
     * Les status de l'utilisateur.
     */
    private final Set<Status> statusSet;
    private Status currentStatus;

    /**
     * Crée un nouvel utilisateur en fonction de son pseudo en clair avec status en premier caractére.
     *
     * @param plainNick Le pseudonyme en clair.
     */
    public User(String plainNick) {
        statusSet = new TreeSet<Status>();
        currentStatus = Status.getStatus(plainNick.substring(0, 1));
        statusSet.add(currentStatus);
        nick = (currentStatus != Status.USER ? plainNick.substring(1) : plainNick);
    }

    public String getPlainNick() {
        return currentStatus.getStatusLetter() + nick;
    }

    public String getNick() {
        return nick;
    }

    public Status getStatus() {
        return currentStatus;
    }

    /**
     * Compare deux utilisateurs en tenant compte du statut.
     *
     * @param u L'utilisateur é comparer.
     * @return Le rang de l'utilisateur par rapport é l'instance courante.
     */
    public int compareTo(User u) {
        if (u == null) {
            throw new IllegalArgumentException("Incomparables");
        }
        int statusComp = currentStatus.compareTo(u.currentStatus);
        return (statusComp != 0 ? statusComp : nick.compareToIgnoreCase(u.nick));
    }

    /**
     * Teste l'égalité de deux utilisateurs sans prendre en compte leur statut.
     *
     * @param o L'autre utilisateur é comparer.
     * @return Vrai si les deux pseudos sont les méme indépendamment de leur statut.
     */
    public boolean equals(Object o) {
        return o != null && o.getClass() == getClass()
                && hashCode() == o.hashCode();
    }

    /**
     * Retourne le hash code associé é l'utilisateur.
     *
     * @return Le hashCode associé.
     */
    public int hashCode() {
        return getNick().toLowerCase().hashCode();
    }

    /**
     * Recherche le statut le plus approprié é l'instant de l'appel.
     */
    private void selectCurrentStatus() {
        currentStatus = Status.USER;
        for (Status s : statusSet) {
            if (currentStatus.compareTo(s) > 0) {
                currentStatus = s;
            }
        }
    }

    /**
     * Ajoute un statut é la pile des statuts.
     *
     * @param status Le statut é ajouter.
     */
    public void addStatus(Status status) {
        statusSet.add(status);
        selectCurrentStatus();
    }

    /**
     * Supprime un statut de la pile des status.
     *
     * @param status Le statut é supprimer.
     */
    public void removeStatus(Status status) {
        statusSet.remove(status);
        selectCurrentStatus();
    }

    public static String parseFullNick(String plain) {
        //List<String> infos = new LinkedList<String>();
        // TODO : enregistrer certaines infos pour mettre dans un systéme de cache (hostinfo é la xchat sur mouseover).
        return plain.split("!")[0];

    }
}
