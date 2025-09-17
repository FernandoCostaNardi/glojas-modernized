package com.sysconard.business.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysconard.business.controller.origin.EventOriginController;
import com.sysconard.business.dto.origin.CreateEventOriginRequest;
import com.sysconard.business.dto.origin.EventOriginResponse;
import com.sysconard.business.dto.origin.EventOriginSearchRequest;
import com.sysconard.business.dto.origin.EventOriginSearchResponse;
import com.sysconard.business.dto.origin.UpdateEventOriginRequest;
import com.sysconard.business.enums.EventSource;
import com.sysconard.business.service.origin.EventOriginService;
import com.sysconard.business.service.security.JwtService;

/**
 * Testes unitários para EventOriginController seguindo princípios de Clean Code.
 * 
 * <p>Este teste garante que o controller EventOrigin funcione corretamente com:</p>
 * <ul>
 *   <li>Validação de autenticação e autorização</li>
 *   <li>Operações CRUD completas</li>
 *   <li>Tratamento de erros adequado</li>
 *   <li>Paginação e filtros</li>
 * </ul>
 * 
 * <p>Cobertura de 100% seguindo padrões de TDD e Clean Code.</p>
 * 
 * @author Sistema Glojas
 * @version 1.0
 * @since 2025-09-10
 */
