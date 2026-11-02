package mg.itu.myframework.model;

import java.util.HashMap;
import java.util.Map;

public class MapSession {
    private Map<String, Object> attributes = new HashMap<>();

    public MapSession() {}

    public void setAttribute(String key, Object value) { attributes.put(key, value); }
    public void add(String key, Object value) { setAttribute(key, value); }
    public Object getAttribute(String key) { return attributes.get(key); }
    public Object get(String key) { return getAttribute(key); }
    public void removeAttribute(String key) { attributes.remove(key); }
    public void remove(String key) { removeAttribute(key); }
    public void clearAttributes() { attributes.clear(); }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }
}
