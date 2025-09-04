package com.sysconard.business.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.sysconard.business.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handler para exceções de validação (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Erro de validação detectado: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "ERROR",
            "Erro de validação dos dados",
            request.getDescription(false),
            fieldErrors
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handler para usuário já existente
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, WebRequest request) {
        
        logger.warn("Tentativa de criar usuário já existente: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "ERROR",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handler para role não encontrada
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRoleNotFoundException(
            RoleNotFoundException ex, WebRequest request) {
        
        logger.warn("Role não encontrada: {}", ex.getRoleName());
        
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "ERROR",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handler para exceções de autenticação
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        logger.warn("Erro de autenticação: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "ERROR",
            "Credenciais inválidas",
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * Handler para exceções de autorização
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        logger.warn("Acesso negado: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "ERROR",
            "Acesso negado. Você não tem permissão para executar esta operação.",
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    /**
     * Handler para exceções de argumento ilegal
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.warn("Argumento ilegal: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "ERROR",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handler para exceções gerais não tratadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Erro inesperado: {}", ex.getMessage(), ex);
        
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "ERROR",
            "Erro interno do servidor. Tente novamente mais tarde.",
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
