package controller;

import mg.itu.myframework.annotation.Auth;
import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.RequestMapping;
import mg.itu.myframework.model.MapSession;
import mg.itu.myframework.mvc.ModelAndView;

@Controller
public class AuthController {
    @RequestMapping("/")
    public ModelAndView home() {
        return new ModelAndView("/index.jsp");
    }

    @RequestMapping("/login")
    public String login(String login, String mdp, MapSession session) {
        if ("admin".equals(login) && "admin".equals(mdp)) {
            session.add("user", login);
            session.add("role", "admin");
            return "Connexion admin reussie";
        }
        if ("user".equals(login) && "user".equals(mdp)) {
            session.add("user", login);
            session.add("role", "user");
            return "Connexion utilisateur reussie";
        }
        if ("manager".equals(login) && "manager".equals(mdp)) {
            session.add("user", login);
            session.add("role", "manager");
            return "Connexion manager reussie";
        }
        return "Identifiants invalides";
    }

    @RequestMapping("/logout")
    public String logout(MapSession session) {
        session.remove("user");
        session.remove("role");
        return "Deconnexion reussie";
    }

    @RequestMapping("/public")
    public String publicPage() {
        return "Page publique accessible";
    }

    @Auth
    @RequestMapping("/profile")
    public String profile() {
        return "Profil authentifie";
    }

    @Auth(role = "user")
    @RequestMapping("/user-area")
    public String userArea() {
        return "Espace utilisateur";
    }

    @Auth(role = "admin")
    @RequestMapping("/admin")
    public String adminArea() {
        return "Espace administrateur";
    }

    @Auth(role = "admin, manager")
    @RequestMapping("/management")
    public String managementArea() {
        return "Espace management";
    }
}
