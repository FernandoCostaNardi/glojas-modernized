package com.sysconard.business.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import com.sysconard.business.dto.config.ApiErrorResponse;
import com.sysconard.business.exception.config.GlobalExceptionHandler;
import com.sysconard.business.exception.security.RoleNotFoundException;
import com.sysconard.business.exception.security.UserAlreadyExistsException;

/**
 * Testes unitários para o GlobalExceptionHandler
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - Testes Unitários")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private WebRequest mockWebRequest;

    @BeforeEach
    void setUp() {
        mockWebRequest = mock(WebRequest.class);
        when(mockWebRequest.getDescription(false)).thenReturn("/api/business/users/create");
    }
    
    /**
     * Extrai e valida o body da resposta HTTP de forma segura.
     * 
     * @param response Resposta HTTP
     * @return Body da resposta validado e não nulo
     */
    private ApiErrorResponse extractValidatedBody(ResponseEntity<ApiErrorResponse> response) {
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        return body;
    }

    @Nested
    @DisplayName("handleValidationExceptions() - Validação de Dados")
    class ValidationExceptionTests {

        @Test
        @DisplayName("Deve retornar 400 com erros de campo quando validação falha")
        void shouldReturn400WithFieldErrorsWhenValidationFails() {
            // Given
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError nameError = new FieldError("createUserRequest", "name", "Nome é obrigatório");
            FieldError emailError = new FieldError("createUserRequest", "email", "Email deve ser válido");
            FieldError passwordError = new FieldError("createUserRequest", "password", "Senha deve ter entre 6 e 100 caracteres");

            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(nameError, emailError, passwordError));

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, mockWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            
            ApiErrorResponse body = extractValidatedBody(response);
            assertThat(body.getStatus()).isEqualTo("ERROR");
            assertThat(body.getMessage()).isEqualTo("Erro de validação dos dados");
            assertThat(body.getPath()).isEqualTo("/api/business/users/create");
            assertThat(body.getFieldErrors()).isNotNull();
            assertThat(body.getFieldErrors()).containsKeys("name", "email", "password");
            assertThat(body.getFieldErrors().get("name")).isEqualTo("Nome é obrigatório");
            assertThat(body.getFieldErrors().get("email")).isEqualTo("Email deve ser válido");
            assertThat(body.getFieldErrors().get("password")).isEqualTo("Senha deve ter entre 6 e 100 caracteres");
        }
    }

    @Nested
    @DisplayName("handleUserAlreadyExistsException() - Usuário Já Existe")
    class UserAlreadyExistsExceptionTests {

        @Test
        @DisplayName("Deve retornar 400 quando username já existe")
        void shouldReturn400WhenUsernameAlreadyExists() {
            // Given
            UserAlreadyExistsException ex = new UserAlreadyExistsException("username", "joao.silva");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleUserAlreadyExistsException(ex, mockWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            
            ApiErrorResponse body = extractValidatedBody(response);
            assertThat(body.getStatus()).isEqualTo("ERROR");
            assertThat(body.getMessage()).isEqualTo("username 'joao.silva' já está em uso");
            assertThat(body.getPath()).isEqualTo("/api/business/users/create");
        }

        @Test
        @DisplayName("Deve retornar 400 quando email já existe")
        void shouldReturn400WhenEmailAlreadyExists() {
            // Given
            UserAlreadyExistsException ex = new UserAlreadyExistsException("email", "joao.silva@exemplo.com");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleUserAlreadyExistsException(ex, mockWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            
            ApiErrorResponse body = extractValidatedBody(response);
            assertThat(body.getStatus()).isEqualTo("ERROR");
            assertThat(body.getMessage()).isEqualTo("email 'joao.silva@exemplo.com' já está em uso");
            assertThat(body.getPath()).isEqualTo("/api/business/users/create");
        }
    }

    @Nested
    @DisplayName("handleRoleNotFoundException() - Role Não Encontrada")
    class RoleNotFoundExceptionTests {

        @Test
        @DisplayName("Deve retornar 400 quando role não existe")
        void shouldReturn400WhenRoleNotFound() {
            // Given
            RoleNotFoundException ex = new RoleNotFoundException("ROLE_INEXISTENTE");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleRoleNotFoundException(ex, mockWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            
            ApiErrorResponse body = extractValidatedBody(response);
            assertThat(body.getStatus()).isEqualTo("ERROR");
            assertThat(body.getMessage()).isEqualTo("Role 'ROLE_INEXISTENTE' não encontrada no sistema");
            assertThat(body.getPath()).isEqualTo("/api/business/users/create");
        }
    }

    @Nested
    @DisplayName("handleAuthenticationException() - Erro de Autenticação")
    class AuthenticationExceptionTests {

        @Test
        @DisplayName("Deve retornar 401 quando há erro de autenticação")
        void shouldReturn401WhenAuthenticationFails() {
            // Given
            AuthenticationException ex = mock(AuthenticationException.class);
            when(ex.getMessage()).thenReturn("Credenciais inválidas");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleAuthenticationException(ex, mockWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            
            ApiErrorResponse body = extractValidatedBody(response);
            assertThat(body.getStatus()).isEqualTo("ERROR");
            assertThat(body.getMessage()).isEqualTo("Credenciais inválidas");
            assertThat(body.getPath()).isEqualTo("/api/business/users/create");
        }
    }

    @Nested
    @DisplayName("handleAccessDeniedException() - Acesso Negado")
    class AccessDeniedExceptionTests {

        @Test
        @DisplayName("Deve retornar 403 quando acesso é negado")
        void shouldReturn403WhenAccessDenied() {
            // Given
            AccessDeniedException ex = new AccessDeniedException("Acesso negado");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleAccessDeniedException(ex, mockWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            
            ApiErrorResponse body = extractValidatedBody(response);
            assertThat(body.getStatus()).isEqualTo("ERROR");
            assertThat(body.getMessage()).isEqualTo("Acesso negado. Você não tem permissão para executar esta operação.");
            assertThat(body.getPath()).isEqualTo("/api/business/users/create");
        }
    }

    @Nested
    @DisplayName("handleIllegalArgumentException() - Argumento Ilegal")
    class IllegalArgumentExceptionTests {

        @Test
        @DisplayName("Deve retornar 400 quando argumento é ilegal")
        void shouldReturn400WhenArgumentIsIllegal() {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, mockWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            
            ApiErrorResponse body = extractValidatedBody(response);
            assertThat(body.getStatus()).isEqualTo("ERROR");
            assertThat(body.getMessage()).isEqualTo("Argumento inválido");
            assertThat(body.getPath()).isEqualTo("/api/business/users/create");
        }
    }

    @Nested
    @DisplayName("handleGenericException() - Exceção Genérica")
    class GenericExceptionTests {

        @Test
        @DisplayName("Deve retornar 500 quando exceção genérica ocorre")
        void shouldReturn500WhenGenericExceptionOccurs() {
            // Given
            Exception ex = new RuntimeException("Erro interno do servidor");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleGenericException(ex, mockWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            
            ApiErrorResponse body = extractValidatedBody(response);
            assertThat(body.getStatus()).isEqualTo("ERROR");
            assertThat(body.getMessage()).isEqualTo("Erro interno do servidor. Tente novamente mais tarde.");
            assertThat(body.getPath()).isEqualTo("/api/business/users/create");
        }
    }
}
