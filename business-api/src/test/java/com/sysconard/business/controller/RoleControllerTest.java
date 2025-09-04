package com.sysconard.business.controller;

import com.sysconard.business.dto.RoleResponse;
import com.sysconard.business.dto.UpdateRoleStatusRequest;
import com.sysconard.business.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para RoleController seguindo princípios de Clean Code.
 * Testa os endpoints REST relacionados às roles do sistema.
 */
@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Test
    void shouldUpdateRoleStatusSuccessfully() throws Exception {
        // Given
        UUID roleId = UUID.randomUUID();
        
        RoleResponse response = RoleResponse.builder()
                .id(roleId)
                .name("ADMIN")
                .description("Administrador do sistema")
                .active(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .permissionNames(Set.of("user:read", "user:write"))
                .build();

        when(roleService.updateRoleStatus(eq(roleId), any(UpdateRoleStatusRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(patch("/roles/{roleId}/status", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(roleId.toString()))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.permissionNames").isArray());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidJson() throws Exception {
        // Given
        UUID roleId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(patch("/roles/{roleId}/status", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": \"invalid\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {
        // Given
        UUID roleId = UUID.randomUUID();

        when(roleService.updateRoleStatus(eq(roleId), any(UpdateRoleStatusRequest.class)))
                .thenThrow(new RuntimeException("Erro interno"));

        // When & Then
        mockMvc.perform(patch("/roles/{roleId}/status", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isInternalServerError());
    }
}
