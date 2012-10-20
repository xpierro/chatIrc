package org.fantasme.chatirc.view.util;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.fantasme.chatirc.font.FontFactory;

/**
 * Affiche le contenu d'une cellule en HTML et sans saut é la ligne.
 */
public class HTMLTableCellRenderer extends JLabel implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setFont(FontFactory.getFont());
        setText("<html><nobr>" + table.getValueAt(row, column) + "</nobr></html>");
        return this;
    }
}
