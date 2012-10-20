package org.fantasme.chatirc.view.space;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.fantasme.chatirc.controller.IRCSpaceController;
import org.fantasme.chatirc.font.FontFactory;
import org.fantasme.chatirc.model.space.IRCSpaceModel;
import org.fantasme.chatirc.view.space.components.InputPanel;
import org.fantasme.chatirc.view.space.components.RoundScrollPane;
import org.fantasme.chatirc.view.text.MultiColumnTextPane;


/**
 * Implémentation partielle d'une vue d'espace IRC.
 */
public class AbstractIRCSpaceView extends JPanel implements IRCSpaceView {
    /**
     * Le panneau central d'affichage.
     */
    private MultiColumnTextPane textPane;

    /**
     * Le paneau central scrollable.
     */
    private JScrollPane scrollPane;

    /**
     * Le modéle d'espace sous-jacent.
     */
    private final IRCSpaceModel spaceModel;

    /**
     * Le panneau des entrées utilisateur.
     */
    private InputPanel inputPanel;

    /**
     * Apparait lors d'un clic droit sur un lien hypertexte
     */
    private JPopupMenu hyperLinkMenu;

    /**
     * Permet de copier le lien hypertexte.
     */
    private JMenuItem copyLink;

    protected AbstractIRCSpaceView(IRCSpaceModel model) {
        this.spaceModel = model;
        createView();
        placeComponents();
        createController();
    }

    /**
     * Crée la vue.
     */
    protected void createView() {
        textPane = new MultiColumnTextPane();
        textPane.setDocument(spaceModel.getDocument());
        textPane.setEditable(false);
        FontFactory.registerStyledDocument(spaceModel.getDocument());

        scrollPane = new RoundScrollPane(textPane);
        inputPanel = new InputPanel(spaceModel);

        hyperLinkMenu = new JPopupMenu();
        copyLink = new JMenuItem("Copier");
        hyperLinkMenu.add(copyLink);

    }

    /**
     * Place les composants.
     */
    protected void placeComponents() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        Dimension hori = new Dimension(5, 1);
        Dimension vert = new Dimension(1, 5);

        add(Box.createRigidArea(hori));

        Box b = Box.createVerticalBox(); {
            b.add(Box.createRigidArea(vert));
            b.add(scrollPane);
            b.add(Box.createRigidArea(vert));
            b.add(inputPanel);
            b.add(Box.createRigidArea(vert));
        }
        add(b);
        add(Box.createRigidArea(hori));
    }

    /**
     * Crée le controlleur.
     */
    protected void createController() {
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                inputPanel.requestFocusInWindow();
            }

            public void focusLost(FocusEvent e) { }
        });

        // Desactive ou active l'autoscroll.
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            // TODO : Faire mieux que le kickin, car les messages serveurs sont distribués totalement aléatoirement.
            private int adjustmentCount = 0; // On ignore les premiers ajustements.
            private final int adjustmentBeforeKickin = 10; // Avant de commencer é desactiver l'autoscroll on attend ce nombre d'ajustements.

            public void adjustmentValueChanged(AdjustmentEvent e) {
                adjustmentCount += 1;
                JScrollBar bar = scrollPane.getVerticalScrollBar();
                // Si la valeur de la bar est assez haute, sactiver l'autoscroll.
                if ((bar.getValue() + bar.getVisibleAmount()) >= 0.90 * bar.getMaximum() || adjustmentCount < adjustmentBeforeKickin) {
                    textPane.setCaretPosition(textPane.getDocument().getLength());
                    textPane.setAutoscrollDisabled(false);
                } else { // Si la barre est trop haute, désactiver l'autoscroll.
                    if (!textPane.isAutoscrollDisabled()) {
                        textPane.setAutoscrollDisabled(true);
                    }
                }
            }
        });

        textPane.addMouseMotionListener(new IRCSpaceController(spaceModel));

        // Le surveillant des urls.
        textPane.addHyperlinkListener(new HyperlinkListener() {
            String currentURL = "";
            private final ActionListener copyAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    StringSelection urlToCopy = new StringSelection(currentURL);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(urlToCopy, urlToCopy);
                    copyLink.removeActionListener(this);
                }
            };

            private final MouseAdapter mouseAdapter = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        copyLink.addActionListener(copyAction);
                        hyperLinkMenu.show(textPane, e.getX(), e.getY());
                    }
                }
            };

            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException e1) {
                        //
                    } catch (URISyntaxException e1) {
                        //
                    }
                } else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) { // On attend clic droit
                    currentURL = e.getURL().toString();
                    textPane.addMouseListener(mouseAdapter);
                    textPane.setToolTipText(currentURL);

                } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) { // On n'attend plus
                    textPane.removeMouseListener(mouseAdapter);
                    textPane.setToolTipText(null);
                }

            }

        });

    }

    public String getName() {
        return spaceModel.getName();
    }

    public IRCSpaceModel getSpaceModel() {
        return spaceModel;
    }

    public JScrollPane getCenterPane() {
        return scrollPane;
    }

    public InputPanel getInputPanel() {
        return inputPanel;
    }

    public JTextField getInputField() {
        return inputPanel.getInputField();
    }
}
