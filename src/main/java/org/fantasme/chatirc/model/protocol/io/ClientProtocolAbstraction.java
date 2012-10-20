package org.fantasme.chatirc.model.protocol.io;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.fantasme.chatirc.error.ExceptionHandler;
import org.fantasme.chatirc.model.space.ChannelModel;
import org.fantasme.chatirc.model.space.IRCSpaceModel;
import org.fantasme.chatirc.model.space.ServerModel;
import org.fantasme.chatirc.model.text.MessageInjecter;
import org.fantasme.chatirc.view.IRCApplet;

/**
 * Comportement é la réception d'une commande utilisateur.
 */
public enum ClientProtocolAbstraction {
    /**
     * Bannir un utilisateur.
     */
    BAN(ClientMessageCode.BAN) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage("MODE " + m.getCurrentSpace().getName() + " +b " + args);
        }
    },
    /**
     * Changer le charset du client en entrée et en sortie.
     */
    CHARSET(ClientMessageCode.CHARSET) {
        public void execute(ServerModel m, String args) {
            try {
                Charset charset = Charset.forName(args);
                try {
                    m.getCore().changeCharset(Charset.forName(args));
                } catch (IOException e) {
                    ExceptionHandler.handleDisplayableException(e, m.getCurrentSpace(), "Charset inchangeable");
                }
                MessageInjecter.injectClientMessage("Vous changez votre charset en " + charset.toString(), m.getCurrentSpace());
            } catch (UnsupportedCharsetException e) {
                System.out.println("Pas de charset avec ce nom.");
            }
        }
    },
    /**
     * Enlever le statut d'opérateur é un utilisateur.
     */
    DEOP(ClientMessageCode.DEOP) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage("MODE " + m.getCurrentSpace().getName() + " -o " + args);
        }
    },
    /**
     * Enlever le status de voice é un utilisateur.
     */
    DEVOICE(ClientMessageCode.DEVOICE) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage("MODE " + m.getCurrentSpace().getName() + " -v " + args);
        }
    },
    /**
     * Rejoindre un canal.
     */
    JOIN(ClientMessageCode.JOIN) {
        public void execute(ServerModel m, String args) {
            args = args.trim();
            String[] chanNames = args.split(" ");
            for (String name : chanNames) {
                if (name.startsWith("#")) {
                    m.sendPlainMessage("JOIN " + name);
                } else {
                    m.sendPlainMessage("JOIN " + "#" + name);
                }
            }
        }

    },
    /**
     * Parler é la premiére personne.
     */
    ME(ClientMessageCode.ME) {
        public void execute(ServerModel m, String args) {
            IRCSpaceModel currentSpace = m.getCurrentSpace();
            if (currentSpace != m) {
                m.sendCTCPRequest("ACTION", currentSpace.getName(), args);
                MessageInjecter.injectPlainMessage("*", m.getNickname() + " " + args, currentSpace, Color.gray);
            }
        }
    },
    /**
     * Changer son pseudonyme.
     */
    NICK(ClientMessageCode.NICK) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage("NICK " + args);
        }
    },
    /**
     * Envoyer une notice.
     */
    NOTICE(ClientMessageCode.NOTICE) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage("NOTICE " + args);
            int spaceIndex = args.indexOf(' ');
            if (spaceIndex != -1) {
                String to = args.substring(0, spaceIndex);
                String mess = args.substring(spaceIndex, args.length());
                if (mess.length() > 0) {
                    MessageInjecter.injectNoticeSent(to, mess, m.getCurrentSpace());
                }
            }
        }
    },
    /**
     * Ajouter le statut d'opérateur é un utilisateur.
     */
    OP(ClientMessageCode.OP) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage("MODE " + m.getCurrentSpace().getName() + " +o " + args);
        }
    },
    /**
     * Envoyer un message en clair au serveur.
     */
    PLAIN(ClientMessageCode.PLAIN) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage(args);
        }
    },
    /**
     * Quitter un canal.
     */
    PART(ClientMessageCode.PART) {
        public void execute(ServerModel m, String args) {
            if (m.getCurrentSpace() instanceof ChannelModel) {
                // Si on est dans un canal on peut en partir
                ((ChannelModel) m.getCurrentSpace()).close(args);
            }
        }
    },
    /**
     * Envoyer un message privé.
     */
    PRIVMSG(ClientMessageCode.PRIVMSG) {
        public void execute(ServerModel m, String args) {
            int spaceIndex = args.indexOf(' ');
            if (spaceIndex != -1) {
                String to = args.substring(0, spaceIndex);
                String mess = args.substring(spaceIndex, args.length());
                if (mess.length() > 0) {
                    if (to.startsWith("#")) {
                        //m.createChan(to);
                    } else {
                        m.createPv(to, true);
                    }
                    m.sendPRIVMSG(to, mess);
                }
            }
        }
    },
    /**
     * Quitter un serveur.
     */
    QUIT(ClientMessageCode.QUIT) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage("QUIT " + args);
            IRCApplet.quitJs();
            System.exit(0);
        }
    },
    /**
     * Quitter puis rejoindre immédiatement un canal.
     */
    REJOIN(ClientMessageCode.REJOIN) {
        public void execute(ServerModel m, String args) {
            if (m.getCurrentSpace() instanceof ChannelModel) {
                m.sendPlainMessage("PART " + m.getCurrentSpace().getName() + " rejoin");
                m.sendPlainMessage("JOIN " + m.getCurrentSpace().getName());
            }
        }
    },
    /**
     * Se connecter é un nouveau serveur.
     */
    SERVER(ClientMessageCode.SERVER) {
        public void execute(ServerModel m, String args) {
            String[] argsArray = args.split(" ");
            if (argsArray.length >= 1) {
                int port = 6667;
                if (argsArray.length >= 2) {
                    try {
                        port = Integer.decode(argsArray[1]);
                    } catch (NumberFormatException e) {
                        port = 6667;
                    }
                }
                // TODO : changer éa pour acces concurrents.
                List<IRCSpaceModel> spaces = new LinkedList<IRCSpaceModel>();
                for (IRCSpaceModel space : m.getSpaces().values()) {
                    spaces.add(space);
                }
                for (IRCSpaceModel space : spaces) {
                    space.close();
                }
                m.changeServerCore(argsArray[0], port, m.getNickname(), "fanta");
            }
        }
    },
    /**
     * Débannir un utilisateur.
     */
    UNBAN(ClientMessageCode.UNBAN) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage("MODE " + m.getCurrentSpace().getName() + " -b " + args);
        }
    },
    /**
     * Ajouter le statut "voice" é un utilisateur.
     */
    VOICE(ClientMessageCode.VOICE) {
        public void execute(ServerModel m, String args) {
            m.sendPlainMessage("MODE " + m.getCurrentSpace().getName() + " +v " + args);
        }
    };

    /**
     * La table associative permettant d'obtenir rapidement le comportement associé é la description d'un message utilisateur.
     */
    private static final Map<ClientMessageCode, ClientProtocolAbstraction> map = new HashMap<ClientMessageCode, ClientProtocolAbstraction>();

    static {
        for (ClientProtocolAbstraction opa : ClientProtocolAbstraction.values()) {
            map.put(opa.associatedCode, opa);
        }
    }

    /**
     * La description du message client associé.
     */
    private final ClientMessageCode associatedCode;

    /**
     * Crée un nouveau comportement de message client.
     *
     * @param cmc La description du comportement du message.
     */
    ClientProtocolAbstraction(ClientMessageCode cmc) {
        associatedCode = cmc;
    }

    /**
     * Le comportement é appliquer é la description.
     *
     * @param m    Le modéle de serveur sur lequel appliquer l'action.
     * @param args Les arguments de la commande utilisateur.
     */
    public abstract void execute(ServerModel m, String args);

    /**
     * Retourne le comportement associé é la description du message.
     *
     * @param umc La description du message client.
     * @return Retourne le comportement associé au message.
     */
    public static ClientProtocolAbstraction getOPA(ClientMessageCode umc) {
        ClientProtocolAbstraction result = map.get(umc);
        if (result == null) {
            return null;
        } else {
            return result;
        }
    }
}
