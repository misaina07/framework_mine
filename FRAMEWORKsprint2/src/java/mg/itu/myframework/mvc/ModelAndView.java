package mg.itu.myframework.mvc;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private String view;
    private Map<String, Object> data = new HashMap<>();

    public ModelAndView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void addObject(String key, Object value) {
        data.put(key, value);
    }
}
