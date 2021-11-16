package framework.di;
import framework.di.annotations.*;
import utils.ClassLoaderUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class DiEngine {

    private final List<Object> instantiatedControllers;
    private final List<Class> classes;

    private DiContainer diContainer;

    public DiEngine() throws Exception {
        this.diContainer = new DiContainer();
        this.classes = ClassLoaderUtils.getClasses("data");
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

    public List<Object> getInstantiatedControllers() {
        return instantiatedControllers;
    }
}