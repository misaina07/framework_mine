package mg.itu.myframework.controller;

import java.io.*;
import jakarta.servlet.*;
import java.util.*;
import jakarta.servlet.http.*;
import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.exception.UrlNotFoundException;
import mg.itu.myframework.util.ClassUtil;
import mg.itu.myframework.util.MethodClassMapping;

@Controller
public class FrontControllerServlet extends HttpServlet {
    private List<String> listController = new ArrayList<>();
    private Map<String, MethodClassMapping> listUrlMapping = new HashMap<>();

    // init
    public void init() throws ServletException {
        List<String> packageNames = new ArrayList<>();
        packageNames.add("mg.itu.myframework.controller");
        packageNames.add("controller");

        List<Class<?>> controllers = ClassUtil.getClassesWithAnnotation(packageNames, listUrlMapping, Controller.class);
        for (Class<?> controller : controllers) {
            listController.add(controller.getName());
        }

    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        process(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        process(req, res);
    }

    private void process(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        String url = processRequest(req, res);
        out.println("URL : " + url + "<br><br>");

        try {
            invoke(url, out);
        } catch (UrlNotFoundException e) {
            out.println(e.getMessage());
        }

        out.println("<br><br>Liste des classes contrôleurs : <br>");
        for (String controller : listController) {
            out.println("- " + controller + "<br>");
        }
    }

    private String processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String url = req.getRequestURL().toString();
        String[] urlParts = url.split("/");
        String path = "";
        for (int i = 4 ; i < urlParts.length; i++) {
            path += "/" + urlParts[i];
        }

        return path;

    }

    private void invoke(String url, PrintWriter out) throws UrlNotFoundException, ServletException {
        MethodClassMapping mapping = listUrlMapping.get(url);

        if (mapping == null) {
            throw new UrlNotFoundException(buildUrlNotFoundMessage(url));
        }

        try {
            Object controllerInstance = mapping.getClasse().getDeclaredConstructor().newInstance();
            Object result = mapping.getMethode().invoke(controllerInstance);
            out.println("Classe : " + mapping.getClasse().getName() + "<br>");
            out.println("Méthode : " + mapping.getMethode().getName() + "<br><br>");
            if (result != null) {
                out.println(result.toString());
            }
        } catch (ReflectiveOperationException e) {
            throw new ServletException("Impossible d'invoquer " + mapping.getClasse().getName() + "." + mapping.getMethode().getName(), e);
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
            for (Map.Entry<String, MethodClassMapping> entry : listUrlMapping.entrySet()) {
                String u = entry.getKey();
                MethodClassMapping m = entry.getValue();
                message.append("<tr><td>").append(u).append("</td><td>")
                       .append(m.getClasse().getName()).append("</td><td>")
                       .append(m.getMethode().getName()).append("</td></tr>");
            }
        }
        message.append("</table>");
        return message.toString();
    }

}
