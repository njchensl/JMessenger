package jmessenger.client.ui;

import jmessenger.client.Messenger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BottomPanel extends JPanel {

    /**
     * create a new bottom panel
     * it is actually a series of jlabels that the user could click on to switch the upper part of the main panel
     */
    public BottomPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(Component.CENTER_ALIGNMENT); // TODO
    }

    /**
     * add a button to the bottom panel
     *
     * @param btn            the button to add (it's actually a label)
     * @param panelToDisplay the panel to display on top
     * @param r              the code to run after the change. e.g. refreshing the content to make them up to date
     */
    public void addButton(@NotNull JLabel btn, @NotNull JPanel panelToDisplay, Runnable r) {
        btn.addMouseListener(new MouseListener() {
            Color originalColor;

            {
                originalColor = BottomPanel.super.getBackground();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // ignored
            }

            @Override
            public void mousePressed(MouseEvent e) {

                ((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).setMainPanel(panelToDisplay);
                r.run();
                Messenger.getInstance().getMainFrame().revalidate();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(originalColor);
            }
        });
        btn.setBackground(this.getBackground());
        this.add(btn);
    }
}
