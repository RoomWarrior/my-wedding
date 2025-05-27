package dev.roomwarrior.wedding.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import dev.roomwarrior.wedding.annotation.RequireApiKey;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${api-key}")
    private String validApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // Проверяем наличие аннотации на методе или классе
        boolean hasAnnotation = handlerMethod.hasMethodAnnotation(RequireApiKey.class) ||
                handlerMethod.getBeanType().isAnnotationPresent(RequireApiKey.class);

        if (!hasAnnotation) {
            return true;
        }

        // Получаем API ключ из заголовка
        String apiKey = request.getHeader("X-API-Key");

        // Проверяем наличие и валидность API ключа
        if (apiKey == null || !apiKey.equals(validApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or missing API key");
            return false;
        }

        return true;
    }
} 
