package jmessenger.client.ui;

import jmessenger.client.Messenger;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class MainPanel extends JPanel {
    private JPanel main, bottom;

    /**
     * create a main panel, which consists of the upper part and the lower part
     */
    public MainPanel() {
        super(new GridBagLayout());
        main = new MessagesPanel();
        initialize();
    }

    public void refreshComponents() {
        this.removeAll();
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.add(main, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 1;
        c.ipady = 1;
        bottom = new BottomPanel();
        bottom.setBackground(new Color(246, 246, 246));
        this.add(bottom, c);


        // init the bottom panel
        JLabel lbl = new JLabel();
        URL url = Messenger.class.getResource("/jmessenger/client/ui/resources/chats.jpg");
        BufferedImage img = null;
        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert img != null;
        Image dimg = img.getScaledInstance(70, 45,
                Image.SCALE_SMOOTH);
        lbl.setIcon(new ImageIcon(dimg));
        //lbl.setFont(new Font("Arial", Font.PLAIN, 10));
        //lbl.setHorizontalAlignment(JLabel.CENTER);

        MessagesPanel msgPnl = new MessagesPanel();
        ((BottomPanel) bottom).addButton(lbl, msgPnl, msgPnl.getConversationListPanel()::updateConversations);
    }

    private void initialize() {
        refreshComponents();
    }

    public JPanel getMainPanel() {
        return this.main;
    }

    public void setMainPanel(@NotNull JPanel pnl) {
        this.main = pnl;
        refreshComponents();
    }

    public BottomPanel getBottomPanel() {
        return (BottomPanel) this.bottom;
    }
}
