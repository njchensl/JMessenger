package jmessenger.client.ui;

import jmessenger.client.Messenger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BottomPanel extends JPanel {
    public BottomPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }

    // TODO
    public void addButton(@NotNull JLabel btn, @NotNull JPanel panelToDisplay) {
        btn.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // ignored
            }

            @Override
            public void mousePressed(MouseEvent e) {
                ((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).setMainPanel(panelToDisplay);
                Messenger.getInstance().getMainFrame().pack();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO change color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO change color
            }
        });
        this.add(btn);
    }
}
