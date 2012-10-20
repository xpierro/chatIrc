package org.fantasme.chatirc.model.space;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.event.EventListenerList;
import org.fantasme.chatirc.controller.EngineListener;
import org.fantasme.chatirc.controller.ServerListener;
import org.fantasme.chatirc.controller.SpaceAdapter;
import org.fantasme.chatirc.engine.Core;
import org.fantasme.chatirc.error.ExceptionHandler;
import org.fantasme.chatirc.model.ChannelsTableModel;
import org.fantasme.chatirc.model.protocol.IRCStringConverter;
import org.fantasme.chatirc.model.protocol.IRCTag;
import org.fantasme.chatirc.model.protocol.ServerMessage;
import org.fantasme.chatirc.model.protocol.io.ClientMessageCode;
import org.fantasme.chatirc.model.protocol.io.ClientProtocolAbstraction;
import org.fantasme.chatirc.model.protocol.io.ServerMessageCodes;
import org.fantasme.chatirc.model.protocol.io.ServerProtocolAbstraction;
import org.fantasme.chatirc.model.text.MessageInjecter;

/**
 * Représentation du serveur. Toute l'abstraction entrée/sortie doit se faire
 * ici pour ne pas briser l'encapsulation.
 * <p/>
 * Notes pour l'évolutivité :
 * Pour ajouter un nouveau comportement en réponse à un message :
 * Les messages IRC sont composés de trois éléments :
 * - Le préfixe (optionnel, toujours précédé d'un ":")
 * - Le code commande (entier ou chaine)
 * - Une liste d'argument séparés par des espaces, sauf l'argument
 * précédé de ":" qui est le dernier (par exemple ":arg u ment"
 * n'est qu'un seul argument contenant des espaces).
 * Message type : ":dev!ident@host PRIVMSG #canal :bonjour"
 * <p/>
 * Le traitement de ses messages du serveur est effectué par l'énumération
 * ServerProtocolAbstraction (I pour Input).
 * Pour en ajouter un, on peut lui donner le nom qu'on veut tant qu'il
 * est crée avec en argument un type de l'énumération
 * org.fantasme.chatirc.tools.ServerMessageCodes. On doit alors redéfinir la methode abstraite
 * execute prenant en argument le prefix (peut etre null), les args
 * et le org.fantasme.chatirc.model de serveur auquel appliquer le traitement.
 * <p/>
 * Pour ajouter un traitement graphique (apparition d'une fenêtre), on
 * peut modifier la classe ServerListener pour qu'elle puisse emettre le
 * nouvel événement qui sera écouté par l'interface graphique.
 * <p/>
 * La présence des deux énumération soeurs pour l'entrée et pour la sortie permet de séparer description
 * d'un message et comportement du client.
 */
public class ServerModel extends AbstractIRCSpaceModel {
    /**
     * Le noyau du moteur associé au serveur.
     */
    private Core core;

    /**
     * Les infos de connections.
     */
    private String address;
    private String nickName;
    private String ident;
    private int port;

    private List<String> defaultChannels;

    /**
     * L'ensemble des espaces visités sur le serveur.
     */
    private final Map<String, IRCSpaceModel> spaces;

    /**
     * La liste des écouteurs de serveur.
     */
    private final EventListenerList serverListeners;

    /**
     * L'espace en cours de visite.
     */
    private IRCSpaceModel currentSpace;

    /**
     * Le modéle de la table des canaux visitables sur le serveur.
     */
    private ChannelsTableModel channelsTableModel;

    public ServerModel(String add, int p, String nick, String ident) {
        super(add);
        address = add;
        nickName = nick;
        this.ident = ident;
        port = p;

        defaultChannels = new LinkedList<String>();

        serverListeners = new EventListenerList();

        // Les noms de canaux et les pseudos sont insensibles à la
        // casse pour le serveur. Par exemple FaNtAsMe et fantasmE sont la méme
        // personne.
        Comparator<String> caseInComp = String.CASE_INSENSITIVE_ORDER;
        spaces = new TreeMap<String, IRCSpaceModel>(caseInComp);
        currentSpace = this;
    }

