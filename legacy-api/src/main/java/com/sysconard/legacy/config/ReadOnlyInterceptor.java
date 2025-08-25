package com.sysconard.legacy.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor para bloquear operações de escrita (POST, PUT, DELETE, PATCH)
 * Garante que a API seja somente leitura.
 */
@Component
public class ReadOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        
        // Bloqueia métodos de escrita
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method)) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.getWriter().write("{\"error\":\"Operação não permitida\",\"message\":\"Esta API é somente leitura (read-only)\",\"method\":\"" + method + "\"}");
            response.setContentType("application/json");
            return false;
        }
        
        return true;
    }
}
