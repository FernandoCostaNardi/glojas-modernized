package com.sysconard.business.service;

import com.sysconard.business.dto.security.UpdateUserLockRequest;
import com.sysconard.business.dto.security.UpdateUserLockResponse;
import com.sysconard.business.dto.security.UpdateUserStatusRequest;
import com.sysconard.business.dto.security.UpdateUserStatusResponse;
import com.sysconard.business.entity.security.User;
import com.sysconard.business.exception.security.UserNotFoundException;
import com.sysconard.business.repository.security.UserRepository;
import com.sysconard.business.service.security.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceStatusTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldUpdateUserStatusSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        UpdateUserStatusRequest request = UpdateUserStatusRequest.builder()
                .isActive(false)
                .comment("Usuário inativo temporariamente")
                .build();

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .name("Test User")
                .isActive(true)
                .isNotLocked(true)
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .username("testuser")
                .name("Test User")
                .isActive(false)
                .isNotLocked(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UpdateUserStatusResponse response = userService.updateUserStatus(userId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getPreviousStatus()).isTrue();
        assertThat(response.getNewStatus()).isFalse();
        assertThat(response.getComment()).isEqualTo("Usuário inativo temporariamente");
        assertThat(response.getMessage()).contains("Status alterado com sucesso");
    }

    @Test
    void shouldUpdateUserLockStatusSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        UpdateUserLockRequest request = UpdateUserLockRequest.builder()
                .isNotLocked(false)
                .comment("Usuário bloqueado por violação")
                .build();

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .name("Test User")
                .isActive(true)
                .isNotLocked(true)
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .username("testuser")
                .name("Test User")
                .isActive(true)
                .isNotLocked(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UpdateUserLockResponse response = userService.updateUserLockStatus(userId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getPreviousLockStatus()).isTrue();
        assertThat(response.getNewLockStatus()).isFalse();
        assertThat(response.getComment()).isEqualTo("Usuário bloqueado por violação");
        assertThat(response.getMessage()).contains("Status de bloqueio alterado com sucesso");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UpdateUserStatusRequest request = UpdateUserStatusRequest.builder()
                .isActive(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUserStatus(userId, request))
                .isInstanceOf(UserNotFoundException.class);
    }
}
