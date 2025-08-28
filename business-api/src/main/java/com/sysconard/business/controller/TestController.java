package com.sysconard.business.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Endpoint público funcionando!");
    }
    
    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Mostrar authorities do usuário
        String authorities = authentication.getAuthorities().stream()
            .map(auth -> auth.getAuthority())
            .collect(java.util.stream.Collectors.joining(", "));
            
        return ResponseEntity.ok(String.format(
            "Endpoint protegido funcionando! Usuário: %s | Authorities: %s", 
            username, authorities));
    }
    
    @GetMapping("/admin")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("Endpoint ADMIN funcionando! Usuário: " + authentication.getName());
    }
    
    @GetMapping("/manager") 
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<String> managerEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("Endpoint MANAGER funcionando! Usuário: " + authentication.getName());
    }
    
    @GetMapping("/user-permission")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<String> userPermissionEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("Endpoint USER_READ permission funcionando! Usuário: " + authentication.getName());
    }
    
    @GetMapping("/debug-authorities")
    public ResponseEntity<String> debugAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok("Usuário não autenticado");
        }
        
        String authorities = authentication.getAuthorities().stream()
            .map(auth -> auth.getAuthority())
            .collect(java.util.stream.Collectors.joining(", "));
            
        return ResponseEntity.ok(String.format(
            "Usuário: %s | Authorities: [%s]", 
            authentication.getName(), authorities));
    }

    @GetMapping("/bcrypt-test")
    public ResponseEntity<String> bcryptTest() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "admin123";
        String currentHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        
        boolean matches = encoder.matches(password, currentHash);
        String newHash = encoder.encode(password);
        
        String result = String.format(
            "Teste BCrypt:\n" +
            "Senha: %s\n" +
            "Hash atual: %s\n" +
            "Hash atual está correto? %s\n" +
            "Novo hash gerado: %s\n" +
            "Novo hash está correto? %s", 
            password, currentHash, matches, newHash, encoder.matches(password, newHash));
            
        return ResponseEntity.ok(result);
    }
}
