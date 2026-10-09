package mg.itu.myframework.controller;

import java.io.*;
import jakarta.servlet.*;
import java.util.*;
import jakarta.servlet.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.WebApi;
import mg.itu.myframework.exception.UrlNotFoundException;
import mg.itu.myframework.exception.ParameterBindingException;
import mg.itu.myframework.listener.FrameworkListener;
import mg.itu.myframework.mvc.ModelAndView;
import mg.itu.myframework.util.MethodClassMapping;
import mg.itu.myframework.util.ParameterBinder;

@Controller
public class FrontControllerServlet extends HttpServlet {
    private List<String> listController;
    private Map<String, MethodClassMapping> listUrlMapping;

    // init
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        listUrlMapping = (Map<String, MethodClassMapping>) getServletContext().getAttribute(FrameworkListener.ATTR_URL_MAPPING);
        listController = (List<String>) getServletContext().getAttribute(FrameworkListener.ATTR_CONTROLLERS);
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
        } catch (ParameterBindingException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private String processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        return path.isEmpty() ? "/" : path;

    }

    private void invoke(HttpServletRequest req, HttpServletResponse res, String url) throws UrlNotFoundException, ServletException, IOException {
        MethodClassMapping mapping = listUrlMapping.get(url);

        if (mapping == null) {
            throw new UrlNotFoundException(buildUrlNotFoundMessage(url));
        }

        try {
            Object controllerInstance = mapping.getClasse().getDeclaredConstructor().newInstance();
            Object[] arguments = ParameterBinder.bind(mapping.getMethode(), req, res);
            Object result = mapping.getMethode().invoke(controllerInstance, arguments);

            if (mapping.getMethode().isAnnotationPresent(WebApi.class)) {
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
                out.println("Classe : " + mapping.getClasse().getName() + "<br>");
                out.println("Méthode : " + mapping.getMethode().getName() + "<br><br>");
                if (result != null) {
                    out.println(result.toString());
                }
                printControllerList(out);
            }
        } catch (ReflectiveOperationException e) {
            throw new ServletException("Impossible d'invoquer " + mapping.getClasse().getName() + "." + mapping.getMethode().getName(), e);
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
