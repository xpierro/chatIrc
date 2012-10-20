package org.fantasme.chatirc.view.util;

import java.awt.Component;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.fantasme.chatirc.model.protocol.User;

/**
 * Affiche un texte stylisé si la souris est sur l'objet et un texte normal sinon.
 */
public class HTMLListCellRenderer extends JLabel implements ListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        User u = (User) value;
        Point p = list.getMousePosition(true);
        if (p != null && index < list.getModel().getSize() && list.locationToIndex(p) == index && list.getCellBounds(index, index).contains(p)) {
            setText("<html><body><i>&nbsp;" + u.getPlainNick() + "</i></body></html>");
        } else {
            setText("<html><body>&nbsp;" + u.getPlainNick() + "</body></html>");
        }
        return this;
    }
}
