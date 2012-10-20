package org.fantasme.chatirc.view.space.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

/**
 * Un JScrollPane é bordure arrondie.
 */
public class RoundScrollPane extends JScrollPane {
    /**
     * @param view La vue é intégrer dans le JScrollPane.
     */
    public RoundScrollPane(Component view) {
        super(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        createView();
    }

    /**
     * Crée la vue.
     */
    protected void createView() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
    }

    /**
     * Paint la bordure arrondie.
     *
     * @param g Le pinceau.
     */
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        // On dessine un rectangle rond.
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, width, height, 10, 10);
        g.setColor(Color.GRAY);
        g.drawRoundRect(0, 0, width - 1, height - 1, 10, 10);

        super.paintComponent(g);
    }
}
