package controller;

import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.UrlMapping;
import mg.itu.myframework.annotation.WebApi;

import java.util.Arrays;
import java.util.List;

@Controller
public class EmpController {

    @UrlMapping(url = "/emp/new")
    public String create() {
        return "Formulaire de création d'un employé";
    }

    @WebApi
    @UrlMapping(url = "/api/employes")
    public List<String> apiEmployes() {
        return Arrays.asList("Rakoto", "Rabe", "Randria");
    }
}