    // GETTERS

    public String[] getVisibleNicknames() {
        return new String[0];
    }

    public Map<String, IRCSpaceModel> getSpaces() {
        return spaces;
    }

    public ChannelModel getChannel(String name) {
        return (ChannelModel) spaces.get(name);
    }

    public ChannelModel[] getChannels() {
        List<ChannelModel> channels = new LinkedList<ChannelModel>();
        for (IRCSpaceModel m : spaces.values()) {
            if (m instanceof ChannelModel) {
                channels.add((ChannelModel) m);
            }
        }
        return channels.toArray(new ChannelModel[channels.size()]);
    }

    public PrivateMessageModel[] getPvs() {
        List<PrivateMessageModel> pvs = new LinkedList<PrivateMessageModel>();
        for (IRCSpaceModel m : spaces.values()) {
            if (m instanceof PrivateMessageModel) {
                pvs.add((PrivateMessageModel) m);
            }
        }
        return pvs.toArray(new PrivateMessageModel[pvs.size()]);
    }

    /**
     * Renvoie le noyau associé au modéle de serveur.
     *
     * @return Le noyau courant auquel le modéle est connecté.
     */
    public Core getCore() {
        return core;
    }

    /**
     * Renvoie le pseudonyme sous lequel le modéle s'est enregistré sur le réseau.
     *
     * @return Le pseudonyme du modéle sur le réseau.
     */
    public String getNickname() {
        return nickName;
    }

    /**
     * Renvoie le modéle d'espace irc en cours de visite.
     *
     * @return Le modéle d'espace visité é l'instant.
     */
    public IRCSpaceModel getCurrentSpace() {
        return currentSpace;
    }

    /**
     * Retourne le modéle de la liste des canaux du réseau.
     *
     * @return Le modéle de la liste des canaux.
     */
    public ChannelsTableModel getChannelListModel() {
        return channelsTableModel;
    }

    /**
     * TODO : trouver un meilleur systéme est-il possible?
     * Compatibilité avec l'interface implémentée.
     *
     * @return L'instance.
     */
    public ServerModel getServerModel() {
        return this;
    }

    // SETTERS

    /**
     * Positionne l'espace visité é m.
     *
     * @param m L'espace en cours de visite.
     */
    public void setCurrentSpace(IRCSpaceModel m) {
        currentSpace = m;
    }

    /**
     * Enregistre un nouveau modéle de liste des canaux.
     *
     * @param m Le nouveau modéle pour la liste des canaux.
     */
    public void setChannelListModel(ChannelsTableModel m) {
        channelsTableModel = m;
    }

    /**
     * Spécifie le pseudonyme utilisé sur le serveur.
     *
     * @param s Le pseudonyme utilisé.
     */
    public void setNickname(String s) {
        for (IRCSpaceModel space : spaces.values()) {
            MessageInjecter.injectClientMessage("Vous vous appelez maintenant " + s, space);
        }
        MessageInjecter.injectClientMessage("Vous vous appelez maintenant " + s, this);
        nickName = s;
        fireNicknameChanged();
    }

    // Interaction avec le moteur.

    /**
     * Lance le moteur.
     *
     * @throws IOException si la connexion est impossible.
     */
    public void launchCore() throws IOException {
        core = new Core(address, port, nickName, ident,
                "realname", "hostname", "servername");
        core.connect();


        core.registerEngineListener(new EngineListener() {
            public void messageReceived(String message) {
                ServerMessage mess = new ServerMessage(message);
                receiveMessage(mess);
            }
        });
    }

    /**
     * Enregistre le coeur sur le serveur.
     */
    public void registerCore() {
        fireServerCreated(address);
        core.register();
        joinDefaultChannels();
    }

    // TODO: concurrence et registerCore() mieux adapté.

