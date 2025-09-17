package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.StoreDTO;
import com.sysconard.legacy.entity.store.Store;
import com.sysconard.legacy.repository.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio das lojas
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: lógica de negócio das lojas
 * - Injeção de dependência via construtor
 * - Conversão de entidades para DTOs
 * - Tratamento de erros adequado
 * - Logging para monitoramento
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
public class StoreService {
    
    /**
     * Formato para ID da loja com 6 dígitos
     */
    private static final String STORE_ID_FORMAT = "%06d";
    
    private final StoreRepository storeRepository;
    
    @Autowired
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    
    /**
     * Busca todas as lojas cadastradas no sistema
     * 
     * @return Lista de lojas convertidas para DTO
     */
    public List<StoreDTO> findAllStores() {
        log.debug("Buscando todas as lojas cadastradas");
        
        List<Store> stores = storeRepository.findAll();
        
        List<StoreDTO> storeDTOs = stores.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        log.debug("Lojas encontradas: {}", storeDTOs.size());
        
        return storeDTOs;
    }
    
    /**
     * Busca uma loja específica pelo ID
     * 
     * @param id ID da loja a ser buscada
     * @return DTO da loja encontrada
     * @throws IllegalArgumentException se o ID for inválido
     * @throws RuntimeException se a loja não for encontrada
     */
    public StoreDTO findStoreById(Long id) {
        log.debug("Buscando loja com ID: {}", id);
        
        // Validação do ID
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da loja deve ser válido (maior que 0)");
        }
        
        Optional<Store> storeOptional = storeRepository.findById(id);
        
        if (storeOptional.isPresent()) {
            StoreDTO storeDTO = convertToDTO(storeOptional.get());
            log.debug("Loja encontrada: ID={}, Name={}", storeDTO.getId(), storeDTO.getName());
            return storeDTO;
        } else {
            log.warn("Loja não encontrada com ID: {}", id);
            throw new RuntimeException("Loja não encontrada com ID: " + id);
        }
    }
    
    /**
     * Converte uma entidade Store para StoreDTO
     * 
     * @param store Entidade a ser convertida
     * @return DTO convertido com ID formatado em 6 dígitos
     */
    private StoreDTO convertToDTO(Store store) {
        return StoreDTO.builder()
                .id(String.format(STORE_ID_FORMAT, store.getId()))
                .name(store.getName())
                .city(store.getCity())
                .build();
    }
}
