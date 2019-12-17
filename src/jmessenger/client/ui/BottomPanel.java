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

    public void addButton(@NotNull JLabel btn, @NotNull JPanel panelToDisplay) {
        btn.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // ignored
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //System.out.println(((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).getMainPanel() instanceof MessagesPanel);
                ((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).setMainPanel(panelToDisplay);
                Messenger.getInstance().getMainFrame().getContentPane().revalidate();
                ((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).getMainPanel().revalidate();
                //System.out.println(((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).getMainPanel() instanceof MessagesPanel);
                /*
                Messenger.getInstance().getMainFrame().pack();
                Messenger.getInstance().getMainFrame().revalidate();
                ((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).getMainPanel().repaint();
                Messenger.getInstance().getMainFrame().getContentPane().revalidate();
                Messenger.getInstance().getMainFrame().getContentPane().repaint();
                ((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).getMainPanel().revalidate();
                Messenger.getInstance().getMainFrame().repaint();
                SwingUtilities.updateComponentTreeUI(Messenger.getInstance().getMainFrame());

                // test
                Container comp = Messenger.getInstance().getMainFrame().getContentPane();
                System.out.println(((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).getMainPanel() instanceof JPanel);
                Messenger.getInstance().getMainFrame().setContentPane(panelToDisplay);

                Messenger.getInstance().getMainFrame().setVisible(false);
                Messenger.getInstance().getMainFrame().setVisible(true);

                Messenger.getInstance().getMainFrame().setContentPane(comp);

                Messenger.getInstance().getMainFrame().setVisible(false);
                Messenger.getInstance().getMainFrame().setVisible(true);

                */
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
