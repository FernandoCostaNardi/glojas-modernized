package com.sysconard.legacy.repository;

import com.sysconard.legacy.entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações com a entidade JobPosition
 * 
 * Estende JpaRepository fornecendo métodos básicos de CRUD:
 * - findAll() - busca todos os cargos
 * - findById(Long id) - busca cargo por ID
 * - save(JobPosition) - salva cargo
 * - deleteById(Long id) - remove cargo por ID
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
    
    // Métodos padrão do JpaRepository já disponíveis:
    // - List<JobPosition> findAll()
    // - Optional<JobPosition> findById(Long id)
    // - JobPosition save(JobPosition jobPosition)
    // - void deleteById(Long id)
    // - long count()
    // - boolean existsById(Long id)
}

