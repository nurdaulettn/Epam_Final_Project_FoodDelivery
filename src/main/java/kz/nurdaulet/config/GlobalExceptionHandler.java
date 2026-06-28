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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource not found: {}";
    private static final String LOG_BUSINESS_RULE_VIOLATION = "Business rule violation: {}";
    private static final String LOG_UNEXPECTED_APPLICATION_ERROR = "Unexpected application error";
    private static final String ERROR_VIEW = "error";
    private static final String HOME_URL = "/";
    private static final String NOT_FOUND_TITLE = "Nothing found";
    private static final String NOT_FOUND_MESSAGE = "The requested object was not found or you do not have access to it.";
    private static final String BUSINESS_RULE_TITLE = "Action is not available";
    private static final String SERVER_ERROR_TITLE = "Server error";
    private static final String SERVER_ERROR_MESSAGE = "An unexpected error occurred. Please try again later.";
    private static final String ERROR_STATUS_ATTRIBUTE = "errorStatus";
    private static final String ERROR_TITLE_ATTRIBUTE = "errorTitle";
    private static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    private static final String ERROR_BACK_URL_ATTRIBUTE = "errorBackUrl";

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
        log.warn(LOG_RESOURCE_NOT_FOUND, exception.getMessage());

        return errorView(
                HttpStatus.NOT_FOUND,
                NOT_FOUND_TITLE,
                NOT_FOUND_MESSAGE,
                HOME_URL
        );
    }

    @ExceptionHandler({
            CartOperationException.class,
            OrderOperationException.class,
            IncorrectAddingFoodException.class,
            DeletingActiveFoodException.class
    })
    public ModelAndView handleBusinessRule(RuntimeException exception) {
        log.warn(LOG_BUSINESS_RULE_VIOLATION, exception.getMessage());

        return errorView(
                HttpStatus.BAD_REQUEST,
                BUSINESS_RULE_TITLE,
                exception.getMessage(),
                HOME_URL
        );
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpected(Exception exception) {
        log.error(LOG_UNEXPECTED_APPLICATION_ERROR, exception);

        return errorView(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_TITLE,
                SERVER_ERROR_MESSAGE,
                HOME_URL
        );
    }

    private ModelAndView errorView(HttpStatus status, String title, String message, String backUrl) {
        ModelAndView modelAndView = new ModelAndView(ERROR_VIEW);
        modelAndView.setStatus(status);
        modelAndView.addObject(ERROR_STATUS_ATTRIBUTE, status.value());
        modelAndView.addObject(ERROR_TITLE_ATTRIBUTE, title);
        modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, message);
        modelAndView.addObject(ERROR_BACK_URL_ATTRIBUTE, backUrl);

        return modelAndView;
    }
}
