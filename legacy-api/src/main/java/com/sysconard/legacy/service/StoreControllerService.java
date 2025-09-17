package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.StoreDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável pela lógica de negócio do StoreController
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: coordenação entre controller e service
 * - Injeção de dependência via construtor
 * - Delegação de lógica para o service
 * - Tratamento de erros adequado
 * - Logging para monitoramento
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
public class StoreControllerService {
    
    private final StoreService storeService;
    
    @Autowired
    public StoreControllerService(StoreService storeService) {
        this.storeService = storeService;
    }
    
    /**
     * Busca todas as lojas cadastradas no sistema
     * 
     * @return Lista de lojas
     */
    public List<StoreDTO> getAllStores() {
        log.debug("Iniciando busca de todas as lojas");
        
        try {
            List<StoreDTO> stores = storeService.findAllStores();
            log.info("Busca de lojas concluída com sucesso. Total: {}", stores.size());
            return stores;
        } catch (Exception e) {
            log.error("Erro ao buscar lojas: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Busca uma loja específica pelo ID
     * 
     * @param id ID da loja a ser buscada (formato string, ex: "000001")
     * @return Loja encontrada
     * @throws IllegalArgumentException se o ID for inválido
     * @throws RuntimeException se a loja não for encontrada
     */
    public StoreDTO getStoreById(String id) {
        log.debug("Iniciando busca de loja com ID: {}", id);
        
        try {
            // Validação do formato do ID
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID da loja não pode ser nulo ou vazio");
            }
            
            // Converte String para Long para busca no banco
            Long numericId;
            try {
                numericId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID da loja deve ser um número válido: " + id);
            }
            
            // Validação do valor numérico
            if (numericId <= 0) {
                throw new IllegalArgumentException("ID da loja deve ser maior que 0: " + id);
            }
            
            StoreDTO store = storeService.findStoreById(numericId);
            log.info("Loja encontrada com sucesso: ID={}, Name={}", 
                    store.getId(), store.getName());
            return store;
        } catch (IllegalArgumentException e) {
            log.warn("ID inválido fornecido: {}", id);
            throw e;
        } catch (RuntimeException e) {
            log.warn("Loja não encontrada com ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar loja com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro interno ao buscar loja", e);
        }
    }
}
