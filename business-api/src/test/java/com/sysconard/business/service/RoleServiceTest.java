package com.sysconard.business.service;

import com.sysconard.business.dto.CreateRoleRequest;
import com.sysconard.business.dto.RoleResponse;
import com.sysconard.business.dto.UpdateRoleStatusRequest;
import com.sysconard.business.entity.Role;
import com.sysconard.business.entity.Permission;
import com.sysconard.business.repository.RoleRepository;
import com.sysconard.business.repository.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para RoleService seguindo princípios de Clean Code.
 * Testa a lógica de negócio relacionada às roles do sistema.
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void shouldUpdateRoleStatusSuccessfully() {
        // Given
        UUID roleId = UUID.randomUUID();
        UpdateRoleStatusRequest request = new UpdateRoleStatusRequest(false);
        
        Role existingRole = Role.builder()
                .id(roleId)
                .name("ADMIN")
                .description("Administrador do sistema")
                .active(true)
                .permissions(new HashSet<>())
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setUpdatedAt(LocalDateTime.now());
            return role;
        });

        // When
        RoleResponse response = roleService.updateRoleStatus(roleId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(roleId);
        assertThat(response.getName()).isEqualTo("ADMIN");
        assertThat(response.isActive()).isFalse();
        assertThat(response.getUpdatedAt()).isAfter(response.getCreatedAt());

        verify(roleRepository).findById(roleId);
        verify(roleRepository).save(existingRole);
    }

    @Test
    void shouldNotUpdateWhenStatusIsAlreadyTheSame() {
        // Given
        UUID roleId = UUID.randomUUID();
        UpdateRoleStatusRequest request = new UpdateRoleStatusRequest(true);
        
        Role existingRole = Role.builder()
                .id(roleId)
                .name("USER")
                .description("Usuário comum")
                .active(true)
                .permissions(new HashSet<>())
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

        // When
        RoleResponse response = roleService.updateRoleStatus(roleId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(roleId);
        assertThat(response.isActive()).isTrue();
        assertThat(response.getUpdatedAt()).isEqualTo(existingRole.getUpdatedAt());

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldThrowExceptionWhenRoleNotFound() {
        // Given
        UUID roleId = UUID.randomUUID();
        UpdateRoleStatusRequest request = new UpdateRoleStatusRequest(false);

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roleService.updateRoleStatus(roleId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role não encontrada com ID: " + roleId);

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldCreateRoleSuccessfully() {
        // Given
        UUID permissionId = UUID.randomUUID();
        CreateRoleRequest request = CreateRoleRequest.builder()
                .name("MANAGER")
                .description("Gerente do sistema")
                .permissionIds(Set.of(permissionId))
                .build();

        Permission permission = Permission.builder()
                .id(permissionId)
                .name("user:read")
                .resource("user")
                .action("read")
                .description("Ler usuários")
                .build();

        when(roleRepository.existsByName("MANAGER")).thenReturn(false);
        when(permissionRepository.findAllById(Set.of(permissionId))).thenReturn(List.of(permission));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(UUID.randomUUID());
            role.setCreatedAt(LocalDateTime.now());
            role.setUpdatedAt(LocalDateTime.now());
            return role;
        });

        // When
        RoleResponse response = roleService.createRole(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("MANAGER");
        assertThat(response.getDescription()).isEqualTo("Gerente do sistema");
        assertThat(response.isActive()).isTrue();
        assertThat(response.getPermissionNames()).contains("user:read");

        verify(roleRepository).existsByName("MANAGER");
        verify(permissionRepository).findAllById(Set.of(permissionId));
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void shouldThrowExceptionWhenRoleNameAlreadyExists() {
        // Given
        CreateRoleRequest request = CreateRoleRequest.builder()
                .name("ADMIN")
                .description("Administrador")
                .permissionIds(Set.of(UUID.randomUUID()))
                .build();

        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> roleService.createRole(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Já existe uma role com o nome: ADMIN");

        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldFindAllActiveRoles() {
        // Given
        Role activeRole = Role.builder()
                .id(UUID.randomUUID())
                .name("USER")
                .description("Usuário comum")
                .active(true)
                .permissions(new HashSet<>())
                .build();

        Role inactiveRole = Role.builder()
                .id(UUID.randomUUID())
                .name("GUEST")
                .description("Convidado")
                .active(false)
                .permissions(new HashSet<>())
                .build();

        when(roleRepository.findAll()).thenReturn(List.of(activeRole, inactiveRole));

        // When
        List<RoleResponse> response = roleService.findAllActiveRoles();

        // Then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("USER");
        assertThat(response.get(0).isActive()).isTrue();

        verify(roleRepository).findAll();
    }
}
