package com.sysconard.business.controller.store;

import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operações de lojas que consomem a Legacy API.
 * Expõe endpoints que buscam dados de lojas do serviço legacy.
 * Segue princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@RestController
@RequestMapping("/stores-legacy")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StoreLegacyController {
    
    private final StoreService storeService;
    
    /**
     * Busca lojas da Legacy API que não estão cadastradas no sistema.
     * Compara os códigos das lojas cadastradas com as lojas da Legacy API
     * e retorna apenas as que não existem no cadastro.
     * O ID é mantido com a formatação original de 6 dígitos (ex: 000001).
     * 
     * @return Lista de lojas não cadastradas da Legacy API
     */
    @GetMapping
    public ResponseEntity<List<StoreResponseDto>> getStores() {
        log.info("Recebida requisição para buscar lojas não cadastradas da Legacy API");
        
        try {
            List<StoreResponseDto> unregisteredStores = storeService.getUnregisteredStores();
            
            log.info("Lojas não cadastradas encontradas: {} registros", 
                    unregisteredStores != null ? unregisteredStores.size() : 0);
            
            return ResponseEntity.ok(unregisteredStores);
            
        } catch (Exception e) {
            log.error("Erro ao buscar lojas não cadastradas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint de health check para o StoreLegacyController.
     * 
     * @return Status de saúde do controller
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check do StoreLegacyController");
        return ResponseEntity.ok("StoreLegacyController está funcionando");
    }
}
