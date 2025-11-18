package com.sysconard.business.controller.collaborator;

import com.sysconard.business.dto.collaborator.CollaboratorResponse;
import com.sysconard.business.exception.collaborator.CollaboratorNotFoundException;
import com.sysconard.business.service.collaborator.CollaboratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para operações com colaboradores.
 * Expõe endpoints para sincronização com Legacy API e busca de colaboradores.
 * Segue princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/collaborators")
@RequiredArgsConstructor
public class CollaboratorController {
    
    private final CollaboratorService collaboratorService;
    
    /**
     * Sincroniza colaboradores ativos da Legacy API para o banco de dados.
     * Busca todos os colaboradores ativos da Legacy API e salva/atualiza no banco.
     * 
     * @return Lista de colaboradores sincronizados
     */
    @PostMapping("/sync")
    public ResponseEntity<List<CollaboratorResponse>> syncCollaborators() {
        log.info("Recebida requisição para sincronizar colaboradores ativos da Legacy API");
        
        try {
            List<CollaboratorResponse> syncedCollaborators = collaboratorService.syncCollaboratorsFromLegacy();
            
            log.info("Sincronização concluída com sucesso. Total sincronizado: {}", 
                    syncedCollaborators != null ? syncedCollaborators.size() : 0);
            
            return ResponseEntity.ok(syncedCollaborators);
            
        } catch (Exception e) {
            log.error("Erro ao sincronizar colaboradores: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca todos os colaboradores cadastrados no sistema.
     * 
     * @return Lista de colaboradores
     */
    @GetMapping
    public ResponseEntity<List<CollaboratorResponse>> getAllCollaborators() {
        log.info("Recebida requisição para buscar todos os colaboradores");
        
        try {
            List<CollaboratorResponse> collaborators = collaboratorService.getAllCollaborators();
            
            log.info("Colaboradores encontrados: {} registros", 
                    collaborators != null ? collaborators.size() : 0);
            
            return ResponseEntity.ok(collaborators);
            
        } catch (Exception e) {
            log.error("Erro ao buscar colaboradores: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca um colaborador específico pelo ID.
     * 
     * @param id ID do colaborador (UUID)
     * @return Dados do colaborador
     */
    @GetMapping("/{id}")
    public ResponseEntity<CollaboratorResponse> getCollaboratorById(@PathVariable UUID id) {
        log.info("Recebida requisição para buscar colaborador por ID: {}", id);
        
        try {
            CollaboratorResponse collaborator = collaboratorService.getCollaboratorById(id);
            
            log.info("Colaborador encontrado: {} (ID: {})", collaborator.getEmployeeCode(), id);
            
            return ResponseEntity.ok(collaborator);
            
        } catch (CollaboratorNotFoundException e) {
            log.warn("Colaborador não encontrado com ID: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Erro ao buscar colaborador por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca um colaborador específico pelo código.
     * 
     * @param code Código do colaborador (ex: "000001")
     * @return Dados do colaborador
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<CollaboratorResponse> getCollaboratorByCode(@PathVariable String code) {
        log.info("Recebida requisição para buscar colaborador por código: {}", code);
        
        try {
            CollaboratorResponse collaborator = collaboratorService.getCollaboratorByCode(code);
            
            log.info("Colaborador encontrado: {} (código: {})", collaborator.getEmployeeCode(), code);
            
            return ResponseEntity.ok(collaborator);
            
        } catch (CollaboratorNotFoundException e) {
            log.warn("Colaborador não encontrado com código: {}", code);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Erro ao buscar colaborador por código {}: {}", code, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

