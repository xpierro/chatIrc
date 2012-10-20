package org.fantasme.chatirc.view.space.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Onglets personnalisés.
 */
public class TabComponent extends JPanel {
    private JTabbedPane pane;
    private JLabel label;
    private boolean hideCross;

    public TabComponent(final JTabbedPane pane, boolean hideCross) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("Le JTabbedPane est nul.");
        }
        this.hideCross = hideCross;
        this.pane = pane;
        setOpaque(false);

        label = new JLabel() {
            // Le JLabel reéoit son contenu du JTabbedPane.
            public String getText() {
                int i = pane.indexOfTabComponent(TabComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };

        add(label);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        if (!hideCross) {
            // On affiche la crois.
            JButton button = new TabButton();
            add(button);
        }
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    /**
     * Fixe la couleur du texte.
     *
     * @param c La nouvelle couleur.
     */
    public void setForeground(Color c) {
        if (label != null) {
            label.setForeground(c);
        } else {
            super.setForeground(c);
        }
    }

    /**
     * Retourne la couleur du texte.
     *
     * @return La couleur du texte.
     */
    public Color getForeground() {
        if (label != null) {
            return label.getForeground();
        } else {
            return super.getForeground();
        }
    }

    /**
     * Met é jour l'affichage.
     */
    public void update() {
        label.revalidate();
    }

    /**
     * Le boutton contenant la croix.
     */
    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            // Mise en place du L&F
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setUI(new BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            // Mise en place du controlleur.
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(TabComponent.this);
            if (i != -1) {
                pane.remove(i);
            }
        }

        /**
         * Met é jour l'interface graphique pour ce boutton, ici ne fait rien.
         */
        public void updateUI() {
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!hideCross) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.BLACK);
                if (getModel().isRollover()) {
                    g2.setColor(Color.MAGENTA);
                }
                int delta = 5;
                int t = 4;
                g2.drawLine(delta + t, delta, getWidth() - delta - 1 + t, getHeight() - delta - 1);
                g2.drawLine(getWidth() - delta - 1 + t, delta, delta + t, getHeight() - delta - 1);
                g2.dispose();
            }
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}
