package controller;

import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.UrlMapping;

@Controller
public class EmpController {

    @UrlMapping(url = "/emp/new")
    public String create() {
        return "Formulaire de création d'un employé";
    }
}
