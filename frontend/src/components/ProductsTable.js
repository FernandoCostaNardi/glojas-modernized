import React from 'react';
import './ProductsTable.css';

const ProductsTable = ({
  products = [],
  currentPage,
  totalPages,
  onPageChange,
}) => {
  // Debug: verificar o que está chegando
  // eslint-disable-next-line no-console
  console.log('ProductsTable recebeu:', {
    products,
    type: typeof products,
    isArray: Array.isArray(products),
  });

  // Garantir que products seja sempre um array
  const productsList = Array.isArray(products) ? products : [];

  if (!productsList || productsList.length === 0) {
    return (
      <div className="no-products">
        <p>📦 Nenhum produto encontrado</p>
      </div>
    );
  }

  const renderPagination = () => {
    const pages = [];
    const maxButtons = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxButtons / 2));
    const endPage = Math.min(totalPages, startPage + maxButtons - 1);

    // Ajustar startPage se endPage estiver no limite
    if (endPage - startPage + 1 < maxButtons) {
      startPage = Math.max(1, endPage - maxButtons + 1);
    }

    // Botão "Primeira"
    if (currentPage > 1) {
      pages.push(
        <button
          key="first"
          onClick={() => onPageChange(1)}
          className="pagination-btn"
        >
          ⏮️ Primeira
        </button>
      );
    }

    // Botão "Anterior"
    if (currentPage > 1) {
      pages.push(
        <button
          key="prev"
          onClick={() => onPageChange(currentPage - 1)}
          className="pagination-btn"
        >
          ⬅️ Anterior
        </button>
      );
    }

    // Botões numerados
    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <button
          key={i}
          onClick={() => onPageChange(i)}
          className={`pagination-btn ${i === currentPage ? 'active' : ''}`}
        >
          {i}
        </button>
      );
    }

    // Botão "Próxima"
    if (currentPage < totalPages) {
      pages.push(
        <button
          key="next"
          onClick={() => onPageChange(currentPage + 1)}
          className="pagination-btn"
        >
          Próxima ➡️
        </button>
      );
    }

    // Botão "Última"
    if (currentPage < totalPages) {
      pages.push(
        <button
          key="last"
          onClick={() => onPageChange(totalPages)}
          className="pagination-btn"
        >
          Última ⏭️
        </button>
      );
    }

    return pages;
  };

  return (
    <div className="products-table-container">
      <div className="table-info">
        <p>
          📊 Mostrando página {currentPage} de {totalPages} (
          {productsList.length} produtos nesta página)
        </p>
      </div>

      <div className="table-wrapper">
        <table className="products-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Código</th>
              <th>Descrição</th>
              <th>Preço</th>
              <th>Estoque</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {productsList.map((product, index) => (
              <tr key={product.codigo || product.id || index}>
                <td>{product.id || product.codigo || '-'}</td>
                <td>{product.codigo || product.code || '-'}</td>
                <td>
                  {product.descricao ||
                    product.description ||
                    product.nome ||
                    '-'}
                </td>
                <td>
                  {product.preco || product.price
                    ? `R$ ${(product.preco || product.price).toLocaleString(
                        'pt-BR',
                        { minimumFractionDigits: 2 }
                      )}`
                    : 'N/A'}
                </td>
                <td>{product.estoque || product.stock || 'N/A'}</td>
                <td>
                  <span className="status ativo">
                    {product.status || 'ATIVO'}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && <div className="pagination">{renderPagination()}</div>}
    </div>
  );
};

export default ProductsTable;
