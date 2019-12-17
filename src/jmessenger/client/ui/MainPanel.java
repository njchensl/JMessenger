package jmessenger.client.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private JPanel main, bottom;

    public MainPanel() {
        super(new GridBagLayout());
        initialize();
    }

    private void initialize() {
        GridBagConstraints c = new GridBagConstraints();

        main = new MessagesPanel();
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
        c.ipady = 25;
        bottom = new BottomPanel();
        this.add(bottom, c);

        // init the bottom panel
        JLabel lbl = new JLabel("AA");
        lbl.setFont(new Font("Arial", Font.PLAIN, 10));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        ((BottomPanel) bottom).addButton(lbl, this.main);
    }

    public JPanel getMainPanel() {
        return this.main;
    }

    public void setMainPanel(@NotNull JPanel pnl) {
        this.main = pnl;
    }

    public BottomPanel getBottomPanel() {
        return (BottomPanel) this.bottom;
    }
}
