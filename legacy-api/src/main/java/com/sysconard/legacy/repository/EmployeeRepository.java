package com.sysconard.legacy.repository;

import com.sysconard.legacy.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações com a entidade Employee
 * 
 * Estende JpaRepository fornecendo métodos básicos de CRUD:
 * - findAll() - busca todos os funcionários
 * - findById(Long id) - busca funcionário por ID
 * - save(Employee) - salva funcionário
 * - deleteById(Long id) - remove funcionário por ID
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    /**
     * Busca funcionários por status ativo/inativo
     * 
     * @param active Status do funcionário (S para ativo, N para inativo)
     * @return Lista de funcionários com o status especificado
     */
    List<Employee> findByActive(String active);
    
    // Métodos padrão do JpaRepository já disponíveis:
    // - List<Employee> findAll()
    // - Optional<Employee> findById(Long id)
    // - Employee save(Employee employee)
    // - void deleteById(Long id)
    // - long count()
    // - boolean existsById(Long id)
}

