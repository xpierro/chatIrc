package org.fantasme.chatirc.view.space.util;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.fantasme.chatirc.model.protocol.User;
import org.fantasme.chatirc.model.space.UsersListModel;
import org.fantasme.chatirc.view.util.HTMLListCellRenderer;

/**
 * La vue de la liste d'utilisateur.
 */
public class UsersListView extends JPanel {
    /**
     * Le modèle associé.
     */
    private final UsersListModel model;

    /**
     * La liste a qui l'ont délègue l'affichage du modèle.
     */
    private JList list;

    /**
     * Le label affichant les informations sur le nombre des utilisateurs.
     */
    private JLabel countLabel;

    /**
     * Le curseur en "main".
     */
    private static final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

    /**
     * Le curseur normal (fléche).
     */
    private static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * @param usersListModel Le modéle dont on veut afficher les informations.
     */
    public UsersListView(UsersListModel usersListModel) {
        model = usersListModel;
        createView();
        placeComponents();
        createController();
    }

    /**
     * Crée la vue.
     */
    private void createView() {
        list = new JList(model);
        list.setCellRenderer(new HTMLListCellRenderer());

        countLabel = new JLabel("");
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Place les composants.
     */
    private void placeComponents() {
        setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridLayout(1, 0));
        {
            p.add(list);
            add(p, BorderLayout.CENTER);
        }
        JPanel q = new JPanel(new GridLayout(1, 0));
        {
            q.add(countLabel);
        }
        add(q, BorderLayout.NORTH);
    }

    /**
     * Crée le controlleur.
     */
    private void createController() {
        model.addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
                recount();
            }

            public void intervalRemoved(ListDataEvent e) {
                recount();
            }

            public void contentsChanged(ListDataEvent e) {
                recount();
            }
        });

        list.addMouseListener(new MouseAdapter() {
            private final MouseAdapter motionAdapter = new MouseAdapter() {
                public void mouseMoved(MouseEvent e) {
                    Point p = list.getMousePosition(true);
                    if (p != null) {
                        int index = list.locationToIndex(p);
                        if (p != null && list.locationToIndex(p) == index && list.getCellBounds(index, index).contains(p)) {
                            list.setCursor(handCursor);
                        } else {
                            list.setCursor(defaultCursor);
                        }
                    }
                    list.repaint();
                }
            };

            public void mouseEntered(MouseEvent e) {
                list.addMouseMotionListener(motionAdapter);
            }

            public void mouseExited(MouseEvent e) {
                list.removeMouseMotionListener(motionAdapter);
                list.repaint();
            }

        });
    }

    /**
     * Recompte le nombre d'utilisateurs.
     */
    private void recount() {
        int ops = 0;
        int total = 0;
        for (int i = 0; i < model.getSize(); i++) {
            User u = model.getElementAt(i);
            int comp = u.getStatus().compareTo(User.Status.OPERATOR);
            if (comp <= 0) {
                ops += 1;
            }
            total += 1;
        }
        countLabel.setText(ops + " op" + (ops > 1 ? "s " : "") + ", " + total + " total");
    }

    public JList getList() {
        return list;
    }
}
