package com.sysconard.business.repository.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sysconard.business.entity.store.Store;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade Store.
 * Estende JpaRepository para operações CRUD básicas e define métodos customizados.
 * Utiliza queries nativas para melhor performance em operações complexas.
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
    
    /**
     * Busca uma loja pelo código.
     * 
     * @param code Código da loja
     * @return Optional contendo a loja se encontrada
     */
    Optional<Store> findByCode(String code);
    
    /**
     * Verifica se existe uma loja com o código especificado.
     * 
     * @param code Código da loja
     * @return true se existe, false caso contrário
     */
    boolean existsByCode(String code);
    
    /**
     * Verifica se existe uma loja com o código especificado, excluindo uma loja específica.
     * Útil para validação durante atualizações.
     * 
     * @param code Código da loja
     * @param id ID da loja a ser excluída da verificação
     * @return true se existe outra loja com o mesmo código, false caso contrário
     */
    @Query("SELECT COUNT(s) > 0 FROM Store s WHERE s.code = :code AND s.id != :id")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") UUID id);
    
    /**
     * Busca lojas com filtros e paginação.
     * Permite filtrar por código, nome, cidade e status (busca parcial) e aplicar paginação e ordenação.
     * 
     * @param code Filtro por código (busca parcial, opcional)
     * @param name Filtro por nome (busca parcial, opcional)
     * @param city Filtro por cidade (busca parcial, opcional)
     * @param status Filtro por status (opcional)
     * @param pageable Configuração de paginação e ordenação
     * @return Página de lojas com filtros aplicados
     */
    @Query(value = "SELECT s.* FROM stores s " +
                   "WHERE (:code IS NULL OR LOWER(s.code) LIKE LOWER(CONCAT('%', CAST(:code AS TEXT), '%'))) " +
                   "AND (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                   "AND (:city IS NULL OR LOWER(s.city) LIKE LOWER(CONCAT('%', CAST(:city AS TEXT), '%'))) " +
                   "AND (:status IS NULL OR s.status = :status) " +
                   "ORDER BY s.code",
           countQuery = "SELECT COUNT(*) FROM stores s " +
                       "WHERE (:code IS NULL OR LOWER(s.code) LIKE LOWER(CONCAT('%', CAST(:code AS TEXT), '%'))) " +
                       "AND (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                       "AND (:city IS NULL OR LOWER(s.city) LIKE LOWER(CONCAT('%', CAST(:city AS TEXT), '%'))) " +
                       "AND (:status IS NULL OR s.status = :status)",
           nativeQuery = true)
    Page<Store> findStoresWithFilters(
        @Param("code") String code,
        @Param("name") String name,
        @Param("city") String city,
        @Param("status") Boolean status,
        Pageable pageable
    );
    
    /**
     * Conta o total de lojas com filtros aplicados.
     * 
     * @param code Filtro por código (busca parcial, opcional)
     * @param name Filtro por nome (busca parcial, opcional)
     * @param city Filtro por cidade (busca parcial, opcional)
     * @param status Filtro por status (opcional)
     * @return Total de lojas que atendem aos filtros
     */
    @Query(value = "SELECT COUNT(*) FROM stores s " +
                   "WHERE (:code IS NULL OR LOWER(s.code) LIKE LOWER(CONCAT('%', CAST(:code AS TEXT), '%'))) " +
                   "AND (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                   "AND (:city IS NULL OR LOWER(s.city) LIKE LOWER(CONCAT('%', CAST(:city AS TEXT), '%'))) " +
                   "AND (:status IS NULL OR s.status = :status)",
           nativeQuery = true)
    long countStoresWithFilters(@Param("code") String code, @Param("name") String name, @Param("city") String city, @Param("status") Boolean status);
    
    /**
     * Conta o total de lojas cadastradas no sistema.
     * 
     * @return Total de lojas cadastradas
     */
    @Query(value = "SELECT COUNT(*) FROM stores", nativeQuery = true)
    long countTotalStores();
}
