// TODO: utiliser l'injecteur.
package org.fantasme.chatirc.model.protocol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.fantasme.chatirc.model.space.ChannelModel;
import org.fantasme.chatirc.model.text.MessageInjecter;

/**
 * Abstraction des modes de canaux, rangés du plus important au moins important.
 */
public enum ChannelMode {
    /**
     * Mode propriétaire de canal.
     */
    OWNER('q', true) {
        public void execute(ChannelModel model, String from, String arg, ModeSwitch modeSwitch) {
            switch (modeSwitch) {
                case SET:
                    model.addUserStatus(User.Status.OWNER, arg);
                    MessageInjecter.injectClientMessage(from + " donne l'état de directeur de canal é " + arg, model);
                    break;
                case UNSET:
                    model.removeUserStatus(User.Status.OWNER, arg);
                    MessageInjecter.injectClientMessage(from + " enléve son état de directeur de canal é " + arg, model);
                    break;
            }
        }
    },
    /**
     * Mode administrateur de canal.
     */
    ADMIN('a', true) {
        public void execute(ChannelModel model, String from, String arg, ModeSwitch modeSwitch) {
            switch (modeSwitch) {
                case SET:
                    model.addUserStatus(User.Status.ADMIN, arg);
                    MessageInjecter.injectClientMessage(from + " donne l'état d'administrateur de canal é " + arg, model);
                    break;
                case UNSET:
                    model.removeUserStatus(User.Status.ADMIN, arg);
                    MessageInjecter.injectClientMessage(from + " enléve son état d'administrateur de canal é " + arg, model);
                    break;
            }
        }
    },
    /**
     * Mode opérateur de canal.
     */
    OP('o', true) {
        public void execute(ChannelModel model, String from, String args, ModeSwitch modeSwitch) {
            switch (modeSwitch) {
                case SET:
                    model.addUserStatus(User.Status.OPERATOR, args);
                    MessageInjecter.injectClientMessage(from + " donne l'état d'opérateur de canal é " + args, model);
                    break;
                case UNSET:
                    model.removeUserStatus(User.Status.OPERATOR, args);
                    MessageInjecter.injectClientMessage(from + " enléve son état d'opérateur de canal é " + args, model);
                    break;
            }
        }
    },
    /**
     * Mode demi-opérateur de canal.
     */
    HALFOP('h', true) {
        public void execute(ChannelModel model, String from, String arg, ModeSwitch modeSwitch) {
            switch (modeSwitch) {
                case SET:
                    model.addUserStatus(User.Status.HALFOP, arg);
                    MessageInjecter.injectClientMessage(from + " donne l'état de semi-opérateur é " + arg, model);
                    break;
                case UNSET:
                    model.removeUserStatus(User.Status.HALFOP, arg);
                    MessageInjecter.injectClientMessage(from + " enléve l'état de semi-opérateur é " + arg, model);
                    break;
            }
        }
    },
    /**
     * Mode "voice" sur le canal.
     */
    VOICE('v', true) {
        public void execute(ChannelModel model, String from, String arg, ModeSwitch modeSwitch) {
            switch (modeSwitch) {
                case SET:
                    model.addUserStatus(User.Status.VOICE, arg);
                    MessageInjecter.injectClientMessage(from + " donne la parole é " + arg, model);
                    break;
                case UNSET:
                    model.removeUserStatus(User.Status.VOICE, arg);
                    MessageInjecter.injectClientMessage(from + " enléve la parole é " + arg, model);
                    break;
            }
        }
    },
    /**
     * Mode créateur du canal.
     */
    CHANNEL_CREATOR('O', true),
    /**
     * Mode d'autorisation des invitations.
     */
    INVITE('i', false),
    /**
     * Mode modéré.
     */
    MODERATED('m', false),
    /**
     * Mode interdiction des messages externes.
     */
    NO_EXTERNAL('n', false),
    /**
     * Mode changement de topic reservé aux OP+.
     */
    TOPIC_BY_OPERS('t', false),
    /**
     * Mode protégé par mot de passe.
     */
    KEY_PROTECTED('k', true),
    /**
     * Mode limite du nombre d'utilisateurs.
     */
    LIMIT('l', true),
    /**
     * Mode banni sur le canal.
     */
    BAN('b', true),
    /**
     * Mode anti-pollution.
     */
    FLOOD_PROTECTION('f', true),

    /**
     * Mode inconnu.
     */
    UNKNOWN(' ', false);

    /**
     * Table associative entre code et mode de canal.
     */
    private static final Map<Character, ChannelMode> map = new HashMap<Character, ChannelMode>();

    static {
        for (ChannelMode m : values()) {
            map.put(m.getCode(), m);
        }
    }

    /**
     * Code litéral correspondant au mode.
     */
    private final Character code;

    /**
     * Vrai si un argument est requis.
     */
    private final boolean requireArg;

    /**
     * Construit un nouveau mode de canal.
     *
     * @param code       Le code litéral.
     * @param requireArg Necessite un argument ?
     */
    ChannelMode(Character code, boolean requireArg) {
        this.code = code;
        this.requireArg = requireArg;
    }

    public Character getCode() {
        return code;
    }

    public boolean requireArg() {
        return requireArg;
    }

    /**
     * Methode lancée é l'execution d'un nouveau mode.
     *
     * @param model      Le modéle de canal concerné.
     * @param from       L'origine du changement de mode.
     * @param args       L'argument du mode.
     * @param modeSwitch La valeur, positive ou négative, du mode.
     */
    public void execute(ChannelModel model, String from, String args, ModeSwitch modeSwitch) {
    }

    ;

    /**
     * Renvoie les modes correspondant au code en clair.
     *
     * @param code Le code de mode (de la forme [+|-].*)
     * @return La liste des modes correspondant au code.
     */
    private static ChannelMode[] getMode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Aucun mode spécifié.");
        }
        List<ChannelMode> modeList = new LinkedList<ChannelMode>();
        for (Character c : code.toCharArray()) {
            ChannelMode m = map.get(c);
            if (m == null) {
                modeList.add(UNKNOWN);
            } else {
                modeList.add(m);
            }
        }
        return modeList.toArray(new ChannelMode[modeList.size()]);
    }

    /**
     * Pour les modes cumulés, execute chacun d'eux l'un aprés l'autre.
     *
     * @param code  L'ensemble des codes.
     * @param model Le modéle de canal concerné.
     * @param from  L'origine du multi-mode.
     * @param args  La liste des arguments des modes.
     */
    public static void executeModes(String code, ChannelModel model, String from, String[] args) {
        ModeSwitch modeSwitch = ModeSwitch.getModeSwitch(code.substring(0, 1));
        String modes = code.substring(1);
        int i = 0; // Compteur des args
        for (ChannelMode mode : getMode(modes)) {
            if (mode == UNKNOWN) {
                // Le mode est inconnu, affichage direct du message
                MessageInjecter.injectClientMessage(from + " applique le mode " + code, model);
            } else {
                String arg;
                if (mode.requireArg) {
                    arg = args[i];
                    i += 1;
                } else {
                    arg = "";
                }
                mode.execute(model, from, arg, modeSwitch);
            }
        }

    }
}
