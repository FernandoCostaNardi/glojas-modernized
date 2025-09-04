package com.sysconard.business.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysconard.business.dto.CreateUserRequest;
import com.sysconard.business.entity.Role;
import com.sysconard.business.entity.User;
import com.sysconard.business.repository.RoleRepository;
import com.sysconard.business.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para a API de Usuários
 * Utiliza @SpringBootTest para testar a aplicação completa
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("User API - Testes de Integração")
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Role userRole;
    private Role adminRole;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Limpar dados de teste
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Criar roles
        userRole = new Role();
        userRole.setName("USER");
        userRole = roleRepository.save(userRole);

        adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole = roleRepository.save(adminRole);

        // Criar usuário admin para testes
        adminUser = new User();
        adminUser.setName("Administrador");
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@exemplo.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setJoinDate(LocalDateTime.now());
        adminUser.setActive(true);
        adminUser.setNotLocked(true);
        adminUser.setRoles(Set.of(adminRole));
        adminUser = userRepository.save(adminUser);
    }

    @Nested
    @DisplayName("POST /users/create - Criação de Usuário")
    class CreateUserIntegrationTests {

        @Test
        @DisplayName("Deve criar usuário com sucesso quando dados são válidos")
        void shouldCreateUserSuccessfully() throws Exception {
            // Given
            CreateUserRequest request = new CreateUserRequest();
            request.setName("João Silva");
            request.setUsername("joao.silva");
            request.setEmail("joao.silva@exemplo.com");
            request.setPassword("senha123");
            request.setProfileImageUrl("https://exemplo.com/foto.jpg");
            request.setRoles(Set.of("USER"));

            // When & Then
            mockMvc.perform(post("/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.username").value("joao.silva"))
                .andExpect(jsonPath("$.email").value("joao.silva@exemplo.com"))
                .andExpect(jsonPath("$.profileImageUrl").value("https://exemplo.com/foto.jpg"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.isNotLocked").value(true))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("USER"))
                .andExpect(jsonPath("$.message").value("Usuário criado com sucesso"));

            // Verificar se o usuário foi salvo no banco
            User savedUser = userRepository.findByUsername("joao.silva").orElse(null);
            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getName()).isEqualTo("João Silva");
            assertThat(savedUser.getEmail()).isEqualTo("joao.silva@exemplo.com");
            assertThat(passwordEncoder.matches("senha123", savedUser.getPassword())).isTrue();
        }

        @Test
        @DisplayName("Deve retornar 400 quando username já existe")
        void shouldReturn400WhenUsernameAlreadyExists() throws Exception {
            // Given
            User existingUser = new User();
            existingUser.setName("Usuário Existente");
            existingUser.setUsername("joao.silva");
            existingUser.setEmail("existente@exemplo.com");
            existingUser.setPassword(passwordEncoder.encode("senha123"));
            existingUser.setJoinDate(LocalDateTime.now());
            existingUser.setActive(true);
            existingUser.setNotLocked(true);
            existingUser.setRoles(Set.of(userRole));
            userRepository.save(existingUser);

            CreateUserRequest request = new CreateUserRequest();
            request.setName("João Silva Novo");
            request.setUsername("joao.silva"); // Username duplicado
            request.setEmail("joao.novo@exemplo.com");
            request.setPassword("senha123");
            request.setRoles(Set.of("USER"));

            // When & Then
            mockMvc.perform(post("/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("username 'joao.silva' já está em uso"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando email já existe")
        void shouldReturn400WhenEmailAlreadyExists() throws Exception {
            // Given
            User existingUser = new User();
            existingUser.setName("Usuário Existente");
            existingUser.setUsername("existente");
            existingUser.setEmail("joao.silva@exemplo.com"); // Email duplicado
            existingUser.setPassword(passwordEncoder.encode("senha123"));
            existingUser.setJoinDate(LocalDateTime.now());
            existingUser.setActive(true);
            existingUser.setNotLocked(true);
            existingUser.setRoles(Set.of(userRole));
            userRepository.save(existingUser);

            CreateUserRequest request = new CreateUserRequest();
            request.setName("João Silva Novo");
            request.setUsername("joao.novo");
            request.setEmail("joao.silva@exemplo.com"); // Email duplicado
            request.setPassword("senha123");
            request.setRoles(Set.of("USER"));

            // When & Then
            mockMvc.perform(post("/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("email 'joao.silva@exemplo.com' já está em uso"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando role não existe")
        void shouldReturn400WhenRoleNotFound() throws Exception {
            // Given
            CreateUserRequest request = new CreateUserRequest();
            request.setName("João Silva");
            request.setUsername("joao.silva");
            request.setEmail("joao.silva@exemplo.com");
            request.setPassword("senha123");
            request.setRoles(Set.of("ROLE_INEXISTENTE")); // Role inexistente

            // When & Then
            mockMvc.perform(post("/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Role 'ROLE_INEXISTENTE' não encontrada no sistema"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando dados de validação são inválidos")
        void shouldReturn400WhenValidationFails() throws Exception {
            // Given
            CreateUserRequest invalidRequest = new CreateUserRequest();
            invalidRequest.setName(""); // Nome vazio
            invalidRequest.setUsername("joao.silva");
            invalidRequest.setEmail("email_invalido"); // Email inválido
            invalidRequest.setPassword("123"); // Senha muito curta
            invalidRequest.setRoles(Set.of("USER"));

            // When & Then
            mockMvc.perform(post("/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Erro de validação dos dados"))
                .andExpect(jsonPath("$.fieldErrors.name").value("Nome é obrigatório"))
                .andExpect(jsonPath("$.fieldErrors.email").value("Email deve ser válido"))
                .andExpect(jsonPath("$.fieldErrors.password").value("Senha deve ter entre 6 e 100 caracteres"));
        }

        @Test
        @DisplayName("Deve criar usuário com múltiplas roles")
        void shouldCreateUserWithMultipleRoles() throws Exception {
            // Given
            CreateUserRequest request = new CreateUserRequest();
            request.setName("João Silva");
            request.setUsername("joao.silva");
            request.setEmail("joao.silva@exemplo.com");
            request.setPassword("senha123");
            request.setRoles(Set.of("USER", "ADMIN"));

            // When & Then
            mockMvc.perform(post("/users/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles").value(org.hamcrest.Matchers.containsInAnyOrder("USER", "ADMIN")));

            // Verificar se o usuário foi salvo com múltiplas roles
            User savedUser = userRepository.findByUsername("joao.silva").orElse(null);
            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getRoles()).hasSize(2);
            assertThat(savedUser.getRoles().stream().map(Role::getName))
                .contains("USER", "ADMIN");
        }
    }

    @Nested
    @DisplayName("GET /users/check-username/{username} - Verificação de Username")
    class CheckUsernameIntegrationTests {

        @Test
        @DisplayName("Deve retornar que username está disponível")
        void shouldReturnUsernameAvailable() throws Exception {
            // When & Then
            mockMvc.perform(get("/users/check-username/novo_usuario"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("novo_usuario"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.message").value("Username disponível"));
        }

        @Test
        @DisplayName("Deve retornar que username não está disponível")
        void shouldReturnUsernameNotAvailable() throws Exception {
            // Given
            User existingUser = new User();
            existingUser.setName("Usuário Existente");
            existingUser.setUsername("usuario_existente");
            existingUser.setEmail("existente@exemplo.com");
            existingUser.setPassword(passwordEncoder.encode("senha123"));
            existingUser.setJoinDate(LocalDateTime.now());
            existingUser.setActive(true);
            existingUser.setNotLocked(true);
            existingUser.setRoles(Set.of(userRole));
            userRepository.save(existingUser);

            // When & Then
            mockMvc.perform(get("/users/check-username/usuario_existente"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("usuario_existente"))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.message").value("Username já está em uso"));
        }
    }

    @Nested
    @DisplayName("GET /users/check-email/{email} - Verificação de Email")
    class CheckEmailIntegrationTests {

        @Test
        @DisplayName("Deve retornar que email está disponível")
        void shouldReturnEmailAvailable() throws Exception {
            // When & Then
            mockMvc.perform(get("/users/check-email/novo@exemplo.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("novo@exemplo.com"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.message").value("Email disponível"));
        }

        @Test
        @DisplayName("Deve retornar que email não está disponível")
        void shouldReturnEmailNotAvailable() throws Exception {
            // Given
            User existingUser = new User();
            existingUser.setName("Usuário Existente");
            existingUser.setUsername("existente");
            existingUser.setEmail("email_existente@exemplo.com");
            existingUser.setPassword(passwordEncoder.encode("senha123"));
            existingUser.setJoinDate(LocalDateTime.now());
            existingUser.setActive(true);
            existingUser.setNotLocked(true);
            existingUser.setRoles(Set.of(userRole));
            userRepository.save(existingUser);

            // When & Then
            mockMvc.perform(get("/users/check-email/email_existente@exemplo.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("email_existente@exemplo.com"))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.message").value("Email já está em uso"));
        }
    }

    @Nested
    @DisplayName("GET /users/health - Health Check")
    class HealthCheckIntegrationTests {

        @Test
        @DisplayName("Deve retornar status de saúde do controller")
        void shouldReturnHealthStatus() throws Exception {
            // When & Then
            mockMvc.perform(get("/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("UserController"))
                .andExpect(jsonPath("$.version").value("1.0"))
                .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}
