package com.sysconard.business.service.store;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.store.CreateStoreRequest;
import com.sysconard.business.dto.store.StoreResponse;
import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.dto.store.UpdateStoreRequest;
import com.sysconard.business.entity.store.Store;
import com.sysconard.business.exception.store.StoreAlreadyExistsException;
import com.sysconard.business.exception.store.StoreNotFoundException;
import com.sysconard.business.repository.store.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de lojas no sistema.
 * Implementa a lógica de negócio para criação, atualização, busca e listagem de lojas.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {
    
    private final StoreRepository storeRepository;
    private final LegacyApiClient legacyApiClient;
    
    /**
     * Cria uma nova loja no sistema.
     * 
     * @param request Dados da loja a ser criada
     * @return Resposta com os dados da loja criada
     * @throws StoreAlreadyExistsException se o código já existir
     */
    public StoreResponse createStore(CreateStoreRequest request) {
        log.info("Iniciando criação de loja: {}", request.getCode());
        
        // Validar se o código já existe
        if (storeRepository.existsByCode(request.getCode())) {
            throw new StoreAlreadyExistsException(request.getCode());
        }
        
        // Criar a entidade
        Store store = Store.builder()
                .code(request.getCode())
                .name(request.getName())
                .city(request.getCity())
                .status(request.isStatus())
                .build();
        
        // Persistir no banco
        Store savedStore = storeRepository.save(store);
        
        log.info("Loja criada com sucesso: {} (ID: {})", savedStore.getCode(), savedStore.getId());
        
        return mapToResponse(savedStore);
    }
    
    /**
     * Atualiza uma loja existente no sistema.
     * 
     * @param id ID da loja a ser atualizada
     * @param request Dados da loja a ser atualizada
     * @return Resposta com os dados da loja atualizada
     * @throws StoreNotFoundException se a loja não for encontrada
     */
    public StoreResponse updateStore(UUID id, UpdateStoreRequest request) {
        log.info("Iniciando atualização de loja: {} (ID: {})", request.getName(), id);
        
        // Buscar a loja existente
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id));
        
        // Atualizar os campos
        store.setName(request.getName());
        store.setCity(request.getCity());
        
        // Atualizar status se fornecido
        if (request.getStatus() != null) {
            store.setStatus(request.getStatus());
        }
        
        // Persistir as alterações
        Store updatedStore = storeRepository.save(store);
        
        log.info("Loja atualizada com sucesso: {} (ID: {})", updatedStore.getCode(), updatedStore.getId());
        
        return mapToResponse(updatedStore);
    }
    
    /**
     * Busca uma loja pelo ID.
     * 
     * @param id ID da loja
     * @return Dados da loja
     * @throws StoreNotFoundException se a loja não for encontrada
     */
    @Transactional(readOnly = true)
    public StoreResponse getStoreById(UUID id) {
        log.debug("Buscando loja por ID: {}", id);
        
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id));
        
        log.debug("Loja encontrada: {} (ID: {})", store.getCode(), store.getId());
        
        return mapToResponse(store);
    }
    
    /**
     * Busca uma loja pelo código.
     * 
     * @param code Código da loja
     * @return Dados da loja
     * @throws StoreNotFoundException se a loja não for encontrada
     */
    @Transactional(readOnly = true)
    public StoreResponse getStoreByCode(String code) {
        log.debug("Buscando loja por código: {}", code);
        
        Store store = storeRepository.findByCode(code)
                .orElseThrow(() -> new StoreNotFoundException(code));
        
        log.debug("Loja encontrada: {} (ID: {})", store.getCode(), store.getId());
        
        return mapToResponse(store);
    }
    
    /**
     * Busca todas as lojas cadastradas no sistema com paginação.
     * 
     * @param page Número da página (baseado em 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc ou desc)
     * @return Página de lojas
     */
    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStores(int page, int size, String sortBy, String sortDir) {
        log.debug("Buscando todas as lojas cadastradas com paginação: page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);
        
        // Configurar paginação e ordenação
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Buscar todas as lojas com paginação
        Page<Store> stores = storeRepository.findAll(pageable);
        
        log.debug("Encontradas {} lojas da página {} de {} total",
                stores.getContent().size(), stores.getNumber() + 1, stores.getTotalPages());
        
        return stores.map(this::mapToResponse);
    }
    
    /**
     * Busca lojas com filtros e paginação.
     * 
     * @param code Filtro por código (opcional)
     * @param name Filtro por nome (opcional)
     * @param city Filtro por cidade (opcional)
     * @param status Filtro por status (opcional)
     * @param page Número da página (baseado em 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc ou desc)
     * @return Página de lojas com filtros aplicados
     */
    @Transactional(readOnly = true)
    public Page<StoreResponse> findStoresWithFilters(
            String code, String name, String city, Boolean status,
            int page, int size, String sortBy, String sortDir) {
        
        log.debug("Buscando lojas com filtros: code={}, name={}, city={}, status={}, page={}, size={}, sortBy={}, sortDir={}",
                code, name, city, status, page, size, sortBy, sortDir);
        
        // Configurar paginação e ordenação
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Buscar com filtros
        Page<Store> stores = storeRepository.findStoresWithFilters(code, name, city, status, pageable);
        
        log.debug("Encontradas {} lojas da página {} de {} total",
                stores.getContent().size(), stores.getNumber() + 1, stores.getTotalPages());
        
        return stores.map(this::mapToResponse);
    }
    
    /**
     * Remove uma loja do sistema.
     * 
     * @param id ID da loja a ser removida
     * @throws StoreNotFoundException se a loja não for encontrada
     */
    public void deleteStore(UUID id) {
        log.info("Iniciando remoção de loja: {}", id);
        
        // Verificar se a loja existe
        if (!storeRepository.existsById(id)) {
            throw new StoreNotFoundException(id);
        }
        
        // Remover a loja
        storeRepository.deleteById(id);
        
        log.info("Loja removida com sucesso: {}", id);
    }
    
    /**
     * Conta o total de lojas cadastradas no sistema.
     * 
     * @return Total de lojas cadastradas
     */
    @Transactional(readOnly = true)
    public long countTotalStores() {
        log.debug("Contando total de lojas cadastradas");
        
        long count = storeRepository.countTotalStores();
        
        log.debug("Total de lojas: {}", count);
        
        return count;
    }
    
    /**
     * Busca lojas da Legacy API que não estão cadastradas no sistema.
     * Compara os códigos das lojas cadastradas com as lojas da Legacy API
     * e retorna apenas as que não existem no cadastro.
     * 
     * @return Lista de lojas não cadastradas da Legacy API
     * @throws RuntimeException se houver erro na comunicação com a Legacy API
     */
    @Transactional(readOnly = true)
    public List<StoreResponseDto> getUnregisteredStores() {
        log.info("Buscando lojas não cadastradas comparando com Legacy API");
        
        try {
            // 1. Buscar todas as lojas cadastradas no sistema
            List<Store> registeredStores = storeRepository.findAll();
            Set<String> registeredCodes = extractStoreCodes(registeredStores);
            
            log.debug("Lojas cadastradas encontradas: {} registros", registeredStores.size());
            
            // 2. Buscar todas as lojas da Legacy API
            List<StoreResponseDto> legacyStores = legacyApiClient.getStores();
            
            log.debug("Lojas da Legacy API encontradas: {} registros", 
                    legacyStores != null ? legacyStores.size() : 0);
            
            // 3. Filtrar lojas não cadastradas
            List<StoreResponseDto> unregisteredStores = filterUnregisteredStores(legacyStores, registeredCodes);
            
            log.info("Encontradas {} lojas não cadastradas de {} lojas da Legacy API", 
                    unregisteredStores.size(), legacyStores != null ? legacyStores.size() : 0);
            
            return unregisteredStores;
            
        } catch (Exception e) {
            log.error("Erro ao buscar lojas não cadastradas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar lojas não cadastradas: " + e.getMessage(), e);
        }
    }

    //Criar metodo para trazer todas as lojas ativas
    public List<StoreResponseDto> getAllActiveStores() {
        log.info("Buscando todas as lojas ativas");
        List<Store> stores = storeRepository.findAllActiveStores();
        return stores.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Extrai os códigos das lojas cadastradas em um Set para comparação eficiente.
     * 
     * @param stores Lista de lojas cadastradas
     * @return Set com os códigos das lojas
     */
    private Set<String> extractStoreCodes(List<Store> stores) {
        return stores.stream()
                .map(Store::getCode)
                .collect(Collectors.toSet());
    }
    
    /**
     * Filtra as lojas da Legacy API que não estão cadastradas no sistema.
     * 
     * @param legacyStores Lista de lojas da Legacy API
     * @param registeredCodes Set com os códigos das lojas cadastradas
     * @return Lista de lojas não cadastradas
     */
    private List<StoreResponseDto> filterUnregisteredStores(List<StoreResponseDto> legacyStores, Set<String> registeredCodes) {
        if (legacyStores == null || legacyStores.isEmpty()) {
            log.warn("Nenhuma loja encontrada na Legacy API");
            return List.of();
        }
        
        return legacyStores.stream()
                .filter(store -> !registeredCodes.contains(store.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Mapeia uma entidade Store para StoreResponse.
     * 
     * @param store Entidade Store
     * @return DTO StoreResponse
     */
    private StoreResponse mapToResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .code(store.getCode())
                .name(store.getName())
                .city(store.getCity())
                .status(store.isStatus())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .build();
    }
    
    /**
     * Mapeia uma entidade Store para StoreResponseDto.
     * 
     * @param store Entidade Store
     * @return DTO StoreResponseDto
     */
    private StoreResponseDto mapToResponseDto(Store store) {
        return StoreResponseDto.builder()
                .id(store.getId().toString())
                .code(store.getCode())
                .name(store.getName())
                .city(store.getCity())
                .status(store.isStatus())
                .build();
    }
}
