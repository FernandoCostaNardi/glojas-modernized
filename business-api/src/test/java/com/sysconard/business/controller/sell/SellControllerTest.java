package com.sysconard.business.controller.sell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sysconard.business.dto.sell.StoreReportRequest;
import com.sysconard.business.dto.sell.StoreReportResponse;
import com.sysconard.business.service.sell.SellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para o SellController.
 * Valida o comportamento da API de relatórios de vendas.
 * 
 * @author Business API
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(SellController.class)
class SellControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private SellService sellService;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    @WithMockUser(authorities = "sell:read")
    void shouldReturnStoreReportSuccessfully() throws Exception {
        // Given
        StoreReportRequest request = StoreReportRequest.builder()
                .startDate(LocalDate.of(2025, 9, 13))
                .endDate(LocalDate.of(2025, 9, 13))
                .storeCodes(List.of("000002", "000003"))
                .build();
        
        List<StoreReportResponse> expectedResponse = List.of(
                StoreReportResponse.builder()
                        .storeName("CD JANGURUSSU")
                        .storeCode("000002")
                        .danfe(BigDecimal.ZERO)
                        .pdv(BigDecimal.ZERO)
                        .troca(BigDecimal.ZERO)
                        .build(),
                StoreReportResponse.builder()
                        .storeName("SMART ANT. SALES")
                        .storeCode("000003")
                        .danfe(new BigDecimal("1788.0000"))
                        .pdv(new BigDecimal("184.3200"))
                        .troca(BigDecimal.ZERO)
                        .build()
        );
        
        when(sellService.getStoreReport(any(StoreReportRequest.class)))
                .thenReturn(expectedResponse);
        
        // When & Then
        mockMvc.perform(post("/sales/store-report")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].storeName").value("CD JANGURUSSU"))
                .andExpect(jsonPath("$[0].storeCode").value("000002"))
                .andExpect(jsonPath("$[0].danfe").value(0))
                .andExpect(jsonPath("$[1].storeName").value("SMART ANT. SALES"))
                .andExpect(jsonPath("$[1].storeCode").value("000003"))
                .andExpect(jsonPath("$[1].danfe").value(1788.0000));
    }
    
    @Test
    @WithMockUser(authorities = "sell:read")
    void shouldReturnStoreReportForUserRole() throws Exception {
        // Given
        StoreReportRequest request = StoreReportRequest.builder()
                .startDate(LocalDate.of(2025, 9, 13))
                .endDate(LocalDate.of(2025, 9, 13))
                .storeCodes(List.of("000002"))
                .build();
        
        List<StoreReportResponse> expectedResponse = List.of(
                StoreReportResponse.builder()
                        .storeName("CD JANGURUSSU")
                        .storeCode("000002")
                        .danfe(BigDecimal.ZERO)
                        .pdv(BigDecimal.ZERO)
                        .troca(BigDecimal.ZERO)
                        .build()
        );
        
        when(sellService.getStoreReport(any(StoreReportRequest.class)))
                .thenReturn(expectedResponse);
        
        // When & Then
        mockMvc.perform(post("/sales/store-report")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    @Test
    void shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        // Given
        StoreReportRequest request = StoreReportRequest.builder()
                .startDate(LocalDate.of(2025, 9, 13))
                .endDate(LocalDate.of(2025, 9, 13))
                .storeCodes(List.of("000002"))
                .build();
        
        // When & Then
        mockMvc.perform(post("/sales/store-report")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(authorities = "sell:read")
    void shouldReturnBadRequestForInvalidDates() throws Exception {
        // Given - Data início posterior à data fim
        StoreReportRequest request = StoreReportRequest.builder()
                .startDate(LocalDate.of(2025, 9, 14))
                .endDate(LocalDate.of(2025, 9, 13))
                .storeCodes(List.of("000002"))
                .build();
        
        // When & Then
        mockMvc.perform(post("/sales/store-report")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(authorities = "sell:read")
    void shouldReturnBadRequestForEmptyStoreCodes() throws Exception {
        // Given
        String requestJson = """
                {
                    "startDate": "2025-09-13",
                    "endDate": "2025-09-13",
                    "storeCodes": []
                }
                """;
        
        // When & Then
        mockMvc.perform(post("/sales/store-report")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @WithMockUser(authorities = "sell:read")
    void shouldReturnBadRequestForMissingRequiredFields() throws Exception {
        // Given - Request sem campos obrigatórios
        String requestJson = """
                {
                    "storeCodes": ["000002"]
                }
                """;
        
        // When & Then
        mockMvc.perform(post("/sales/store-report")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }
}
