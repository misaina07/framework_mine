package mg.itu.myframework.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.util.ClassUtil;
import mg.itu.myframework.util.MethodClassMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebListener
public class FrameworkListener implements ServletContextListener {

    public static final String ATTR_URL_MAPPING = "listUrlMapping";
    public static final String ATTR_CONTROLLERS = "listController";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        List<String> packageNames = new ArrayList<>();
        packageNames.add("mg.itu.myframework.controller");
        packageNames.add("controller");

        Map<String, MethodClassMapping> listUrlMapping = new HashMap<>();
        List<Class<?>> controllers = ClassUtil.getClassesWithAnnotation(packageNames, listUrlMapping, Controller.class);

        List<String> listController = new ArrayList<>();
        for (Class<?> controller : controllers) {
            listController.add(controller.getName());
        }

        sce.getServletContext().setAttribute(ATTR_URL_MAPPING, listUrlMapping);
        sce.getServletContext().setAttribute(ATTR_CONTROLLERS, listController);
    }
}
