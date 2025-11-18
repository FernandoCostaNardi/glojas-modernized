package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.JobPositionDTO;
import com.sysconard.legacy.entity.JobPosition;
import com.sysconard.legacy.repository.JobPositionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio dos cargos
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: lógica de negócio dos cargos
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
public class JobPositionService {
    
    private final JobPositionRepository jobPositionRepository;
    
    @Autowired
    public JobPositionService(JobPositionRepository jobPositionRepository) {
        this.jobPositionRepository = jobPositionRepository;
    }
    
    /**
     * Busca todos os cargos cadastrados no sistema
     * 
     * @return Lista de cargos convertidas para DTO
     */
    public List<JobPositionDTO> findAllJobPositions() {
        log.debug("Buscando todos os cargos cadastrados");
        
        List<JobPosition> jobPositions = jobPositionRepository.findAll();
        
        List<JobPositionDTO> jobPositionDTOs = jobPositions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        log.debug("Cargos encontrados: {}", jobPositionDTOs.size());
        
        return jobPositionDTOs;
    }
    
    /**
     * Converte uma entidade JobPosition para JobPositionDTO
     * 
     * @param jobPosition Entidade a ser convertida
     * @return DTO convertido
     */
    private JobPositionDTO convertToDTO(JobPosition jobPosition) {
        return JobPositionDTO.builder()
                .id(formatJobPositionId(jobPosition.getId()))
                .description(jobPosition.getName())
                .build();
    }
    
    /**
     * Formata o ID do cargo com 6 dígitos, preenchendo com zeros à esquerda
     * 
     * @param id ID do cargo
     * @return ID formatado com 6 dígitos (ex: 000001)
     */
    private String formatJobPositionId(Long id) {
        if (id == null) {
            return null;
        }
        return String.format("%06d", id);
    }
}

