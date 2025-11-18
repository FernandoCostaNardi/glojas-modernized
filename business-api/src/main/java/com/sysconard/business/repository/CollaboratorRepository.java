package com.sysconard.business.repository;

import com.sysconard.business.entity.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade Collaborator.
 * Estende JpaRepository para operações CRUD básicas e define métodos customizados.
 */
@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, UUID> {
    
    /**
     * Busca um colaborador pelo código.
     * 
     * @param code Código do colaborador
     * @return Optional contendo o colaborador se encontrado
     */
    Optional<Collaborator> findByEmployeeCode(String code);
    
    /**
     * Verifica se existe um colaborador com o código especificado.
     * 
     * @param code Código do colaborador
     * @return true se existe, false caso contrário
     */
    boolean existsByEmployeeCode(String code);
}

