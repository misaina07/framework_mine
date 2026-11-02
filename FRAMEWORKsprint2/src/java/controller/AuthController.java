package controller;

import mg.itu.myframework.annotation.Auth;
import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.UrlMapping;
import mg.itu.myframework.model.MapSession;
import mg.itu.myframework.mvc.ModelAndView;

@Controller
public class AuthController {

    @UrlMapping(url = "/login")
    public ModelAndView loginForm() {
        return new ModelAndView("/login.jsp");
    }

    @UrlMapping(url = "/login/check")
    public String login(String login, String mdp, MapSession session) {
        if ("admin".equals(login) && "admin".equals(mdp)) {
            session.add("user", login);
            return "Connexion reussie. <a href='protected'>Acceder a la page protegee</a>";
        }
        return "Identifiants invalides. <a href='login'>Reessayer</a>";
    }

    @UrlMapping(url = "/logout")
    public String logout(MapSession session) {
        session.remove("user");
        return "Deconnexion reussie. <a href='login'>Se connecter</a>";
    }

    @UrlMapping(url = "/public")
    public String publicPage() {
        return "Page publique accessible sans authentification.";
    }

    @Auth
    @UrlMapping(url = "/protected")
    public String protectedPage() {
        return "Page protegee accessible.";
    }
}