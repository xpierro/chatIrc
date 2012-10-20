package org.fantasme.chatirc.model.protocol.io;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fantasme.chatirc.model.protocol.ChannelMode;
import org.fantasme.chatirc.model.protocol.User;
import org.fantasme.chatirc.model.space.ChannelModel;
import org.fantasme.chatirc.model.space.PrivateMessageModel;
import org.fantasme.chatirc.model.space.ServerModel;
import org.fantasme.chatirc.model.text.MessageInjecter;

/**
 * Abstraction des entrées (messages venant du serveur).
 */
public enum ServerProtocolAbstraction {
    ERR_CANNOTSENDTOCHAN(ServerMessageCodes.ERR_CANNOTSENDTOCHAN) {
        public void execute(String prefix, String[] args, ServerModel m) {
            MessageInjecter.injectServerMessage("Impossible d'envoyer ce message (" + args[2] + ")", m.getChannel(args[1]));
        }
    },
    JOIN(ServerMessageCodes.JOIN) {
        public void execute(String prefix, String[] args, ServerModel m) {
            String nickname = User.parseFullNick(prefix);
            if (nickname.equals(m.getNickname())) {
                m.createChan(args[0]);
            }
            ChannelModel channel = m.getChannel(args[0]);
            channel.addNickname(User.parseFullNick(prefix));
            channel.join(nickname);

        }
    },
    KICK(ServerMessageCodes.KICK) {
        public void execute(String prefix, String[] args, ServerModel m) {
            String kicker = User.parseFullNick(prefix);
            String kicked = args[1];
            String from = args[0];
            String reason = (args.length > 2 ? "(" + args[2] + ")" : "");
            if (kicked.equals(m.getNickname())) {
                MessageInjecter.injectClientMessage("Vous avez été éjecté de " + from + " " + reason + ".", m);
                m.getChannel(args[0]).close();
            } else {
                MessageInjecter.injectClientMessage(kicker + " a éjecté " + kicked + " du canal (" + reason + ").", m.getChannel(from));
                m.getChannel(from).removeNickname(kicked);
            }

        }
    },
    NICK(ServerMessageCodes.NICK) {
        public void execute(String prefix, String[] args, ServerModel m) {
            String oldNick = User.parseFullNick(prefix);
            String newNick = args[0];
            boolean myNick = false;
            if (oldNick.equalsIgnoreCase(m.getNickname())) {
                m.setNickname(newNick);
                myNick = true;
            }
            for (ChannelModel channel : m.getChannels()) {
                if (channel.containsNickname(oldNick)) {
                    channel.changeNickname(oldNick, newNick);
                    if (!myNick) {
                        MessageInjecter.injectClientMessage(oldNick + " s'appelle maintenant " + newNick, channel);
                    }
                }
            }
            for (PrivateMessageModel pv : m.getPvs()) {
                if (pv.getName().equals(oldNick)) {
                    pv.setName(newNick);
                }
            }

        }
    },
    MODE(ServerMessageCodes.MODE) {
        public void execute(String prefix, String[] args, ServerModel m) {
            if (args[0].startsWith("#")) {
                ChannelModel model = m.getChannel(args[0]);
                ChannelMode.executeModes(args[1], model, User.parseFullNick(prefix), Arrays.copyOfRange(args, 2, args.length));
            }
        }
    },
    NOTICE(ServerMessageCodes.NOTICE) {
        public void execute(String prefix, String[] args, ServerModel m) {
            if (args[0].startsWith("#")) {
                MessageInjecter.injectNoticeReceived(User.parseFullNick(prefix) + "/" + args[0], args[1], m.getChannel(args[0]));
            } else if (args[0].startsWith("AUTH")) {
                MessageInjecter.injectServerMessage(args[1].substring(args[1].indexOf(" ") + 1), m);
            } else {
                MessageInjecter.injectNoticeReceived(User.parseFullNick(prefix), args[1], m.getCurrentSpace());
            }
        }
    },
    PART(ServerMessageCodes.PART) {
        public void execute(String prefix, String[] args, ServerModel m) {
            String nick = User.parseFullNick(prefix);
            if (!nick.equals(m.getNickname())) {
                m.getChannel(args[0]).part(User.parseFullNick(prefix), (args.length > 1 ? args[1] : ""));
            } else {
                if (m.getChannel(args[0]) != null) {
                    MessageInjecter.injectClientMessage("Vous étes parti de " + args[0], m.getChannel(args[0]));
                }
            }
        }
    },
    PING(ServerMessageCodes.PING) {
        public void execute(String prefix, String[] args, ServerModel m) {
            m.sendPlainMessage("PONG :" + args[0]);
        }
    },
    PRIVMSG(ServerMessageCodes.PRIVMSG) {
        /**
         * Cherche l'ensemble des commandes ICTCPRequest encapsulées dans le PRIVMSG.
         * @param content Le contenu du message.
         * @return Les commandes ICTCPRequest.
         */
        private String[] parseCTCP(String content) {
            List<String> results = new LinkedList<String>();

            Pattern p = Pattern.compile("(\\x01(.+)\\x01)+");
            Matcher m = p.matcher(content);
            while (m.find()) {
                results.add(m.group(2));
            }
            return results.toArray(new String[results.size()]);
        }

        public void execute(String prefix, String[] args, ServerModel m) {
            String orig = User.parseFullNick(prefix);
            String dest;
            if (args[0].startsWith("#")) { // Chan
                if (!m.getSpaces().containsKey(args[0])) {
                    m.createChan(args[0]);
                }
                dest = args[0];
            } else { // PV
                if (!m.getSpaces().containsKey(orig)) {
                    m.createPv(orig, false);
                }
                dest = orig;
            }
            String[] ctcps = parseCTCP(args[1]);
            if (ctcps.length == 0) {
                m.getSpaces().get(dest).postMessage(orig, args[1]);
            } else {
                for (String ctcp : ctcps) {
                    int sep = ctcp.indexOf(' ');
                    String command = ctcp;
                    String ctcpArgs = "";

                    if (sep > -1) {
                        command = ctcp.substring(0, sep);
                        ctcpArgs = ctcp.substring(sep + 1);
                    }
                    ICTCPRequest.getCTCP(command).execute(orig, dest, ctcpArgs, m);
                }
            }
        }
    },
    QUIT(ServerMessageCodes.QUIT) {
        public void execute(String prefix, String[] args, ServerModel m) {
            String leftNickname = User.parseFullNick(prefix);
            // Tous les canaux contenant le pseudo doivent étre signalés.
            for (ChannelModel c : m.getChannels()) {
                if (c.containsNickname(leftNickname)) {
                    c.quit(leftNickname, args[0]);
                }
            }
            // Si message privé, signaler aussi.
            PrivateMessageModel pv = (PrivateMessageModel) m.getSpaces().get(leftNickname);
            if (pv != null) {
                pv.quit(leftNickname, args[0]);
            }
        }
    },
    RPL_ISUPPORT(ServerMessageCodes.RPL_ISUPPORT) { // ATTENTION NON STANDARD...
        public void execute(String prefix, String[] args, ServerModel m) {
            // On reconstruit le message
            String original = "";
            for (int i = 1; i < args.length - 1; i++) {
                original += args[i] + " ";
            }
            original = original.trim();
            original += ":" + args[args.length - 1];

            original = original.replace(":are supported by this server", "").trim();
            args = original.split(" ");

            // Format des arg: token[=value]
            Map<String, String> isupportMap = new HashMap<String, String>();
            for (String tokenValue : args) {
                String[] pair = tokenValue.split("=");
                isupportMap.put(pair[0], pair.length > 1 ? pair[1] : null);
            }

            // Maintenant on cherche les token/value intéressants:
            String networkName = isupportMap.get("NETWORK");
            if (networkName != null) {
                m.setName(networkName);
            }
        }
    },
    RPL_LIST(ServerMessageCodes.RPL_LIST) {
        public void execute(String prefix, String[] args, ServerModel m) {
            m.addChannelToList(args[1], args[2], args[3]);
        }
    },
    RPL_LISTSTART(ServerMessageCodes.RPL_LISTSTART) {
        public void execute(String prefix, String[] args, ServerModel m) {
            m.startChannelList();
        }
    },
    RPL_NAMREPLY(ServerMessageCodes.RPL_NAMREPLY) {
        public void execute(String prefix, String[] args, ServerModel m) {
            for (String nickname : args[3].split(" ")) {
                m.getChannel(args[2]).addNickname(nickname);
            }
        }
    },
    RPL_TOPIC(ServerMessageCodes.RPL_TOPIC) {
        public void execute(String prefix, String[] args, ServerModel m) {
            m.getChannel((args[1])).setTopic(args[2]);
        }
    },
    RPL_WELCOME(ServerMessageCodes.RPL_WELCOME) {
        public void execute(String prefix, String[] args, ServerModel m) {
            // Le dernier argument est le nick complet de l'utilisateur
            String[] welcomeArgs = args[1].split(" ");
            m.setNickname(User.parseFullNick(welcomeArgs[welcomeArgs.length - 1]));
        }
    },
    TOPIC(ServerMessageCodes.TOPIC) {
        public void execute(String prefix, String[] args, ServerModel m) {
            m.getChannel((args[0])).setTopic(args[1]);
        }
    };

