package org.fantasme.chatirc.controller;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import org.fantasme.chatirc.error.ExceptionHandler;
import org.fantasme.chatirc.model.space.IRCSpaceModel;
import org.fantasme.chatirc.model.text.ExtendedStyledDocument;

/**
 * Permet d'associer un comportement aux liens hypertextes au sens général (liens cliquables) sur un JTextPane.
 * Active les HyperLinkListener (en cas de liens hypertextes html) du JTextPane car cela n'est pas fait automatiquement par le StyledEditorKit.
 */
public class IRCSpaceController extends MouseAdapter implements MouseMotionListener {
    /**
     * Le curseur en main.
     */
    private static final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    /**
     * Le curseur normal (flèche).
     */
    private static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * La derniére url survolée.
     */
    private String lastUrl;

    private String lastNick;

    /**
     * Vrai si la souris est au dessus du lien.
     */
    private boolean onLink;

    /**
     * Vrai si la souris est au-dessus d'un pseudonyme.
     */
    private boolean onNick;

    /**
     * L'offset de début du dernier pseudo clickable.
     */
    private int startNickOffset;

    /**
     * L'offset de fin du dernier pseudo clickable.
     */
    private int endNickOffset;

    /**
     * L'espace associé au JTextpane.
     */
    private IRCSpaceModel space;

    public IRCSpaceController(IRCSpaceModel space) {
        super();
        onLink = false;
        onNick = false;
        startNickOffset = -1;
        endNickOffset = -1;
        lastUrl = null;
        lastNick = null;

        this.space = space;
    }
    
