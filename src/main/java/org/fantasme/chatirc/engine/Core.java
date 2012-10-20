/**
 * Gére les entrées/sorties bas niveau entre le serveur et le client.
 */

package org.fantasme.chatirc.engine;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import org.fantasme.chatirc.controller.EngineListener;
import org.fantasme.chatirc.tools.properties.GestionnaireProperties;

public class Core {
    /**
     * Le charset par défaut.
     */
    private static final Charset DEFAULT_ENCODING = Charset.forName(GestionnaireProperties.getInstance().getPropriete("irc.conf.engine.defaultCharset", "FR"));

    /**
     * L'adresse du serveur.
     */
    private final String serverAddress;

    /**
     * Le port sur lequel se connecter au serveur.
     */
    private final int port;

    /**
     * Le pseudo de l'utilisateur.
     */
    private final String nick;

    /**
     * L'ident de l'utilisateur.
     */
    private final String ident;

    /**
     * Le nom d'héte.
     */
    private final String hostname;

    /**
     * Le nom de serveur.
     */
    private final String servername;

    /**
     * Le nom réel.
     * TODO : prendre on compte l'asl (age sex location).
     */
    private final String realname;

    /**
     * Le socket utilisé pour se connecter.
     */
    private Socket s;

    /**
     * Le thread écoutant le serveur.
     */
    private InputThread inputThread;

    /**
     * L'écrivain vers le serveur.
     */
    private PrintWriter outWriter;

    public Core(String serverAddress, int port, String nick, String ident, String realname, String hostname, String servername) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.nick = nick;
        this.ident = ident;
        this.hostname = hostname;
        this.servername = servername;
        this.realname = realname;

        s = null;
        inputThread = null;
        outWriter = null;
    }

    /**
     * Connecte le noyau au serveur.
     *
     * @throws IOException Rejetée si la connexion échoue.
     */
    public void connect() throws IOException {
        s = new Socket(InetAddress.getByName(serverAddress), port);
        inputThread = new InputThread(s.getInputStream(), DEFAULT_ENCODING);
        outWriter = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), DEFAULT_ENCODING));
    }

    /**
     * Enregistre l'utilisateur sur le réseau.
     */
    public void register() {
        inputThread.start();
        write("NICK " + nick + "\r\n"); // Autoflush ici
        write("USER " + ident + " " + hostname + " " + servername + " :" + realname + "\r\n");
    }

    /**
     * Se déconnecte du réseau.
     *
     * @throws IOException Rejetée si la déconnexion échoue.
     */
    public void disconnect() throws IOException {
        inputThread.interrupt();
        s.close();
    }

    /**
     * Change le charset utilisé pour la lecture et l'écriture.
     *
     * @param charset Le nouveau charset é utiliser.
     * @throws IOException Rejetée si le changement de charset a échoué.
     */
    public void changeCharset(Charset charset) throws IOException {
        inputThread.changeEncoding(charset);
        outWriter = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), charset));
    }

    /**
     * Enregistre le listener de moteur.
     *
     * @param l Le nouvel EngineListener.
     */
    public void registerEngineListener(EngineListener l) {
        inputThread.addEngineListener(l);
    }

    /**
     * Supprime un écouteur de moteur.
     *
     * @param l L'écouteur é supprimer.
     */
    public void removeEngineListener(EngineListener l) {
        inputThread.removeEngineListener(l);
    }

    /**
     * Ecrit le message vers le serveur en tant que ligne.
     *
     * @param s La chaine é envoyer.
     */
    public void writeln(String s) {
        write(s + "\r\n");
    }

    /**
     * Ecrit le message en clair vers le serveur.
     *
     * @param s La chaine é envoyer.
     */
    public void write(String s) {
        outWriter.write(s);
        outWriter.flush();
    }
}
