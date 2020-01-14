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
package jmessenger.client;

import jmessenger.shared.Message;
import jmessenger.shared.PluginMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginManager {

    private static volatile PluginManager pluginManager;
    private List<AbstractPlugin> plugins;
    private List<Class<?>> classes;
    //private List<Object> objects = new ArrayList<>(); // testing dynamic class loading and unloading
    /*
     * IF THE OBJECTS ARE NOT GC'ED, THEIR RESPECTIVE CLASSES WILL NOT BE UNLOADED
     */

    /**
     * NO INSTANCE OF PLUGIN MANAGER FOR YOU!
     */
    private PluginManager() {
        plugins = new ArrayList<>();
        classes = new ArrayList<>();
    }

    /**
     * NOTE: INITIALIZE BEFORE GETTING AN INSTANCE
     *
     * @return an instance of the plugin manager
     */
    @Nullable
    public static PluginManager getInstance() {
        return pluginManager;
    }

    /**
     * called when the program starts
     */
    protected void onStart() {
        for (AbstractPlugin p : plugins) {
            try {
                p.onStart();
            } catch (Throwable e) {
                NotificationCenter.getInstance().add(e);
            }
        }
    }

    /**
     * called when the program closes
     */
    protected void onClose() {
        for (AbstractPlugin p : plugins) {
            try {
                p.onClose();
            } catch (Throwable e) {
                NotificationCenter.getInstance().add(e);
            }
        }
    }

    /**
     * called when a message is received
     *
     * @param msg the message
     */
    protected void onMessageReceived(@NotNull Message msg) {
        for (AbstractPlugin p : plugins) {
            try {
                p.onMessageReceived(msg);
            } catch (Throwable e) {
                NotificationCenter.getInstance().add(e);
            }
        }
    }

    /**
     * instantiate a plugin manager
     */
    protected static void initialize() {
        pluginManager = new PluginManager();
    }

    /*
    private static void addPath(String s) throws Exception {
        File f = new File(s);
        URI u = f.toURI();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{u.toURL()});
    }
    */

    /**
     * called when a message is sent
     *
     * @param msg the message
     */
    protected void onMessageSent(@NotNull Message msg) {
        for (AbstractPlugin p : plugins) {
            try {
                p.onMessageSent(msg);
            } catch (Throwable e) {
                NotificationCenter.getInstance().add(e);
            }
        }
    }

    /**
     * the plugin manager will ask each plugin to provide with a custom JPanel
     */
    public List<@NotNull JComponent> getAdditionalPanels() {
        List<JComponent> components = new ArrayList<>();
        for (AbstractPlugin p : plugins) {
            JComponent pnl = p.getCustomJComponent();
            if (pnl != null) {
                components.add(pnl);
            }
        }
        return components;
    }

    /**
     * @return a list of custom buttons from the plugins
     */
    public List<@NotNull PluginButton> getAdditionalJButtons() {
        List<PluginButton> buttons = new ArrayList<>();
        for (AbstractPlugin p : plugins) {
            PluginButton btn = p.getCustomJButton();
            if (btn != null) {
                buttons.add(btn);
            }
        }
        return buttons;
    }

    /**
     * requests the plugins to render an unsupported client message
     *
     * @param pm the ClientMessage to render
     * @return the rendered JLabel, null if non of the plugins supported this message type
     */
    public JLabel renderCustomMessage(PluginMessage pm) {
        for (AbstractPlugin p : plugins) {
            try {
                JLabel lbl = p.renderCustomMessage(pm);
                if (lbl != null) {
                    return lbl;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * load plugins from the .jar files under the /plugins directory and loads the classes
     * then instantiate the plugin objects and store them in a list
     */
    protected void loadPlugins() throws IOException {
        Stream<Path> paths = Files.walk(Paths.get("plugins"));
        List<Path> files = paths
                .filter(Files::isReadable)
                .filter(Files::isRegularFile)
                .filter((f) -> f.toString().toLowerCase().endsWith(".jar"))
                .collect(Collectors.toList());
        files.forEach(System.out::println);

        files.forEach((f) -> {
            String pathToJar = f.toString();
            try {
                JarFile jarFile = new JarFile(pathToJar);
                Enumeration<JarEntry> e = jarFile.entries();

                URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
                URLClassLoader cl = URLClassLoader.newInstance(urls);

                while (e.hasMoreElements()) {
                    JarEntry je = e.nextElement();
                    if (je.isDirectory() || !je.getName().endsWith(".class")) {
                        continue;
                    }
                    // -6 because of .class
                    String className = je.getName().substring(0, je.getName().length() - 6);
                    className = className.replace('/', '.');
                    Class<?> c = cl.loadClass(className);
                    //addPath(className);
                    classes.add(c);

                }
            } catch (Exception e) {
                // convert stack trace to string
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                // show stack trace
                JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea() {{
                    setEditable(false);
                    setText("Error loading plugin: " + pathToJar + "\n" + exceptionAsString);
                }}), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // instantiate plugin objects
        for (Class<?> c : classes) {
            String className = c.getName();
            //System.out.println(className);
            if (!className.contains("$")) {
                //System.out.println(className);
                try {
                    Constructor<?> con = c.getConstructor();
                    Object o = con.newInstance();
                    //objects.add(o);
                    if (o instanceof AbstractPlugin) {
                        // load the plugin
                        AbstractPlugin p = (AbstractPlugin) o;
                        p.setMessenger(Messenger.getInstance());
                        this.plugins.add(p);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

}
