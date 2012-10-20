package org.fantasme.chatirc.view.text;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;
import org.fantasme.chatirc.error.ExceptionHandler;
import org.fantasme.chatirc.font.FontFactory;
import org.fantasme.chatirc.view.space.components.BackgroundTextPane;

/**
 * Panneau de texte à colonnes multiples.
 */
public class MultiColumnTextPane extends BackgroundTextPane {
    private static final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * La position en pixel de la limite inférieure de la barre.
     */
    private int minBarPos;

    /**
     * Le pourcentage de la taille du JTextPane apres lequel on arrête le déplacement latéral vers la droite de la barre.
     */
    private static final double MAX_BAR_POS_FACTOR = 0.75;

    /**
     * La position de la barre.
     */
    private int barPos;

    /**
     * Vrai si la selection est en cours.
     */
    private boolean selecting;

    /**
     * Le menu affiché aprés un clic droit sur la sélection.
     */
    private JPopupMenu selectionMenu;

    /**
     * L'objet du menu qui demande la copie si cliqué.
     */
    JMenuItem copySelection;

    /**
     * Verouille l'autoscroll si vrai.
     */
    private boolean disableAutoScroll;

    /**
     * Vrai si la barre est en déplacement.
     */
    private boolean onLine;

    public MultiColumnTextPane() {
        setMargin(new Insets(0, 0, 0, 0));
        barPos = 150;
        setEditable(false);

        selecting = false;
        disableAutoScroll = false;

        setEditorKit(new StyledEditorKit() {
            public ViewFactory getViewFactory() {
                return new MultiColumnViewFactory(MultiColumnTextPane.this);
            }
        });

        setCaret(new DefaultCaret());

        createView();
        createController();
    }

    /**
     * Crée la vue.
     */
    private void createView() {
        selectionMenu = new JPopupMenu();
        copySelection = new JMenuItem("Copier");
        selectionMenu.add(copySelection);
    }

    /**
     * Crée le controlleur.
     */
    private void createController() {
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                if (mouseWheelEvent.isControlDown()) {
                    int clicksToward = mouseWheelEvent.getWheelRotation();
                    FontFactory.increaseSize(-clicksToward);
                    int documentPosition = viewToModel(new Point(barPos, 0));
                    updateUI();
                    try {
                        Rectangle barRect = MultiColumnTextPane.this.modelToView(documentPosition);
                        //barPos = barRect.x;
                        updateUI();
                    } catch (BadLocationException e) {
                        ExceptionHandler.handleTextException(e);
                    }

                } else {
                    getParent().dispatchEvent(mouseWheelEvent);
                }
            }
        });


        addMouseMotionListener(new MouseAdapter() {
            private boolean outed = false;

            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();

                if (mouseX > barPos - 5 && mouseX < barPos + 5) {
                    setCursor(handCursor);
                    outed = false;
                } else {
                    if (!outed) {
                        setCursor(defaultCursor);
                        outed = true;
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            private MouseAdapter barMotionListener = new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (onLine) {
                        int mouseX = e.getX();
                        if (mouseX > minBarPos && mouseX < getWidth() * MAX_BAR_POS_FACTOR) {
                            barPos = mouseX;
                            updateUI();
                        } else if (mouseX < minBarPos) {
                            barPos = minBarPos;
                            updateUI();
                        }
                        selecting = false;
                    }
                }
            };

            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                if (mouseX > barPos - 5 && mouseX < barPos + 5) {
                    onLine = true;
                    selecting = false;
                    addMouseMotionListener(barMotionListener);
                }
            }

            public void mouseReleased(MouseEvent e) {
                onLine = false;
                removeMouseMotionListener(barMotionListener);
            }
        });

        addMouseListener(new MouseAdapter() {
            int selectionBegin = 0;
            int selectionEnd = 0;

            public void mouseDragged(MouseEvent e) {
                selectionEnd = getSelectionEnd();
                select(selectionBegin, selectionEnd);
            }

            public void mousePressed(MouseEvent e) {
                if (!onLine) {
                    selectionBegin = getSelectionStart();
                    selectionEnd = selectionBegin;
                    selecting = true;
                    addMouseMotionListener(this);
                }
            }

            public void mouseReleased(MouseEvent e) {
                selecting = false;
                removeMouseMotionListener(this);
            }

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && getSelectedText() != null) {
                    selectionMenu.show(MultiColumnTextPane.this, e.getX(), e.getY());
                }
            }
        });

        // Surveille la selection du texte.
        copySelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringSelection selectedText = new StringSelection(getSelectedText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selectedText, selectedText);
            }
        });

    }

    /**
     * Scroll desactivable.
     * @param rec Le rectange é rendre visible.
     */
    public void scrollRectToVisible(Rectangle rec) {
        if (!disableAutoScroll) {
            // Si un texte est surligné, ne pas repositionner le caret
            if (!selecting) {
                super.scrollRectToVisible(rec);
                setCaretPosition(getDocument().getLength());
            }
        }
    }

    /**
     * Renvoie la position courante de la barre.
     * @return La position de la barre sur le panneau en pixel.
     */
    public int getBarPos() {
        return barPos;
    }

    /**
     * Change la position courante de la barre.
     * @param barPos La nouvelle position de la barre.
     */
    public void setBarPos(int barPos) {
        this.barPos = barPos;
    }

    public void setMinBarPos(int minPos) {
        minBarPos = minPos;
    }

    /**
     * Peint la barre par dessus le panneau.
     * @param g Le pinceau.
     */
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.gray);
        g.drawLine(barPos + getInsets().left, 0, barPos + getInsets().left, getHeight() );
    }

    /**
     * Desactive ou réactive l'autoscroll.
     * @param b Si vrai, desactive l'autoscroll, si faut le réactive.
     */
    public void setAutoscrollDisabled(boolean b) {
        disableAutoScroll = b;
    }

    /**
     * Indique si l'autoscroll est désactivé.
     * @return L'état de l'autoscroll.
     */
    public boolean isAutoscrollDisabled() {
        return disableAutoScroll;
    }
}
