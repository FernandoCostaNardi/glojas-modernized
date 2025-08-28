package com.sysconard.business.controller;

import com.sysconard.business.dto.LoginRequest;
import com.sysconard.business.dto.LoginResponse;
import com.sysconard.business.entity.User;
import com.sysconard.business.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        logger.info("=== INICIANDO LOGIN ===");
        logger.info("Username recebido: {}", loginRequest.getUsername());
        logger.info("Password recebido: {}", loginRequest.getPassword() != null ? "***" : "NULL");
        
        try {
            logger.info("Criando token de autenticação...");
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            logger.info("Tentando autenticar com AuthenticationManager...");
            Authentication authentication = authenticationManager.authenticate(authToken);
            logger.info("Autenticação bem-sucedida!");
            
            // Gerar token JWT
            logger.info("Gerando token JWT...");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            logger.info("Token JWT gerado com sucesso!");
            
            // Buscar dados completos do usuário
            User user = (User) userDetails;
            logger.info("Usuário encontrado: {} (ID: {})", user.getUsername(), user.getId());
            
            // Extrair roles e permissions
            logger.info("Extraindo roles e permissions...");
            Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
            logger.info("Roles encontradas: {}", roles);
                
            Set<String> permissions = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());
            logger.info("Permissions encontradas: {}", permissions);
            
            // Criar resposta
            LoginResponse response = new LoginResponse(
                token,
                user.getUsername(),
                user.getName(),
                roles,
                permissions
            );
            
            logger.info("=== LOGIN CONCLUÍDO COM SUCESSO ===");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("=== ERRO NO LOGIN ===");
            logger.error("Exceção capturada: {}", e.getClass().getSimpleName());
            logger.error("Mensagem de erro: {}", e.getMessage());
            logger.error("Stack trace:", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Teste do endpoint /auth/test");
        return ResponseEntity.ok("Auth endpoint funcionando!");
    }
}
