package org.fantasme.chatirc.view.space.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import org.fantasme.chatirc.error.ExceptionHandler;
import org.fantasme.chatirc.model.protocol.IRCColor;
import org.fantasme.chatirc.model.protocol.IRCTag;
import org.fantasme.chatirc.view.space.util.ColorMenu;

/**
 * Un champ de saisie aux contours arrondis.
 */
public class RoundInputField extends JTextField {
    /**
     * La liste des messages déja tappés.
     */
    private final List<String> history;
    /**
     * La position courante dans l'historique.
     */
    private int historyPos;


    /**
     * Le menu déroulant associé au champ de saisie.
     */
    private JPopupMenu popupMenu;
    /**
     * Le menu de choix des couleurs de texte.
     */
    private ColorMenu frontColorMenu;
    /**
     * Le menu des couleurs de fond.
     */
    private ColorMenu backColorMenu;

    /**
     * La table associative permettant d'acceder é un caractére de controle en fonction de l'objet d'un menu.
     */
    private Map<JMenuItem, Character> controlCharMap;

    /**
     * Le menu de selection basique des couleurs.
     */
    private ColorMenu colorMenu;

    /**
     * Le menu de choix de la couleur de fond par défaut.
     */
    private ColorMenu backDefault;

    /**
     * Le menu de choix de la couleur de texte par défaut.
     */
    private ColorMenu frontDefault;

    /**
     * L'objet de menu permettant de remettre é zéro.
     */
    private JMenuItem resetDefault;

    /**
     * L'objet de menu permettant de copier
     */
    private JMenuItem copy;

    /**
     * L'objet de menu permettant de coller
     */
    private JMenuItem paste;

    /**
     * Le code de la couleur de fond par défaut.
     */
    private String defaultBackCode;

    /**
     * Le code de la couleur de texte par défaut.
     */
    private String defaultFrontCode;

    public RoundInputField() {
        super("");

        history = new LinkedList<String>();
        historyPos = 0;

        defaultBackCode = "";
        defaultFrontCode = "";
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        createView();
        createController();
    }

    /**
     * Crée la vue.
     */
    private void createView() {
        popupMenu = new JPopupMenu();

        copy = new JMenuItem("Copier");
        paste = new JMenuItem("Coller");

        popupMenu.add(copy);
        popupMenu.add(paste);

        popupMenu.add(new JPopupMenu.Separator());

        JMenu selectMenu = new JMenu("Changer la sélection");
        frontColorMenu = new ColorMenu("Couleur du texte");
        backColorMenu = new ColorMenu("Couleur du fond");
        selectMenu.add(frontColorMenu);
        selectMenu.add(backColorMenu);
        popupMenu.add(selectMenu);

        // Les caractéres de contréle
        controlCharMap = new HashMap<JMenuItem, Character>();

        JMenu controlChars = new JMenu("Insérer un caractére de formatage IRC");
        JMenuItem insertColor = new JMenuItem("Couleur");
        controlCharMap.put(insertColor, IRCTag.COLOR.getCode());

        JMenuItem insertBack = new JMenuItem("Fond");

        controlCharMap.put(insertBack, ',');

        colorMenu = new ColorMenu("Code couleur");

        JMenuItem bold = new JMenuItem("Gras");
        controlCharMap.put(bold, IRCTag.BOLD.getCode());

        JMenuItem underlined = new JMenuItem("Souligné");
        controlCharMap.put(underlined, IRCTag.UNDERLINED.getCode());

        JMenuItem invert = new JMenuItem("Inverser couleurs");
        controlCharMap.put(invert, IRCTag.REVERSED.getCode());

        JMenuItem reset = new JMenuItem("Remise é zéro");
        controlCharMap.put(reset, IRCTag.RESET.getCode());

        controlChars.add(insertColor);
        controlChars.add(insertBack);
        controlChars.add(colorMenu);
        controlChars.add(bold);
        controlChars.add(underlined);
        controlChars.add(invert);
        controlChars.add(reset);

        popupMenu.add(controlChars);

        JMenu defaultColors = new JMenu("Changer la couleur par défaut");
        backDefault = new ColorMenu("Couleur de fond");
        frontDefault = new ColorMenu("Couleur du texte");
        resetDefault = new JMenuItem("Remise é zéro");
        defaultColors.add(frontDefault);
        defaultColors.add(backDefault);
        defaultColors.add(resetDefault);
        popupMenu.add(defaultColors);
    }

