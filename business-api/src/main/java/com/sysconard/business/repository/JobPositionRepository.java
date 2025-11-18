package com.sysconard.business.repository;

import com.sysconard.business.entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade JobPosition.
 * Estende JpaRepository para operações CRUD básicas e define métodos customizados.
 */
@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, UUID> {
    
    /**
     * Busca um cargo pelo código.
     * 
     * @param code Código do cargo
     * @return Optional contendo o cargo se encontrado
     */
    Optional<JobPosition> findByJobPositionCode(String code);
    
    /**
     * Verifica se existe um cargo com o código especificado.
     * 
     * @param code Código do cargo
     * @return true se existe, false caso contrário
     */
    boolean existsByJobPositionCode(String code);
}

