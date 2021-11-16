package framework.di;
import framework.di.annotations.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class DiEngine {

    private final List<Object> instantiatedControllers;
    private final List<Class> classes;

    private DiContainer diContainer;

    public DiEngine() throws Exception {
        this.diContainer = new DiContainer();
        this.classes = getClasses("data");
        instantiateBeans(this.classes);
        this.instantiatedControllers = instantiateControllers();
        instantiateControllerDependencies();
    }

    private void instantiateBeans(List<Class> classes) throws Exception {
        for (var clazz : classes) {
            diContainer.addBeanDefinition(clazz);
        }
    }

    private void instantiateControllerDependencies() throws Exception {
        for (var controller: instantiatedControllers) {
            diContainer.instantiateFromRoot(controller);
        }
    }

    private List<Object> instantiateControllers() {
        var controllers = new ArrayList<>();
        for (var controller: collectControllers()) {
            try {
                controllers.add(controller.getDeclaredConstructor().newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return controllers;
    }

    private List<Class> collectControllers() {
        return this.classes.stream()
                .filter(c -> isControllerAnnotated(c))
                .collect(Collectors.toList());
    }

    private boolean isControllerAnnotated(Class clazz) {
        return clazz.getDeclaredAnnotation(Controller.class) != null;
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private List<Class> getClasses(String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            try {
                classes.addAll(findClasses(directory, packageName));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public List<Object> getInstantiatedControllers() {
        return instantiatedControllers;
    }
}