package org.fantasme.chatirc.model.text;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import org.fantasme.chatirc.error.ExceptionHandler;
import org.fantasme.chatirc.font.FontFactory;
import org.fantasme.chatirc.model.protocol.IRCStringConverter;

/**
 * Document à multi-colonne.
 */
public class MultiColumnDocument extends DefaultStyledDocument implements ExtendedStyledDocument {
    public static final String MESSAGE = "Message";
    public static final String HOUR = "Hour";
    public static final String ID = "Id";
    public static final String M_CONTENT = "M_CONTENT";

    private boolean first;
    private ArrayList<DefaultStyledDocument.ElementSpec> messSpecs;

    public MultiColumnDocument() {
        super();
        first = true;
    }

    public void appendChar(char c, AttributeSet styles) {
        insertContentChar(c, styles);
    }

    public void startMessage() {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        offset = getLength();
        messSpecs = new ArrayList<DefaultStyledDocument.ElementSpec>();
        if (first) {
            messSpecs.add(new ElementSpec(attrs, ElementSpec.EndTagType)); //close paragraph tag
            first = false;
        } else {
            messSpecs.add(new ElementSpec(attrs, ElementSpec.EndTagType)); //close paragraph tag
            messSpecs.add(new ElementSpec(attrs, ElementSpec.EndTagType)); //close paragraph tag
        }

        messAttrs = new SimpleAttributeSet();
        messAttrs.addAttribute(DefaultStyledDocument.ElementNameAttribute, MESSAGE);

        ElementSpec messStart = new ElementSpec(messAttrs, ElementSpec.StartTagType);
        messSpecs.add(messStart);

        // Horodatage
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String timeStamp = simpleDateFormat.format(new Date()); // convert date to string

        SimpleAttributeSet hourSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(hourSet, FontFactory.getSize());
        StyleConstants.setBold(hourSet, true);
        makeTag("[" + timeStamp + "]", HOUR, hourSet);
    }

    // TODO: ne pas faire se propager l'alignement: attribut non mutable + création d'un mutable clone aligné

    public void insertId(String id, MutableAttributeSet styles) {
        StyleConstants.setAlignment(styles, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setFontSize(styles, FontFactory.getSize());
        makeTag(id, ID, styles);
        bodyAttr = new SimpleAttributeSet();
        startMessageBody(bodyAttr);
    }

    public void endMessage() {
        endMessageBody(bodyAttr);
        ElementSpec messEnd = new ElementSpec(messAttrs, ElementSpec.EndTagType);
        messSpecs.add(messEnd); //end table tag

        ElementSpec[] spec = new ElementSpec[messSpecs.size()];
        messSpecs.toArray(spec);
        try {
            insert(offset, spec);
        } catch (BadLocationException e) {
            ExceptionHandler.handleTextException(e);
        }
    }

    private int offset;
    private MutableAttributeSet messAttrs;
    private MutableAttributeSet bodyAttr;

    public void applyPostOperations(String originalString) {
        parseForURL(originalString);
    }

    public void insertContentString(String contentString, AttributeSet style) {
        contentString += "\n";
        ElementSpec tagContentSpec = new ElementSpec(style, ElementSpec.ContentType, contentString.toCharArray(), 0, contentString.length());
        messSpecs.add(tagContentSpec);
    }

    public void append(String ircString) {
        startMessage();
        insertId(ircString.split(" ")[0], new SimpleAttributeSet());

        // Contenu
        IRCStringConverter.insertIntoStyledDocument(this, ircString.substring(ircString.indexOf(" ")));

        endMessage();
        applyPostOperations(ircString);
    }


    /**
     * Transforme toutes les urls en liens cliquables aprés insertion.
     *
     * @param ircString La chaine originale
     */
    private void parseForURL(String ircString) {
        matchURLPattern("(http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+@&#/%=~_|])", ircString, "");
        matchURLPattern("[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+", ircString, "mailto:");
        matchURLPattern("(?<!http://)(www[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+@&#/%=~_|])", ircString, "http://");
    }

    /**
     * Ajoute les attributs stylisé aux url.
     *
     * @param pattern   Permet de détecter l'url.
     * @param ircString La derniére chaine insérée.
     * @param prefix    Un préfixe é ajouter é l'url.
     */
    private void matchURLPattern(String pattern, String ircString, String prefix) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(ircString);

        while (m.find()) {
            // On cherche l'url.
            String url = ircString.substring(m.start(), m.end());
            int start = getLength() - ircString.length() + m.start() - 1;
            int end = getLength() - ircString.length() + m.end() - 1;

            // On créé les attributs.
            SimpleAttributeSet hrefAttr = new SimpleAttributeSet();
            hrefAttr.addAttribute(HTML.Attribute.HREF, prefix + url);
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            attrs.addAttribute(HTML.Tag.A, hrefAttr);
            attrs.addAttribute(HTML.Attribute.HREF, prefix + url);
            StyleConstants.setUnderline(attrs, true);
            StyleConstants.setForeground(attrs, Color.BLUE);

            // On ajoute les attributs.
            setCharacterAttributes(start, end - start, attrs, false);
        }
    }

    private void startMessageBody(MutableAttributeSet bodyAttrs) {
        bodyAttrs.addAttribute(DefaultStyledDocument.ElementNameAttribute, M_CONTENT);
        ElementSpec tagStart = new ElementSpec(bodyAttrs, ElementSpec.StartTagType);
        messSpecs.add(tagStart);
    }

    private void insertContentChar(char c, AttributeSet attr) {
        ElementSpec tagContentSpec = new ElementSpec(attr, ElementSpec.ContentType, new char[]{c}, 0, 1);
        messSpecs.add(tagContentSpec);
    }

    private void endMessageBody(AttributeSet bodyAttrs) {
        ElementSpec tagEnd = new ElementSpec(bodyAttrs, ElementSpec.EndTagType);
        messSpecs.add(tagEnd);
    }

    private void makeTag(String tagContent, String tagName, MutableAttributeSet tagAttrs) {
        tagAttrs.addAttribute(DefaultStyledDocument.ElementNameAttribute, tagName);
        ElementSpec tagStart = new ElementSpec(tagAttrs, ElementSpec.StartTagType);
        messSpecs.add(tagStart);

        tagContent += '\n'; // Pour pouvoir selectionner jusqu'é la fin

        DefaultStyledDocument.ElementSpec tagContentSpec = new DefaultStyledDocument.ElementSpec(new SimpleAttributeSet(), DefaultStyledDocument.ElementSpec.ContentType, tagContent.toCharArray(), 0, tagContent.length());
        messSpecs.add(tagContentSpec);

        DefaultStyledDocument.ElementSpec tagEnd = new DefaultStyledDocument.ElementSpec(tagAttrs, DefaultStyledDocument.ElementSpec.EndTagType);
        messSpecs.add(tagEnd);
    }

}
