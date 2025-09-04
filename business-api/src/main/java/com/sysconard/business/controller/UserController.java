package com.sysconard.business.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sysconard.business.dto.ChangePasswordRequest;
import com.sysconard.business.dto.ChangePasswordResponse;
import com.sysconard.business.dto.CreateUserRequest;
import com.sysconard.business.dto.CreateUserResponse;
import com.sysconard.business.dto.UpdateUserLockRequest;
import com.sysconard.business.dto.UpdateUserLockResponse;
import com.sysconard.business.dto.UpdateUserRequest;
import com.sysconard.business.dto.UpdateUserResponse;
import com.sysconard.business.dto.UpdateUserStatusRequest;
import com.sysconard.business.dto.UpdateUserStatusResponse;
import com.sysconard.business.dto.UserSearchRequest;
import com.sysconard.business.dto.UserSearchResponse;
import com.sysconard.business.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    private final UserService userService;
    
    /**
     * Cria um novo colaborador no sistema
     * Apenas usuários com permissão user:create podem acessar este endpoint
     * 
     * @param request Dados do colaborador a ser criado
     * @return Resposta com os dados do colaborador criado
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Recebida requisição para criar usuário: {}", request.getUsername());
        
        CreateUserResponse response = userService.createUser(request);
        log.info("Usuário criado com sucesso: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Verifica se um username está disponível
     * 
     * @param username Username a ser verificado
     * @return true se disponível, false se já existe
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(@PathVariable String username) {
        log.debug("Verificando disponibilidade do username: {}", username);
        
        boolean isAvailable = !userService.usernameExists(username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "Username disponível" : "Username já está em uso");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Verifica se um email está disponível
     * 
     * @param email Email a ser verificado
     * @return true se disponível, false se já existe
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@PathVariable String email) {
        log.debug("Verificando disponibilidade do email: {}", email);
        
        boolean isAvailable = !userService.emailExists(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "Email disponível" : "Email já está em uso");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Atualiza um usuário existente no sistema
     * Apenas usuários com role ADMIN podem acessar este endpoint
     * 
     * @param userId ID do usuário a ser atualizado
     * @param request Dados do usuário a ser atualizado
     * @return Resposta com os dados do usuário atualizado
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Recebida requisição para atualizar usuário: {} (ID: {})", request.getName(), userId);
        
        UpdateUserResponse response = userService.updateUser(userId, request);
        log.info("Usuário atualizado com sucesso: {} (ID: {})", response.getUsername(), response.getId());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Altera a senha de um usuário
     * O usuário pode alterar sua própria senha ou ADMIN pode alterar qualquer senha
     * 
     * @param userId ID do usuário
     * @param request Dados da alteração de senha
     * @return Resposta com os dados da alteração
     */
    @PutMapping("/{userId}/change-password")
    @PreAuthorize("hasAuthority('user:change-password')")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Recebida requisição para alterar senha do usuário: {}", userId);
        
        ChangePasswordResponse response = userService.changePassword(userId, request);
        log.info("Senha alterada com sucesso para usuário: {} (ID: {})", response.getUsername(), response.getUserId());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca um usuário pelo ID
     * ADMIN pode buscar qualquer usuário, usuário comum pode buscar apenas seus próprios dados
     * 
     * @param userId ID do usuário
     * @return Dados do usuário
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UpdateUserResponse> getUserById(@PathVariable UUID userId) {
        log.debug("Recebida requisição para buscar usuário: {}", userId);
        
        UpdateUserResponse response = userService.getUserResponse(userId);
        log.debug("Usuário encontrado: {} (ID: {})", response.getUsername(), response.getId());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca usuários com filtros e paginação.
     * Apenas usuários com role ADMIN podem acessar este endpoint.
     * 
     * @param name Filtro por nome (busca parcial)
     * @param roles Filtro por roles específicas (separadas por vírgula)
     * @param isActive Filtro por status ativo
     * @param isNotLocked Filtro por status não bloqueado
     * @param page Número da página (baseado em 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc ou desc)
     * @return Resposta com usuários, paginação e totalizadores
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserSearchResponse> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String roles,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isNotLocked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Recebida requisição para buscar usuários com filtros: name={}, roles={}, isActive={}, isNotLocked={}, page={}, size={}, sortBy={}, sortDir={}",
                name, roles, isActive, isNotLocked, page, size, sortBy, sortDir);
        
        try {
            // Converter string de roles para Set
            Set<String> rolesSet = null;
            if (roles != null && !roles.trim().isEmpty()) {
                rolesSet = Set.of(roles.split(","));
            }
            
            // Criar request de busca
            UserSearchRequest searchRequest = UserSearchRequest.builder()
                    .name(name)
                    .roles(rolesSet)
                    .isActive(isActive)
                    .isNotLocked(isNotLocked)
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .sortDir(sortDir)
                    .build();
            
            UserSearchResponse response = userService.findUsersWithFiltersAndCounts(searchRequest);
            
            log.info("Retornando {} usuários da página {} de {} total, com totalizadores: ativos={}, inativos={}, bloqueados={}",
                    response.getPagination().getPageSize(), response.getPagination().getCurrentPage() + 1, 
                    response.getPagination().getTotalPages(), response.getCounts().getTotalActive(),
                    response.getCounts().getTotalInactive(), response.getCounts().getTotalBlocked());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao buscar usuários: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Busca todos os usuários cadastrados no sistema (sem filtros).
     * Apenas usuários com role ADMIN podem acessar este endpoint.
     * 
     * @return Lista de todos os usuários cadastrados
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<UpdateUserResponse>> getAllUsersWithoutFilters() {
        log.info("Recebida requisição para buscar todos os usuários sem filtros");
        
        try {
            List<UpdateUserResponse> users = userService.findAllUsers();
            
            log.info("Retornando {} usuários encontrados", users.size());
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            log.error("Erro ao buscar usuários: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Altera o status ativo/inativo de um usuário.
     * Apenas usuários com role ADMIN podem acessar este endpoint.
     * 
     * @param userId ID do usuário a ser alterado
     * @param request Dados da alteração de status
     * @return Resposta com os dados da alteração
     */
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<UpdateUserStatusResponse> updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        
        log.info("Recebida requisição para alterar status do usuário: {} (ID: {}) para isActive={}", 
                request.getIsActive() ? "ativo" : "inativo", userId, request.getIsActive());
        
        try {
            UpdateUserStatusResponse response = userService.updateUserStatus(userId, request);
            
            log.info("Status do usuário alterado com sucesso: {} (ID: {}) - {} -> {}", 
                    response.getUsername(), response.getUserId(), 
                    response.getPreviousStatus() ? "ativo" : "inativo",
                    response.getNewStatus() ? "ativo" : "inativo");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao alterar status do usuário {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Altera o status de bloqueio de um usuário.
     * Apenas usuários com role ADMIN podem acessar este endpoint.
     * 
     * @param userId ID do usuário a ser alterado
     * @param request Dados da alteração de bloqueio
     * @return Resposta com os dados da alteração
     */
    @PatchMapping("/{userId}/lock")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<UpdateUserLockResponse> updateUserLockStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserLockRequest request) {
        
        log.info("Recebida requisição para alterar status de bloqueio do usuário: {} (ID: {}) para isNotLocked={}", 
                request.getIsNotLocked() ? "não bloqueado" : "bloqueado", userId, request.getIsNotLocked());
        
        try {
            UpdateUserLockResponse response = userService.updateUserLockStatus(userId, request);
            
            log.info("Status de bloqueio do usuário alterado com sucesso: {} (ID: {}) - {} -> {}", 
                    response.getUsername(), response.getUserId(), 
                    response.getPreviousLockStatus() ? "não bloqueado" : "bloqueado",
                    response.getNewLockStatus() ? "não bloqueado" : "bloqueado");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao alterar status de bloqueio do usuário {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de health check para o UserController
     * 
     * @return Status de saúde do controller
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("Health check do UserController");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "UserController");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("version", "1.0");
        
        return ResponseEntity.ok(response);
    }
}
