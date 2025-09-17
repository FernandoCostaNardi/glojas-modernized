package com.sysconard.business.repository.operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sysconard.business.entity.operation.Operation;
import com.sysconard.business.enums.OperationSource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade Operation.
 * Estende JpaRepository para operações CRUD básicas e define métodos customizados.
 * Utiliza queries nativas para melhor performance em operações complexas.
 */
@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {
    
    /**
     * Busca uma operação pelo código.
     * 
     * @param code Código da operação
     * @return Optional contendo a operação se encontrada
     */
    Optional<Operation> findByCode(String code);
    
    /**
     * Verifica se existe uma operação com o código especificado.
     * 
     * @param code Código da operação
     * @return true se existe, false caso contrário
     */
    boolean existsByCode(String code);
    
    /**
     * Verifica se existe uma operação com o código especificado, excluindo uma operação específica.
     * Útil para validação durante atualizações.
     * 
     * @param code Código da operação
     * @param id ID da operação a ser excluída da verificação
     * @return true se existe outra operação com o mesmo código, false caso contrário
     */
    @Query("SELECT COUNT(o) > 0 FROM Operation o WHERE o.code = :code AND o.id != :id")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") UUID id);
    
    /**
     * Busca operações com filtros e paginação.
     * Permite filtrar por código (busca parcial) e aplicar paginação e ordenação.
     * 
     * @param code Filtro por código (busca parcial, opcional)
     * @param pageable Configuração de paginação e ordenação
     * @return Página de operações com filtros aplicados
     */
    @Query(value = "SELECT o.* FROM operations o " +
                   "WHERE (:code IS NULL OR LOWER(o.code) LIKE LOWER(CONCAT('%', CAST(:code AS TEXT), '%'))) " +
                   "ORDER BY o.code",
           countQuery = "SELECT COUNT(*) FROM operations o " +
                       "WHERE (:code IS NULL OR LOWER(o.code) LIKE LOWER(CONCAT('%', CAST(:code AS TEXT), '%')))",
           nativeQuery = true)
    Page<Operation> findOperationsWithFilters(
        @Param("code") String code,
        Pageable pageable
    );
    
    /**
     * Conta o total de operações com filtros aplicados.
     * 
     * @param code Filtro por código (busca parcial, opcional)
     * @return Total de operações que atendem aos filtros
     */
    @Query(value = "SELECT COUNT(*) FROM operations o " +
                   "WHERE (:code IS NULL OR LOWER(o.code) LIKE LOWER(CONCAT('%', CAST(:code AS TEXT), '%')))",
           nativeQuery = true)
    long countOperationsWithFilters(@Param("code") String code);
    
    /**
     * Conta o total de operações cadastradas no sistema.
     * 
     * @return Total de operações cadastradas
     */
    @Query(value = "SELECT COUNT(*) FROM operations", nativeQuery = true)
    long countTotalOperations();
    
    /**
     * Busca operações por fonte da operação.
     * 
     * @param operationSource Fonte da operação
     * @return Lista de operações com a fonte especificada
     */
    List<Operation> findByOperationSource(OperationSource operationSource);
    
    /**
     * Conta operações por fonte da operação.
     * 
     * @param operationSource Fonte da operação
     * @return Total de operações com a fonte especificada
     */
    long countByOperationSource(OperationSource operationSource);
}