    /**
     * Change d'adresse de serveur.
     *
     * @param address  La nouvelle adresse.
     * @param port     Le port.
     * @param nickname Le pseudonyme.
     * @param ident    L'ident.
     */
    public void changeServerCore(String address, int port, String nickname, String ident) {
        try {
            core.disconnect();
        } catch (IOException e) {
            ExceptionHandler.handleNonBlockingException(e);
        }
        this.address = address;
        this.port = port;
        nickName = nickname;
        this.ident = ident;
        try {
            launchCore();
        } catch (IOException e) {
            //
        }
        core.register();
        joinDefaultChannels();
    }

    // TODO: refaire pour la concurrence.

    /**
     * Enregistre les canaux par défaut.
     *
     * @param defaultChannelsString La liste des canaux é rejoindre.
     */
    public void registerDefaultChannels(String defaultChannelsString) {
        if (defaultChannelsString != null && defaultChannelsString.length() > 0) {
            String[] channels = defaultChannelsString.split(",");
            for (String channel : channels) {
                defaultChannels.add(channel);
            }
        }
    }

    /**
     * Rejoint les canaux par défaut.
     */
    public void joinDefaultChannels() {
        for (String channel : defaultChannels) {
            sendPlainMessage("JOIN " + channel);
        }
    }


    // INTERACTIONS SERVEUR

    // ENTREES

    // TOUTE l'abstraction se fait ici.

    public void receiveMessage(ServerMessage mess) {
        Object command;
        try {
            command = Integer.decode(mess.getCommand());
        } catch (NumberFormatException e) {
            command = mess.getCommand();
        }
        ServerMessageCodes code = ServerMessageCodes.getMessageCode(command);
        String[] args = mess.getArgs();
        ServerProtocolAbstraction ipa = ServerProtocolAbstraction.getIPA(code);
        if (ipa == null) { // Le code n'est pas associé é un comportement.
            String defaultMessage = "";
            for (int i = 1; i < args.length; i++) {
                defaultMessage += args[i] + " ";
            }
            MessageInjecter.injectClientMessage(defaultMessage, this);
        } else { // On execute le comportement associé.
            ipa.execute(mess.getPrefix(), args, this);
        }
    }

    /**
     * Envoie le message brut (plus retour é la ligne) au serveur.
     *
     * @param plain Le message brut é envoyer.
     */
    public void sendPlainMessage(String plain) {
        core.writeln(plain);
    }

    /**
     * Envoi un message privé.
     *
     * @param destination La destination du message.
     * @param message     Le message.
     *                    TODO: est-il mieux de déléguer l'envoie de privmsg aux espaces correspondant en amont?
     */
    public void sendPRIVMSG(String destination, String message) {
        sendPlainMessage("PRIVMSG " + destination + " :" + message);
        IRCSpaceModel dest = spaces.get(destination);
        if (dest != null) {
            MessageInjecter.injectPrivateMessage(nickName, message, dest);
        }
    }

    /**
     * Envoie une requete ctcp.
     *
     * @param ctcpRequest Le nom de la requéte.
     * @param dest        Le destinataire de la requéte.
     * @param args        Les arguments de la requéte.
     */
    public void sendCTCPRequest(String ctcpRequest, String dest, String args) {
        sendPlainMessage("PRIVMSG " + dest + " :" + ((char) 0x001) + ctcpRequest + " " + args + ((char) 0x001));
    }

    // TODO : implémenter des réponse ctcp (PING par exemple)

    public void sendCTCPReply(String ctcpReply, String dest, String args) {
        sendPlainMessage("NOTICE " + dest + " :" + ((char) 0x001) + ctcpReply + " " + args + ((char) 0x001));
    }

