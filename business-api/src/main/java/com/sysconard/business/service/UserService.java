package com.sysconard.business.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sysconard.business.dto.CreateUserRequest;
import com.sysconard.business.dto.CreateUserResponse;
import com.sysconard.business.dto.UpdateUserRequest;
import com.sysconard.business.dto.UpdateUserResponse;
import com.sysconard.business.dto.ChangePasswordRequest;
import com.sysconard.business.dto.ChangePasswordResponse;
import com.sysconard.business.dto.UserSearchRequest;
import com.sysconard.business.dto.UserSearchResponse;
import com.sysconard.business.dto.UpdateUserStatusRequest;
import com.sysconard.business.dto.UpdateUserStatusResponse;
import com.sysconard.business.dto.UpdateUserLockRequest;
import com.sysconard.business.dto.UpdateUserLockResponse;
import com.sysconard.business.entity.Role;
import com.sysconard.business.entity.User;
import com.sysconard.business.exception.RoleNotFoundException;
import com.sysconard.business.exception.UserAlreadyExistsException;
import com.sysconard.business.exception.UserNotFoundException;
import com.sysconard.business.exception.InvalidPasswordException;
import com.sysconard.business.factory.UserFactory;
import com.sysconard.business.mapper.UserMapper;
import com.sysconard.business.repository.RoleRepository;
import com.sysconard.business.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Serviço responsável pela gestão de usuários no sistema.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 */
@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final UserValidationService userValidationService;
    private final UserMapper userMapper;
    private final UserFactory userFactory;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, 
                      RoleRepository roleRepository, 
                      UserValidationService userValidationService,
                      UserMapper userMapper,
                      UserFactory userFactory,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userValidationService = userValidationService;
        this.userMapper = userMapper;
        this.userFactory = userFactory;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Cria um novo usuário/colaborador no sistema
     * 
     * @param request Dados do usuário a ser criado
     * @return Resposta com os dados do usuário criado
     * @throws UserAlreadyExistsException se username ou email já existirem
     * @throws RoleNotFoundException se alguma role não existir
     */
    public CreateUserResponse createUser(CreateUserRequest request) {
        logger.info("Iniciando criação de usuário: {}", request.getUsername());
        
        // Validar dados de entrada
        userValidationService.validateUserCreation(request);
        
        // Buscar e validar roles
        Set<Role> roles = userValidationService.validateAndGetRoles(request.getRoles());
        
        // Criar e salvar usuário
        User user = userFactory.createUser(request, roles);
        User savedUser = userRepository.save(user);
        
        logger.info("Usuário criado com sucesso: {} (ID: {})", savedUser.getUsername(), savedUser.getId());
        
        // Preparar e retornar resposta
        CreateUserResponse response = userMapper.toCreateUserResponse(savedUser);
        logger.info("Resposta preparada para usuário: {}", savedUser.getUsername());
        
        return response;
    }
    
    /**
     * Verifica se um username já existe no sistema
     * 
     * @param username Username a ser verificado
     * @return true se o username existe, false caso contrário
     */
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    /**
     * Verifica se um email já existe no sistema
     * 
     * @param email Email a ser verificado
     * @return true se o email existe, false caso contrário
     */
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    /**
     * Busca um usuário pelo ID
     * 
     * @param id ID do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
    
    /**
     * Busca um usuário pelo username
     * 
     * @param username Username do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Atualiza um usuário existente no sistema
     * 
     * @param userId ID do usuário a ser atualizado
     * @param request Dados do usuário a ser atualizado
     * @return Resposta com os dados do usuário atualizado
     * @throws UserNotFoundException se o usuário não existir
     * @throws UserAlreadyExistsException se o email já existir em outro usuário
     * @throws RoleNotFoundException se alguma role não existir
     */
    public UpdateUserResponse updateUser(UUID userId, UpdateUserRequest request) {
        logger.info("Iniciando atualização de usuário: {} (ID: {})", request.getName(), userId);
        
        // Validar dados de entrada
        userValidationService.validateUserUpdate(userId, request);
        
        // Buscar usuário existente
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Buscar e validar roles
        Set<Role> roles = userValidationService.validateAndGetRoles(request.getRoles());
        
        // Atualizar dados do usuário
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setProfileImageUrl(request.getProfileImageUrl());
        existingUser.setActive(request.getIsActive());
        existingUser.setNotLocked(request.getIsNotLocked());
        existingUser.setRoles(roles);
        
        // Salvar usuário atualizado
        User updatedUser = userRepository.save(existingUser);
        
        logger.info("Usuário atualizado com sucesso: {} (ID: {})", updatedUser.getUsername(), updatedUser.getId());
        
        // Preparar e retornar resposta
        UpdateUserResponse response = userMapper.toUpdateUserResponse(updatedUser);
        logger.info("Resposta preparada para usuário atualizado: {}", updatedUser.getUsername());
        
        return response;
    }
    
    /**
     * Altera a senha de um usuário
     * 
     * @param userId ID do usuário
     * @param request Dados da alteração de senha
     * @return Resposta com os dados da alteração
     * @throws UserNotFoundException se o usuário não existir
     * @throws InvalidPasswordException se a senha atual estiver incorreta ou as senhas não coincidirem
     */
    public ChangePasswordResponse changePassword(UUID userId, ChangePasswordRequest request) {
        logger.info("Iniciando alteração de senha para usuário: {}", userId);
        
        // Validar dados de entrada
        userValidationService.validatePasswordChange(userId, request);
        
        // Buscar usuário existente
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Codificar e atualizar nova senha
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        existingUser.setPassword(encodedNewPassword);
        
        // Salvar usuário com nova senha
        User updatedUser = userRepository.save(existingUser);
        
        logger.info("Senha alterada com sucesso para usuário: {} (ID: {})", updatedUser.getUsername(), updatedUser.getId());
        
        // Preparar e retornar resposta
        ChangePasswordResponse response = userMapper.toChangePasswordResponse(updatedUser.getId(), updatedUser.getUsername());
        logger.info("Resposta preparada para alteração de senha: {}", updatedUser.getUsername());
        
        return response;
    }
    
    /**
     * Busca um usuário e retorna como UpdateUserResponse
     * 
     * @param userId ID do usuário
     * @return UpdateUserResponse com os dados do usuário
     * @throws UserNotFoundException se o usuário não existir
     */
    public UpdateUserResponse getUserResponse(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        return userMapper.toUpdateUserResponse(user);
    }
    
    /**
     * Busca usuários com filtros e paginação.
     * Permite filtrar por nome, roles, status ativo e bloqueado.
     * 
     * @param request Requisição com filtros e parâmetros de paginação
     * @return Página de usuários com informações básicas
     */
    public Page<UpdateUserResponse> findUsersWithFilters(UserSearchRequest request) {
        logger.info("Buscando usuários com filtros: name={}, roles={}, isActive={}, isNotLocked={}, page={}, size={}, sortBy={}, sortDir={}",
                request.getName(), request.getRoles(), request.getIsActive(), request.getIsNotLocked(),
                request.getPage(), request.getSize(), request.getSortBy(), request.getSortDir());
        
        try {
            // Criar Pageable com ordenação
            Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDir()), request.getSortBy());
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            
            // Buscar usuários com filtros básicos (sem roles)
            Page<User> usersPage = userRepository.findUsersWithFilters(
                request.getName(),
                request.getIsActive(),
                request.getIsNotLocked(),
                pageable
            );
            
            // Aplicar filtro de roles em memória se necessário
            List<User> filteredUsers = usersPage.getContent();
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                filteredUsers = filteredUsers.stream()
                    .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> request.getRoles().contains(role.getName())))
                    .collect(Collectors.toList());
            }
            
            // Criar nova página com usuários filtrados
            Pageable adjustedPageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            Page<User> adjustedPage = new PageImpl<>(filteredUsers, adjustedPageable, usersPage.getTotalElements());
            
            // Mapear para DTOs
            Page<UpdateUserResponse> responsePage = adjustedPage.map(userMapper::toUpdateUserResponse);
            
            logger.info("Encontrados {} usuários de {} total, página {} de {}",
                    responsePage.getNumberOfElements(), responsePage.getTotalElements(),
                    responsePage.getNumber() + 1, responsePage.getTotalPages());
            
            return responsePage;
            
        } catch (Exception e) {
            logger.error("Erro ao buscar usuários com filtros: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao buscar usuários", e);
        }
    }
    
    /**
     * Busca usuários com filtros, paginação e totalizadores.
     * Retorna uma resposta completa com usuários, paginação e contadores.
     * 
     * @param request Requisição com filtros e parâmetros de paginação
     * @return Resposta completa com usuários, paginação e totalizadores
     */
    public UserSearchResponse findUsersWithFiltersAndCounts(UserSearchRequest request) {
        logger.info("Buscando usuários com filtros e totalizadores: name={}, roles={}, isActive={}, isNotLocked={}, page={}, size={}, sortBy={}, sortDir={}",
                request.getName(), request.getRoles(), request.getIsActive(), request.getIsNotLocked(),
                request.getPage(), request.getSize(), request.getSortBy(), request.getSortDir());
        
        try {
            // Buscar usuários com filtros
            Page<UpdateUserResponse> usersPage = findUsersWithFilters(request);
            
            // Buscar totalizadores
            long totalActive = userRepository.countActiveUsers();
            long totalInactive = userRepository.countInactiveUsers();
            long totalBlocked = userRepository.countBlockedUsers();
            
            logger.info("Totalizadores encontrados: ativos={}, inativos={}, bloqueados={}",
                    totalActive, totalInactive, totalBlocked);
            
            // Criar resposta completa
            UserSearchResponse response = UserSearchResponse.fromPage(usersPage, totalActive, totalInactive, totalBlocked);
            
            logger.info("Resposta preparada com {} usuários e totalizadores", usersPage.getNumberOfElements());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Erro ao buscar usuários com totalizadores: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao buscar usuários com totalizadores", e);
        }
    }

    /**
     * Busca todos os usuários cadastrados no sistema (método legado).
     * Retorna uma lista com informações básicas de todos os usuários.
     * 
     * @return Lista de usuários com informações básicas
     */
    public List<UpdateUserResponse> findAllUsers() {
        logger.info("Buscando todos os usuários cadastrados no sistema");
        
        try {
            List<User> users = userRepository.findAll();
            
            List<UpdateUserResponse> userResponses = users.stream()
                    .map(userMapper::toUpdateUserResponse)
                    .collect(Collectors.toList());
            
            logger.info("Encontrados {} usuários no sistema", userResponses.size());
            
            return userResponses;
            
        } catch (Exception e) {
            logger.error("Erro ao buscar usuários: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao buscar usuários", e);
        }
    }

    /**
     * Altera o status ativo/inativo de um usuário.
     * Apenas usuários com role ADMIN podem executar esta operação.
     * 
     * @param userId ID do usuário a ser alterado
     * @param request Dados da alteração de status
     * @return Resposta com os dados da alteração
     * @throws UserNotFoundException se o usuário não existir
     */
    public UpdateUserStatusResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        logger.info("Alterando status do usuário: {} para isActive={}", userId, request.getIsActive());
        
        try {
            // Buscar usuário existente
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
            
            // Armazenar status anterior
            Boolean previousStatus = user.isActive();
            
            // Validar se não está tentando alterar para o mesmo status
            if (previousStatus.equals(request.getIsActive())) {
                String message = String.format("Usuário já está com status %s", 
                    request.getIsActive() ? "ativo" : "inativo");
                logger.warn("Tentativa de alterar status para o mesmo valor: {}", message);
                
                return UpdateUserStatusResponse.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .name(user.getName())
                    .previousStatus(previousStatus)
                    .newStatus(request.getIsActive())
                    .comment(request.getComment())
                    .updatedAt(java.time.LocalDateTime.now())
                    .message(message)
                    .build();
            }
            
            // Atualizar status
            user.setActive(request.getIsActive());
            User updatedUser = userRepository.save(user);
            
            // Preparar resposta
            UpdateUserStatusResponse response = UpdateUserStatusResponse.builder()
                .userId(userId)
                .username(updatedUser.getUsername())
                .name(updatedUser.getName())
                .previousStatus(previousStatus)
                .newStatus(request.getIsActive())
                .comment(request.getComment())
                .updatedAt(java.time.LocalDateTime.now())
                .message(String.format("Status alterado com sucesso de %s para %s", 
                    previousStatus ? "ativo" : "inativo",
                    request.getIsActive() ? "ativo" : "inativo"))
                .build();
            
            logger.info("Status do usuário {} alterado com sucesso: {} -> {}", 
                updatedUser.getUsername(), previousStatus, request.getIsActive());
            
            return response;
            
        } catch (UserNotFoundException e) {
            logger.error("Usuário não encontrado para alteração de status: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao alterar status do usuário {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Erro interno ao alterar status do usuário", e);
        }
    }
    
    /**
     * Altera o status de bloqueio de um usuário.
     * Apenas usuários com role ADMIN podem executar esta operação.
     * 
     * @param userId ID do usuário a ser alterado
     * @param request Dados da alteração de bloqueio
     * @return Resposta com os dados da alteração
     * @throws UserNotFoundException se o usuário não existir
     */
    public UpdateUserLockResponse updateUserLockStatus(UUID userId, UpdateUserLockRequest request) {
        logger.info("Alterando status de bloqueio do usuário: {} para isNotLocked={}", userId, request.getIsNotLocked());
        
        try {
            // Buscar usuário existente
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
            
            // Armazenar status anterior
            Boolean previousLockStatus = user.isNotLocked();
            
            // Validar se não está tentando alterar para o mesmo status
            if (previousLockStatus.equals(request.getIsNotLocked())) {
                String message = String.format("Usuário já está com status de bloqueio %s", 
                    request.getIsNotLocked() ? "não bloqueado" : "bloqueado");
                logger.warn("Tentativa de alterar status de bloqueio para o mesmo valor: {}", message);
                
                return UpdateUserLockResponse.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .name(user.getName())
                    .previousLockStatus(previousLockStatus)
                    .newLockStatus(request.getIsNotLocked())
                    .comment(request.getComment())
                    .updatedAt(java.time.LocalDateTime.now())
                    .message(message)
                    .build();
            }
            
            // Atualizar status de bloqueio
            user.setNotLocked(request.getIsNotLocked());
            User updatedUser = userRepository.save(user);
            
            // Preparar resposta
            UpdateUserLockResponse response = UpdateUserLockResponse.builder()
                .userId(userId)
                .username(updatedUser.getUsername())
                .name(updatedUser.getName())
                .previousLockStatus(previousLockStatus)
                .newLockStatus(request.getIsNotLocked())
                .comment(request.getComment())
                .updatedAt(java.time.LocalDateTime.now())
                .message(String.format("Status de bloqueio alterado com sucesso de %s para %s", 
                    previousLockStatus ? "não bloqueado" : "bloqueado",
                    request.getIsNotLocked() ? "não bloqueado" : "bloqueado"))
                .build();
            
            logger.info("Status de bloqueio do usuário {} alterado com sucesso: {} -> {}", 
                updatedUser.getUsername(), previousLockStatus, request.getIsNotLocked());
            
            return response;
            
        } catch (UserNotFoundException e) {
            logger.error("Usuário não encontrado para alteração de status de bloqueio: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao alterar status de bloqueio do usuário {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Erro interno ao alterar status de bloqueio do usuário", e);
        }
    }
}
