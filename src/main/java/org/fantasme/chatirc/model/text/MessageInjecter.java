package org.fantasme.chatirc.model.text;

import java.awt.Color;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.fantasme.chatirc.model.protocol.IRCStringConverter;
import org.fantasme.chatirc.model.space.IRCSpaceModel;
// TODO : insérer plutot dans un document.
// TODO: support des styles intra message.

/**
 * Se charge d'injecter les messages selon leur type dans les documents des modéles.
 * Cela permet une trés grande personnalisation du style des messages, moins fouillie que si intégrée dans AbstractSpaceModel
 * comme auparavant.
 */
public class MessageInjecter {
    /**
     * Injecte un message é priorité serveur (xchat: *** contenu)
     *
     * @param message Le message serveur
     * @param space   L'espace oé injecter.
     */
    public static void injectServerMessage(String message, IRCSpaceModel space) {
        ExtendedStyledDocument doc = space.getDocument();
        doc.startMessage();
        SimpleAttributeSet serverIdStyles = new SimpleAttributeSet();
        StyleConstants.setForeground(serverIdStyles, Color.gray);
        doc.insertId("***", serverIdStyles);
        SimpleAttributeSet serverContentStyles = new SimpleAttributeSet();
        StyleConstants.setForeground(serverContentStyles, Color.gray);
        doc.insertContentString(message, serverContentStyles);
        doc.endMessage();
    }

    /**
     * Injecte un message é priorité client (xchat: * contenu)
     *
     * @param message Le message serveur
     * @param space   L'espace oé injecter.
     */
    public static void injectClientMessage(String message, IRCSpaceModel space) {
        ExtendedStyledDocument doc = space.getDocument();
        doc.startMessage();
        SimpleAttributeSet clientIdStyles = new SimpleAttributeSet();
        StyleConstants.setForeground(clientIdStyles, Color.gray);
        doc.insertId("*", clientIdStyles);
        IRCStringConverter.insertIntoStyledDocument(doc, message);
        doc.endMessage();
        doc.applyPostOperations(message);
    }

    /**
     * Injecte un message privé (<identité> contenu) au sens IRC (code PRIVMSG)
     *
     * @param senderNickname L'identité de l'envoyeur du message.
     * @param message        Le message stylisé irc é injecter
     * @param space          L'espace cible oé injecter le message privé.
     */
    public static void injectPrivateMessage(String senderNickname, String message, IRCSpaceModel space) {
        ExtendedStyledDocument doc = space.getDocument();
        doc.startMessage();
        SimpleAttributeSet pvStyles = new SimpleAttributeSet();
        StyleConstants.setForeground(pvStyles, Color.gray);

        pvStyles.addAttribute(ExtendedStyledDocument.Attribute.CLICKABLE_NICKNAME, senderNickname);

        doc.insertId("<" + senderNickname + ">", pvStyles);
        IRCStringConverter.insertIntoStyledDocument(doc, message);
        doc.endMessage();
        doc.applyPostOperations(message);
    }

    /**
     * Injecte une notice envoyée.
     *
     * @param receiverNickname Le pseudonyme du récepteur.
     * @param message          Le contenu de la notice.
     * @param space            L'espace oé injecter.
     */
    public static void injectNoticeSent(String receiverNickname, String message, IRCSpaceModel space) {
        ExtendedStyledDocument doc = space.getDocument();
        doc.startMessage();
        SimpleAttributeSet pvStyles = new SimpleAttributeSet();
        StyleConstants.setForeground(pvStyles, Color.gray);
        doc.insertId(">" + receiverNickname + "<", pvStyles);
        IRCStringConverter.insertIntoStyledDocument(doc, message);
        doc.endMessage();
        doc.applyPostOperations(message);
    }

    /**
     * Injecte une notice reéu.
     *
     * @param senderNickname Le pseudonyme de l'envoyeur.
     * @param message        Le contenu de la notice.
     * @param space          L'espace oé injecter.
     */
    public static void injectNoticeReceived(String senderNickname, String message, IRCSpaceModel space) {
        ExtendedStyledDocument doc = space.getDocument();
        doc.startMessage();
        SimpleAttributeSet pvStyles = new SimpleAttributeSet();
        StyleConstants.setForeground(pvStyles, Color.gray);
        doc.insertId("-" + senderNickname + "-", pvStyles);
        IRCStringConverter.insertIntoStyledDocument(doc, message);
        doc.endMessage();
        doc.applyPostOperations(message);
    }

    /**
     * Injecte un message en clair (TOKEN contenu)
     *
     * @param token   L'id à envoyer
     * @param space   L'espace où injecter les message en clair.
     * @param message Le message en clair à insérer.
     * @param color   Couleur du message.
     */
    public static void injectPlainMessage(String token, String message, IRCSpaceModel space, Color color) {
        ExtendedStyledDocument doc = space.getDocument();
        doc.startMessage();
        SimpleAttributeSet tokenSet = new SimpleAttributeSet();
        StyleConstants.setForeground(tokenSet, color);
        doc.insertId(token, tokenSet);
        SimpleAttributeSet contentSet = new SimpleAttributeSet();
        StyleConstants.setForeground(contentSet, color);
        doc.insertContentString(message, contentSet);
        doc.endMessage();
        doc.applyPostOperations(message);
    }

    /**
     * Injecte un message en clair avec stylisation du contenu (TOKEN contenu)
     *
     * @param token   L'id é envoyer
     * @param space   L'espace oé injecter les message en clair.
     * @param message Le message en clair é insérer.
     * @param color   Couleur du message.
     */
    public static void injectStyledPlainMessage(String token, String message, IRCSpaceModel space, Color color) {
        ExtendedStyledDocument doc = space.getDocument();
        doc.startMessage();
        SimpleAttributeSet tokenSet = new SimpleAttributeSet();
        StyleConstants.setForeground(tokenSet, color);
        doc.insertId(token, tokenSet);
        IRCStringConverter.insertIntoStyledDocument(doc, message);
        doc.endMessage();
        doc.applyPostOperations(message);
    }
}
