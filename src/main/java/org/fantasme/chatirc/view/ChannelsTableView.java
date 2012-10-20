package org.fantasme.chatirc.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import org.fantasme.chatirc.model.ChannelsTableModel;
import org.fantasme.chatirc.view.util.HTMLTableCellRenderer;

/**
 * La vue des canaux visitables sur le serveur.
 */
public class ChannelsTableView {
    /**
     * La table représentant les canaux.
     */
    private JTable channels;

    /**
     * Modéle sous-jacent.
     */
    private ChannelsTableModel model;

    /**
     * La vue de la table.
     */
    private JFrame view;

    public ChannelsTableView() {
        createModel();
        createView();
        placeComponent();
        createController();
    }

    /**
     * Crée le modéle.
     */
    private void createModel() {
        model = new ChannelsTableModel();
    }

    /**
     * Crée la vue.
     */
    private void createView() {
        view = new JFrame("Liste des canaux du réseau");
        final TableCellRenderer renderer = new HTMLTableCellRenderer();
        channels = new JTable(model) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 2) {
                    return renderer;
                }
                return super.getCellRenderer(row, column);
            }
        };
        channels.setShowGrid(false);
        channels.setAutoCreateRowSorter(true);
        channels.getColumnModel().getColumn(2).setPreferredWidth(3500);

        channels.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    /**
     * Place les composants.
     */
    private void placeComponent() {
        view.setLayout(new BorderLayout());
        JScrollPane channelsPane = new JScrollPane(channels);
        channelsPane.getVerticalScrollBar().setUnitIncrement(18);
        view.add(channelsPane, BorderLayout.CENTER);
    }

    /**
     * Crée le controlleur.
     */
    private void createController() {
        model.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                channels.scrollRectToVisible(channels.getCellRect(e.getLastRow(), 0, true));
            }
        });
    }

    /**
     * Affiche la vue dans une nouvelle fenétre.
     */
    public void display() {
        view.pack();
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        view.toFront();
        view.requestFocus();
        view.requestFocusInWindow();
    }

    public ChannelsTableModel getModel() {
        return model;
    }

    public JTable getTable() {
        return channels;
    }
}
