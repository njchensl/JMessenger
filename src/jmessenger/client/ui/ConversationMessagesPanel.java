package jmessenger.client.ui;

import jmessenger.shared.ClientMessage;
import jmessenger.shared.TextMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class ConversationMessagesPanel extends JPanel {
    private List<ClientMessage> messages;
    private JScrollPane scrollPane;

    public ConversationMessagesPanel(@NotNull List<ClientMessage> messages, @Nullable JScrollPane scrollPane) {
        super(new GridBagLayout());
        this.messages = messages;
        this.scrollPane = scrollPane;
        refresh();
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public void refresh() {
        this.removeAll();
        int gridy = 0;
        synchronized (messages) {
            for (ClientMessage m : messages) {
                // render each message
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = gridy;
                c.weightx = 1;
                c.weighty = 0;

                if (m instanceof TextMessage) {
                /*
                JButton btn = new JButton(((TextMessage) m).getText());
                if (m.isMyMessage()) {
                    btn.setBackground(Color.LIGHT_GRAY);
                } else {
                    btn.setBackground(Color.WHITE);
                }
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                this.add(btn, c);

                 */
                    String text = ((TextMessage) m).getText();
                    JLabel lbl = new JLabel((text.contains("<html>") ? "" : "    ") + text) /* {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(this.getBackground());
                        g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
                    }
                } */;
                    lbl.setFont(new Font("Arial", Font.PLAIN, 17));
                    lbl.setOpaque(true);
                    if (m.isMyMessage()) {
                        lbl.setBackground(new Color(240, 240, 240));
                    } else {
                        lbl.setBackground(Color.WHITE);
                    }
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    lbl.addMouseListener(new MouseListener() {
                        private Color defaultColor;
                        private Color originalColor;

                        {
                            defaultColor = lbl.getBackground();
                        }

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            String myString = lbl.getText().trim();
                            StringSelection stringSelection = new StringSelection(myString);
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(stringSelection, null);
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            originalColor = lbl.getBackground();
                            lbl.setBackground(Color.GRAY);
                            //lbl.paintComponents(lbl.getGraphics());
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            lbl.setBackground(originalColor);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            defaultColor = lbl.getBackground();
                            lbl.setBackground(Color.LIGHT_GRAY);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            lbl.setBackground(defaultColor);
                        }
                    });
                    this.add(lbl, c);
                }
                gridy++;
            }
        }

        // the "spring" will push everything upwards so that everything is align to the top of the panel
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = gridy;
        c.weightx = 1;
        c.weighty = 1;
        JLabel lblEmpty = new JLabel();
        this.add(lblEmpty, c);
        this.revalidate();
    }

    @Override
    public void revalidate() {
        super.revalidate();
        this.scrollToBottom();
    }

    /**
     * scrolls the JScrollPane to the bottom
     */
    public void scrollToBottom() {
        if (scrollPane != null) {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(Integer.MAX_VALUE);
            //scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));
        }
    }

    public List<ClientMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ClientMessage> messages) {
        this.messages = messages;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }
}
