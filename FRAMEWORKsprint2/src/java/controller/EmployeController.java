package controller;

import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.UrlMapping;
import mg.itu.myframework.mvc.ModelAndView;

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
}
