package org.fantasme.chatirc.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import netscape.javascript.JSObject;
import org.fantasme.chatirc.controller.ServerAdapter;
import org.fantasme.chatirc.javascript.JSTest;
import org.fantasme.chatirc.model.space.ServerModel;
import org.fantasme.chatirc.tools.properties.GestionnaireProperties;
import org.fantasme.chatirc.view.space.SpaceManager;
import org.fantasme.chatirc.view.space.TabbedSpaceManager;

/**
 * La fenétre principale de l'applet.
 */
public class IRCApplet extends JApplet {
    /**
     * Le panel des onglets.
     */
    private SpaceManager spaceManager;

    /**
     * Le panel d'aide.
     */
    private HelpView helpView;

    /**
     * La table des canaux visitables sur le serveur.
     */
    private ChannelsTableView channels;

    /**
     * La barre de menu.
     */
    private JMenuBar menuBar;

    /**
     * Le contenu des menus.
     */
    private JMenuItem quit;
    private JMenuItem channelList;
    private JMenuItem help;
    //private JMenuItem aboutus;

    /**
     * Le modèle représentant le serveur.
     */
    private ServerModel serverModel;

    /**
     * Le panel contenant tout.
     */
    private JPanel mainPanel;

    private static JPanel defaultMainPanel = null;
    private static JMenuBar defaultMenuBar = null;
    private static IRCApplet lastInstance = null;

    //TODO: faire autrement
    public static IRCApplet getLastInstance() {
        return lastInstance;
    }

    /**
     * Crée la vue.
     */
    private void createView() {
        spaceManager = new TabbedSpaceManager(serverModel);
        helpView = new HelpView();
        channels = new ChannelsTableView();

        menuBar = new JMenuBar();
        {
            JMenu fileMenu = new JMenu("Fichier");
            {
                quit = new JMenuItem("Quitter");
                fileMenu.add(quit);
            }
            //menuBar.add(fileMenu);

            JMenu serverMenu = new JMenu("Serveur");
            {
                channelList = new JMenuItem("Liste des canaux");
                serverMenu.add(channelList);
            }
            menuBar.add(serverMenu);

            JMenu helpMenu = new JMenu("Aide");
            {
                help = new JMenuItem("Base de connaissance");
                helpMenu.add(help);
                //aboutus = new JMenuItem("A propos");
                //helpMenu.add(aboutus);
            }
            menuBar.add(helpMenu);
        }

    }

    /**
     * Place les composants
     */
    private void placeComponents() {
        mainPanel = new JPanel(new BorderLayout());
        {
            mainPanel.add((Component) spaceManager, BorderLayout.CENTER);
        }
        add(mainPanel);
        setJMenuBar(menuBar);
    }

    /**
     * Crée le controlleur.
     */
    private void createController() {
        // Actions des menus
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpView.display();
            }
        });

        channelList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                serverModel.sendPlainMessage("LIST");
                channels.getModel().clearRows();
            }
        });

        channels.getTable().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable t = (JTable) e.getSource();
                if (e.getClickCount() == 2) {
                    int c = t.columnAtPoint(e.getPoint());

                    if (c == 0) {
                        int r = t.rowAtPoint(e.getPoint());
                        String chanName = (String) t.getValueAt(r, c);
                        if (chanName.startsWith("#")) {
                            serverModel.createChan(chanName);
                            serverModel.sendPlainMessage("JOIN " + chanName);
                        }
                    }
                }
            }
        });

        //helpView
        helpView.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable t = (JTable) e.getSource();
                if (e.getClickCount() == 2) {
                    int c = t.columnAtPoint(e.getPoint());

                    if (c == 0) {
                        int r = t.rowAtPoint(e.getPoint());
                        String command = (String) t.getValueAt(r, c);
                        spaceManager.getCurrentSpaceView().getInputField().setText(command.split(" ")[0] + " ");
                    }
                }
            }
        });

        // Ecoute les évènements du serveurs concernant l'interface graphique.
        serverModel.addServerListener(new ServerAdapter() {
            public void channelListStarted() {
                serverModel.setChannelListModel(channels.getModel());
                channels.display();
            }
        });
    }

    /**
     * Lance la connexion initiale.
     * @param nickname Le pseudonyme de l'utilisateur.
     * @param ident L'ident de l'utilisateur.
     */
    public void launchApplet(String nickname, String ident) {
        String defaultChannels = GestionnaireProperties.getInstance().getPropriete("irc.conf.server.autojoin", "FR");

        String server = GestionnaireProperties.getInstance().getPropriete("irc.server.address", "FR");
        int port = Integer.valueOf(GestionnaireProperties.getInstance().getPropriete("irc.server.port", "FR"));
        serverModel = new ServerModel(server, port, nickname, ident);
        serverModel.registerDefaultChannels(defaultChannels);

        if (defaultMenuBar == null || defaultMainPanel == null || lastInstance == null) {

            // Doivent étre faits avant c.register() pour éviter la perte de mess
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createView();
                    placeComponents();
                    createController();
                    setVisible(true);
                }
            });
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        serverModel.launchCore();
                    } catch (IOException e) {
                        //
                    }
                    serverModel.registerCore();
                }
            });
            defaultMenuBar = menuBar;
            defaultMainPanel = mainPanel;
            lastInstance = this;
        }
    }

    /**
     * Méthode d'initialisation de l'applet. Normalement appelé une et une seule fois.
     */
    public synchronized void init() {
        String nickname = getParameter("nickname");
        String ident = getParameter("ident");
        launchApplet(nickname, ident);
    }

    /**
     * Permet a du code javascript de fermer l'applet.
     */
    public void jsQuit() {
        System.out.println("Exited by javascript");
        serverModel.sendPlainMessage("QUIT");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                defaultMainPanel = null;
                defaultMenuBar = null;
                lastInstance = null;
                System.exit(0);
            }
        });
    }

    public void testJS() {
        JSTest.displayTestDiv();
    }

    /**
     * Permet à l'applet de fermer la fenêtre extjs associée.
     */
    public static void quitJs() {
        JSObject jso = JSObject.getWindow(lastInstance);
        JSObject extDesktopApp = (JSObject) jso.getMember("FantasmeApplication");
        JSObject extDesktop = (JSObject) extDesktopApp.call("getDesktop", new String[0]);
        JSObject chatWindow = (JSObject) extDesktop.call("getWindow", new String[] {"chat-win"});
        chatWindow.call("close", new String[0]);
    }

    /**
     * Lancement en standalone
     */
    public static void main(String[] argv) {
        System.out.println("frame launched");
        IRCApplet contentPane = new IRCApplet();
        contentPane.launchApplet("fantasme", "fanta");
        JFrame mainFrame = new JFrame("Fantasme IRC Client");
        mainFrame.setContentPane(contentPane);
        mainFrame.setSize(600, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }
}
