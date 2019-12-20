package jmessenger.client;

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
    //private List<Object> objects = new ArrayList<>(); // testing dynamic class loading and unloading
    /*
     * IF THE OBJECTS ARE NOT GC'ED, THEIR RESPECTIVE CLASSES WILL NOT BE UNLOADED
     */

    /**
     * NO INSTANCE OF PLUGIN MANAGER FOR YOU!
     */
    private PluginManager() {
        plugins = new ArrayList<>();
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
     * NOTE: INITIALIZE BEFORE GETTING AN INSTANCE
     *
     * @return an instance of the plugin manager
     */
    public static PluginManager getInstance() {
        while (pluginManager == null) {
            Thread.onSpinWait();
        }
        return pluginManager;
    }

    /**
     * load plugins
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

                    if (!className.contains("$")) {
                        System.out.println(className);
                        try {
                            Object o = c.newInstance();
                            //objects.add(o);
                            if (o instanceof AbstractPlugin) {
                                // load the plugin
                                this.plugins.add((AbstractPlugin) o);

                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

}