    /**
     * Ecoute le clic sur le lien.
     */
    private final MouseListener activationListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                try {
                    fireActivated((JTextPane) e.getSource(), new URL(lastUrl));
                } catch (MalformedURLException e1) {
                    ExceptionHandler.handleTextException(e1);
                }
            }
        }
    };

    /**
     * Ecoute les clicks sur un pseudo cliquable.
     */
    private MouseListener nickClickListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            String lastNickPlain = lastNick.substring(lastNick.indexOf("<") + 1, lastNick.lastIndexOf(">"));
            for (String nick : space.getVisibleNicknames()) {
                if (nick.equals(lastNickPlain)) {
                    space.getServerModel().createPv(nick, true);
                }
            }
        }
    };

    /**
     * Ecoute les mouvements de la souris.
     *
     * @param e L'événement géréré par le mouvement.
     */
    public void mouseMoved(MouseEvent e) {
        // On récupére le textPane
        JTextPane textPane = (JTextPane) e.getSource();
        // On cherche si un lien est sous la souris.
        int pos = textPane.viewToModel(e.getPoint());
        if (pos >= 0) {
            // On recupére l'attribut de lien
            StyledDocument doc = (StyledDocument) textPane.getDocument();
            Element elem = doc.getCharacterElement(pos);
            AttributeSet set = elem.getAttributes();
            String href = (String) set.getAttribute(HTML.Attribute.HREF);
            String nick = (String) set.getAttribute(ExtendedStyledDocument.Attribute.CLICKABLE_NICKNAME);
            if (nick != null) {
                SimpleAttributeSet clickableStyle = new SimpleAttributeSet();
                StyleConstants.setUnderline(clickableStyle, true);
                if (!nick.equals(lastNick)) {
                    if (onNick) {
                        SimpleAttributeSet declickableStyle = new SimpleAttributeSet();
                        StyleConstants.setUnderline(declickableStyle, false);
                        doc.setCharacterAttributes(startNickOffset, endNickOffset - startNickOffset, declickableStyle, false);
                        textPane.removeMouseListener(nickClickListener);
                    }
                    startNickOffset = elem.getStartOffset();
                    endNickOffset = elem.getEndOffset();
                    try {
                        lastNick = doc.getText(startNickOffset, endNickOffset - startNickOffset);
                    } catch (BadLocationException e1) {
                        ExceptionHandler.handleTextException(e1);
                    }
                    doc.setCharacterAttributes(startNickOffset, endNickOffset - startNickOffset, clickableStyle, false);
                    onNick = true;
                    textPane.setCursor(handCursor);
                    textPane.addMouseListener(nickClickListener);
                }
            } else {
                if (startNickOffset != -1 && endNickOffset != -1) {
                    lastNick = null;
                    SimpleAttributeSet clickableStyle = new SimpleAttributeSet();
                    StyleConstants.setUnderline(clickableStyle, false);
                    doc.setCharacterAttributes(startNickOffset, endNickOffset - startNickOffset, clickableStyle, false);
                    startNickOffset = -1;
                    endNickOffset = -1;
                    textPane.removeMouseListener(nickClickListener);
                    textPane.setCursor(defaultCursor);
                }
            }
            // On a bien un lien
            if (href != null) {
                textPane.setToolTipText(href);
                textPane.setCursor(handCursor);
                onLink = true;
                // Si on a changé de lien par rapport é la derniére écoute.
                if (!href.equals(lastUrl)) {
                    // Si le dernier lien n'a pas été invalidé.
                    if (lastUrl != null) {
                        textPane.removeMouseListener(activationListener);
                        try {
                            fireExited(textPane, new URL(lastUrl));
                        } catch (MalformedURLException e1) {
                            ExceptionHandler.handleTextException(e1);
                        }
                    }
                    try {
                        // On dit aux écouteurs des liens que la souris est entrée sur l'un d'eux
                        fireEntered(textPane, new URL(href));
                        lastUrl = href;
                        textPane.addMouseListener(activationListener);
                    } catch (MalformedURLException e1) {
                        ExceptionHandler.handleTextException(e1);
                    }
                }
                // Si on est pas sur un lien.
            } else {
                if (onLink) {
                    textPane.setToolTipText(null);
                    textPane.setCursor(defaultCursor);
                    if (lastUrl != null) {
                        try {
                            // On dit aux écouteurs des liens que la souris est sortie du dernier entré.
                            fireExited(textPane, new URL(lastUrl));
                            textPane.removeMouseListener(activationListener);
                        } catch (MalformedURLException e1) {
                            ExceptionHandler.handleTextException(e1);
                        }
                    }
                    lastUrl = null;
                    onLink = false;
                }
            }
        }
    }

    /**
     * Signale aux HyperLinkListener du JTextPane que la souris est entrée sur le lien.
     *
     * @param textPane Le textpane sur lequel appliquer l'événement.
     * @param url      L'url concernée.
     */
    private static void fireEntered(JTextPane textPane, URL url) {
        for (HyperlinkListener l : textPane.getHyperlinkListeners()) {
            l.hyperlinkUpdate(new HyperlinkEvent(textPane, HyperlinkEvent.EventType.ENTERED, url));
        }
    }

    /**
     * Signale aux HyperLinkListener du JTextPane que la souris est sortie du lien.
     *
     * @param textPane Le textpane sur lequel appliquer l'événement.
     * @param url      L'url concernée.
     */
    private static void fireExited(JTextPane textPane, URL url) {
        for (HyperlinkListener l : textPane.getHyperlinkListeners()) {
            l.hyperlinkUpdate(new HyperlinkEvent(textPane, HyperlinkEvent.EventType.EXITED, url));
        }
    }

    /**
     * Signale aux HyperLinkListener du JTextPane que la souris é cliqué sur le lien.
     *
     * @param textPane Le textpane sur lequel appliquer l'événement.
     * @param url      L'url concernée.
     */
    private static void fireActivated(JTextPane textPane, URL url) {
        for (HyperlinkListener l : textPane.getHyperlinkListeners()) {
            l.hyperlinkUpdate(new HyperlinkEvent(textPane, HyperlinkEvent.EventType.ACTIVATED, url));
        }
    }
}