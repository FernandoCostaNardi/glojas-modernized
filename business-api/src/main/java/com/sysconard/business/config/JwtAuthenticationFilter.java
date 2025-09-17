package com.sysconard.business.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sysconard.business.service.security.JwtService;

import java.io.IOException;

/**
 * Filtro de autenticação JWT que processa tokens Bearer em requisições HTTP.
 * 
 * Este filtro intercepta todas as requisições e verifica a presença de um token JWT
 * válido no header Authorization. Se o token for válido, estabelece a autenticação
 * no contexto de segurança do Spring Security.
 * 
 * @author Sistema Glojas
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    /**
     * Processa a requisição HTTP para autenticação JWT.
     * 
     * @param request Requisição HTTP
     * @param response Resposta HTTP
     * @param filterChain Cadeia de filtros
     * @throws ServletException Se ocorrer erro no servlet
     * @throws IOException Se ocorrer erro de I/O
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        log.debug("Processing JWT authentication for request: {}", request.getRequestURI());
        
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (!isValidAuthHeader(authHeader)) {
            log.debug("No valid authorization header found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwtToken = extractJwtToken(authHeader);
        
        try {
            processJwtAuthentication(jwtToken, request);
        } catch (Exception e) {
            log.error("Error processing JWT authentication for request: {}, error: {}", 
                     request.getRequestURI(), e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Verifica se o header de autorização é válido.
     * 
     * @param authHeader Header de autorização
     * @return true se o header for válido, false caso contrário
     */
    private boolean isValidAuthHeader(String authHeader) {
        return authHeader != null && authHeader.startsWith(BEARER_PREFIX);
    }
    
    /**
     * Extrai o token JWT do header de autorização.
     * 
     * @param authHeader Header de autorização
     * @return Token JWT sem o prefixo "Bearer "
     */
    private String extractJwtToken(String authHeader) {
        return authHeader.substring(BEARER_PREFIX.length());
    }
    
    /**
     * Processa a autenticação JWT se o token for válido.
     * 
     * @param jwtToken Token JWT
     * @param request Requisição HTTP
     */
    private void processJwtAuthentication(String jwtToken, HttpServletRequest request) {
        String username = jwtService.extractUsername(jwtToken);
        
        if (username == null || isUserAlreadyAuthenticated()) {
            log.debug("Username is null or user already authenticated, skipping JWT processing");
            return;
        }
        
        UserDetails userDetails = loadUserDetails(username);
        
        if (jwtService.validateToken(jwtToken, userDetails)) {
            setSecurityContext(userDetails, request);
            log.debug("JWT authentication successful for user: {}", username);
        } else {
            log.warn("JWT token validation failed for user: {}", username);
        }
    }
    
    /**
     * Verifica se o usuário já está autenticado no contexto de segurança.
     * 
     * @return true se já estiver autenticado, false caso contrário
     */
    private boolean isUserAlreadyAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }
    
    /**
     * Carrega os detalhes do usuário pelo username.
     * 
     * @param username Nome do usuário
     * @return Detalhes do usuário
     */
    private UserDetails loadUserDetails(String username) {
        return userDetailsService.loadUserByUsername(username);
    }
    
    /**
     * Define o contexto de segurança com a autenticação do usuário.
     * 
     * @param userDetails Detalhes do usuário
     * @param request Requisição HTTP
     */
    private void setSecurityContext(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(userDetails, request);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    
    /**
     * Cria o token de autenticação do Spring Security.
     * 
     * @param userDetails Detalhes do usuário
     * @param request Requisição HTTP
     * @return Token de autenticação
     */
    private UsernamePasswordAuthenticationToken createAuthenticationToken(
            UserDetails userDetails, 
            HttpServletRequest request) {
        
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authToken;
    }
}
