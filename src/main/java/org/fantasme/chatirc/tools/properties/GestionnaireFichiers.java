package org.fantasme.chatirc.tools.properties;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe regroupant des méthodes de lecture/écriture de fichiers
 *
 * @author BHOGREL
 */
public class GestionnaireFichiers {

    /**
     * Liste des flux des fichiers chargés en mémoire
     */
    private List<InputStream> fluxFichiers = new ArrayList<InputStream>();

    /**
     * Liste des noms de fichier é charger
     */
    private List<String> nomsFichiers = new ArrayList<String>();

    /**
     * Constructeur pour un seul fichier é charger
     *
     * @param nomFichier nom du fichier é charger
     */
    public GestionnaireFichiers(String nomFichier) {

        //Récupération du fichier
        nomsFichiers.add(nomFichier);

        //Enregistrement du flux
        setInputStream();
    }

    /**
     * Constructeur pour plusieurs fichiers
     *
     * @param nomsFichiers tableau des noms de fichiers é charger
     */
    public GestionnaireFichiers(String[] nomsFichiers) {

        //Récupération des fichiers
        for (int i = 0; i < nomsFichiers.length; i++) {
            this.nomsFichiers.add(i, nomsFichiers[i]);
        }

        //Enregistrement du flux
        setInputStream();

    }

    /**
     * Récupére les flux des fichiers récupérés
     */
    private void setInputStream() {

        try {
            for (int i = 0; i < nomsFichiers.size(); i++) {
                fluxFichiers.add(i, this.getClass().getClassLoader().getResourceAsStream(nomsFichiers.get(i)));
            }
        } catch (Exception e) {
            new ExceptionService("Le chargement des fichiers suivants a échoué : " + nomsFichiers.toString(), e);
        }
    }

    /**
     * Retourne le flux du fichier unique/premier fichier passé en argument
     *
     * @return flux du fichier
     */
    public InputStream getInputStream() {

        if (fluxFichiers == null || fluxFichiers.size() < 1) return null;
        return fluxFichiers.get(0);
    }

    /**
     * Retourne le flux de l'ensemble des fichiers passés en argument
     *
     * @return les flux des fichiers passés en argument
     */
    public List<InputStream> getInputsStream() {
        if (fluxFichiers == null || fluxFichiers.size() < 1) return null;
        return fluxFichiers;
	}

}

