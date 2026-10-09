package mg.itu.myframework.model;

import java.lang.reflect.Method;

public class Mapping {
    private final Object controller;
    private final Method method;

    public Mapping(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}