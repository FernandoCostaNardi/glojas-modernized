package com.sysconard.business.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sysconard.business.dto.security.LoginRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidLoginRequest() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        // When
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Then
        assertThat(loginRequest.email()).isEqualTo(email);
        assertThat(loginRequest.password()).isEqualTo(password);
    }

    @Test
    void shouldValidateEmailFormat() {
        // Given
        LoginRequest invalidEmail = new LoginRequest("invalid-email", "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(invalidEmail);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Formato de email inválido");
    }

    @Test
    void shouldValidateEmailRequired() {
        // Given
        LoginRequest emptyEmail = new LoginRequest("", "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(emptyEmail);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email é obrigatório");
    }

    @Test
    void shouldValidatePasswordRequired() {
        // Given
        LoginRequest emptyPassword = new LoginRequest("test@example.com", "");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(emptyPassword);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Senha é obrigatória");
    }

    @Test
    void shouldPassValidationWithValidData() {
        // Given
        LoginRequest validRequest = new LoginRequest("test@example.com", "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).isEmpty();
    }
}