    /**
     * Point d'entrée avant abstraction des commandes de sortie.
     *
     * @param plain La chaine tapée par l'utilisateur.
     */
    public void sendUserMessage(String plain) {
        if (!plain.startsWith("/")) {
            if (currentSpace != this) {
                sendPRIVMSG(currentSpace.getName(), plain);
            } else {
                MessageInjecter.injectClientMessage("Vous n'avez rejoint aucun canal, essayez " + IRCTag.BOLD.getCode() + "/join #<canal>", this);
            }
        } else {
            String command;
            String args;
            if (plain.indexOf(' ') != -1) {
                command = plain.substring(1, plain.indexOf(' '));
                args = plain.substring(plain.indexOf(' ') + 1);
            } else {
                command = plain.substring(1);
                args = "";
            }
            ClientProtocolAbstraction opa = ClientProtocolAbstraction.getOPA(ClientMessageCode.findMessageCode(command));
            if (opa != null) {
                opa.execute(this, args);
            } else {
                sendPlainMessage(command.toUpperCase() + " " + args);
            }
        }
    }

    /**
     * Ferme la connexion au serveur.
     */
    public void close() {
        System.exit(0);
    }

    /**
     * Crée un espace.
     *
     * @param model      Le modéle de l'espace.
     * @param setCurrent Signale si l'espace doit étre affiché immédiatement.
     */
    public void createSpace(final IRCSpaceModel model, boolean setCurrent) {
        spaces.put(model.getName(), model);
        if (setCurrent) {
            currentSpace = model;
        }
        // Lorsque l'espace change de nom, on rafraichit la map.
        model.addSpaceListener(new SpaceAdapter() {
            public void spaceNameChanged(String newName) {
                spaces.remove(model.getName());
                spaces.put(newName, model);
            }
        });
    }


    /**
     * Crée un message privé.
     *
     * @param name       Le nom du message privé
     * @param setCurrent Indique si l'on doit afficher le message immédiatement.
     */
    public void createPv(final String name, boolean setCurrent) {
        if (!spaces.containsKey(name) && !name.equals(nickName)) {
            final PrivateMessageModel model = new PrivateMessageModel(name, this);
            createSpace(model, setCurrent);
            firePvCreated(model, setCurrent);
        }
    }

    /**
     * Crée un canal.
     *
     * @param name Le nom du canal
     */
    public void createChan(final String name) {
        if (!spaces.containsKey(name)) {
            final ChannelModel model = new ChannelModel(name, this);
            createSpace(model, true);
            fireChanCreated(model);
        }
    }

    /**
     * Crée et attend le peuplage de la liste des canaux visitables.
     */
    public void startChannelList() {
        fireChannelListStarted();
    }

    /**
     * Ajoute un canal é la liste des canaux visitables.
     *
     * @param name  Le nom du canal
     * @param users Le nombre d'utilisateurs du canal.
     * @param topic Le sujet du canal.
     */
    public void addChannelToList(String name, String users, String topic) {
        if (channelsTableModel != null) {
            channelsTableModel.addRow(name, users, IRCStringConverter.toHTML(topic));
        }
    }

    /**
     * Retourne la liste des écouteurs de serveur.
     *
     * @return La liste des écouteurs du serveur.
     */
    public ServerListener[] getServerListeners() {
        return serverListeners.getListeners(ServerListener.class);
    }

    private void fireChanCreated(ChannelModel m) {
        for (ServerListener l : getServerListeners()) {
            l.chanCreated(m);
        }
    }

    private void firePvCreated(PrivateMessageModel m, boolean setCurrent) {
        for (ServerListener l : getServerListeners()) {
            l.pvCreated(m, setCurrent);
        }
    }

    private void fireServerCreated(String name) {
        for (ServerListener l : getServerListeners()) {
            l.serverCreated(name);
        }
    }

    private void fireNicknameChanged() {
        for (ServerListener l : getServerListeners()) {
            l.nicknameChanged(nickName);
        }
    }

    private void fireChannelListStarted() {
        for (ServerListener l : getServerListeners()) {
            l.channelListStarted();
        }
    }

    /**
     * Ajoute un écouteur de serveur.
     *
     * @param l L'écouteur de serveur é ajouter.
     */
    public void addServerListener(ServerListener l) {
        serverListeners.add(ServerListener.class, l);
    }

    /**
     * Supprime un écouteur de serveur.
     *
     * @param l L'écouteur de serveur é supprimer.
     */
    public void removeServerListener(ServerListener l) {
        serverListeners.remove(ServerListener.class, l);
    }
}
