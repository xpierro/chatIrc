/**
 * Description des commandes utilisateur, tapée en général sous la forme:
 *  /commande arguments, par exemple /ping nick
 */

package org.fantasme.chatirc.model.protocol.io;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public enum ClientMessageCode {
    BAN(new String[]{"ban"}),
    CHARSET(new String[]{"charset"}),
    DEOP(new String[]{"deop"}),
    DEVOICE(new String[]{"devoice"}),
    JOIN(new String[]{"j", "join"}),
    ME(new String[]{"me"}),
    NICK(new String[]{"nick"}),
    NOTICE(new String[]{"notice"}),
    OP(new String[]{"op"}),
    PART(new String[]{"part"}),
    PLAIN(new String[]{"p", "plain"}),
    PRIVMSG(new String[]{"msg"}),
    QUIT(new String[]{"quit"}),
    REJOIN(new String[]{"rejoin"}),
    SERVER(new String[]{"server"}),
    UNBAN(new String[]{"unban"}),
    VOICE(new String[]{"voice"});

    /**
     * Construit un nouveau message client.
     *
     * @param aliasArray Le tableau des alias.
     */
    ClientMessageCode(String[] aliasArray) {
        aliases = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        aliases.addAll(Arrays.asList(aliasArray));
    }

    /**
     * L'ensemble des alias de la commande.
     */
    private final Set<String> aliases;

    /**
     * La table associative permettant d'accélérer l'acces aux commandes en fonction de l'alias.
     */
    private static Map<String, ClientMessageCode> map = new HashMap<String, ClientMessageCode>();

    static {
        for (ClientMessageCode cmc : values()) {
            for (String alias : cmc.aliases) {
                map.put(alias, cmc);
            }
        }
    }

    /**
     * Permet de retrouver un message en fonction de son alias.
     *
     * @param alias L'alias dont on veut la traduction en ClientMessageCode
     * @return L'objet correspondant é l'alias ou null si inconnu.
     */
    public static ClientMessageCode findMessageCode(String alias) {
        return map.get(alias);
    }
}
