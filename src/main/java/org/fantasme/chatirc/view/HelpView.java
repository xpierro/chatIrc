package org.fantasme.chatirc.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import org.fantasme.chatirc.model.HelpTableModel;

/**
 * La base de connaissance.
 */
public class HelpView {
    /**
     * La carte des commandes utilisateur.
     */
    private Map<String, String> userCommands;

    /**
     * La carte des messages utilisateur.
     */
    private Map<String, String> userMessages;

    /**
     * La carte des modes utilisateur.
     */
    private Map<String, String> userModes;

    /**
     * La carte des informations serveur.
     */
    private Map<String, String> servInfos;

    /**
     * La carte des informations diverses.
     */
    private Map<String, String> misc;

    /**
     * La fenétre principale.
     */
    private JFrame helpFrame;

    /**
     * Le panneau des onglets.
     */
    private JTabbedPane helpTabs;

    public HelpView() {
        super();
        createModel();
        createView();
        placeComponents();
        createController();
    }

    /**
     * Crée le modéle.
     */
    private void createModel() {

        userCommands = new TreeMap<String, String>();
        userCommands.put("/join [#]<canal>", "Joindre le canal <canal>");
        userCommands.put("/part", "Quitte le canal courant");
        userCommands.put("/quit", "Coupe la connexion avec le serveur");
        userCommands.put("/nick <nouveau pseudo>", "Modifie le pseudo courant");
        userCommands.put("/ns <commandes>", "Pour toute intervention avec nickserv s'il existe sur le serveur");
        userCommands.put("/rejoin", "Rejoindre le canal courant");

        userMessages = new TreeMap<String, String>();
        userMessages.put("/me <message>", "Montrer aux autres ce que vous faites");
        userMessages.put("/msg <canal> <message>", "Envoyer le message <message> dans le canal <canal> si vous en avez le droit");
        userMessages.put("/msg <pseudo> <message>", "Envoyer le message <message> à <pseudo> dans une fenêtre privée");
        userMessages.put("/notice <pseudo> <message>", "Envoyer la notification <message> à <pseudo> ");

        userModes = new TreeMap<String, String>();
        userModes.put("/kick #<canal> <pseudo> [<raison>]", "Ejecter la personne <pseudo> du canal <canal>");
        userModes.put("/ban <pseudo> [<raison>]", "Bannir la personne <pseudo> du canal courant");
        userModes.put("/unban <pseudo> [<raison>]", "Debannir la personne <pseudo> du canal courant");
        userModes.put("/op [<pseudo>]", "Obtenir ou donner les droits d'opérateur à <pseudo> sur le canal courant");
        userModes.put("/deop [<pseudo>]", "Perdre ou enlever les droits d'opérateur");
        userModes.put("/voice [<pseudo>]", "Obtenir ou donner le droit de parler à <pseudo> sur le canal courant");
        userModes.put("/devoice [<pseudo>]", "Perdre ou enlever les droits de parler");

        servInfos = new TreeMap<String, String>();
        servInfos.put(
                "/charset <charset>",
                "Modifier son jeu de caractéres (utf8, iso-85591, windows-1252, etc.)"
        );
        servInfos.put("/topic #<canal> [<topic>]", "Afficher ou modifier le topic du canal <canal>");
        servInfos.put("/list", "Lister les canaux visibles du serveur courant");
        servInfos.put("/whois <pseudo>", "Obtenir des informations sur la personne <pseudo>");

        misc = new TreeMap<String, String>();
        misc.put("CTRL + Molette sur une fenétre de discussion", "Augmente ou diminue la taille de la police");
        misc.put("Clic droit sur le champ de saisie", "Permet de personnaliser le formatage du texte");
        misc.put("Double clic sur le nom d'un canal dans la liste des canaux", "Rejoint le canal selectionné");
    }

