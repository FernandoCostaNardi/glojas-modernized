package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.JobPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável pela lógica de negócio do JobPositionController
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
public class JobPositionControllerService {
    
    private final JobPositionService jobPositionService;
    
    @Autowired
    public JobPositionControllerService(JobPositionService jobPositionService) {
        this.jobPositionService = jobPositionService;
    }
    
    /**
     * Busca todos os cargos cadastrados no sistema
     * 
     * @return Lista de cargos
     */
    public List<JobPositionDTO> getAllJobPositions() {
        log.debug("Iniciando busca de todos os cargos");
        
        try {
            List<JobPositionDTO> jobPositions = jobPositionService.findAllJobPositions();
            log.info("Busca de cargos concluída com sucesso. Total: {}", jobPositions.size());
            return jobPositions;
        } catch (Exception e) {
            log.error("Erro ao buscar cargos: {}", e.getMessage(), e);
            throw e;
        }
    }
}

