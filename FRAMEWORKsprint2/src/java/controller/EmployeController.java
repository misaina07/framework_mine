package controller;

import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.UrlMapping;
import mg.itu.myframework.mvc.ModelAndView;
import model.Employe;

import java.util.Arrays;
import java.util.List;

@Controller
public class EmployeController {

    @UrlMapping(url = "/emp/liste")
    public ModelAndView liste() {
        List<String> employes = Arrays.asList("Rakoto", "Rabe", "Randria");

        ModelAndView modelAndView = new ModelAndView("/liste.jsp");
        modelAndView.addObject("employes", employes);
        return modelAndView;
    }

    @UrlMapping(url = "/emp/new-object")
    public ModelAndView nouveau() {
        return new ModelAndView("/formulaire-objet.jsp");
    }

    @UrlMapping(url = "/emp/save-object")
    public String enregistrer(Employe employe) {
        return "Employe enregistre : " + employe.getNom()
                + " (" + employe.getAge() + " ans, " + employe.getEmail() + ")";
    }
}
