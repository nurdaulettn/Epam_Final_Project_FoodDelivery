package kz.nurdaulet.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String MANAGER_ROLE = "ROLE_MANAGER";
    private static final String CUSTOMER_ROLE = "ROLE_CUSTOMER";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ADMIN_ROLE))) {
            response.sendRedirect(request.getContextPath() + "/admin/create-requests");
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(MANAGER_ROLE))) {
            response.sendRedirect(request.getContextPath() + "/restaurants/manager/my-restaurants");
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(CUSTOMER_ROLE))) {
            response.sendRedirect(request.getContextPath() + "/restaurants");
        } else {
            response.sendRedirect(request.getContextPath() + "/restaurants");
        }
    }
}
