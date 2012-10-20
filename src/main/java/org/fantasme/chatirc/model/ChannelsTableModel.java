package org.fantasme.chatirc.model;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * Le modèle de la table des canaux du serveur.
 */
public class ChannelsTableModel extends AbstractTableModel implements TableModel {
    /**
     * Les données de la table.
     */
    private final List<List<Object>> data;

    public ChannelsTableModel() {
        super();
        data = new LinkedList<List<Object>>();
    }

    public Class<?> getColumnClass(int index) {
        switch (index) {
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return String.class;
            default:
                throw new IllegalArgumentException("Index invalide.");
        }
    }

    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(int index) {
        switch (index) {
            case 0:
                return "Nom du canal";
            case 1:
                return "Utilisateurs";
            case 2:
                return "Sujet";
            default:
                throw new IllegalArgumentException("Index invalide.");
        }

    }

    public int getRowCount() {
        return data.size();
    }

    public Object getValueAt(int row, int column) {
        if (row < 0 || row >= data.size()) {
            throw new IllegalArgumentException("Ligne invalide");
        }

        if (column < 0 || column >= getColumnCount()) {
            throw new IllegalArgumentException("Colonne invalide");
        }
        return data.get(row).get(column);
    }

    public boolean isCellEditable(int row, int column) {
        if (row < 0 || row >= getRowCount()
                || column < 0 || column >= getColumnCount()) {
            throw new IllegalArgumentException("Numéro invalide");
        }
        return false;
    }

    public void addRow(String name, String users, String topic) {
        insertRow(name, users, topic, data.size());
    }

    public void clearRows() {
        data.clear();
        fireTableDataChanged();
    }

    public void insertRow(String name, String users, String topic, int index) {
        if (name == null || users == null || topic == null) {
            throw new IllegalArgumentException("Ligne nulle");
        }

        List<Object> line = new LinkedList<Object>();
        line.add(name);
        line.add(Integer.decode(users));
        line.add(topic);

        data.add(index, line);
        fireTableRowsInserted(index, index);
    }
}
