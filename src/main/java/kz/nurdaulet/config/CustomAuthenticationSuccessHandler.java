package kz.nurdaulet.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String MANAGER_ROLE = "ROLE_MANAGER";
    private static final String CUSTOMER_ROLE = "ROLE_CUSTOMER";
    private static final String LOG_AUTHENTICATED_ADMIN = "User {} authenticated with admin role";
    private static final String LOG_AUTHENTICATED_MANAGER = "User {} authenticated with manager role";
    private static final String LOG_AUTHENTICATED_CUSTOMER = "User {} authenticated with customer role";
    private static final String LOG_AUTHENTICATED_UNSUPPORTED_ROLE = "User {} authenticated without supported role";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ADMIN_ROLE))) {
            log.info(LOG_AUTHENTICATED_ADMIN, authentication.getName());
            response.sendRedirect(request.getContextPath() + "/admin/create-requests");
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(MANAGER_ROLE))) {
            log.info(LOG_AUTHENTICATED_MANAGER, authentication.getName());
            response.sendRedirect(request.getContextPath() + "/restaurants/manager/my-restaurants");
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(CUSTOMER_ROLE))) {
            log.info(LOG_AUTHENTICATED_CUSTOMER, authentication.getName());
            response.sendRedirect(request.getContextPath() + "/restaurants");
        } else {
            log.warn(LOG_AUTHENTICATED_UNSUPPORTED_ROLE, authentication.getName());
            response.sendRedirect(request.getContextPath() + "/restaurants");
        }
    }
}
