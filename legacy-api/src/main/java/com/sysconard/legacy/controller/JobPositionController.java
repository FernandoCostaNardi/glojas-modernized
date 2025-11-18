package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.JobPositionDTO;
import com.sysconard.legacy.service.JobPositionControllerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operações com cargos do sistema
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
@RequestMapping("/job-positions")
public class JobPositionController {

    private final JobPositionControllerService jobPositionControllerService;
    
    @Autowired
    public JobPositionController(JobPositionControllerService jobPositionControllerService) {
        this.jobPositionControllerService = jobPositionControllerService;
    }

    /**
     * Busca todos os cargos cadastrados no sistema
     * 
     * @return Lista de cargos com status 200 OK
     */
    @GetMapping
    public ResponseEntity<List<JobPositionDTO>> getAllJobPositions() {
        log.debug("Recebida requisição para buscar todos os cargos");
        
        try {
            List<JobPositionDTO> jobPositions = jobPositionControllerService.getAllJobPositions();
            log.info("Requisição de busca de cargos processada com sucesso. Total: {}", jobPositions.size());
            return ResponseEntity.ok(jobPositions);
        } catch (Exception e) {
            log.error("Erro ao processar requisição de busca de cargos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

