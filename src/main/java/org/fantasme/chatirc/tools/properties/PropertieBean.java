package org.fantasme.chatirc.tools.properties;

import java.io.InputStream;
import java.util.Properties;

/**
 * Structure d'un fichier de propriété
 *
 * @author BHOGREL
 */
public class PropertieBean {

    /**
     * Séparateur de localisation (ex : _FR)
     */
    private static char PREFIX_LOCALISATION = '_';

    /**
     * Séparateur de l'extension du fichier (ex: .properties)
     */
    private static char SUFFIX_FICHIER = '.';

    /**
     * Langue par défaut
     */
    private static String LANGUE_DEFAUT = "FR";

    /**
     * Langue du fichier (ex: FR)
     */
    private String langue;

    /**
     * Libellé du fichier (ex: assist)
     */
    private String libelleFichier;

    /**
     * Libellé complet du fichier
     */
    private String libelleCompletFichier;

    /**
     * Paramètrage des propriétés
     */
    private Properties properties = new Properties();


    /**
     * Constructeur par défaut
     *
     * @param nomFichier  : nom du fichier
     * @param fluxFichier : flux d'entrée du fichier
     * @throws java.io.IOException : I/O exception
     */
    public PropertieBean(String nomFichier, InputStream fluxFichier) throws Exception {

        int prefixLocalisation = nomFichier.indexOf(PREFIX_LOCALISATION);
        int suffixFichier = nomFichier.indexOf(SUFFIX_FICHIER);

        if (prefixLocalisation == -1) { //Langue non renseignée
            prefixLocalisation = suffixFichier;
            langue = LANGUE_DEFAUT;
        } else { //Langue renseignée
            langue = nomFichier.substring(prefixLocalisation + 1, suffixFichier);
        }

        //Récupération du descriptif du fichier
        libelleCompletFichier = nomFichier;
        libelleFichier = nomFichier.substring(0, prefixLocalisation);

        //Récupération du contenu du fichier
        properties.load(fluxFichier);
        fluxFichier.close();
    }


    /**
     * @return the langue
     */
    public String getLangue() {
        return langue;
    }


    /**
     * @return the libelleFichier
     */
    public String getLibelleFichier() {
        return libelleFichier;
    }


    /**
     * @return the libelleCompletFichier
     */
    public String getLibelleCompletFichier() {
        return libelleCompletFichier;
    }


    /**
     * @param langue the langue to set
     */
    public void setLangue(String langue) {
        this.langue = langue;
    }


    /**
     * @param libelleFichier the libelleFichier to set
     */
    public void setLibelleFichier(String libelleFichier) {
        this.libelleFichier = libelleFichier;
    }


    /**
     * @param libelleCompletFichier the libelleCompletFichier to set
     */
    public void setLibelleCompletFichier(String libelleCompletFichier) {
        this.libelleCompletFichier = libelleCompletFichier;
    }


    /**
     * @param properties the properties to set
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }


	/**
     * @return the properties
     */
	public Properties getProperties(){
		return properties;
	}
}
