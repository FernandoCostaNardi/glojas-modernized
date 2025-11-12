import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useLayout } from '@/contexts/LayoutContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import DashboardSalesChart from './Dashboard/DashboardSalesChart';
import { getDashboardSummary } from '@/services/dashboardApi';
import { DashboardState } from '@/types/dashboard';

/**
 * Página principal do Dashboard
 * Layout responsivo com sidebar, header e área de conteúdo de boas-vindas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const { isMobile } = useLayout();
  
  // Estado para dados do dashboard
  const [dashboardState, setDashboardState] = useState<DashboardState>({
    data: null,
    loading: true,
    error: null
  });

  /**
   * Carrega dados do dashboard ao montar o componente
   */
  useEffect(() => {
    const loadDashboardData = async (): Promise<void> => {
      try {
        setDashboardState(prev => ({ ...prev, loading: true, error: null }));
        
        const data = await getDashboardSummary();
        
        setDashboardState({
          data,
          loading: false,
          error: null
        });
      } catch (error) {
        setDashboardState({
          data: null,
          loading: false,
          error: error instanceof Error ? error.message : 'Erro ao carregar dados do dashboard'
        });
      }
    };

    loadDashboardData();
  }, []);

  /**
   * Formata valor monetário para exibição
   */
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  /**
   * Renderiza cards de métricas do dashboard
   */
  const renderDashboardStats = (): React.ReactNode => {
    if (dashboardState.loading) {
      return (
        <div className={`grid gap-4 mb-6 ${
          isMobile 
            ? 'grid-cols-1' 
            : 'grid-cols-2 lg:grid-cols-4'
        }`}>
          {[1, 2, 3, 4].map((index) => (
            <div key={index} className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 ${
              isMobile ? 'p-4' : 'p-6'
            }`}>
              <div className="animate-pulse">
                <div className="flex items-center">
                  <div className="bg-smart-gray-200 rounded-lg p-3 w-12 h-12 mr-4"></div>
                  <div className="flex-1">
                    <div className="h-4 bg-smart-gray-200 rounded mb-2"></div>
                    <div className="h-8 bg-smart-gray-200 rounded"></div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      );
    }

    if (dashboardState.error) {
      return (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
          <div className="flex items-center">
            <div className="text-red-500 mr-3">⚠️</div>
            <div>
              <h3 className="text-sm font-medium text-red-800">Erro ao carregar dados</h3>
              <p className="text-sm text-red-600">{dashboardState.error}</p>
            </div>
          </div>
        </div>
      );
    }

    const stats = [
      { 
        title: 'Total Vendas Dia Atual', 
        value: formatCurrency(dashboardState.data?.totalSalesToday || 0), 
        icon: '💰', 
        color: 'bg-green-500' 
      },
      { 
        title: 'Total Vendas Mês Atual', 
        value: formatCurrency(dashboardState.data?.totalSalesMonth || 0), 
        icon: '📈', 
        color: 'bg-blue-500' 
      },
      { 
        title: 'Total Vendas Ano Atual', 
        value: formatCurrency(dashboardState.data?.totalSalesYear || 0), 
        icon: '📊', 
        color: 'bg-purple-500' 
      },
      { 
        title: 'Quantidade Lojas Ativas', 
        value: (dashboardState.data?.activeStoresCount || 0).toString(), 
        icon: '🏪', 
        color: 'bg-orange-500' 
      },
    ];

    return (
      <div className={`grid gap-4 mb-6 ${
        isMobile 
          ? 'grid-cols-1' 
          : 'grid-cols-2 lg:grid-cols-4'
      }`}>
        {stats.map((stat, index) => (
          <div key={index} className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 ${
            isMobile ? 'p-4' : 'p-6'
          }`}>
            <div className="flex items-center">
              <div className={`${stat.color} rounded-lg p-3 text-white text-2xl mr-4`}>
                {stat.icon}
              </div>
              <div>
                <h3 className="text-sm font-medium text-smart-gray-600">{stat.title}</h3>
                <p className="text-2xl font-bold text-smart-gray-800">{stat.value}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    );
  };


  /**
   * Renderiza informações do usuário
   */
  const renderUserInfo = (): React.ReactNode => (
    <div className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 ${
      isMobile ? 'p-4' : 'p-6'
    }`}>
      <h2 className={`font-semibold text-smart-gray-800 mb-4 ${
        isMobile ? 'text-base' : 'text-lg'
      }`}>Informações da Sessão</h2>
      <div className="space-y-3">
        <div>
          <span className="text-sm font-medium text-smart-gray-600">Nome: </span>
          <span className="text-sm text-smart-gray-800">{user?.name}</span>
        </div>
        <div>
          <span className="text-sm font-medium text-smart-gray-600">Usuário: </span>
          <span className="text-sm text-smart-gray-800">@{user?.username}</span>
        </div>
        <div>
          <span className="text-sm font-medium text-smart-gray-600">Funções: </span>
          <div className="flex flex-wrap gap-1 mt-1">
            {user?.roles?.map((role, index) => (
              <span
                key={index}
                className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-smart-red-100 text-smart-red-800"
              >
                {role}
              </span>
            ))}
          </div>
        </div>
        <div>
          <span className="text-sm font-medium text-smart-gray-600">Permissões: </span>
          <div className="flex flex-wrap gap-1 mt-1">
            {user?.permissions?.map((permission, index) => (
              <span
                key={index}
                className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-smart-gray-100 text-smart-gray-700"
              >
                {permission}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );

  /**
   * Renderiza conteúdo principal da página
   */
  const renderMainContent = (): React.ReactNode => (
    <main className={`flex-1 bg-smart-gray-50 ${
      isMobile ? 'overflow-y-auto p-4 pb-8' : 'overflow-auto p-6'
    }`}>
      {/* Cabeçalho de boas-vindas */}
      <div className={`mb-6 ${isMobile ? 'mb-4' : 'mb-6'}`}>
        <h1 className={`font-bold text-smart-gray-800 mb-2 ${
          isMobile ? 'text-xl' : 'text-3xl'
        }`}>
          Bem-vindo de volta, {user?.name?.split(' ')[0] || user?.username}! 👋
        </h1>
        <p className={`text-smart-gray-600 ${
          isMobile ? 'text-sm' : 'text-base'
        }`}>
          Aqui está um resumo das atividades do seu sistema Smart Eletron.
        </p>
      </div>

      {/* Cards de métricas do dashboard */}
      {renderDashboardStats()}
      
      <div className={`grid gap-6 ${
        isMobile 
          ? 'grid-cols-1' 
          : 'grid-cols-1 lg:grid-cols-3'
      }`}>
        <div className={isMobile ? '' : 'lg:col-span-2'}>
          <DashboardSalesChart />
        </div>
        <div>
          {renderUserInfo()}
        </div>
      </div>
    </main>
  );

  return (
    <div className={`${isMobile ? 'min-h-screen' : 'h-screen'} flex flex-col bg-smart-gray-50`}>
      {/* Header */}
      <Header />
      
      {/* Layout principal */}
      <div className={`flex flex-1 ${isMobile ? 'overflow-y-auto' : 'overflow-hidden'} relative`}>
        {/* Sidebar */}
        <Sidebar />
        
        {/* Conteúdo principal */}
        <div className="flex-1">
          {renderMainContent()}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
