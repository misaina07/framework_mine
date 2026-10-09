package controller;

import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.UrlMapping;
import mg.itu.myframework.annotation.WebApi;
import mg.itu.myframework.mvc.ModelAndView;

import java.util.Arrays;
import java.util.List;

@Controller
public class EmpController {

    @UrlMapping(url = "/emp/new")
    public ModelAndView create() {
        return new ModelAndView("/formulaire.jsp");
    }

    @UrlMapping(url = "/emp/save")
    public String save(String nom, int age) {
        return "Employe enregistre : " + nom + " (" + age + " ans)";
    }

    @WebApi
    @UrlMapping(url = "/api/employes")
    public List<String> apiEmployes() {
        return Arrays.asList("Rakoto", "Rabe", "Randria");
    }
}
