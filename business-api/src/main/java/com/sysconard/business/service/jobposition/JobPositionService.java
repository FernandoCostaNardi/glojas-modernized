package com.sysconard.business.service.jobposition;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.jobposition.JobPositionLegacyDTO;
import com.sysconard.business.dto.jobposition.JobPositionResponse;
import com.sysconard.business.entity.JobPosition;
import com.sysconard.business.exception.jobposition.JobPositionNotFoundException;
import com.sysconard.business.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de cargos no sistema.
 * Implementa a lógica de negócio para sincronização, busca e listagem de cargos.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobPositionService {
    
    private final JobPositionRepository jobPositionRepository;
    private final LegacyApiClient legacyApiClient;
    
    /**
     * Sincroniza cargos da Legacy API para o banco de dados.
     * Busca todos os cargos da Legacy API e salva/atualiza no banco.
     * A sincronização é idempotente (pode ser executada múltiplas vezes sem duplicar dados).
     * 
     * @return Lista de cargos sincronizados
     * @throws RuntimeException se houver erro na comunicação com a Legacy API
     */
    public List<JobPositionResponse> syncJobPositionsFromLegacy() {
        log.info("Iniciando sincronização de cargos da Legacy API");
        
        try {
            // 1. Buscar cargos da Legacy API
            List<JobPositionLegacyDTO> legacyJobPositions = legacyApiClient.getJobPositions();
            
            log.info("Cargos encontrados na Legacy API: {}", 
                    legacyJobPositions != null ? legacyJobPositions.size() : 0);
            
            if (legacyJobPositions == null || legacyJobPositions.isEmpty()) {
                log.warn("Nenhum cargo encontrado na Legacy API");
                return List.of();
            }
            
            // 2. Sincronizar cada cargo
            List<JobPositionResponse> syncedJobPositions = legacyJobPositions.stream()
                    .map(this::syncJobPosition)
                    .collect(Collectors.toList());
            
            log.info("Sincronização concluída com sucesso. Total sincronizado: {}", syncedJobPositions.size());
            
            return syncedJobPositions;
            
        } catch (Exception e) {
            log.error("Erro ao sincronizar cargos da Legacy API: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao sincronizar cargos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Sincroniza um cargo individual da Legacy API.
     * Verifica se já existe pelo código e cria ou atualiza conforme necessário.
     * 
     * @param legacyJobPosition DTO do cargo da Legacy API
     * @return DTO de resposta do cargo sincronizado
     */
    private JobPositionResponse syncJobPosition(JobPositionLegacyDTO legacyJobPosition) {
        // Verificar se já existe pelo código
        JobPosition existingJobPosition = jobPositionRepository
                .findByJobPositionCode(legacyJobPosition.getId())
                .orElse(null);
        
        JobPosition jobPosition;
        
        if (existingJobPosition != null) {
            // Atualizar descrição se necessário
            if (!existingJobPosition.getJobPositionDescription().equals(legacyJobPosition.getDescription())) {
                log.debug("Atualizando descrição do cargo: {}", legacyJobPosition.getId());
                existingJobPosition.setJobPositionDescription(legacyJobPosition.getDescription());
                jobPosition = jobPositionRepository.save(existingJobPosition);
                log.debug("Cargo atualizado: {}", jobPosition.getJobPositionCode());
            } else {
                jobPosition = existingJobPosition;
                log.debug("Cargo já existe e está atualizado: {}", jobPosition.getJobPositionCode());
            }
        } else {
            // Criar novo cargo
            log.debug("Criando novo cargo: {}", legacyJobPosition.getId());
            jobPosition = JobPosition.builder()
                    .jobPositionCode(legacyJobPosition.getId())
                    .jobPositionDescription(legacyJobPosition.getDescription())
                    .build();
            
            jobPosition = jobPositionRepository.save(jobPosition);
            log.debug("Cargo criado: {}", jobPosition.getJobPositionCode());
        }
        
        return mapToResponse(jobPosition);
    }
    
    /**
     * Busca todos os cargos cadastrados no sistema.
     * 
     * @return Lista de cargos
     */
    @Transactional(readOnly = true)
    public List<JobPositionResponse> getAllJobPositions() {
        log.debug("Buscando todos os cargos cadastrados");
        
        List<JobPosition> jobPositions = jobPositionRepository.findAll();
        
        log.debug("Cargos encontrados: {}", jobPositions.size());
        
        return jobPositions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca um cargo pelo ID.
     * 
     * @param id ID do cargo
     * @return Dados do cargo
     * @throws JobPositionNotFoundException se o cargo não for encontrado
     */
    @Transactional(readOnly = true)
    public JobPositionResponse getJobPositionById(UUID id) {
        log.debug("Buscando cargo por ID: {}", id);
        
        JobPosition jobPosition = jobPositionRepository.findById(id)
                .orElseThrow(() -> new JobPositionNotFoundException(id));
        
        log.debug("Cargo encontrado: {} (ID: {})", jobPosition.getJobPositionCode(), jobPosition.getId());
        
        return mapToResponse(jobPosition);
    }
    
    /**
     * Busca um cargo pelo código.
     * 
     * @param code Código do cargo
     * @return Dados do cargo
     * @throws JobPositionNotFoundException se o cargo não for encontrado
     */
    @Transactional(readOnly = true)
    public JobPositionResponse getJobPositionByCode(String code) {
        log.debug("Buscando cargo por código: {}", code);
        
        JobPosition jobPosition = jobPositionRepository.findByJobPositionCode(code)
                .orElseThrow(() -> new JobPositionNotFoundException(code));
        
        log.debug("Cargo encontrado: {} (ID: {})", jobPosition.getJobPositionCode(), jobPosition.getId());
        
        return mapToResponse(jobPosition);
    }
    
    /**
     * Mapeia uma entidade JobPosition para JobPositionResponse.
     * 
     * @param jobPosition Entidade JobPosition
     * @return DTO JobPositionResponse
     */
    private JobPositionResponse mapToResponse(JobPosition jobPosition) {
        return JobPositionResponse.builder()
                .id(jobPosition.getId())
                .jobPositionCode(jobPosition.getJobPositionCode())
                .jobPositionDescription(jobPosition.getJobPositionDescription())
                .createdAt(jobPosition.getCreatedAt())
                .updatedAt(jobPosition.getUpdatedAt())
                .build();
    }
}

