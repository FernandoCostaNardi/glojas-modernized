package com.sysconard.legacy.repository;

import com.sysconard.legacy.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações com a entidade Operation
 * 
 * Estende JpaRepository fornecendo métodos básicos de CRUD:
 * - findAll() - busca todas as operações
 * - findById(Long id) - busca operação por ID
 * - save(Operation) - salva operação
 * - deleteById(Long id) - remove operação por ID
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    
    // Métodos padrão do JpaRepository já disponíveis:
    // - List<Operation> findAll()
    // - Optional<Operation> findById(Long id)
    // - Operation save(Operation operation)
    // - void deleteById(Long id)
    // - long count()
    // - boolean existsById(Long id)
}
