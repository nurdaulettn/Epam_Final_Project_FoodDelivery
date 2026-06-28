package kz.nurdaulet.config;

import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.exception.CategoryNotFoundException;
import kz.nurdaulet.exception.DeletingActiveFoodException;
import kz.nurdaulet.exception.FoodNotFoundException;
import kz.nurdaulet.exception.IncorrectAddingFoodException;
import kz.nurdaulet.exception.OrderNotFoundException;
import kz.nurdaulet.exception.OrderOperationException;
import kz.nurdaulet.exception.RestaurantNotFoundException;
import kz.nurdaulet.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            NoHandlerFoundException.class,
            NoResourceFoundException.class,
            FoodNotFoundException.class,
            RestaurantNotFoundException.class,
            CategoryNotFoundException.class,
            UserNotFoundException.class,
            OrderNotFoundException.class
    })
    public ModelAndView handleNotFound(RuntimeException exception) {
        log.warn("Resource not found: {}", exception.getMessage());

        return errorView(
                HttpStatus.NOT_FOUND,
                "Ничего не найдено",
                "Запрошенный объект не найден или у вас нет доступа к нему.",
                "/"
        );
    }

    @ExceptionHandler({
            CartOperationException.class,
            OrderOperationException.class,
            IncorrectAddingFoodException.class,
            DeletingActiveFoodException.class
    })
    public ModelAndView handleBusinessRule(RuntimeException exception) {
        log.warn("Business rule violation: {}", exception.getMessage());

        return errorView(
                HttpStatus.BAD_REQUEST,
                "Действие недоступно",
                exception.getMessage(),
                "/"
        );
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpected(Exception exception) {
        log.error("Unexpected application error", exception);

        return errorView(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ошибка сервера",
                "Произошла непредвиденная ошибка. Попробуйте повторить действие позже.",
                "/"
        );
    }

    private ModelAndView errorView(HttpStatus status, String title, String message, String backUrl) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.setStatus(status);
        modelAndView.addObject("errorStatus", status.value());
        modelAndView.addObject("errorTitle", title);
        modelAndView.addObject("errorMessage", message);
        modelAndView.addObject("errorBackUrl", backUrl);

        return modelAndView;
    }
}
