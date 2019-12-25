package jmessenger.client;

import jmessenger.shared.Message;
import jmessenger.shared.PluginMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
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
    @SuppressWarnings("rawtypes")
    private List<Class> classes;
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

    protected void onStart() {
        for (AbstractPlugin p : plugins) {
            try {
                p.onStart();
            } catch (Throwable e) {
                NotificationCenter.getInstance().add(e);
            }
        }
    }

    protected void onClose() {
        for (AbstractPlugin p : plugins) {
            try {
                p.onClose();
            } catch (Throwable e) {
                NotificationCenter.getInstance().add(e);
            }
        }
    }

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
     * load plugins
     */
    @SuppressWarnings({"deprecation", "rawtypes"})
    protected void loadPlugins() throws IOException {
        Stream<Path> paths = Files.walk(Paths.get("plugins"));
        List<Path> files = paths
                .filter(Files::isReadable)
                .filter(Files::isRegularFile)
                .filter((f) -> f.toString().toLowerCase().endsWith(".jar"))
                .collect(Collectors.toList());
        files.forEach(System.out::println);

        files.forEach((f) -> {
            try {
                String pathToJar = f.toString();
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
                    Class c = cl.loadClass(className);
                    //addPath(className);
                    classes.add(c);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // instantiate plugin objects
        for (Class c : classes) {
            String className = c.getName();
            System.out.println(className);
            if (!className.contains("$")) {
                //System.out.println(className);
                try {
                    Object o = c.newInstance();
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
