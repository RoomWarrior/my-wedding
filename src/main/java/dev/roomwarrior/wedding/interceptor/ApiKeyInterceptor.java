
package dev.roomwarrior.wedding.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import dev.roomwarrior.wedding.annotation.RequireApiKey;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String UNAUTHORIZED_MESSAGE = "Invalid or missing API key";

    @Value("${api-key}")
    private String validApiKey;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        if (!isApiKeyRequired(handlerMethod)) {
            return true;
        }

        return validateApiKey(request, response);
    }

    private boolean isApiKeyRequired(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(RequireApiKey.class) ||
                handlerMethod.getBeanType().isAnnotationPresent(RequireApiKey.class);
    }

    private boolean validateApiKey(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String providedApiKey = request.getHeader(API_KEY_HEADER);

        if (providedApiKey == null || !providedApiKey.equals(validApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(UNAUTHORIZED_MESSAGE);
            return false;
        }

        return true;
    }
}
