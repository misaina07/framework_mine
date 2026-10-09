package mg.itu.myframework.controller;

import java.io.*;
import java.lang.reflect.Method;
import jakarta.servlet.*;
import java.util.*;
import jakarta.servlet.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.RequestMapping;
import mg.itu.myframework.annotation.WebApi;
import mg.itu.myframework.exception.UrlNotFoundException;
import mg.itu.myframework.model.Mapping;
import mg.itu.myframework.mvc.ModelAndView;
import mg.itu.myframework.util.PackageScanner;

public class FrontControllerServlet extends HttpServlet {
    private final List<String> listController = new ArrayList<>();
    private final Map<String, Mapping> listUrlMapping = new HashMap<>();

    @Override
    public void init() throws ServletException {
        String packages = getServletContext().getInitParameter("packageNames");
        if (packages == null || packages.trim().isEmpty()) {
            packages = "controller";
        }

        try {
            for (String packageName : packages.split("[,;]")) {
                for (Class<?> clazz : PackageScanner.scan(packageName.trim())) {
                    if (!clazz.isAnnotationPresent(Controller.class)) {
                        continue;
                    }

                    Object controller = clazz.getDeclaredConstructor().newInstance();
                    listController.add(clazz.getName());
                    for (Method method : clazz.getDeclaredMethods()) {
                        String url = getMappedUrl(method);
                        if (url != null && listUrlMapping.put(url, new Mapping(controller, method)) != null) {
                            throw new ServletException("URL dupliquée : " + url);
                        }
                    }
                }
            }
        } catch (ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException("Impossible d'initialiser le conteneur", e);
        }
    }

    private String getMappedUrl(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return requestMapping.value();
        }
        mg.itu.myframework.annotation.UrlMapping urlMapping =
                method.getAnnotation(mg.itu.myframework.annotation.UrlMapping.class);
        return urlMapping == null ? null : urlMapping.url();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        process(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        process(req, res);
    }

    private void process(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String url = processRequest(req, res);

        try {
            invoke(req, res, url);
        } catch (UrlNotFoundException e) {
            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            out.println("URL : " + url + "<br><br>");
            out.println(e.getMessage());
            printControllerList(out);
        }
    }

    private String processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        return path.isEmpty() ? "/" : path;

    }

    private void invoke(HttpServletRequest req, HttpServletResponse res, String url) throws UrlNotFoundException, ServletException, IOException {
        Mapping mapping = listUrlMapping.get(url);

        if (mapping == null) {
            throw new UrlNotFoundException(buildUrlNotFoundMessage(url));
        }

        try {
            Object result = mapping.getMethod().invoke(mapping.getController());

            if (mapping.getMethod().isAnnotationPresent(WebApi.class)) {
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");

                PrintWriter jsonWriter = res.getWriter();
                if (result instanceof String) {
                    jsonWriter.println((String) result);
                } else {
                    ObjectMapper objectMapper = new ObjectMapper();
                    jsonWriter.println(objectMapper.writeValueAsString(result));
                }
            } else if (result instanceof ModelAndView) {
                ModelAndView modelAndView = (ModelAndView) result;
                for (Map.Entry<String, Object> entry : modelAndView.getData().entrySet()) {
                    req.setAttribute(entry.getKey(), entry.getValue());
                }
                req.getRequestDispatcher(modelAndView.getView()).forward(req, res);
            } else {
                res.setContentType("text/html");
                PrintWriter out = res.getWriter();
                out.println("URL : " + url + "<br><br>");
                out.println("Classe : " + mapping.getController().getClass().getName() + "<br>");
                out.println("Méthode : " + mapping.getMethod().getName() + "<br><br>");
                if (result != null) {
                    out.println(result.toString());
                }
                printControllerList(out);
            }
        } catch (ReflectiveOperationException e) {
            throw new ServletException("Impossible d'invoquer " + mapping.getController().getClass().getName() + "." + mapping.getMethod().getName(), e);
        }
    }

    private void printControllerList(PrintWriter out) {
        out.println("<br><br>Liste des classes contrôleurs : <br>");
        for (String controller : listController) {
            out.println("- " + controller + "<br>");
        }
    }

    private String buildUrlNotFoundMessage(String url) {
        StringBuilder message = new StringBuilder();
        message.append("L'URL '").append(url).append("' n'est pas accessible, voici la liste des URL accessibles : <br>");
        message.append("<table border='1'>");
        message.append("<tr><th>URL</th><th>Classe</th><th>Méthode</th></tr>");
        if (listUrlMapping.isEmpty()) {
            message.append("<tr><td colspan=\"3\">Aucune URL n'a été trouvée</td></tr>");
        } else {
            for (Map.Entry<String, Mapping> entry : listUrlMapping.entrySet()) {
                String u = entry.getKey();
                Mapping m = entry.getValue();
                  message.append("<tr><td>").append(u).append("</td><td>")
                      .append(m.getController().getClass().getName()).append("</td><td>")
                      .append(m.getMethod().getName()).append("</td></tr>");
            }
        }
        message.append("</table>");
        return message.toString();
    }

}