    /**
     * La description associée au comportement.
     */
    private final ServerMessageCodes associatedCode;

    /**
     * L'association description->comportement pour un acces rapide.
     */
    private static final Map<ServerMessageCodes, ServerProtocolAbstraction> map = new HashMap<ServerMessageCodes, ServerProtocolAbstraction>();

    static {
        for (ServerProtocolAbstraction ipa : ServerProtocolAbstraction.values()) {
            map.put(ipa.associatedCode, ipa);
        }
    }

    /**
     * Crée une action associée au message serveur.
     *
     * @param smc Le code du message serveur dont on veut créer une nouvelle action associée.
     */
    ServerProtocolAbstraction(ServerMessageCodes smc) {
        associatedCode = smc;
    }

    /**
     * La méthode é executer lorsqu'un message serveur a été reéu.
     *
     * @param prefix Le prefixe du message.
     * @param args   Les arguments du message.
     * @param m      Le modéle du serveur qui a reéu le message.
     */
    public abstract void execute(String prefix, String[] args, ServerModel m);

    /**
     * Renvoie l'objet associé au message serveur.
     *
     * @param smc La description du message serveur.
     * @return L'action associée é la description.
     */
    public static ServerProtocolAbstraction getIPA(ServerMessageCodes smc) {
        return map.get(smc);
    }
}
