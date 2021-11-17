package server;

import java.lang.reflect.Method;

public class ControllerMethod {
    public Method method;
    public Object controller;

    public ControllerMethod(Object controller, Method method) {
        this.method = method;
        this.controller = controller;
    }
}
