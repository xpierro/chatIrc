package org.fantasme.chatirc.view.space;

import javax.swing.JTextField;
import org.fantasme.chatirc.model.space.IRCSpaceModel;

/**
 * Une vue d'espace IRC.
 */
public interface IRCSpaceView {
    /**
     * Retourne le modéle d'espace sous-jacent.
     *
     * @return Le modéle d'espace sous-jacent.
     */
    IRCSpaceModel getSpaceModel();

    /**
     * Demande le focus pour la vue.
     *
     * @return Vrai si le focus a pu étre obtenu.
     */
    boolean requestFocusInWindow();

    /**
     * Retourne le champ de saisie de la vue.
     *
     * @return Le champ de saisie.
     */
    JTextField getInputField();
}
