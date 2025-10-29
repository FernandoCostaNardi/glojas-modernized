package com.sysconard.business.controller;

import com.sysconard.business.dto.emailnotifier.CreateEmailNotifierRequest;
import com.sysconard.business.dto.emailnotifier.EmailNotifierPageResponse;
import com.sysconard.business.dto.emailnotifier.EmailNotifierResponse;
import com.sysconard.business.dto.emailnotifier.UpdateEmailNotifierRequest;
import com.sysconard.business.service.EmailNotifierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para gerenciamento de EmailNotifier
 * Seguindo princípios de Clean Code com responsabilidade única
 */
@Slf4j
@RestController
@RequestMapping("/email-notifiers")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class EmailNotifierController {
    
    private final EmailNotifierService emailNotifierService;
    
    // Log para verificar se o controller está sendo instanciado
    {
        log.info("=== EmailNotifierController instanciado ===");
    }
    
    /**
     * Cria um novo EmailNotifier
     * @param request Dados de criação
     * @return DTO de resposta
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public EmailNotifierResponse createEmailNotifier(@Valid @RequestBody CreateEmailNotifierRequest request) {
        log.info("Criando EmailNotifier via API: {}", request.email());
        return emailNotifierService.create(request);
    }
    
    /**
     * Lista todos os EmailNotifiers com paginação
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @return Página de DTOs de resposta
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public EmailNotifierPageResponse getAllEmailNotifiers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "email") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.debug("Listando EmailNotifiers via API: page={}, size={}, sortBy={}, sortDir={}", 
                page, size, sortBy, sortDir);
        
        // Cria Pageable com ordenação
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return emailNotifierService.findAll(pageable);
    }
    
    /**
     * Busca EmailNotifier por ID
     * @param id ID do notificador
     * @return DTO de resposta
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public EmailNotifierResponse getEmailNotifierById(@PathVariable UUID id) {
        log.debug("Buscando EmailNotifier por ID via API: {}", id);
        return emailNotifierService.findById(id);
    }
    
    /**
     * Atualiza um EmailNotifier existente
     * @param id ID do notificador
     * @param request Dados de atualização
     * @return DTO de resposta atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public EmailNotifierResponse updateEmailNotifier(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmailNotifierRequest request) {
        
        log.info("Atualizando EmailNotifier via API: {}", id);
        return emailNotifierService.update(id, request);
    }
    
    /**
     * Remove um EmailNotifier
     * @param id ID do notificador
     * @return Resposta de confirmação
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteEmailNotifier(@PathVariable UUID id) {
        log.info("Removendo EmailNotifier via API: {}", id);
        emailNotifierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
