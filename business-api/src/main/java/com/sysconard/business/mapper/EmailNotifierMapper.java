package com.sysconard.business.mapper;

import com.sysconard.business.dto.emailnotifier.CreateEmailNotifierRequest;
import com.sysconard.business.dto.emailnotifier.EmailNotifierPageResponse;
import com.sysconard.business.dto.emailnotifier.EmailNotifierResponse;
import com.sysconard.business.dto.emailnotifier.UpdateEmailNotifierRequest;
import com.sysconard.business.entity.EmailNotifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper para conversão entre entidades e DTOs de EmailNotifier
 * Seguindo princípios de Clean Code com responsabilidade única
 */
@Component
public class EmailNotifierMapper {
    
    // Log para verificar se o mapper está sendo instanciado
    {
        System.out.println("=== EmailNotifierMapper instanciado ===");
    }
    
    /**
     * Converte CreateEmailNotifierRequest para EmailNotifier
     * @param request Dados de criação
     * @return Entidade EmailNotifier
     */
    public EmailNotifier toEntity(CreateEmailNotifierRequest request) {
        return EmailNotifier.builder()
                .email(request.email())
                .dailySellNotifier(request.dailySellNotifier())
                .dailyMonthNotifier(request.dailyMonthNotifier())
                .monthYearNotifier(request.monthYearNotifier())
                .build();
    }
    
    /**
     * Converte EmailNotifier para EmailNotifierResponse
     * @param entity Entidade EmailNotifier
     * @return DTO de resposta
     */
    public EmailNotifierResponse toResponse(EmailNotifier entity) {
        return EmailNotifierResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .dailySellNotifier(entity.isDailySellNotifier())
                .dailyMonthNotifier(entity.isDailyMonthNotifier())
                .monthYearNotifier(entity.isMonthYearNotifier())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Converte lista de EmailNotifier para lista de EmailNotifierResponse
     * @param entities Lista de entidades
     * @return Lista de DTOs de resposta
     */
    public List<EmailNotifierResponse> toResponseList(List<EmailNotifier> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
    
    /**
     * Converte Page<EmailNotifier> para EmailNotifierPageResponse
     * @param page Página de entidades
     * @return DTO de resposta paginada
     */
    public EmailNotifierPageResponse toPageResponse(Page<EmailNotifier> page) {
        return EmailNotifierPageResponse.builder()
                .content(toResponseList(page.getContent()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
    
    /**
     * Atualiza entidade existente com dados de UpdateEmailNotifierRequest
     * @param entity Entidade existente
     * @param request Dados de atualização
     */
    public void updateEntity(EmailNotifier entity, UpdateEmailNotifierRequest request) {
        entity.setDailySellNotifier(request.dailySellNotifier());
        entity.setDailyMonthNotifier(request.dailyMonthNotifier());
        entity.setMonthYearNotifier(request.monthYearNotifier());
    }
}
