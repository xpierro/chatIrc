package org.fantasme.chatirc.view.space;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import org.fantasme.chatirc.controller.ChannelListener;
import org.fantasme.chatirc.model.protocol.IRCStringConverter;
import org.fantasme.chatirc.model.protocol.User;
import org.fantasme.chatirc.model.space.ChannelModel;
import org.fantasme.chatirc.model.text.MessageInjecter;
import org.fantasme.chatirc.view.space.components.RoundScrollPane;
import org.fantasme.chatirc.view.space.util.UsersListView;

/**
 * La vue d'un canal IRC.
 */
public class ChannelView extends AbstractIRCSpaceView {
    /**
     * Position (pourcentage du total) de la barre de séparation entre la fenétre des message et la liste des pseudonymes.
     */
    private static final double SLIDEBARPOS = 0.75;

    /**
     * La liste des pseudonymes.
     */
    private UsersListView nickList;

    /**
     * Le label affichant le sujet du canal s'il existe.
     */
    private JLabel topicLabel;

    /**
     * @param model Le modéle de canal associé.
     */
    public ChannelView(ChannelModel model) {
        super(model);
    }

    /**
     * Crée la vue.
     */
    protected void createView() {
        super.createView();
        nickList = new UsersListView(((ChannelModel) getSpaceModel()).getUsersListModel());
        topicLabel = new JLabel();
    }

    /**
     * Place les composants.
     */
    protected void placeComponents() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        Dimension hori = new Dimension(5, 1);
        Dimension vert = new Dimension(1, 5);

        add(Box.createRigidArea(hori));

        Box b = Box.createVerticalBox();
        {
            JScrollPane listPane = new RoundScrollPane(nickList);
            listPane.getVerticalScrollBar().setUnitIncrement(18);
            JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getCenterPane(), listPane);
            p.setContinuousLayout(true);

            p.setDividerSize(5);
            p.setUI(new BasicSplitPaneUI() {
                public BasicSplitPaneDivider createDefaultDivider() {
                    return new BasicSplitPaneDivider(this) {
                        public void setBorder(Border b) {
                        }
                    };
                }
            });
            p.setBorder(null);

            b.add(Box.createRigidArea(vert));

            b.add(topicLabel);
            topicLabel.setAlignmentX(.5f);
            b.add(Box.createRigidArea(vert));

            JPanel splitPanel = new JPanel(new BorderLayout());
            {
                splitPanel.add(p, BorderLayout.CENTER);
            }
            b.add(splitPanel);

            b.add(Box.createRigidArea(vert));

            b.add(getInputPanel());

            b.add(Box.createRigidArea(vert));

            p.setResizeWeight(SLIDEBARPOS);
        }
        add(b);

        add(Box.createRigidArea(hori));
    }

    /**
     * Crée le controlleur.
     */
    protected void createController() {
        super.createController();
        final ChannelModel model = ((ChannelModel) getSpaceModel());
        model.addChannelListener(new ChannelListener() {
            public void topicChanged(String newTopic) {
                setTopicHTML(IRCStringConverter.toHTML(newTopic));
                MessageInjecter.injectClientMessage("Le sujet de " + getName() + " est " + newTopic, model);
            }

            public void channelClosed() {
                getSpaceModel().getServerModel().sendPlainMessage("PART " + model.getName());
            }
        });

        nickList.getList().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = nickList.getList().getSelectedIndex();
                if (nickList.getList().getCellBounds(index, index).contains(e.getPoint())) {
                    String nick = ((User) (nickList.getList().getModel().getElementAt(index))).getNick();
                    if (!getSpaceModel().getServerModel().getNickname().equals(nick)) {
                        getSpaceModel().getServerModel().createPv(nick, true);
                    }
                }
            }
        });
    }

    /**
     * Affiche le topic formaté en html.
     *
     * @param htmlString La chaine stylisée en html é afficher.
     */
    private void setTopicHTML(String htmlString) {
        topicLabel.setText("<html><body><p>" + htmlString + "</p></body></html>");
    }
}
