import React from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useLayout } from '@/contexts/LayoutContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';

/**
 * Página principal do Dashboard
 * Layout responsivo com sidebar, header e área de conteúdo de boas-vindas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const { isMobile } = useLayout();

  /**
   * Renderiza estatísticas rápidas (placeholder)
   */
  const renderQuickStats = (): React.ReactNode => (
    <div className={`grid gap-4 mb-6 ${
      isMobile 
        ? 'grid-cols-1' 
        : 'grid-cols-2 lg:grid-cols-4'
    }`}>
      {[
        { title: 'Usuários Ativos', value: '248', icon: '👥', color: 'bg-blue-500' },
        { title: 'Vendas Hoje', value: 'R$ 12.450', icon: '💰', color: 'bg-green-500' },
        { title: 'Produtos', value: '1.024', icon: '📦', color: 'bg-purple-500' },
        { title: 'Relatórios', value: '15', icon: '📊', color: 'bg-orange-500' },
      ].map((stat, index) => (
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

  /**
   * Renderiza ações rápidas
   */
  const renderQuickActions = (): React.ReactNode => (
    <div className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 mb-6 ${
      isMobile ? 'p-4' : 'p-6'
    }`}>
      <h2 className={`font-semibold text-smart-gray-800 mb-4 ${
        isMobile ? 'text-base' : 'text-lg'
      }`}>Ações Rápidas</h2>
      <div className={`grid gap-4 ${
        isMobile ? 'grid-cols-1' : 'grid-cols-2 lg:grid-cols-3'
      }`}>
        {[
          { title: 'Novo Produto', description: 'Cadastrar novo produto', icon: '➕' },
          { title: 'Relatório de Vendas', description: 'Gerar relatório do dia', icon: '📈' },
          { title: 'Gerenciar Usuários', description: 'Administrar usuários', icon: '⚙️' },
        ].map((action, index) => (
          <button
            key={index}
            className="p-4 border border-smart-gray-200 rounded-lg hover:bg-smart-gray-50 transition-colors duration-200 text-left"
          >
            <div className="text-2xl mb-2">{action.icon}</div>
            <h3 className="font-medium text-smart-gray-800">{action.title}</h3>
            <p className="text-sm text-smart-gray-600">{action.description}</p>
          </button>
        ))}
      </div>
    </div>
  );

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
    <main className={`flex-1 bg-smart-gray-50 overflow-auto ${
      isMobile ? 'p-4' : 'p-6'
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

      {/* Conteúdo da dashboard */}
      {renderQuickStats()}
      
      <div className={`grid gap-6 ${
        isMobile 
          ? 'grid-cols-1' 
          : 'grid-cols-1 lg:grid-cols-3'
      }`}>
        <div className={isMobile ? '' : 'lg:col-span-2'}>
          {renderQuickActions()}
        </div>
        <div>
          {renderUserInfo()}
        </div>
      </div>
    </main>
  );

  return (
    <div className="h-screen flex flex-col bg-smart-gray-50">
      {/* Header */}
      <Header />
      
      {/* Layout principal */}
      <div className="flex flex-1 overflow-hidden relative">
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