    /**
     * Crée le controlleur.
     */
    private void createController() {
        frontColorMenu.addItemActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = getText();
                if (text != null && text.length() > 0) {
                    String selectedText = getSelectedText();
                    if (selectedText != null && selectedText.length() > 0) {
                        int start = getSelectionStart();
                        int end = getSelectionEnd();
                        String finalText = text.substring(0, start) + IRCTag.COLOR.getCode() + frontColorMenu.getColor((JMenuItem) e.getSource()).getCode() + selectedText + IRCTag.RESET.getCode() + getDefaultColorString() + text.substring(end);
                        setText(finalText);
                    }
                }
            }
        });

        // Couleur de fond
        backColorMenu.addItemActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = getText();
                if (text != null && text.length() > 0) {
                    String selectedText = getSelectedText();
                    if (selectedText != null && selectedText.length() > 0) {
                        int start = getSelectionStart();
                        int end = getSelectionEnd();
                        String finalText = text.substring(0, start) + IRCTag.COLOR.getCode() + "," + backColorMenu.getColor((JMenuItem) e.getSource()).getCode() + selectedText + IRCTag.RESET.getCode() + getDefaultColorString() + text.substring(end);
                        setText(finalText);
                    }
                }
            }
        });

        // Les couleurs par défaut.

        frontDefault.addItemActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // On cherche é savoir si une couleur de front a déja été appliquée.
                IRCColor front = frontDefault.getColor((JMenuItem) e.getSource());
                if (front == IRCColor.BLACK) {
                    defaultFrontCode = "";
                } else {
                    defaultFrontCode = "" + front.getCode();
                }
            }
        });

        backDefault.addItemActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IRCColor back = backDefault.getColor((JMenuItem) e.getSource());
                if (back == IRCColor.WHITE) {
                    defaultBackCode = "";
                } else {
                    defaultBackCode = "" + back.getCode();
                }
            }
        });

        resetDefault.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                defaultBackCode = "";
                defaultFrontCode = "";
            }
        });

        ActionListener controlCharListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMenuItem source = (JMenuItem) e.getSource();
                Character controlChar = controlCharMap.get(source);
                if (controlChar != null) {
                    int insertAt = getCaretPosition();
                    String inputText = getText();
                    setText(inputText.substring(0, insertAt) + controlChar + inputText.substring(insertAt));
                }
            }
        };

        for (JMenuItem i : controlCharMap.keySet()) {
            i.addActionListener(controlCharListener);
        }

        // Le menu des codes couleurs bruts
        colorMenu.addItemActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int insertAt = getCaretPosition();
                String inputText = getText();
                setText(inputText.substring(0, insertAt) + colorMenu.getColor((JMenuItem) e.getSource()).getCode() + inputText.substring(insertAt));
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show(RoundInputField.this, e.getX(), e.getY());
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                String inputText = getText();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (historyPos == history.size() && !inputText.equals("")) {
                            history.add(inputText);
                        }
                        if (historyPos != 0) {
                            historyPos -= 1;
                            setText(history.get(historyPos));
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        historyPos += 1;
                        if (historyPos >= history.size()) {
                            setText("");
                            historyPos = history.size();
                        } else {
                            setText(history.get(historyPos));

                        }
                        break;
                    default:
                        break;
                }
            }
        });

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getText() != null && getText().length() > 0) {
                    history.add(getText());
                    historyPos = history.size();
                    setText("");
                }
            }
        });

        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringSelection selectedText = new StringSelection(getSelectedText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selectedText, selectedText);
            }
        });

        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        String content = (String) t.getTransferData(DataFlavor.stringFlavor);
                        String currentText = getText();
                        String finalText = currentText.substring(0, getCaretPosition()) + content + currentText.substring(getCaretPosition());
                        int lastCaret = getCaretPosition();
                        setText(finalText);
                        setCaretPosition(lastCaret + content.length());
                    } catch (UnsupportedFlavorException e1) {
                        ExceptionHandler.handleNonBlockingException(e1);
                    } catch (IOException e1) {
                        ExceptionHandler.handleNonBlockingException(e1);
                    }
                }
            }
        });
    }

    /**
     * Retourne le texte en clair avec balises irc.
     *
     * @return Le contenu brut du champ de texte.
     */
    public String getStyledText() {
        String text = super.getText();
        if (!text.startsWith("/")) {
            return getDefaultColorString() + (getDefaultColorString().length() > 0 && (text.startsWith(",") || (text.charAt(0) >= '0' && text.charAt(0) <= '9')) ? " " + text : text);
        } else {
            return text;
        }
    }

    /**
     * Renvoie la chaine de formattage couleur complete.
     *
     * @return La chaine permettant de coder la couleur par défaut.
     */
    private String getDefaultColorString() {
        String res = "";
        if (defaultBackCode.equals("") && defaultFrontCode.equals("")) {
            return res;
        }
        res += IRCTag.COLOR.getCode();
        res += defaultFrontCode;
        if (!defaultBackCode.equals("")) {
            res += ",";
            res += defaultBackCode;
        }
        return res;
    }

    /**
     * Paint la bordure arrondie.
     *
     * @param g Le pinceau.
     */
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        g.setColor(getBackground());
        g.fillRoundRect(0, 0, width, height, 10, 10);
        g.setColor(Color.GRAY);
        g.drawRoundRect(0, 0, width - 1, height - 1, 10, 10);

        super.paintComponent(g);
    }
}

