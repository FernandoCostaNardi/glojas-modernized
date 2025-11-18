package com.sysconard.business.service.salestargetconfig;

import com.sysconard.business.dto.salestargetconfig.SalesTargetConfigRequest;
import com.sysconard.business.dto.salestargetconfig.SalesTargetConfigResponse;
import com.sysconard.business.dto.salestargetconfig.UpdateSalesTargetConfigRequest;
import com.sysconard.business.entity.SalesTargetConfig;
import com.sysconard.business.exception.salestargetconfig.SalesTargetConfigNotFoundException;
import com.sysconard.business.repository.SalesTargetConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de configurações de metas e comissões no sistema.
 * Implementa a lógica de negócio para criação, atualização, busca e exclusão de configurações.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * Permite múltiplas configurações para a mesma loja e competência, possibilitando metas escalonadas.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SalesTargetConfigService {
    
    private final SalesTargetConfigRepository salesTargetConfigRepository;
    
    /**
     * Cria uma nova configuração de metas e comissões.
     * 
     * @param request Dados da configuração a ser criada
     * @return DTO de resposta com os dados da configuração criada
     */
    public SalesTargetConfigResponse create(SalesTargetConfigRequest request) {
        log.info("Criando nova configuração de metas e comissões: loja={}, competência={}", 
                request.storeCode(), request.competenceDate());
        
        // Criar entidade a partir do request
        SalesTargetConfig config = SalesTargetConfig.builder()
                .storeCode(request.storeCode())
                .competenceDate(request.competenceDate())
                .storeSalesTarget(request.storeSalesTarget())
                .collectiveCommissionPercentage(request.collectiveCommissionPercentage())
                .individualSalesTarget(request.individualSalesTarget())
                .individualCommissionPercentage(request.individualCommissionPercentage())
                .build();
        
        // Salvar no banco
        SalesTargetConfig savedConfig = salesTargetConfigRepository.save(config);
        
        log.info("Configuração criada com sucesso: id={}, loja={}, competência={}", 
                savedConfig.getId(), savedConfig.getStoreCode(), savedConfig.getCompetenceDate());
        
        return mapToResponse(savedConfig);
    }
    
    /**
     * Atualiza uma configuração de metas e comissões existente.
     * 
     * @param id ID da configuração a ser atualizada
     * @param request Dados atualizados da configuração
     * @return DTO de resposta com os dados da configuração atualizada
     * @throws SalesTargetConfigNotFoundException se a configuração não for encontrada
     */
    public SalesTargetConfigResponse update(UUID id, UpdateSalesTargetConfigRequest request) {
        log.info("Atualizando configuração de metas e comissões: id={}", id);
        
        // Buscar configuração existente
        SalesTargetConfig config = salesTargetConfigRepository.findById(id)
                .orElseThrow(() -> new SalesTargetConfigNotFoundException(id));
        
        // Atualizar campos
        config.setStoreCode(request.storeCode());
        config.setCompetenceDate(request.competenceDate());
        config.setStoreSalesTarget(request.storeSalesTarget());
        config.setCollectiveCommissionPercentage(request.collectiveCommissionPercentage());
        config.setIndividualSalesTarget(request.individualSalesTarget());
        config.setIndividualCommissionPercentage(request.individualCommissionPercentage());
        
        // Salvar atualização
        SalesTargetConfig updatedConfig = salesTargetConfigRepository.save(config);
        
        log.info("Configuração atualizada com sucesso: id={}, loja={}, competência={}", 
                updatedConfig.getId(), updatedConfig.getStoreCode(), updatedConfig.getCompetenceDate());
        
        return mapToResponse(updatedConfig);
    }
    
    /**
     * Busca todas as configurações de metas e comissões cadastradas.
     * 
     * @return Lista de configurações
     */
    @Transactional(readOnly = true)
    public List<SalesTargetConfigResponse> findAll() {
        log.debug("Buscando todas as configurações de metas e comissões");
        
        List<SalesTargetConfig> configs = salesTargetConfigRepository.findAll();
        
        log.debug("Configurações encontradas: {}", configs.size());
        
        return configs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca configurações de metas e comissões com filtros opcionais.
     * Permite buscar por loja e/ou competência, ou retornar todas se nenhum filtro for fornecido.
     * 
     * @param storeCode Código da loja (opcional, pode ser null ou vazio)
     * @param competenceDate Data de competência no formato MM/YYYY (opcional, pode ser null ou vazio)
     * @return Lista de configurações que atendem aos critérios de filtro
     */
    @Transactional(readOnly = true)
    public List<SalesTargetConfigResponse> findByFilters(String storeCode, String competenceDate) {
        log.debug("Buscando configurações de metas e comissões com filtros: loja={}, competência={}", 
                storeCode, competenceDate);
        
        // Normalizar parâmetros: converter strings vazias para null
        String normalizedStoreCode = (storeCode != null && storeCode.trim().isEmpty()) ? null : storeCode;
        String normalizedCompetenceDate = (competenceDate != null && competenceDate.trim().isEmpty()) ? null : competenceDate;
        
        List<SalesTargetConfig> configs = salesTargetConfigRepository.findByFilters(
                normalizedStoreCode, 
                normalizedCompetenceDate
        );
        
        log.debug("Configurações encontradas com filtros: {}", configs.size());
        
        return configs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca uma configuração de metas e comissões pelo ID.
     * 
     * @param id ID da configuração
     * @return DTO de resposta com os dados da configuração
     * @throws SalesTargetConfigNotFoundException se a configuração não for encontrada
     */
    @Transactional(readOnly = true)
    public SalesTargetConfigResponse findById(UUID id) {
        log.debug("Buscando configuração de metas e comissões por ID: {}", id);
        
        SalesTargetConfig config = salesTargetConfigRepository.findById(id)
                .orElseThrow(() -> new SalesTargetConfigNotFoundException(id));
        
        log.debug("Configuração encontrada: id={}, loja={}, competência={}", 
                config.getId(), config.getStoreCode(), config.getCompetenceDate());
        
        return mapToResponse(config);
    }
    
    /**
     * Deleta uma configuração de metas e comissões pelo ID.
     * 
     * @param id ID da configuração a ser deletada
     * @throws SalesTargetConfigNotFoundException se a configuração não for encontrada
     */
    public void delete(UUID id) {
        log.info("Deletando configuração de metas e comissões: id={}", id);
        
        // Verificar se existe
        if (!salesTargetConfigRepository.existsById(id)) {
            throw new SalesTargetConfigNotFoundException(id);
        }
        
        // Deletar
        salesTargetConfigRepository.deleteById(id);
        
        log.info("Configuração deletada com sucesso: id={}", id);
    }
    
    /**
     * Mapeia uma entidade SalesTargetConfig para SalesTargetConfigResponse.
     * 
     * @param config Entidade SalesTargetConfig
     * @return DTO SalesTargetConfigResponse
     */
    private SalesTargetConfigResponse mapToResponse(SalesTargetConfig config) {
        return SalesTargetConfigResponse.builder()
                .id(config.getId())
                .storeCode(config.getStoreCode())
                .competenceDate(config.getCompetenceDate())
                .storeSalesTarget(config.getStoreSalesTarget())
                .collectiveCommissionPercentage(config.getCollectiveCommissionPercentage())
                .individualSalesTarget(config.getIndividualSalesTarget())
                .individualCommissionPercentage(config.getIndividualCommissionPercentage())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}

