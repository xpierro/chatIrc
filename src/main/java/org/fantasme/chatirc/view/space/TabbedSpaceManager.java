package org.fantasme.chatirc.view.space;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.fantasme.chatirc.controller.ServerAdapter;
import org.fantasme.chatirc.controller.SpaceListener;
import org.fantasme.chatirc.model.space.ChannelModel;
import org.fantasme.chatirc.model.space.IRCSpaceModel;
import org.fantasme.chatirc.model.space.PrivateMessageModel;
import org.fantasme.chatirc.model.space.ServerModel;
import org.fantasme.chatirc.view.space.components.TabComponent;

/**
 * Gestionnaire d'espaces é onglet.
 */
public class TabbedSpaceManager extends JTabbedPane implements SpaceManager {
    /**
     * Le modéle de serveur associé.
     */
    private final ServerModel server;

    /**
     * L'écouteur de déplacement d'onglet par ctrl + tab sur l'input d'un composant.
     */
    private KeyListener tabMoveListener;

    /**
     * @param server Le modéle de serveur associé.
     */
    public TabbedSpaceManager(ServerModel server) {
        super();
        this.server = server;
        this.setBorder(BorderFactory.createEmptyBorder(0, -5, -5, -5));
        this.setFocusTraversalKeysEnabled(false);
        createController();
    }

    /**
     * Crée le controlleur.
     */
    private void createController() {
        tabMoveListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    if (e.isControlDown()) {
                        int current = getSelectedIndex();
                        if (e.isShiftDown()) {
                            if (current > 0) {
                                setSelectedIndex(current - 1);
                            }
                        } else {
                            if (current < getTabCount() - 1) {
                                setSelectedIndex(current + 1);
                            }
                        }

                    }
                }
            }
        };

        // Rafraichit l'espace courant vu par l'utilisateur.
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                IRCSpaceView currentView = (IRCSpaceView) getSelectedComponent();
                if (currentView != null) {
                    server.setCurrentSpace(currentView.getSpaceModel());
                    setForegroundAt(getSelectedIndex(), Color.BLACK);
                    currentView.requestFocusInWindow();
                }
            }
        });

        server.addServerListener(new ServerAdapter() {
            public void chanCreated(ChannelModel model) {
                ChannelView channelView = new ChannelView(model);
                addSpace(channelView, true);
            }

            public void pvCreated(PrivateMessageModel model, boolean setCurrent) {
                PrivateMessageView pvView = new PrivateMessageView(model);
                addSpace(pvView, setCurrent);
            }

            public void serverCreated(String serverName) {
                ServerView serverView = new ServerView(server);
                addSpace(serverView, true);
            }
        });
    }

    /**
     * Ajoute un espace irc au gestionnaire.
     *
     * @param space      L'espace é ajouter.
     * @param setCurrent Indique s'il immédiatement afficher l'espace ajouté.
     */
    public void addSpace(IRCSpaceView space, boolean setCurrent) {
        // Ajoute l'écouteur d'événement clavier de l'inputField associé é l'espace, pour le déplacement d'onglet.
        space.getInputField().addKeyListener(tabMoveListener);
        addTab(space.getSpaceModel().getName(), (Component) space);
        if (setCurrent) {
            setSelectedIndex(getTabCount() - 1);
        }
        setTabComponentAt(getTabCount() - 1, new TabComponent(this, space.getSpaceModel() instanceof ServerModel));
        setSpaceListener(space);
    }

    /**
     * Supprime un espace du gestionnaire.
     *
     * @param space L'espace é supprimer
     */
    public void removeSpace(IRCSpaceView space) {
        //remove((Component) space);
        removeTabAt(indexOfTab(space.getSpaceModel().getName()));

    }

    /**
     * Quite l'espace.
     *
     * @param index L'index de l'espace é quitter.
     */
    public void remove(int index) {
        ((IRCSpaceView) getComponentAt(index)).getSpaceModel().close();
    }

    /**
     * Renvoie la couleur d'un l'onglet.
     *
     * @param index L'index de l'onglet.
     * @return La couleur du texte de l'onglet.
     */
    public Color getForegroundAt(int index) {
        return getTabComponentAt(index).getForeground();
    }

    /**
     * Retourne la vue en cours d'affichage.
     *
     * @return La vue affichée actuellement.
     */
    public IRCSpaceView getCurrentSpaceView() {
        return (IRCSpaceView) getSelectedComponent();
    }

    /**
     * Change la couleur du texte de l'onlet.
     *
     * @param index L'index de l'onglet.
     * @param c     La nouvelle couleur é appliquer.
     */
    public void setForegroundAt(int index, Color c) {
        TabComponent comp = (TabComponent) getTabComponentAt(index);
        if (comp != null) {
            comp.setForeground(c);
        }
    }

    /**
     * Change le titre de l'onglet.
     *
     * @param index L'index de l'onglet.
     * @param title Le noveau titre de l'onglet.
     */
    public void setTitleAt(int index, String title) {
        super.setTitleAt(index, title);
        ((TabComponent) getTabComponentAt(index)).update();
    }

    /**
     * Install un écouteur d'espace.
     *
     * @param space L'espace sur lequel ajouter l'écouteur.
     */
    private void setSpaceListener(final IRCSpaceView space) {
        IRCSpaceModel model = space.getSpaceModel();
        model.addSpaceListener(new SpaceListener() {
            public void spaceUpdated(String message) {
                int index = indexOfComponent((Component) space);
                if (index != getSelectedIndex()) {
                    //TODO : coloration du message tapé.
                    if (getForegroundAt(index) != Color.BLUE) {
                        if (message.contains(server.getNickname())) {
                            setForegroundAt(index, Color.BLUE);
                        } else {
                            setForegroundAt(index, Color.RED);
                        }
                    }
                }
            }

            public void spaceNameChanged(String name) {
                int index = indexOfComponent((Component) space);
                setTitleAt(index, name);
            }

            public void spaceClosed() {
                removeSpace(space);
            }
        });
    }
}
