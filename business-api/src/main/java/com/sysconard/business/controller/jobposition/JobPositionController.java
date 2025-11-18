package com.sysconard.business.controller.jobposition;

import com.sysconard.business.dto.jobposition.JobPositionResponse;
import com.sysconard.business.exception.jobposition.JobPositionNotFoundException;
import com.sysconard.business.service.jobposition.JobPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para operações com cargos (Job Positions).
 * Expõe endpoints para sincronização com Legacy API e busca de cargos.
 * Segue princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/job-positions")
@RequiredArgsConstructor
public class JobPositionController {
    
    private final JobPositionService jobPositionService;
    
    /**
     * Sincroniza cargos da Legacy API para o banco de dados.
     * Busca todos os cargos da Legacy API e salva/atualiza no banco.
     * 
     * @return Lista de cargos sincronizados
     */
    @PostMapping("/sync")
    public ResponseEntity<List<JobPositionResponse>> syncJobPositions() {
        log.info("Recebida requisição para sincronizar cargos da Legacy API");
        
        try {
            List<JobPositionResponse> syncedJobPositions = jobPositionService.syncJobPositionsFromLegacy();
            
            log.info("Sincronização concluída com sucesso. Total sincronizado: {}", 
                    syncedJobPositions != null ? syncedJobPositions.size() : 0);
            
            return ResponseEntity.ok(syncedJobPositions);
            
        } catch (Exception e) {
            log.error("Erro ao sincronizar cargos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca todos os cargos cadastrados no sistema.
     * 
     * @return Lista de cargos
     */
    @GetMapping
    public ResponseEntity<List<JobPositionResponse>> getAllJobPositions() {
        log.info("Recebida requisição para buscar todos os cargos");
        
        try {
            List<JobPositionResponse> jobPositions = jobPositionService.getAllJobPositions();
            
            log.info("Cargos encontrados: {} registros", 
                    jobPositions != null ? jobPositions.size() : 0);
            
            return ResponseEntity.ok(jobPositions);
            
        } catch (Exception e) {
            log.error("Erro ao buscar cargos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca um cargo específico pelo ID.
     * 
     * @param id ID do cargo (UUID)
     * @return Dados do cargo
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobPositionResponse> getJobPositionById(@PathVariable UUID id) {
        log.info("Recebida requisição para buscar cargo por ID: {}", id);
        
        try {
            JobPositionResponse jobPosition = jobPositionService.getJobPositionById(id);
            
            log.info("Cargo encontrado: {} (ID: {})", jobPosition.getJobPositionCode(), id);
            
            return ResponseEntity.ok(jobPosition);
            
        } catch (JobPositionNotFoundException e) {
            log.warn("Cargo não encontrado com ID: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Erro ao buscar cargo por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca um cargo específico pelo código.
     * 
     * @param code Código do cargo (ex: "000001")
     * @return Dados do cargo
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<JobPositionResponse> getJobPositionByCode(@PathVariable String code) {
        log.info("Recebida requisição para buscar cargo por código: {}", code);
        
        try {
            JobPositionResponse jobPosition = jobPositionService.getJobPositionByCode(code);
            
            log.info("Cargo encontrado: {} (código: {})", jobPosition.getJobPositionCode(), code);
            
            return ResponseEntity.ok(jobPosition);
            
        } catch (JobPositionNotFoundException e) {
            log.warn("Cargo não encontrado com código: {}", code);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Erro ao buscar cargo por código {}: {}", code, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

