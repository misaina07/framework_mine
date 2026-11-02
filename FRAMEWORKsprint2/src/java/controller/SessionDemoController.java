package controller;

import mg.itu.myframework.annotation.Controller;
import mg.itu.myframework.annotation.RequestMapping;
import mg.itu.myframework.model.MapSession;
import mg.itu.myframework.mvc.ModelAndView;

@Controller
public class SessionDemoController {

    @RequestMapping("/session/increment")
    public ModelAndView increment(MapSession session) {
        Integer count = (Integer) session.getAttribute("count");
        count = (count == null) ? 1 : count + 1;
        session.setAttribute("count", count);

        ModelAndView model = new ModelAndView("/WEB-INF/views/session.jsp");
        model.addObject("count", count);
        return model;
    }

    @RequestMapping("/session/reset")
    public ModelAndView reset(MapSession session) {
        session.removeAttribute("count");
        ModelAndView model = new ModelAndView("/WEB-INF/views/session.jsp");
        model.addObject("count", 0);
        return model;
    }
}
