package com.sysconard.business.config;

import com.sysconard.business.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        // Verificar se o header Authorization existe e começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extrair o token JWT (remover "Bearer " do início)
        jwt = authHeader.substring(7);
        
        try {
            // Extrair username do token
            username = jwtService.extractUsername(jwt);
            
            // Se temos um username e não há autenticação no contexto atual
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Carregar UserDetails do banco
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // Validar o token
                if (jwtService.validateToken(jwt, userDetails)) {
                    
                    // Criar token de autenticação
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    
                    // Definir detalhes da autenticação
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Definir a autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log do erro (opcional)
            logger.error("Erro ao processar JWT: " + e.getMessage());
        }
        
        // Continuar com a cadeia de filtros
        filterChain.doFilter(request, response);
    }
}
