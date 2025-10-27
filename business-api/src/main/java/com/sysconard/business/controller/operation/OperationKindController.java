package com.sysconard.business.controller.operation;

import com.sysconard.business.dto.operation.OperationKindDto;
import com.sysconard.business.service.operation.OperationKindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de tipos de operação (OperationKind).
 * Implementa endpoints REST para busca de tipos de operação da Legacy API.
 * Segue princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@RestController
@RequestMapping("/operation-kinds")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class OperationKindController {
    
    private final OperationKindService operationKindService;
    
    /**
     * Busca todos os tipos de operação disponíveis para cadastro.
     * Retorna apenas os tipos de operação da Legacy API que ainda não foram cadastrados no sistema.
     * A comparação é feita entre o campo 'id' do OperationKindDto e o campo 'code' da entidade Operation.
     * Acesso permitido para usuários com permissão operation:read.
     * 
     * @return Lista de tipos de operação disponíveis (não cadastrados)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('operation:read')")
    public ResponseEntity<List<OperationKindDto>> getAllOperationKinds() {
        log.info("Recebida requisição para buscar todos os tipos de operação");
        
        try {
            List<OperationKindDto> operationKinds = operationKindService.getAllOperationKinds();
            
            log.info("Retornando {} tipos de operação", operationKinds.size());
            return ResponseEntity.ok(operationKinds);
            
        } catch (Exception e) {
            log.error("Erro ao buscar tipos de operação: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
}
