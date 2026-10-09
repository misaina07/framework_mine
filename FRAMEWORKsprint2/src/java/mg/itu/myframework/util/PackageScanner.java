package mg.itu.myframework.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class PackageScanner {
    private PackageScanner() {
    }

    public static List<Class<?>> scan(String packageName) throws IOException, ClassNotFoundException {
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        List<Class<?>> classes = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if ("file".equals(resource.getProtocol())) {
                classes.addAll(scanDirectory(packageName, new File(URI.create(resource.toString())), classLoader));
            } else if ("jar".equals(resource.getProtocol())) {
                classes.addAll(scanJar(packageName, resource, classLoader));
            }
        }
        return classes;
    }

    private static List<Class<?>> scanDirectory(String packageName, File directory, ClassLoader classLoader)
            throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(scanDirectory(packageName + "." + file.getName(), file, classLoader));
            } else if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className, false, classLoader));
            }
        }
        return classes;
    }

    private static List<Class<?>> scanJar(String packageName, URL resource, ClassLoader classLoader)
            throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        URLConnection connection = resource.openConnection();
        if (!(connection instanceof JarURLConnection)) {
            return classes;
        }

        try (JarFile jar = ((JarURLConnection) connection).getJarFile()) {
            String prefix = packageName.replace('.', '/') + "/";
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(prefix) && name.endsWith(".class") && !name.contains("$")) {
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    classes.add(Class.forName(className, false, classLoader));
                }
            }
        }
        return classes;
    }
}