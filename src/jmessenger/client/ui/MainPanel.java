/*
 * The MIT License
 *
 * Copyright 2019 frche1699.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jmessenger.client.ui;

import jmessenger.client.PluginManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static jmessenger.client.ui.resources.Resources.rb;

public class MainPanel extends JPanel {
    private JPanel main;
    private JTabbedPane tp;
    private List<JComponent> components;

    /**
     * create a main panel, which consists of the upper part and the lower part
     */
    public MainPanel() {
        super(new GridBagLayout());
        // ask the plugin manager for the extra components
        components = Objects.requireNonNull(PluginManager.getInstance()).getAdditionalPanels();
        main = new MessagesPanel();
        initialize();
    }

    /**
     * removes and adds all components back
     */
    public void refreshComponents() {
        this.removeAll();
        tp = new JTabbedPane();
        tp.add(rb.getString("MESSAGES"), main);
        for (JComponent p : components) {
            tp.add(p.getName(), p);
        }

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.add(tp, c);



        /*
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
        */

        /*
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

         */
    }

    /**
     * initializes the panel by refreshing its components
     */
    private void initialize() {
        refreshComponents();
    }

    /**
     * @return the main panel
     */
    public JPanel getMainPanel() {
        return this.main;
    }

}