    /**
     * Crée la vue.
     */
    private void createView() {
        helpFrame = new JFrame("Aide utilisateur");
        helpTabs = new JTabbedPane();

        helpTabs.add(
                "Actions utilisateurs",
                new JScrollPane(
                        fillTable(
                                userCommands,
                                new String[]{"Syntaxe", "Description"}
                        ),
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                )
        );
        helpTabs.add(
                "Messages IRC",
                new JScrollPane(
                        fillTable(
                                userMessages,
                                new String[]{"Syntaxe", "Description"}
                        ),
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                )
        );
        helpTabs.add(
                "Droits utilisateurs",
                new JScrollPane(
                        fillTable(
                                userModes,
                                new String[]{"Syntaxe", "Description"}
                        ),
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                )
        );
        helpTabs.add(
                "Informations serveur/utilisateurs",
                new JScrollPane(
                        fillTable(
                                servInfos,
                                new String[]{"Syntaxe", "Description"}
                        ),
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                )
        );

        helpTabs.add(
                "Informations utiles",
                new JScrollPane(
                        fillTable(
                                misc,
                                new String[]{"Action", "Résultat"}
                        ),
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                )
        );
    }

    /**
     * Crée une table correspondant é une carte.
     *
     * @param map      La carte représentant les données de la table.
     * @param colNames Le nom des colonnes de la table.
     * @return La table rempli avec les données de la carte.
     */
    private JTable fillTable(Map<String, String> map, String[] colNames) {
        HelpTableModel model = new HelpTableModel();
        for (String syntax : map.keySet()) {
            model.addRow(syntax, map.get(syntax));
        }

        JTable t = new JTable(model);

        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        t.getColumnModel().getColumn(0).setHeaderValue(colNames[0]);
        t.getColumnModel().getColumn(1).setHeaderValue(colNames[1]);
        t.getTableHeader().setVisible(true);

        return t;
    }

    /**
     * Place les composants.
     */
    private void placeComponents() {
        helpFrame.setLayout(new BorderLayout());
        helpFrame.add(helpTabs, BorderLayout.CENTER);
    }

    /**
     * Crée le controlleur.
     */
    private void createController() {
        helpTabs.addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                // Trouve la table courante
                resizeCurrentTable();
            }
        });

        helpTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                resizeCurrentTable();
            }
        });

        for (Component comp : helpTabs.getComponents()) {
            final JScrollPane scrollPane = (JScrollPane) comp;
            final JTable table = (JTable) scrollPane.getViewport().getView();
            table.getTableHeader().setResizingAllowed(false);
        }
    }

    /**
     * Change la taille des colonnes de la table selectionnée afin qu'elle ne soit pas trop grande pour le scrollPane.
     */
    private void resizeCurrentTable() {
        JScrollPane scrollPane = (JScrollPane) helpTabs.getSelectedComponent();
        JTable table = (JTable) scrollPane.getViewport().getView();
        TableColumn c0 = table.getColumnModel().getColumn(0);
        TableColumn c1 = table.getColumnModel().getColumn(1);

        // On calcule la taille minimale dont la premiere colonne a besoin
        int minWidth = 0;
        for (int row = 0; row < table.getRowCount(); row++) {
            Component comp = table.getCellRenderer(row, 0).getTableCellRendererComponent(table, table.getValueAt(row, 0), false, false, row, 0);
            comp.setSize(c0.getWidth(), table.getRowHeight(row));
            if (minWidth < comp.getPreferredSize().width) {
                minWidth = comp.getPreferredSize().width;
            }
        }

        c0.setPreferredWidth(minWidth + 15);
        c1.setPreferredWidth(scrollPane.getSize().width - c0.getPreferredWidth());
    }

    /**
     * Affiche la base de connaissance.
     */
    public void display() {
        helpFrame.setPreferredSize(new Dimension(800, 600));
        helpFrame.pack();
        helpFrame.setLocationRelativeTo(null);
        helpFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        helpFrame.setVisible(true);
        helpFrame.toFront();
        helpFrame.requestFocus();
        helpFrame.requestFocusInWindow();
    }

    /**
     * Installe un écouteur de clic sur toutes les JTable
     *
     * @param l L'écouteur de clic é ajouter.
     */
    public void addMouseListener(MouseListener l) {
        for (Component c : helpTabs.getComponents()) {
            JTable t = (JTable) ((JScrollPane) c).getViewport().getView();
            t.addMouseListener(l);
        }
    }

    /**
     * Supprime un écouteur de clic de toutes les JTable.
     *
     * @param l L'écouteur de clic é supprimer.
     */
    public void removeMouseListener(MouseListener l) {
        for (Component c : helpTabs.getComponents()) {
            JTable t = (JTable) ((JScrollPane) c).getViewport().getView();
            t.removeMouseListener(l);
        }
    }

}
