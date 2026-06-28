package kz.nurdaulet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {
    private static final String NOTHING_FOUND = "Nothing found";
    private static final String THIS_PAGE_DOES_NOT_EXIST = "This page doesn't exist or expired";

    @GetMapping("/error/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView notFound() {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorStatus", HttpStatus.NOT_FOUND.value());
        modelAndView.addObject("errorTitle", NOTHING_FOUND);
        modelAndView.addObject("errorMessage", THIS_PAGE_DOES_NOT_EXIST);
        modelAndView.addObject("errorBackUrl", "/");

        return modelAndView;
    }
}
