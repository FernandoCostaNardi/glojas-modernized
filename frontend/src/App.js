import React, { useState, useEffect } from 'react';
import axios from 'axios';
import ProductsTable from './components/ProductsTable';
import { mockProducts, delay } from './mockData'; // Import mock data
import './App.css';

// const API_BASE_URL = 'http://localhost:8081'; // URL completa (sem proxy)
const API_BASE_URL = ''; // URL relativa (com proxy)
const USE_MOCK_DATA = false; // true = sempre usar mock, false = tentar API primeiro

function App() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [usingMockData, setUsingMockData] = useState(false);
  const itemsPerPage = 10;

  const fetchProductsMock = async (page = 1) => {
    try {
      setLoading(true);
      setError(null);

      // Simular delay da API
      await delay(1000);

      // Simular paginação
      const startIndex = (page - 1) * itemsPerPage;
      const endIndex = startIndex + itemsPerPage;
      const paginatedData = {
        ...mockProducts,
        content: mockProducts.content.slice(startIndex, endIndex),
        number: page - 1,
        totalPages: Math.ceil(mockProducts.totalElements / itemsPerPage),
      };

      setProducts(paginatedData.content);
      setTotalPages(paginatedData.totalPages);
      setCurrentPage(page);
      setUsingMockData(true);
      setError(null); // Limpar erro quando mock data carregar com sucesso
    } catch (err) {
      setError(err.message || 'Erro ao carregar dados mock');
    } finally {
      setLoading(false);
    }
  };

  const fetchProducts = async (page = 1) => {
    // Se configurado para usar mock, usar direto
    if (USE_MOCK_DATA) {
      return fetchProductsMock(page);
    }

    try {
      setLoading(true);
      setError(null);
      setUsingMockData(false);

      const response = await axios.get(
        `${API_BASE_URL}/api/business/products/registered`,
        {
          params: {
            page: page - 1, // Backend geralmente usa 0-based indexing
            size: itemsPerPage,
          },
        }
      );

      // API retorna: { products: [], pagination: { totalPages, totalElements, currentPage } }
      // eslint-disable-next-line no-console
      console.log('=== RESPOSTA DA API ===');
      // eslint-disable-next-line no-console
      console.log('response.data:', response.data);
      // eslint-disable-next-line no-console
      console.log('response.data.products:', response.data.products);
      // eslint-disable-next-line no-console
      console.log('response.data.pagination:', response.data.pagination);

      const apiProducts =
        response.data.products || response.data.content || response.data || [];
      const apiPagination = response.data.pagination || {};

      setProducts(apiProducts);
      setTotalPages(
        apiPagination.totalPages ||
          response.data.totalPages ||
          Math.ceil(apiProducts.length / itemsPerPage)
      );
      setCurrentPage(page);

      // eslint-disable-next-line no-console
      console.log('=== ESTADO APÓS SETAR ===');
      // eslint-disable-next-line no-console
      console.log('products será:', apiProducts);
      // eslint-disable-next-line no-console
      console.log('products.length:', apiProducts.length);
      // eslint-disable-next-line no-console
      console.log('totalPages será:', apiPagination.totalPages);
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error('Erro ao buscar produtos da API:', err);

      // Se falhar, tentar dados mock como fallback
      // eslint-disable-next-line no-console
      console.log('API falhou, usando dados mock...');
      return fetchProductsMock(page);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts(currentPage);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Debug da renderização
  // eslint-disable-next-line no-console
  console.log('=== RENDERIZAÇÃO ===', {
    loading,
    error,
    usingMockData,
    products,
    productsLength: products?.length,
    productsIsArray: Array.isArray(products),
  });

  const handlePageChange = newPage => {
    if (newPage >= 1 && newPage <= totalPages) {
      fetchProducts(newPage);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>🏪 Glojas - Produtos Cadastrados</h1>
        {usingMockData && (
          <p style={{ fontSize: '0.9rem', opacity: 0.8, marginTop: '0.5rem' }}>
            📦 Usando dados de exemplo (API não disponível)
          </p>
        )}
      </header>

      <main className="App-main">
        {loading && (
          <div className="loading">
            <p>Carregando produtos...</p>
          </div>
        )}

        {!loading && error && !usingMockData && (
          <div className="error">
            <p>❌ {error}</p>
            <button onClick={() => fetchProducts(currentPage)}>
              Tentar Novamente
            </button>
            <button
              onClick={() => fetchProductsMock(currentPage)}
              style={{ marginLeft: '1rem', background: '#28a745' }}
            >
              Usar Dados de Exemplo
            </button>
          </div>
        )}

        {!loading && products && products.length > 0 && (
          <ProductsTable
            products={products}
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        )}

        {/* Debug adicional */}
        {!loading && (!products || products.length === 0) && (
          <div
            style={{
              padding: '2rem',
              background: '#fff3cd',
              border: '1px solid #ffeeba',
              borderRadius: '8px',
            }}
          >
            <h3>🐛 Debug Info:</h3>
            <ul>
              <li>
                <strong>loading:</strong> {loading.toString()}
              </li>
              <li>
                <strong>error:</strong> {error || 'null'}
              </li>
              <li>
                <strong>usingMockData:</strong> {usingMockData.toString()}
              </li>
              <li>
                <strong>products:</strong> {JSON.stringify(products)}
              </li>
              <li>
                <strong>products.length:</strong>{' '}
                {products?.length || 'undefined'}
              </li>
              <li>
                <strong>totalPages:</strong> {totalPages}
              </li>
            </ul>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
