package com.sysconard.legacy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler para tratar erros HTTP de forma mais amigável
 * Especialmente útil para erros de parsing HTTP (SSL/TLS handshake)
 */
@Slf4j
@Controller
public class HttpErrorHandler implements ErrorController {

    /**
     * Trata erros HTTP de forma amigável
     * Suprime logs de erro para problemas de parsing HTTP conhecidos
     */
    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        
        // Verifica se é um erro de parsing HTTP (SSL/TLS handshake)
        if (exception instanceof IllegalArgumentException) {
            String message = exception.toString();
            if (message.contains("Invalid character found in method name") || 
                message.contains("HTTP method names must be tokens")) {
                
                // Log amigável em vez de stack trace
                log.debug("Requisição HTTP inválida detectada (provavelmente handshake SSL/TLS em servidor HTTP) - ignorando");
                
                // Retorna resposta simples sem stack trace
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Bad Request");
                response.put("message", "Requisição HTTP inválida");
                response.put("status", 400);
                
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        // Para outros erros, mantém o comportamento padrão
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (status != null) {
            try {
                httpStatus = HttpStatus.valueOf(Integer.parseInt(status.toString()));
            } catch (NumberFormatException e) {
                // Mantém INTERNAL_SERVER_ERROR se não conseguir parsear
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", httpStatus.getReasonPhrase());
        response.put("message", "Erro interno do servidor");
        response.put("status", httpStatus.value());
        
        return ResponseEntity.status(httpStatus).body(response);
    }
}
