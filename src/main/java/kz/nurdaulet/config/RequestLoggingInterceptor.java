package kz.nurdaulet.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = "requestStartTime";
    private static final String LOG_HTTP_REQUEST_STARTED = "HTTP request started: method={}, uri={}, query={}";
    private static final String LOG_HTTP_REQUEST_COMPLETED = "HTTP request completed: method={}, uri={}, status={}, durationMs={}";
    private static final String LOG_HTTP_REQUEST_FAILED = "HTTP request failed: method={}, uri={}, status={}, durationMs={}";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        log.debug(LOG_HTTP_REQUEST_STARTED,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception exception) {
        long durationMs = calculateDuration(request);

        if (exception == null) {
            log.info(LOG_HTTP_REQUEST_COMPLETED,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMs);
        } else {
            log.error(LOG_HTTP_REQUEST_FAILED,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMs,
                    exception);
        }
    }

    private long calculateDuration(HttpServletRequest request) {
        Object startTime = request.getAttribute(START_TIME_ATTRIBUTE);

        if (startTime instanceof Long startedAt) {
            return System.currentTimeMillis() - startedAt;
        }

        return 0L;
    }
}
