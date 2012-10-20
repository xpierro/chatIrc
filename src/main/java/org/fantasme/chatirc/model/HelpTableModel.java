package org.fantasme.chatirc.model;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * Le modèle de la table de l'aide.
 */
public class HelpTableModel extends AbstractTableModel implements TableModel {
    /**
     * Les données de la table.
     */
    private final List<List<Object>> data;

    public HelpTableModel() {
        super();
        data = new LinkedList<List<Object>>();
    }

    public Class<?> getColumnClass(int index) {
        switch (index) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            default:
                throw new IllegalArgumentException("Index invalide.");
        }
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int index) {
        switch (index) {
            case 0:
                return "Syntaxe";
            case 1:
                return "Description";
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

    public void addRow(String syntax, String description) {
        insertRow(syntax, description, data.size());
    }

    public void insertRow(String syntax, String description, int index) {
        if (syntax == null || description == null) {
            throw new IllegalArgumentException("Ligne nulle");
        }

        List<Object> line = new LinkedList<Object>();
        line.add(syntax);
        line.add(description);

        data.add(index, line);
        fireTableRowsInserted(index, index);
    }
}

