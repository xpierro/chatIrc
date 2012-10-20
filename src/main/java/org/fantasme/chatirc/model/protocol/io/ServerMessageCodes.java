/**
 * Classe de description des messages serveur.
 */
package org.fantasme.chatirc.model.protocol.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ServerMessageCodes {
    //"<nickname> :No such nick/channel"
    ERR_NOSUCHNICK(401, new String[]{"nickname"}),

    //"<server name> :No such server"
    ERR_NOSUCHSERVER(402, new String[]{"server name"}),

    //"<channel name> :No such channel"
    ERR_NOSUCHCHANNEL(403, new String[]{"channel name"}),

    //"<channel name> :Cannot send to channel"
    ERR_CANNOTSENDTOCHAN(404, new String[]{"channel name"}),

    //"<channel name> :You have joined too many channels"
    ERR_TOOMANYCHANNELS(405, new String[]{"channel name"}),

    //"<nickname> :There was no such nickname"
    ERR_WASNOSUCHNICK(406, new String[]{"nickname"}),

    //"<target> :Duplicate recipients. No message delivered"
    ERR_TOOMANYTARGETS(407, new String[]{"target"}),

    //":No origin specified"
    ERR_NOORIGIN(409),

    //":No recipient given (<command>)"
    ERR_NORECIPIENT(411, "^.*?\\((.*+)\\)$", new String[]{"command"}),

    //":No text to send"
    ERR_NOTEXTTOSEND(412),

    //"<mask> :No toplevel domain specified"
    ERR_NOTOPLEVEL(413, new String[]{"mask"}),

    //"<mask> :Wildcard in toplevel domain"
    ERR_WILDTOPLEVEL(414, new String[]{"mask"}),

    //"<command> :Unknown command"
    ERR_UNKNOWNCOMMAND(421, new String[]{"command"}),

    //":MOTD File is missing"
    ERR_NOMOTD(422),

    //"<server> :No administrative info available"
    ERR_NOADMININFO(423, new String[]{"server"}),

    //":File error doing <file op> on <file>"
    ERR_FILEERROR(424),

    //":No nickname given"
    ERR_NONICKNAMEGIVEN(431),

    //"<nick> :Erroneus nickname"
    ERR_ERRONEUSNICKNAME(432, new String[]{"nick"}),

    //"<nick> :Nickname is already in use"
    ERR_NICKNAMEINUSE(433, new String[]{"nick"}),

    //"<nick> :Nickname collision KILL"
    ERR_NICKCOLLISION(436, new String[]{"nick"}),

    //"<nick> <channel> :They aren’t on that channel"
    ERR_USERNOTINCHANNEL(441, new String[]{"nick", "channel"}),

    //"<channel> :You’re not on that channel"
    ERR_NOTONCHANNEL(442, new String[]{"channel"}),

    //"<user> <channel> :is already on channel"
    ERR_USERONCHANNEL(443, new String[]{"user", "channel"}),

    //"<user> :User not logged in"
    ERR_NOLOGIN(444, new String[]{"user"}),

    //":SUMMON has been disabled"
    ERR_SUMMONDISABLED(445),

    //":USERS has been disabled"
    ERR_USERSDISABLED(446),

    //":You have not registered"
    ERR_NOTREGISTERED(451),

    //"<command> :Not enough parameters"
    ERR_NEEDMOREPARAMS(461, new String[]{"command"}),

    //":You may not reregister"
    ERR_ALREADYREGISTRED(462),

    //":Your host isn’t among the privileged"
    ERR_NOPERMFORHOST(463),

    //":Password incorrect"
    ERR_PASSWDMISMATCH(464),

    //":You are banned from this server"
    ERR_YOUREBANNEDCREEP(465),

    //"<channel> :ChannelModel key already set"
    ERR_KEYSET(467, new String[]{"channel"}),

    //"<channel> :Cannot join channel (+l)"
    ERR_CHANNELISFULL(471, new String[]{"channel"}),

    //"<char> :is unknown mode char to me"
    ERR_UNKNOWNMODE(472, new String[]{"char"}),

    //"<channel> :Cannot join channel (+i)"
    ERR_INVITEONLYCHAN(473, new String[]{"channel"}),

    //"<channel> :Cannot join channel (+b)"
    ERR_BANNEDFROMCHAN(474, new String[]{"channel"}),

    //"<channel> :Cannot join channel (+k)"
    ERR_BADCHANNELKEY(475, new String[]{"channel"}),

    //":Permission Denied- You’re not an IRC operator"
    ERR_NOPRIVILEGES(481),

    //"<channel> :You’re not channel operator"
    ERR_CHANOPRIVSNEEDED(482, new String[]{"channel"}),

    //":You cant kill a server!"
    ERR_CANTKILLSERVER(483),

    //":No O-lines for your host"
    ERR_NOOPERHOST(491),

    //":Unknown MODE flag"
    ERR_UMODEUNKNOWNFLAG(501),

    //":Cant change mode for other users"
    ERR_USERSDONTMATCH(502),

    //Dummy reply number. Not used.
    RPL_NONE(300),

    //":[<reply>{<space><reply>}]"
    //<reply> ::= <nick>[’*’] ’=’ <’+’|’-’><hostname>
    RPL_USERHOST(302,
            "^([^*= ]+)(\\*?)=([+-])([^ ]+)"
                    + "(?: ([^*= ]+)(\\*?)=([+-])([^ ]+)"
                    + "(?: ([^*= ]+)(\\*?)=([+-])([^ ]+)"
                    + "(?: ([^*= ]+)(\\*?)=([+-])([^ ]+)"
                    + "(?: ([^*= ]+)(\\\\*?)=([+-])([^ ]+))?)?)?)?$",
            new String[]{
                    "nick1", "op1", "away1", "host1",
                    "nick2", "op2", "away2", "host2",
                    "nick3", "op3", "away3", "host3",
                    "nick4", "op4", "away4", "host4",
                    "nick5", "op5", "away5", "host5"
            }
    ),
    //"Welcome to the Internet Relay Network <nick>!<user>@<host>"
    RPL_WELCOME(001),

    //":[<nick> {<space><nick>}]"
    RPL_ISON(303, new String[]{"nicknames"}),

    //"<nick> :<away message>"
    RPL_AWAY(301, "^(.*)$", new String[]{"nick", "away message"}),

    //":You are no longer marked as being away"
    RPL_UNAWAY(305),

    //":You have been marked as being away"
    RPL_NOWAWAY(306),

    //"<nick> <user> <host> * :<real name>"
    RPL_WHOISUSER(311, "^(.*)$",
            new String[]{"nick", "user", "host", "star", "real name"}),

    //"<nick> <server> :<server info>"
    RPL_WHOISSERVER(312, "^(.*)$",
            new String[]{"nick", "server", "server info"}),

    //"<nick> :is an IRC operator"
    RPL_WHOISOPERATOR(313, new String[]{"nick"}),

    //"<nick> <integer> :seconds idle"
    RPL_WHOISIDLE(317, new String[]{"nick", "integer"}),

    //"<nick> :End of /WHOIS list"
    RPL_ENDOFWHOIS(318, new String[]{"nick"}),

    //"<nick> :{[@|+]<channel><space>}"
    RPL_WHOISCHANNELS(319, "^(.*)$", new String[]{"nick", "channels"}),

    //"<nick> <user> <host> * :<real name>"
    RPL_WHOWASUSER(314, "^(.*)$",
            new String[]{"nick", "user", "host", "star", "real name"}),

    //"<nick> :End of WHOWAS"
    RPL_ENDOFWHOWAS(369, new String[]{"nick"}),

    //"ChannelModel :Users Name"
    RPL_LISTSTART(321),

    //"<channel> <# visible> :<topic>"
    RPL_LIST(322, "^(.*)$", new String[]{"channel", "users", "topic"}),

    //":End of /LIST"
    RPL_LISTEND(323),

    //"<channel> <mode> <mode params>"
    RPL_CHANNELMODEIS(324, new String[]{"channel", "mode", "mode params"}),

    //"<channel> :No topic is set"
    RPL_NOTOPIC(331, new String[]{"channel"}),

    //"<channel> :<topic>"
    RPL_TOPIC(332, "^(.*)$", new String[]{"channel", "topic"}),

    //"<channel> <nick>"
    RPL_INVITING(341, new String[]{"channel", "nick"}),

    //"<user> :Summoning user to IRC"
    RPL_SUMMONING(342, new String[]{"user"}),

    //"<version>.<debuglevel> <server> :<comments>"
    RPL_VERSION(351, "^(.*)$",
            new String[]{"version.debuglevel", "server", "comment"}),

    //"<channel> <user> <host> <server> <nick> <H|G>[*][@|+]
    // :<hopcount> <real name>"
    RPL_WHOREPLY(352, "^(\\d+ (.+))$",
            new String[]{"channel", "user", "host", "server",
                    "nick", "flags", "hopcount", "real name"}),

    //"<name> :End of /WHO list"
    RPL_ENDOFWHO(315, new String[]{"name"}),

    //"<channel> :[[@|+]<nick> [[@|+]<nick> [...]]]"
    RPL_NAMREPLY(353, "^(.*)$", new String[]{"channel", "names"}),

    //"<channel> :End of /NAMES list"
    RPL_ENDOFNAMES(366, new String[]{"channel"}),

    //"<mask> <server> :<hopcount> <server info>"
    RPL_LINKS(364, "^(\\d+) (.*)$",
            new String[]{"mask", "server", "hopcount", "server info"}),

    //"<mask> :End of /LINKS list"
    RPL_ENDOFLINKS(365, new String[]{"mask"}),

    //"<channel> <banid>"
    RPL_BANLIST(367, new String[]{"channel", "banid"}),

    //"<channel> :End of channel ban list"
    RPL_ENDOFBANLIST(368, new String[]{"channel"}),

    //":<string>"
    RPL_INFO(371, "^(.*)$", new String[]{"info"}),

    //":End of /INFO list"
    RPL_ENDOFINFO(374),

    //":- <server> Message of the day - "
    RPL_MOTDSTART(375, "^- (.+) .*+ -", new String[]{"server"}),

    //":- <text>"
    RPL_MOTD(372, "^- (.*)$", new String[]{"text"}),

    //":End of /MOTD command"
    RPL_ENDOFMOTD(376),

    //":You are now an IRC operator"
    RPL_YOUREOPER(381),

    //"<config file> :Rehashing"
    RPL_REHASHING(382, new String[]{"config file"}),

    //"<server> :<string showing server’s local time>"
    RPL_TIME(382, "^(.*)$", new String[]{"server", "local time"}),

    //":UserID Terminal Host"
    RPL_USERSSTART(392),

    //":%-8s %-9s %-8s"
    RPL_USERS(393, "^(.{8}) (.{9}) (.{8})$",
            new String[]{"UserID", "Terminal", "Host"}),

    //":End of users"
    RPL_ENDOFUSERS(394),

    //":Nobody logged in"
    RPL_NOUSERS(395),

    //"Link <version & debug level> <destination> <next server>"
    RPL_TRACELINK(200,
            new String[]{"link", "version & debug level",
                    "destination", "next server"}),

    //"Try. <class> <server>"
    RPL_TRACECONNECTING(201, new String[]{"Try", "class", "server"}),

    //"H.S. <class> <server>"
    RPL_TRACEHANDSHAKE(202, new String[]{"hs", "class", "server"}),

    //"???? <class> [<client IP address in dot form>]"
    RPL_TRACEUNKNOWN(203, new String[]{"what", "class", "ip"}),

    //"Oper <class> <nick>"
    RPL_TRACEOPERATOR(204, new String[]{"oper", "class", "nick"}),

    //"User <class> <nick>"
    RPL_TRACEUSER(205, new String[]{"user", "class", "nick"}),

    //"Serv <class> <int>S <int>C <server> <nick!user|*!*>@<host|server>"
    RPL_TRACESERVER(206, new String[]{"serv", "class", "S",
            "C", "server", "fullhost"}),

    //"<newtype> 0 <client name>"
    RPL_TRACENEWTYPE(208, new String[]{"newtype", "0", "client name"}),

    //"File <logfile> <debug level>"
    RPL_TRACELOG(261, new String[]{"file", "logfile", "debuglevel"}),

    //"<linkname> <sendq> <sent messages> <sent bytes> <received messages>
    // <received bytes> <time open>"
    RPL_STATSLINKINFO(211,
            new String[]{"linkname", "sendq", "sent messages",
                    "sent bytes", "received messages",
                    "received bytes", "time open"}),

    //"<command> <count>"
    RPL_STATSCOMMANDS(212, new String[]{"command", "count"}),

    //"C <host> * <name> <port> <class>"
    RPL_STATSCLINE(213, new String[]{"C", "host", "star", "name", "port", "class"}),

    //"N <host> * <name> <port> <class>"
    RPL_STATSNLINE(214, new String[]{"N", "host", "star", "name", "port", "class"}),

    //"I <host> * <host> <port> <class>"
    RPL_STATSILINE(215, new String[]{"I", "host", "star", "host", "port", "class"}),

    //"K <host> * <username> <port> <class>"
    RPL_STATSKLINE(216, new String[]{"K", "host", "star", "username", "port", "class"}),

    //"Y <class> <ping frequency> <connect frequency> <max sendq>"
    RPL_STATSYLINE(218, new String[]{"Y", "class", "ping freq", "connect freq", "max sendq"}),

    //"<stats letter> :End of /STATS report"
    RPL_ENDOFSTATS(219, new String[]{"letters"}),

    //"L <hostmask> * <servername> <maxdepth>"
    RPL_STATSLLINE(241, new String[]{"L", "hostmask", "star", "servername", "maxdepth"}),

    //":Server Up %d days %d:%02d:%02d"
    RPL_STATSUPTIME(242, "^.+(\\d++).+(\\d++:\\d:\\d).*$", new String[]{"days", "time"}),

    //"O <hostmask> * <name>"
    RPL_STATSOLINE(243, new String[]{"O", "hostmask", "star", "name"}),

    //"H <hostmask> * <servername>"
    RPL_STATSHLINE(244, new String[]{"H", "hostmask", "star", "servername"}),

    //"<user mode string>"
    RPL_UMODEIS(221, new String[]{"usermode"}),

    //":There are <integer> users and <integer> invisible on <integer> servers"
    RPL_LUSERCLIENT(251, "^.+(\\d++).+(\\d++).+(\\d++).+$", new String[]{"users", "invisible", "servers"}),

    //"<integer> :operator(s) online"
    RPL_LUSEROP(252, new String[]{"operators"}),

    //"<integer> :unknown connection(s)"
    RPL_LUSERUNKNOWN(253, new String[]{"unknown connections"}),

    //"<integer> :channels formed"
    RPL_LUSERCHANNELS(254, new String[]{"channels"}),

    //":I have <integer> clients and <integer> servers"
    RPL_LUSERME(255, "^.+(\\d++).+(\\d++).+$",
            new String[]{"clients", "servers"}),

    //"<server> :Administrative info"
    RPL_ADMINME(256, new String[]{"server"}),

    //":<admin info>"
    RPL_ADMINLOC1(257, "^(.*)$", new String[]{"admin info"}),

    //":<admin info>"
    RPL_ADMINLOC2(258, "^(.*)$", new String[]{"admin info"}),

    //":<admin info>"
    RPL_ADMINEMAIL(259, "^(.*)$", new String[]{"admin info"}),

    RPL_TRACECLASS(209),
    RPL_STATSQLINE(217),
    RPL_SERVICEINFO(231),
    RPL_ENDOFSERVICES(232),
    RPL_SERVICE(233),
    RPL_SERVLIST(234),
    RPL_SERVLISTEND(235),
    RPL_WHOISCHANOP(316),
    RPL_KILLDONE(361),
    RPL_CLOSING(362),
    RPL_CLOSEEND(363),
    RPL_INFOSTART(373),
    RPL_MYPORTIS(384),
    ERR_YOUWILLBEBANNED(466),
    ERR_BADCHANMASK(476),
    ERR_NOSERVICEHOST(492),

    // NON STANDARD numeric 005 : 
    RPL_ISUPPORT(005),

    // Literaux
    PRIVMSG("PRIVMSG"),
    NOTICE("NOTICE"),
    JOIN("JOIN"),
    PART("PART"),
    KICK("KICK"),
    QUIT("QUIT"),
    NICK("NICK"),
    MODE("MODE"),
    TOPIC("TOPIC"),
    PING("PING");

    /**
     * Le code, litéral ou numérique, du message.
     */
    private final Object messageCode;

    /**
     * Le pattern matchant le dernier argument. Permet d'utiliser find et group.
     */
    private final Pattern textPattern;

    /**
     * Le nom des paramétres.
     */
    private final String[] paramNames;

    /**
     * Association Code->ServerMessageCodes pour un acces rapide.
     */
    private static final Map<Object, ServerMessageCodes> map = new HashMap<Object, ServerMessageCodes>();

    static {
        for (ServerMessageCodes smc : ServerMessageCodes.values()) {
            map.put(smc.messageCode, smc);
        }
    }

    // CONSTRUCTEURS

    ServerMessageCodes(Object code) {
        this(code, null, new String[0]);
    }

    ServerMessageCodes(Object code, String[] names) {
        this(code, null, names);
    }

    ServerMessageCodes(Object code, String textRegex, String[] names) {
        messageCode = code;
        if (textRegex != null) {
            textPattern = Pattern.compile(textRegex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        } else {
            textPattern = null;
        }
        paramNames = names;

    }

    // GETTERS

    public Object getCode() {
        return messageCode;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public String[] getParams(String[] parametres) {
        ArrayList<String> parametresLocaux = new ArrayList<String>();
        int last = parametres.length;
        if (textPattern != null) {
            last -= 1;
        } else if (last > paramNames.length) {
            last = paramNames.length;
        }

        for (int i = 0; i < last; i++) {
            parametresLocaux.add(parametres[i]);
        }

        if ((last != -1) && (last != parametres.length)) {
            String lastParam = parametres[last];
            if (lastParam != null && textPattern != null) {
                Matcher m = textPattern.matcher(lastParam);
                if (m.matches()) {
                    int groupCount = m.groupCount();
                    for (int i = 1; i <= groupCount; i++) {
                        parametresLocaux.add(m.group(i));
                    }
                }
            }
        }
        return parametresLocaux.toArray(new String[parametresLocaux.size()]);
    }

    public static ServerMessageCodes getMessageCode(Object command) {
        return map.get(command);
	}
}


