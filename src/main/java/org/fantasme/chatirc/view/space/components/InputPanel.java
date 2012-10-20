package org.fantasme.chatirc.view.space.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.fantasme.chatirc.controller.ServerAdapter;
import org.fantasme.chatirc.model.space.IRCSpaceModel;
import org.fantasme.chatirc.model.space.ServerModel;

/**
 * Le panneau des saisies utilisateur.
 */
public class InputPanel extends JPanel {
    /**
     * Le label du pseudonyme.
     */
    private JLabel nicknameLabel;

    /**
     * Le champ de saisie.
     */
    private RoundInputField input;

    /**
     * L'espace associé au panneau d'entrée.
     */
    private final IRCSpaceModel associatedSpace;

    /**
     * @param associatedSpace L'espace associé au paneau d'entrée.
     */
    public InputPanel(IRCSpaceModel associatedSpace) {
        this.associatedSpace = associatedSpace;
        createView();
        placeComponents();
        createController();
    }

    /**
     * Crée la vue.
     */
    private void createView() {
        nicknameLabel = new JLabel(getServerModel().getNickname());
        input = new RoundInputField();
        // On desactive la touche tab pour le changement de focus.
        input.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Place les composants.
     */
    private void placeComponents() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        Dimension hori = new Dimension(5, 1);

        add(nicknameLabel);
        add(Box.createRigidArea(hori));
        add(input);

        Dimension size = input.getPreferredSize();
        input.setMinimumSize(size);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, size.height));
    }

    /**
     * Crée le controlleur.
     */
    private void createController() {
        getServerModel().addServerListener(new ServerAdapter() {
            public void nicknameChanged(String nickname) {
                nicknameLabel.setText(nickname);
            }
        });

        input.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (input.getText() != null && input.getText().length() > 0) {
                    getServerModel().sendUserMessage(input.getStyledText());
                }
            }
        });

        input.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                String inputText = input.getText();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_TAB:
                        // Trouver le mot en cours de saisie
                        int end = input.getCaretPosition();
                        int begin = inputText.lastIndexOf(" ", end - 1) + 1;
                        String currentWord = inputText.substring(begin, end);
                        // On cherche l'autocompletion possible.
                        if (currentWord != null && currentWord.length() > 0) {
                            List<String> matchedNicknames = new LinkedList<String>();
                            String[] visibleNicknames = associatedSpace.getVisibleNicknames();
                            for (String nick : visibleNicknames) {
                                if (nick.toLowerCase().startsWith(currentWord.toLowerCase())) {
                                    matchedNicknames.add(nick);
                                }
                            }
                            if (matchedNicknames.size() == 1) {
                                String replacement = matchedNicknames.get(0) + " ";
                                input.setText(inputText.substring(0, begin) + replacement + inputText.substring(end));
                                input.setCaretPosition(begin + replacement.length());
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                input.requestFocus();
            }

            public void focusLost(FocusEvent e) {
            }
        });
    }

    public RoundInputField getInputField() {
        return input;
    }

    private ServerModel getServerModel() {
        return associatedSpace.getServerModel();
    }


}
