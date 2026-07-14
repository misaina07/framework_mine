package controller;

import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.UrlMapping;

@Controller
public class EmployeController {

    @UrlMapping(url = "/emp/liste")
    public String liste() {
        return "Liste des employés";
    }
}
