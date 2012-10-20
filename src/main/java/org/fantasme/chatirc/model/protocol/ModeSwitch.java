package org.fantasme.chatirc.model.protocol;

/**
 * Ajout ou suppression d'un mode.
 */
public enum ModeSwitch {
    // Ajout
    SET("+"),
    // Suppression
    UNSET("-");

    /**
     * La description de l'interrupteur.
     */
    private final String desc;

    /**
     * Crée un nouvel interrupteur.
     *
     * @param desc Description de l'interrupteur.
     */
    ModeSwitch(String desc) {
        this.desc = desc;
    }

    /**
     * Retourne la description de l'interrupteur.
     *
     * @return La chaine de caractére décrivant l'interrupteur.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Retourne l'interrupteur de mode correspondant au caractére.
     *
     * @param desc La description de l'interrupteur.
     * @return L'interrupteur correspondant é la description.
     */
    public static ModeSwitch getModeSwitch(String desc) {
        for (ModeSwitch modeSwitch : values()) {
            if (modeSwitch.desc.equals(desc)) {
                return modeSwitch;
            }
        }
        return null;
    }
}
