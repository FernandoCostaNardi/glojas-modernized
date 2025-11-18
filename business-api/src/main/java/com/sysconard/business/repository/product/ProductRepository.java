package com.sysconard.business.repository.product;

import com.sysconard.business.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade Product.
 * Estende JpaRepository para operações CRUD básicas e define métodos customizados.
 * Utiliza queries otimizadas para melhor performance em operações em lote.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    /**
     * Busca um produto pelo código de referência.
     * 
     * @param productRefCode Código de referência do produto
     * @return Optional contendo o produto se encontrado
     */
    Optional<Product> findByProductRefCode(String productRefCode);
    
    /**
     * Verifica se existe um produto com o código de referência especificado.
     * 
     * @param productRefCode Código de referência do produto
     * @return true se existe, false caso contrário
     */
    boolean existsByProductRefCode(String productRefCode);
    
    /**
     * Busca todos os códigos de referência de produtos existentes no banco.
     * Utilizado para verificação em lote de produtos existentes.
     * 
     * @return Lista com todos os códigos de referência de produtos cadastrados
     */
    @Query("SELECT p.productRefCode FROM Product p")
    List<String> findAllProductRefCodes();
    
    /**
     * Busca produtos existentes pelos códigos de referência fornecidos.
     * Utilizado para verificação em lote de produtos existentes.
     * 
     * @param productRefCodes Conjunto de códigos de referência para verificar
     * @return Lista de produtos encontrados
     */
    @Query("SELECT p FROM Product p WHERE p.productRefCode IN :productRefCodes")
    List<Product> findByProductRefCodeIn(@Param("productRefCodes") Set<String> productRefCodes);
    
    /**
     * Busca apenas os códigos de referência dos produtos existentes pelos códigos fornecidos.
     * Utilizado para verificação em lote mais eficiente.
     * 
     * @param productRefCodes Conjunto de códigos de referência para verificar
     * @return Lista de códigos de referência encontrados
     */
    @Query("SELECT p.productRefCode FROM Product p WHERE p.productRefCode IN :productRefCodes")
    Set<String> findExistingProductRefCodes(@Param("productRefCodes") Set<String> productRefCodes);
}

