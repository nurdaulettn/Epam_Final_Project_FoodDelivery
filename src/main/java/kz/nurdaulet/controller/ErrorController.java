package kz.nurdaulet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

    @GetMapping("/error/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView notFound() {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorStatus", HttpStatus.NOT_FOUND.value());
        modelAndView.addObject("errorTitle", "Ничего не найдено");
        modelAndView.addObject("errorMessage", "Такой страницы не существует или ссылка устарела.");
        modelAndView.addObject("errorBackUrl", "/");

        return modelAndView;
    }
}
