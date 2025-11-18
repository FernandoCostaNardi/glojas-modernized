package com.sysconard.legacy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor para bloquear operações de escrita (POST, PUT, DELETE, PATCH)
 * Garante que a API seja somente leitura, exceto para endpoints de relatório.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Component
public class ReadOnlyInterceptor implements HandlerInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(ReadOnlyInterceptor.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        
        log.info("=== ReadOnlyInterceptor ===");
        log.info("Método: {}", method);
        log.info("URI: {}", requestURI);
        log.info("Handler: {}", handler.getClass().getSimpleName());
        
        // Permite POST para endpoints de relatório (operações de consulta)
        if ("POST".equals(method) && isReportEndpoint(requestURI)) {
            log.info("✓ PERMITINDO POST para endpoint de relatório: {}", requestURI);
            return true;
        }
        
        // Bloqueia métodos de escrita para outros endpoints
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method)) {
            log.warn("✗ BLOQUEANDO operação {} para endpoint: {}", method, requestURI);
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.getWriter().write("{\"error\":\"Operação não permitida\",\"message\":\"Esta API é somente leitura (read-only)\",\"method\":\"" + method + "\"}");
            response.setContentType("application/json");
            return false;
        }
        
        log.info("✓ Permitindo operação {} para endpoint: {}", method, requestURI);
        return true;
    }
    
    /**
     * Verifica se o endpoint é um endpoint de relatório (consulta) que pode usar POST.
     * 
     * Permite os seguintes padrões:
     * - /api/sales/store-report (endpoint direto)
     * - /api/legacy/api/sales/store-report (endpoint com contexto legacy)
     * - Qualquer URI que contenha /store-report
     * - Qualquer URI que contenha /sale-items/details
     * - Qualquer URI que contenha /exchanges
     * 
     * @param requestURI URI da requisição
     * @return true se for um endpoint de relatório permitido
     */
    private boolean isReportEndpoint(String requestURI) {
        log.info("Verificando se é endpoint de relatório: {}", requestURI);
        
        // Verifica se contém padrões de endpoints de relatório
        boolean isReport = requestURI.contains("store-report") 
                || requestURI.contains("sale-items/details")
                || requestURI.contains("exchanges");
        
        log.info("Resultado final: {}", isReport ? "É ENDPOINT DE RELATÓRIO" : "NÃO É ENDPOINT DE RELATÓRIO");
        
        return isReport;
    }
}
