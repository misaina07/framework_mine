package mg.itu.myframework.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapSession {
    private Map<String, Object> attributes;

    public MapSession() {
        attributes = new HashMap<>();
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void add(String name, Object value) {
        setAttribute(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Object get(String name) {
        return getAttribute(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void remove(String name) {
        removeAttribute(name);
    }

    public void clearAttributes() {
        attributes.clear();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }

    public Set<String> getAttributeKeys() {
        return attributes.keySet();
    }

    public Set<String> getKeys() {
        return getAttributeKeys();
    }
}