package org.fantasme.chatirc.tools.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire des fichiers de propriétés
 *
 * @author BHOGREL
 */
public class GestionnaireProperties {

    /**
     * Liste des fichiers de propriété
     */
    private List<PropertieBean> properties = new ArrayList<PropertieBean>();

    /**
     * Instance unique du gestionnaire de fichiers de propriétés
     */
    private static GestionnaireProperties gestionnaireProperties;

    /**
     * Liste des fichiers de propriétés
     */
    private static String[] FICHIERS_PROPRIETES = {"chat.properties"};

    /**
     * Constructeur par défaut
     */
    private GestionnaireProperties() {

        //Récupération des propriétés
        for (String fichier : FICHIERS_PROPRIETES) {

            try {
                GestionnaireFichiers gestionnaireFichiers = new GestionnaireFichiers(fichier);
                properties.add(new PropertieBean(fichier, gestionnaireFichiers.getInputStream()));
            } catch (Exception e) {
                new ExceptionService("Le chargement du fichier " + fichier + " a échoué", e);
            }
        }
    }

    /**
     * Initialisation du gestionnaire de fichiers de propriétés
     */
    private static void init() {
        try {
            gestionnaireProperties = new GestionnaireProperties();
        } catch (Exception e) {
            new ExceptionService("Le chargement des fichiers suivants a échoué : " + FICHIERS_PROPRIETES.toString(), e);
        }
    }

    /**
     * Récupération de l'instance unique du gestionnaire de fichiers de propriétés
     *
     * @return gestionnaire de fichier de propriétés
     */
    public static GestionnaireProperties getInstance() {
        if (gestionnaireProperties == null) init();
        return gestionnaireProperties;
    }

    /**
     * Retourne la valeur correspondant à la clef passée en argument pour la langue demandée
     *
     * @param clef   identifiant de la propriété (ex: opposition.accordee)
     * @param langue (ex: "FR")
     * @return propriété
     */
    public String getPropriete(String clef, String langue) {

        for (PropertieBean proprieteBean : properties) {
            if (proprieteBean.getLangue().equalsIgnoreCase(langue)) {

                String valeur = proprieteBean.getProperties().getProperty(clef);
                if (valeur != null) return valeur;
            }
        }

        return null;
    }

    /**
     * Retourne la valeur correspondant à la clef passée en argument pour la langue demandée
     *
     * @param clef       identifiant de la propriété (ex: opposition.accordee)
     * @param parametres Paramètres à insérer dans le fichier de localisation
     * @param langue     (ex: "FR")
     * @return propriété
     */
    public String getPropriete(String clef, String[] parametres, String langue) {

        for (PropertieBean proprieteBean : properties) {
            if (proprieteBean.getLangue().equalsIgnoreCase(langue)) {

                String valeur = proprieteBean.getProperties().getProperty(clef);
                if (valeur != null) {

                    for (int i = 0; i < parametres.length; i++) {
                        valeur = valeur.replaceAll("\\{" + i + "\\}", parametres[i]);
                    }
                    return valeur;
                }
            }
        }

        return null;
    }
}