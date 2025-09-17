package com.sysconard.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sysconard.business.entity.security.User;
import com.sysconard.business.repository.security.UserRepository;
import com.sysconard.business.service.security.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void shouldLoadUserByEmailSuccessfully() {
        // Given
        String email = "test@example.com";
        UUID userId = UUID.randomUUID();
        
        User user = User.builder()
                .id(userId)
                .username("testuser")
                .email(email)
                .name("Test User")
                .isActive(true)
                .isNotLocked(true)
                .roles(new HashSet<>())
                .build();

        when(userRepository.findByEmailWithRolesAndPermissions(email)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmailWithRolesAndPermissions(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com email: " + email);
    }
}
