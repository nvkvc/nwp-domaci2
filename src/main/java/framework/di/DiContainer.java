package framework.di;

import framework.di.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiContainer {

    private Map<String, Class> beans;
    private Map<String, Object> singletons;

    public DiContainer() {
        beans = new HashMap<>();
        singletons = new HashMap<>();
    }

    public void addBeanDefinition(Class clazz) throws Exception {
        var annotations = Arrays.asList(clazz.getDeclaredAnnotations());
        for (var annotation: annotations) {
            if (annotation instanceof Bean || annotation instanceof Service || annotation instanceof Component) {
                var identifier = getIdentifier(clazz); // identifier == qualifier
                if (beans.containsKey(identifier)) {
                    throw new Exception("There's already a bean with name: " + identifier);
                }
                beans.put(identifier, clazz);
            }
        }
    }

    private String getIdentifier(Class clazz) {
        for (var a : Arrays.asList(clazz.getDeclaredAnnotations())) {
            if (a instanceof Qualifier) {
                return ((Qualifier) a).value();
            }
        }

        return clazz.getSimpleName();
    }

    public void instantiateFromRoot(Object root) throws Exception {
        instantiateAutowiredFields(root.getClass().getDeclaredFields(), root);
    }

    private void instantiateAutowiredFields(Field[] fields, Object parent) throws Exception {
        if (fields == null) {
            return;
        }

        for (var field : fields) {
            if (isAutowired(field)) {
                if(!isBeanServiceOrComponent(field)) {
                    throw new Exception("In class " + parent.getClass().getName() + " attribute " + field.getName() + " has @Autowired but it is not a Bean, Component or Service!");
                }
                instantiateField(field, parent);
            }
        }
    }

    private boolean isAutowired(Field field) {
        for (var a : Arrays.asList(field.getDeclaredAnnotations())) {
            if (a instanceof Autowired) {
                return true;
            }
        }

        return false;
    }

    private boolean isBeanServiceOrComponent(Field field) throws ClassNotFoundException {
        String identifier = field.getType().getSimpleName();
        for (var a : Arrays.asList(field.getDeclaredAnnotations())) {
            if (a instanceof Qualifier) {
                identifier = ((Qualifier) a).value();
            }
        }
        Class clazz = beans.get(identifier);

        if(clazz != null) return true;
        else return false;
    }

    private void instantiateField(Field field, Object parent) throws Exception {
        var accessible = field.isAccessible();
        field.setAccessible(true);
        var annotations = Arrays.asList(field.getDeclaredAnnotations());
        Object instance = null;
        String identifier = null;
        if (field.getClass().isInterface()) {
            if (!containsQualifier(annotations)) {
                throw new Exception("Cannot instantiate interface without qualifier");
            }
        }
        identifier = getIdentifierFromField(field);
        instance = createBean(identifier);

        field.set(parent, instance);
        field.setAccessible(accessible);
        instantiateAutowiredFields(field.getType().getDeclaredFields(), instance);
    }

    private boolean containsQualifier(List<Annotation> annotations) {
        for (var a : annotations) {
            if (a instanceof Qualifier) {
                return true;
            }
        }

        return false;
    }

    private String getIdentifierFromField(Field field) {
        for (var a : Arrays.asList(field.getDeclaredAnnotations())) {
            if (a instanceof Qualifier) {
                return ((Qualifier) a).value();
            }
        }
        return field.getType().getSimpleName();
    }

    public Object createBean(String identifier) throws Exception {
        var annotations = Arrays.asList(beans.get(identifier).getDeclaredAnnotations());
        for (var a : annotations) {
            if (a instanceof Service) {
                return createSingletonBean(identifier);
            } else if (a instanceof Bean && ((Bean) a).scope().equals("singleton")) {
                return createSingletonBean(identifier);
            } else if (a instanceof Bean && ((Bean) a).scope().equals("prototype")) {
                return beans.get(identifier).getDeclaredConstructor().newInstance();
            } else if (a instanceof Component) {
                return beans.get(identifier).getDeclaredConstructor().newInstance();
            }
        }

        throw new Exception("No Bean annotation added");
    }

    private Object createSingletonBean(String identifier) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (singletons.containsKey(identifier)) {
            return singletons.get(identifier);
        } else {
            var clazz = beans.get(identifier);
            var obj = clazz.getDeclaredConstructor().newInstance();
            singletons.put(identifier, obj);
            return obj;
        }
    }
}
