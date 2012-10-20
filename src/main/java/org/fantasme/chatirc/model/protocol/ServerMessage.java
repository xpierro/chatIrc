package org.fantasme.chatirc.model.protocol;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Un message du serveur.
 */
public class ServerMessage {
    /**
     * Le préfixe du message.
     */
    private String prefix;

    /**
     * La commande du message.
     */
    private String command;

    /**
     * La liste des arguments du message.
     */
    private List<String> args;

    /**
     * Crée un nouveau message serveur é partir d'une chaine en claire reéu par le serveur.
     *
     * @param plain La chaine en clair correspondant au message du serveur.
     */
    public ServerMessage(String plain) {
        if (plain == null || plain.equals("")) {
            throw new IllegalArgumentException("Message vide.");
        }
        args = new LinkedList<String>();

        if (plain.startsWith(":")) { // Il y a un prefixe
            prefix = plain.substring(1, plain.indexOf(' '));
            plain = plain.substring(plain.indexOf(' ') + 1);
        }
        // On parse la commande
        command = plain.substring(0, plain.indexOf(' '));
        plain = plain.substring(plain.indexOf(' '), plain.length());

        // On parse les arguments
        // Tout ce qui se situe apres n'est qu'un seul argument.
        int indexOfEnd = plain.indexOf(" :");

        if (indexOfEnd == -1) { // On n'a qu'une liste simple
            args = new LinkedList<String>(Arrays.asList(plain.substring(1).split(" ")));
        } else { // On a une liste d'argument suivie d'un argument complexe

            plain = plain.substring(1);
            String left = plain.substring(0, indexOfEnd);
            String right = plain.substring(indexOfEnd + 1, plain.length());
            args = new LinkedList<String>();
            if (left.length() > 0) {
                args.addAll(Arrays.asList(left.split(" ")));
            }
            args.add(right);
        }
    }

    public String getCommand() {
        return command;
    }

    public String getPrefix() {
        return prefix;
    }

    public String[] getArgs() {
        return args.toArray(new String[args.size()]);
    }
}
