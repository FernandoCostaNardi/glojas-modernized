package com.sysconard.business.service;

import com.sysconard.business.dto.emailnotifier.CreateEmailNotifierRequest;
import com.sysconard.business.dto.emailnotifier.EmailNotifierPageResponse;
import com.sysconard.business.dto.emailnotifier.EmailNotifierResponse;
import com.sysconard.business.dto.emailnotifier.UpdateEmailNotifierRequest;
import com.sysconard.business.entity.EmailNotifier;
import com.sysconard.business.exception.EmailAlreadyExistsException;
import com.sysconard.business.mapper.EmailNotifierMapper;
import com.sysconard.business.repository.EmailNotifierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço para gerenciamento de EmailNotifier
 * Seguindo princípios de Clean Code com responsabilidade única
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotifierService {
    
    private final EmailNotifierRepository emailNotifierRepository;
    private final EmailNotifierMapper emailNotifierMapper;
    
    // Log para verificar se o service está sendo instanciado
    {
        log.info("=== EmailNotifierService instanciado ===");
    }
    
    /**
     * Cria um novo EmailNotifier
     * @param request Dados de criação
     * @return DTO de resposta
     * @throws EmailAlreadyExistsException se o email já existir
     */
    @Transactional
    public EmailNotifierResponse create(CreateEmailNotifierRequest request) {
        log.info("Criando EmailNotifier para email: {}", request.email());
        
        // Verifica se o email já existe
        if (emailNotifierRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email já cadastrado: " + request.email());
        }
        
        // Converte para entidade e salva
        EmailNotifier entity = emailNotifierMapper.toEntity(request);
        EmailNotifier savedEntity = emailNotifierRepository.save(entity);
        
        log.info("EmailNotifier criado com sucesso: {}", savedEntity.getId());
        
        return emailNotifierMapper.toResponse(savedEntity);
    }
    
    /**
     * Busca EmailNotifier por ID
     * @param id ID do notificador
     * @return DTO de resposta
     */
    public EmailNotifierResponse findById(UUID id) {
        log.debug("Buscando EmailNotifier por ID: {}", id);
        
        EmailNotifier entity = emailNotifierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EmailNotifier não encontrado: " + id));
        
        return emailNotifierMapper.toResponse(entity);
    }
    
    /**
     * Lista todos os EmailNotifiers com paginação
     * @param pageable Configurações de paginação
     * @return Página de DTOs de resposta
     */
    public EmailNotifierPageResponse findAll(Pageable pageable) {
        log.debug("Listando EmailNotifiers com paginação: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<EmailNotifier> page = emailNotifierRepository.findAll(pageable);
        
        log.debug("EmailNotifiers encontrados: {} de {}", 
                page.getContent().size(), page.getTotalElements());
        
        return emailNotifierMapper.toPageResponse(page);
    }
    
    /**
     * Atualiza um EmailNotifier existente
     * @param id ID do notificador
     * @param request Dados de atualização
     * @return DTO de resposta atualizado
     */
    @Transactional
    public EmailNotifierResponse update(UUID id, UpdateEmailNotifierRequest request) {
        log.info("Atualizando EmailNotifier: {}", id);
        
        EmailNotifier entity = emailNotifierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EmailNotifier não encontrado: " + id));
        
        // Atualiza os campos
        emailNotifierMapper.updateEntity(entity, request);
        
        // Salva as alterações
        EmailNotifier updatedEntity = emailNotifierRepository.save(entity);
        
        log.info("EmailNotifier atualizado com sucesso: {}", updatedEntity.getId());
        
        return emailNotifierMapper.toResponse(updatedEntity);
    }
    
    /**
     * Remove um EmailNotifier
     * @param id ID do notificador
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Removendo EmailNotifier: {}", id);
        
        if (!emailNotifierRepository.existsById(id)) {
            throw new RuntimeException("EmailNotifier não encontrado: " + id);
        }
        
        emailNotifierRepository.deleteById(id);
        
        log.info("EmailNotifier removido com sucesso: {}", id);
    }
}
