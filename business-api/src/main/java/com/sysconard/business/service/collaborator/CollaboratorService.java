package com.sysconard.business.service.collaborator;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.collaborator.CollaboratorLegacyDTO;
import com.sysconard.business.dto.collaborator.CollaboratorResponse;
import com.sysconard.business.entity.Collaborator;
import com.sysconard.business.exception.collaborator.CollaboratorNotFoundException;
import com.sysconard.business.repository.CollaboratorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de colaboradores no sistema.
 * Implementa a lógica de negócio para sincronização, busca e listagem de colaboradores.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CollaboratorService {
    
    private final CollaboratorRepository collaboratorRepository;
    private final LegacyApiClient legacyApiClient;
    
    /**
     * Sincroniza colaboradores ativos da Legacy API para o banco de dados.
     * Busca todos os colaboradores ativos da Legacy API e salva/atualiza no banco.
     * A sincronização é idempotente (pode ser executada múltiplas vezes sem duplicar dados).
     * 
     * @return Lista de colaboradores sincronizados
     * @throws RuntimeException se houver erro na comunicação com a Legacy API
     */
    public List<CollaboratorResponse> syncCollaboratorsFromLegacy() {
        log.info("Iniciando sincronização de colaboradores ativos da Legacy API");
        
        try {
            // 1. Buscar colaboradores ativos da Legacy API
            List<CollaboratorLegacyDTO> legacyCollaborators = legacyApiClient.getActiveEmployees();
            
            log.info("Colaboradores ativos encontrados na Legacy API: {}", 
                    legacyCollaborators != null ? legacyCollaborators.size() : 0);
            
            if (legacyCollaborators == null || legacyCollaborators.isEmpty()) {
                log.warn("Nenhum colaborador ativo encontrado na Legacy API");
                return List.of();
            }
            
            // 2. Sincronizar cada colaborador
            List<CollaboratorResponse> syncedCollaborators = legacyCollaborators.stream()
                    .map(this::syncCollaborator)
                    .collect(Collectors.toList());
            
            log.info("Sincronização concluída com sucesso. Total sincronizado: {}", syncedCollaborators.size());
            
            return syncedCollaborators;
            
        } catch (Exception e) {
            log.error("Erro ao sincronizar colaboradores da Legacy API: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao sincronizar colaboradores: " + e.getMessage(), e);
        }
    }
    
    /**
     * Sincroniza um colaborador individual da Legacy API.
     * Verifica se já existe pelo código e cria ou atualiza conforme necessário.
     * 
     * @param legacyCollaborator DTO do colaborador da Legacy API
     * @return DTO de resposta do colaborador sincronizado
     */
    private CollaboratorResponse syncCollaborator(CollaboratorLegacyDTO legacyCollaborator) {
        // Verificar se já existe pelo código
        Collaborator existingCollaborator = collaboratorRepository
                .findByEmployeeCode(legacyCollaborator.getId())
                .orElse(null);
        
        Collaborator collaborator;
        
        if (existingCollaborator != null) {
            // Atualizar dados se necessário
            boolean needsUpdate = false;
            
            if (!equalsString(existingCollaborator.getJobPositionCode(), legacyCollaborator.getJobPositionCode())) {
                existingCollaborator.setJobPositionCode(legacyCollaborator.getJobPositionCode());
                needsUpdate = true;
            }
            if (!equalsString(existingCollaborator.getStoreCode(), legacyCollaborator.getStoreCode())) {
                existingCollaborator.setStoreCode(legacyCollaborator.getStoreCode());
                needsUpdate = true;
            }
            if (!equalsString(existingCollaborator.getName(), legacyCollaborator.getName())) {
                existingCollaborator.setName(legacyCollaborator.getName());
                needsUpdate = true;
            }
            if (!equalsLocalDate(existingCollaborator.getBirthDate(), convertDateToLocalDate(legacyCollaborator.getBirthDate()))) {
                existingCollaborator.setBirthDate(convertDateToLocalDate(legacyCollaborator.getBirthDate()));
                needsUpdate = true;
            }
            if (!equalsBigDecimal(existingCollaborator.getCommissionPercentage(), legacyCollaborator.getCommissionPercentage())) {
                existingCollaborator.setCommissionPercentage(legacyCollaborator.getCommissionPercentage());
                needsUpdate = true;
            }
            if (!equalsString(existingCollaborator.getEmail(), legacyCollaborator.getEmail())) {
                existingCollaborator.setEmail(legacyCollaborator.getEmail());
                needsUpdate = true;
            }
            if (!equalsString(existingCollaborator.getActive(), legacyCollaborator.getActive())) {
                existingCollaborator.setActive(legacyCollaborator.getActive());
                needsUpdate = true;
            }
            if (!equalsString(existingCollaborator.getGender(), legacyCollaborator.getGender())) {
                existingCollaborator.setGender(legacyCollaborator.getGender());
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                log.debug("Atualizando colaborador: {}", legacyCollaborator.getId());
                collaborator = collaboratorRepository.save(existingCollaborator);
                log.debug("Colaborador atualizado: {}", collaborator.getEmployeeCode());
            } else {
                collaborator = existingCollaborator;
                log.debug("Colaborador já existe e está atualizado: {}", collaborator.getEmployeeCode());
            }
        } else {
            // Criar novo colaborador
            log.debug("Criando novo colaborador: {}", legacyCollaborator.getId());
            collaborator = Collaborator.builder()
                    .employeeCode(legacyCollaborator.getId())
                    .jobPositionCode(legacyCollaborator.getJobPositionCode())
                    .storeCode(legacyCollaborator.getStoreCode())
                    .name(legacyCollaborator.getName())
                    .birthDate(convertDateToLocalDate(legacyCollaborator.getBirthDate()))
                    .commissionPercentage(legacyCollaborator.getCommissionPercentage())
                    .email(legacyCollaborator.getEmail())
                    .active(legacyCollaborator.getActive())
                    .gender(legacyCollaborator.getGender())
                    .build();
            
            collaborator = collaboratorRepository.save(collaborator);
            log.debug("Colaborador criado: {}", collaborator.getEmployeeCode());
        }
        
        return mapToResponse(collaborator);
    }
    
    /**
     * Busca todos os colaboradores cadastrados no sistema.
     * 
     * @return Lista de colaboradores
     */
    @Transactional(readOnly = true)
    public List<CollaboratorResponse> getAllCollaborators() {
        log.debug("Buscando todos os colaboradores cadastrados");
        
        List<Collaborator> collaborators = collaboratorRepository.findAll();
        
        log.debug("Colaboradores encontrados: {}", collaborators.size());
        
        return collaborators.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca um colaborador pelo ID.
     * 
     * @param id ID do colaborador
     * @return Dados do colaborador
     * @throws CollaboratorNotFoundException se o colaborador não for encontrado
     */
    @Transactional(readOnly = true)
    public CollaboratorResponse getCollaboratorById(UUID id) {
        log.debug("Buscando colaborador por ID: {}", id);
        
        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(() -> new CollaboratorNotFoundException(id));
        
        log.debug("Colaborador encontrado: {} (ID: {})", collaborator.getEmployeeCode(), collaborator.getId());
        
        return mapToResponse(collaborator);
    }
    
    /**
     * Busca um colaborador pelo código.
     * 
     * @param code Código do colaborador
     * @return Dados do colaborador
     * @throws CollaboratorNotFoundException se o colaborador não for encontrado
     */
    @Transactional(readOnly = true)
    public CollaboratorResponse getCollaboratorByCode(String code) {
        log.debug("Buscando colaborador por código: {}", code);
        
        Collaborator collaborator = collaboratorRepository.findByEmployeeCode(code)
                .orElseThrow(() -> new CollaboratorNotFoundException(code));
        
        log.debug("Colaborador encontrado: {} (ID: {})", collaborator.getEmployeeCode(), collaborator.getId());
        
        return mapToResponse(collaborator);
    }
    
    /**
     * Mapeia uma entidade Collaborator para CollaboratorResponse.
     * 
     * @param collaborator Entidade Collaborator
     * @return DTO CollaboratorResponse
     */
    private CollaboratorResponse mapToResponse(Collaborator collaborator) {
        return CollaboratorResponse.builder()
                .id(collaborator.getId())
                .employeeCode(collaborator.getEmployeeCode())
                .jobPositionCode(collaborator.getJobPositionCode())
                .storeCode(collaborator.getStoreCode())
                .name(collaborator.getName())
                .birthDate(collaborator.getBirthDate())
                .commissionPercentage(collaborator.getCommissionPercentage())
                .email(collaborator.getEmail())
                .active(collaborator.getActive())
                .gender(collaborator.getGender())
                .createdAt(collaborator.getCreatedAt())
                .updatedAt(collaborator.getUpdatedAt())
                .build();
    }
    
    /**
     * Converte Date para LocalDate.
     * 
     * @param date Data a ser convertida
     * @return LocalDate ou null se a data for null
     */
    private LocalDate convertDateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    
    /**
     * Compara duas strings de forma segura (tratando null).
     * 
     * @param str1 Primeira string
     * @param str2 Segunda string
     * @return true se forem iguais, false caso contrário
     */
    private boolean equalsString(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }
    
    /**
     * Compara duas LocalDate de forma segura (tratando null).
     * 
     * @param date1 Primeira data
     * @param date2 Segunda data
     * @return true se forem iguais, false caso contrário
     */
    private boolean equalsLocalDate(LocalDate date1, LocalDate date2) {
        if (date1 == null && date2 == null) {
            return true;
        }
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.equals(date2);
    }
    
    /**
     * Compara dois BigDecimal de forma segura (tratando null).
     * 
     * @param bd1 Primeiro BigDecimal
     * @param bd2 Segundo BigDecimal
     * @return true se forem iguais, false caso contrário
     */
    private boolean equalsBigDecimal(java.math.BigDecimal bd1, java.math.BigDecimal bd2) {
        if (bd1 == null && bd2 == null) {
            return true;
        }
        if (bd1 == null || bd2 == null) {
            return false;
        }
        return bd1.compareTo(bd2) == 0;
    }
}

