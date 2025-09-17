package com.sysconard.legacy.controller.store;

import com.sysconard.legacy.dto.StoreDTO;
import com.sysconard.legacy.service.StoreControllerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operações com lojas do sistema
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: apenas roteamento HTTP
 * - Injeção de dependência via construtor
 * - Delegação de lógica para o serviço
 * - Documentação clara
 * - Tratamento adequado de códigos HTTP
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreControllerService storeControllerService;
    
    @Autowired
    public StoreController(StoreControllerService storeControllerService) {
        this.storeControllerService = storeControllerService;
    }

    /**
     * Busca todas as lojas cadastradas no sistema
     * 
     * @return Lista de lojas com status 200 OK
     */
    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        log.debug("Recebida requisição para buscar todas as lojas");
        
        try {
            List<StoreDTO> stores = storeControllerService.getAllStores();
            log.info("Requisição de busca de lojas processada com sucesso. Total: {}", stores.size());
            return ResponseEntity.ok(stores);
        } catch (Exception e) {
            log.error("Erro ao processar requisição de busca de lojas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca uma loja específica pelo ID
     * 
     * @param id ID da loja a ser buscada (formato string, ex: "000001")
     * @return Loja encontrada com status 200 OK, ou 404 Not Found se não encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable String id) {
        log.debug("Recebida requisição para buscar loja com ID: {}", id);
        
        try {
            StoreDTO store = storeControllerService.getStoreById(id);
            log.info("Requisição de busca de loja por ID processada com sucesso: ID={}", id);
            return ResponseEntity.ok(store);
        } catch (IllegalArgumentException e) {
            log.warn("ID inválido fornecido na requisição: {}", id);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrada")) {
                log.warn("Loja não encontrada com ID: {}", id);
                return ResponseEntity.notFound().build();
            } else {
                log.error("Erro ao processar requisição de busca de loja por ID {}: {}", id, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Erro inesperado ao processar requisição de busca de loja por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
