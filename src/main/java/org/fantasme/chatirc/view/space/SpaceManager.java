package org.fantasme.chatirc.view.space;

/**
 * Gére l'affichage des espaces.
 */
public interface SpaceManager {

    /**
     * Ajoute un espace au gestionnaire.
     *
     * @param space      L'espace é ajouter.
     * @param setCurrent Indique si l'espace doit étre automatiquement affiché.
     */
    void addSpace(IRCSpaceView space, boolean setCurrent);

    /**
     * Supprime l'espace du gestionnaire.
     *
     * @param space L'espace é supprimer
     */
    void removeSpace(IRCSpaceView space);

    /**
     * Retourne la vue de l'espace en cours de visualisation
     *
     * @return L'espace visible.
     */
    IRCSpaceView getCurrentSpaceView();
}
