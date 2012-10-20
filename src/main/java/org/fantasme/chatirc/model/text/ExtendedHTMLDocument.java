package org.fantasme.chatirc.model.text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import org.fantasme.chatirc.model.protocol.IRCStringConverter;

/**
 * Document stylisé utilisant l'HTML pour structurer les données.
 * Notes d'évolutivité :
 * Pour traiter un nouveau code de style IRC:
 * l'ajouter é l'énumération IRCTag avec les balises ouvrantes et fermantes
 * HTML si besoin, et ajouter un case au branchement conditionnel de
 * parseIRCString.
 * Pour ajouter une nouvelle couleur :
 * ajouter sa description é l'énumération IRCColor.
 */
public class ExtendedHTMLDocument extends HTMLDocument {
    public ExtendedHTMLDocument() {
        super();
    }

    /**
     * Ajoute une chaine au document.
     *
     * @param ircString La chaine formattée pour l'irc.
     */
    public void append(String ircString) {
        this.fireChangedUpdate(new DefaultDocumentEvent(0, 0, DocumentEvent.EventType.CHANGE));
        Element body =
                findElement(getRootElements()[0], HTML.Tag.P.toString());
        try {
            ircString = ircString.replace("&", "&amp;")
                    .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
                    .replace("  ", " &nbsp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
            String htmlString = IRCStringConverter.toHTML(ircString);
            // On va chercher toutes les urls de la ligne
            htmlString = parseForURL(htmlString);

            // Horodatage
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String timeStamp = simpleDateFormat.format(new Date()); // convert date to string

            insertBeforeEnd(body, "<b>[" + timeStamp + "] </b>" + htmlString + "<br>");
        } catch (BadLocationException e) {
            //
        } catch (IOException e) {
            // 
        }
    }

    /**
     * Transforme toutes les urls en liens cliquables.
     *
     * @param htmlString La chaine originale
     * @return Un chaine ou toutes les urls sont cliquables.
     */
    private String parseForURL(String htmlString) {
        Pattern p = Pattern.compile("(http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+@&#/%=~_|])");
        Matcher m = p.matcher(htmlString);
        while (m.find()) {
            String url = htmlString.substring(m.start(), m.end());
            String target;
            if (url.length() > 30) {
                target = url.substring(0, 16) + "[...]" + url.substring(url.length() - 5);
            } else {
                target = url;
            }
            htmlString = htmlString.replace(url, "<a href=" + url + "/>" + target + "</a>");
        }
        p = Pattern.compile("(?<!http://)(www[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+@&#/%=~_|])");
        m = p.matcher(htmlString);
        while (m.find()) {
            String url = htmlString.substring(m.start(), m.end());
            String target;
            if (url.length() > 30) {
                target = url.substring(0, 16) + "[...]" + url.substring(url.length() - 5);
            } else {
                target = url;
            }
            htmlString = htmlString.replace(url, "<a href=http://" + url + "/>" + target + "</a>");
        }
        htmlString = htmlString.replaceAll("([a-zA-Z0-9]+@.*\\.[a-zA-Z0-9]+)", "<a href=mailto:$1>$1</a>");
        /*
        String rep = htmlString.replaceAll("", "<a href=$1$2>$1$2</a>");
        rep = rep.replaceAll("(http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+@&#/%=~_|)", "<a href=$1$2$3$4>$1$2[...]$4</a>");
        rep = rep.replaceAll("(?<!http://)(www[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+@&#/%=~_|])", "<a href=http://$1>$1</a>");
        rep = rep.replaceAll("([a-zA-Z0-9]+@.*\\.[a-zA-Z0-9]+)", "<a href=mailto:$1>$1</a>");
        return rep;                   */
        return htmlString;
    }

    /**
     * Trouve l'élément HTML dans le Document.
     *
     * @param parent Le noeud parent de l'élément.
     * @param name   Le nom de l'élément.
     * @return L'élément désiré.
     */
    private Element findElement(Element parent, String name) {
        Element foundElement = null;
        Element thisElement;
        int count = parent.getElementCount();
        int i = 0;
        while (i < count && foundElement == null) {
            thisElement = parent.getElement(i);
            if (thisElement.getName().equalsIgnoreCase(name)) {
                foundElement = thisElement;
            } else {
                foundElement = findElement(thisElement, name);
            }
            i++;
        }
        return foundElement;
    }
}