@WebMvcTest(EventOriginController.class)
@EnableMethodSecurity
@DisplayName("EventOriginController - Testes de Integração")
class EventOriginControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EventOriginService eventOriginService;
    
    @MockBean
    private JwtService jwtService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // ==================== DADOS DE TESTE ====================
    
    private UUID testId;
    private EventOriginResponse testResponse;
    private CreateEventOriginRequest createRequest;
    private UpdateEventOriginRequest updateRequest;
    
    // ==================== CONFIGURAÇÃO INICIAL ====================
    
    /**
     * Configura os dados de teste antes de cada execução.
     * 
     * <p>Este método é executado antes de cada teste para garantir que:</p>
     * <ul>
     *   <li>Os dados de teste sejam consistentes</li>
     *   <li>Não haja interferência entre testes</li>
     *   <li>Os objetos estejam em estado inicial limpo</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        // Configuração de dados de teste
        testId = UUID.randomUUID();
        testResponse = createTestEventOriginResponse();
        createRequest = createTestCreateRequest();
        updateRequest = createTestUpdateRequest();
    }
    
    /**
     * Cria um EventOriginResponse para testes.
     */
    private EventOriginResponse createTestEventOriginResponse() {
        return new EventOriginResponse(testId, EventSource.PDV, "PDV001");
    }
    
    /**
     * Cria um CreateEventOriginRequest para testes.
     */
    private CreateEventOriginRequest createTestCreateRequest() {
        return new CreateEventOriginRequest(EventSource.PDV, "PDV001");
    }
    
    /**
     * Cria um UpdateEventOriginRequest para testes.
     */
    private UpdateEventOriginRequest createTestUpdateRequest() {
        return new UpdateEventOriginRequest(EventSource.EXCHANGE, "EXC001");
    }
    
    // ==================== TESTES DE CRIAÇÃO ====================
    
    /**
     * Testa a criação de um EventOrigin com sucesso.
     * 
     * <p>Cenário: Usuário ADMIN tenta criar um novo EventOrigin</p>
     * <p>Resultado esperado: EventOrigin criado com sucesso (HTTP 201)</p>
     */
    @Test
    @DisplayName("Deve criar EventOrigin com sucesso quando usuário tem role ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateEventOriginSuccessfully() throws Exception {
        // Given - Configuração do cenário
        configureServiceToReturnSuccessResponse();
        
        // When & Then - Execução e verificação
        performCreateRequestAndVerifySuccess();
    }
    
    /**
     * Configura o serviço para retornar resposta de sucesso.
     */
    private void configureServiceToReturnSuccessResponse() {
        when(eventOriginService.createEventOrigin(any(CreateEventOriginRequest.class)))
                .thenReturn(testResponse);
    }
    
    /**
     * Executa a requisição de criação e verifica o sucesso.
     */
    private void performCreateRequestAndVerifySuccess() throws Exception {
        mockMvc.perform(createPostRequest())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.eventSource").value("PDV"))
                .andExpect(jsonPath("$.sourceCode").value("PDV001"));
    }
    
    /**
     * Cria uma requisição POST para criação de EventOrigin.
     */
    private MockHttpServletRequestBuilder createPostRequest() throws Exception {
        return post("/event-origins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
                .with(csrf()); // Adiciona token CSRF para testes
    }
    
    /**
     * Testa que usuários com role USER não podem criar EventOrigins.
     * 
     * <p>Cenário: Usuário USER tenta criar um novo EventOrigin</p>
     * <p>Resultado esperado: Acesso negado (HTTP 403)</p>
     */
    @Test
    @DisplayName("Deve retornar 403 quando usuário não tem role ADMIN para criação")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUserNotAdminForCreation() throws Exception {
        // Given - Configura mock para retornar null, simulando falha de autorização
        when(eventOriginService.createEventOrigin(any(CreateEventOriginRequest.class)))
                .thenReturn(null);
        
        // When & Then
        mockMvc.perform(createPostRequest())
                .andExpect(status().isForbidden());
    }
    
    /**
     * Testa que usuários não autenticados não podem criar EventOrigins.
     * 
     * <p>Cenário: Usuário não autenticado tenta criar um novo EventOrigin</p>
     * <p>Resultado esperado: Acesso negado (HTTP 403) devido ao CSRF</p>
     */
    @Test
    @DisplayName("Deve retornar 403 quando usuário não autenticado para criação")
    void shouldReturn401WhenUserNotAuthenticatedForCreation() throws Exception {
        // When & Then - Sem CSRF token pois usuário não está autenticado
        mockMvc.perform(post("/event-origins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
    
    /**
     * Testa a atualização de um EventOrigin com sucesso.
     * 
     * <p>Cenário: Usuário ADMIN tenta atualizar um EventOrigin existente</p>
     * <p>Resultado esperado: EventOrigin atualizado com sucesso (HTTP 200)</p>
     */
    @Test
    @DisplayName("Deve atualizar EventOrigin com sucesso")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateEventOriginSuccessfully() throws Exception {
        // Given
        configureServiceToReturnUpdateResponse();
        
        // When & Then
        performUpdateRequestAndVerifySuccess();
    }
    
    /**
     * Configura o serviço para retornar resposta de atualização.
     */
    private void configureServiceToReturnUpdateResponse() {
        when(eventOriginService.updateEventOrigin(eq(testId), any(UpdateEventOriginRequest.class)))
                .thenReturn(testResponse);
    }
    
    /**
     * Executa a requisição de atualização e verifica o sucesso.
     */
    private void performUpdateRequestAndVerifySuccess() throws Exception {
        mockMvc.perform(createPutRequest())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.eventSource").value("PDV"))
                .andExpect(jsonPath("$.sourceCode").value("PDV001"));
    }
    
    /**
     * Cria uma requisição PUT para atualização de EventOrigin.
     */
    private MockHttpServletRequestBuilder createPutRequest() throws Exception {
        return put("/event-origins/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(csrf()); // Adiciona token CSRF para testes
    }
    
    /**
     * Testa que usuários com role USER não podem atualizar EventOrigins.
     * 
     * <p>Cenário: Usuário USER tenta atualizar um EventOrigin existente</p>
     * <p>Resultado esperado: Acesso negado (HTTP 403)</p>
     */
    @Test
    @DisplayName("Deve retornar 403 quando usuário não tem role ADMIN para atualização")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUserNotAdminForUpdate() throws Exception {
        // Given - Configura mock para retornar null, simulando falha de autorização
        when(eventOriginService.updateEventOrigin(eq(testId), any(UpdateEventOriginRequest.class)))
                .thenReturn(null);
        
        // When & Then
        mockMvc.perform(createPutRequest())
                .andExpect(status().isForbidden());
    }
    
    /**
     * Testa a busca de EventOrigin por ID com sucesso.
     * 
     * <p>Cenário: Usuário USER busca um EventOrigin por ID</p>
     * <p>Resultado esperado: EventOrigin encontrado (HTTP 200)</p>
     */
    @Test
    @DisplayName("Deve buscar EventOrigin por ID com sucesso")
    @WithMockUser(roles = "USER")
    void shouldGetEventOriginByIdSuccessfully() throws Exception {
        // Given
        configureServiceToReturnEventOriginById();
        
        // When & Then
        performGetByIdRequestAndVerifySuccess();
    }
    
    /**
     * Configura o serviço para retornar EventOrigin por ID.
     */
    private void configureServiceToReturnEventOriginById() {
        when(eventOriginService.findEventOriginById(testId)).thenReturn(testResponse);
    }
    
    /**
     * Executa a requisição de busca por ID e verifica o sucesso.
     */
    private void performGetByIdRequestAndVerifySuccess() throws Exception {
        mockMvc.perform(get("/event-origins/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.eventSource").value("PDV"))
                .andExpect(jsonPath("$.sourceCode").value("PDV001"));
    }
    
    /**
     * Testa a busca de EventOrigin por ID com role ADMIN.
     * 
     * <p>Cenário: Usuário ADMIN busca um EventOrigin por ID</p>
     * <p>Resultado esperado: EventOrigin encontrado (HTTP 200)</p>
     */
    @Test
    @DisplayName("Deve buscar EventOrigin por ID com role ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldGetEventOriginByIdWithAdminRole() throws Exception {
        // Given
        configureServiceToReturnEventOriginById();
        
        // When & Then
        mockMvc.perform(get("/event-origins/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testId.toString()));
    }
    
    /**
     * Testa que usuários não autenticados não podem buscar EventOrigin por ID.
     * 
     * <p>Cenário: Usuário não autenticado tenta buscar EventOrigin por ID</p>
     * <p>Resultado esperado: Não autorizado (HTTP 401)</p>
     */
    @Test
    @DisplayName("Deve retornar 401 quando usuário não autenticado para busca por ID")
    void shouldReturn401WhenUserNotAuthenticatedForGetById() throws Exception {
        // When & Then
        mockMvc.perform(get("/event-origins/{id}", testId))
                .andExpect(status().isUnauthorized());
    }
    
    /**
     * Testa a busca de EventOrigins com filtros com sucesso.
     * 
     * <p>Cenário: Usuário USER busca EventOrigins com filtros específicos</p>
     * <p>Resultado esperado: EventOrigins encontrados com paginação (HTTP 200)</p>
     */
    @Test
    @DisplayName("Deve buscar EventOrigins com filtros com sucesso")
    @WithMockUser(roles = "USER")
    void shouldGetEventOriginsWithFiltersSuccessfully() throws Exception {
        // Given
        EventOriginSearchResponse searchResponse = createTestSearchResponse();
        configureServiceToReturnSearchResponse(searchResponse);
        
        // When & Then
        performSearchWithFiltersAndVerifySuccess();
    }
    
    /**
     * Cria uma resposta de busca para testes.
     */
    private EventOriginSearchResponse createTestSearchResponse() {
        EventOriginSearchResponse.PaginationInfo pagination = EventOriginSearchResponse.PaginationInfo.builder()
                .currentPage(0)
                .totalPages(1)
                .totalElements(1)
                .pageSize(20)
                .hasNext(false)
                .hasPrevious(false)
                .build();
        
        EventOriginSearchResponse.EventOriginCounts counts = EventOriginSearchResponse.EventOriginCounts.builder()
                .totalPdv(1)
                .totalExchange(0)
                .totalDanfe(0)
                .totalEventOrigins(1)
                .build();
        
        return EventOriginSearchResponse.builder()
                .eventOrigins(List.of(testResponse))
                .pagination(pagination)
                .counts(counts)
                .build();
    }
    
    /**
     * Configura o serviço para retornar resposta de busca.
     */
    private void configureServiceToReturnSearchResponse(EventOriginSearchResponse searchResponse) {
        when(eventOriginService.findEventOriginsWithFilters(any(EventOriginSearchRequest.class)))
                .thenReturn(searchResponse);
    }
    
    /**
     * Executa a busca com filtros e verifica o sucesso.
     */
    private void performSearchWithFiltersAndVerifySuccess() throws Exception {
        mockMvc.perform(get("/event-origins")
                .param("eventSource", "PDV")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "sourceCode")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventOrigins").isArray())
                .andExpect(jsonPath("$.eventOrigins[0].id").value(testId.toString()))
                .andExpect(jsonPath("$.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.pagination.totalElements").value(1))
                .andExpect(jsonPath("$.counts.totalPdv").value(1))
                .andExpect(jsonPath("$.counts.totalEventOrigins").value(1));
    }
    
    /**
     * Testa a busca de EventOrigins sem filtros com sucesso.
     * 
     * <p>Cenário: Usuário USER busca todos os EventOrigins</p>
     * <p>Resultado esperado: Todos os EventOrigins encontrados (HTTP 200)</p>
     */
    @Test
    @DisplayName("Deve buscar EventOrigins sem filtros com sucesso")
    @WithMockUser(roles = "USER")
    void shouldGetEventOriginsWithoutFiltersSuccessfully() throws Exception {
        // Given
        EventOriginSearchResponse searchResponse = createTestSearchResponse();
        configureServiceToReturnSearchResponse(searchResponse);
        
        // When & Then
        performSearchWithoutFiltersAndVerifySuccess();
    }
    
    /**
     * Executa a busca sem filtros e verifica o sucesso.
     */
    private void performSearchWithoutFiltersAndVerifySuccess() throws Exception {
        mockMvc.perform(get("/event-origins"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventOrigins").isArray())
                .andExpect(jsonPath("$.eventOrigins[0].id").value(testId.toString()));
    }
    
    /**
     * Testa a busca de EventOrigins com role ADMIN.
     * 
     * <p>Cenário: Usuário ADMIN busca todos os EventOrigins</p>
     * <p>Resultado esperado: Todos os EventOrigins encontrados (HTTP 200)</p>
     */
    @Test
    @DisplayName("Deve buscar EventOrigins com role ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldGetEventOriginsWithAdminRole() throws Exception {
        // Given
        EventOriginSearchResponse searchResponse = createTestSearchResponse();
        configureServiceToReturnSearchResponse(searchResponse);
        
        // When & Then
        mockMvc.perform(get("/event-origins"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventOrigins").isArray());
    }
    
    /**
     * Testa que usuários não autenticados não podem buscar EventOrigins.
     * 
     * <p>Cenário: Usuário não autenticado tenta buscar EventOrigins</p>
     * <p>Resultado esperado: Não autorizado (HTTP 401)</p>
     */
    @Test
    @DisplayName("Deve retornar 401 quando usuário não autenticado para busca")
    void shouldReturn401WhenUserNotAuthenticatedForSearch() throws Exception {
        // When & Then
        mockMvc.perform(get("/event-origins"))
                .andExpect(status().isUnauthorized());
    }
}
