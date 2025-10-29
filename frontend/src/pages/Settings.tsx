import React from 'react';
import { useAuth } from '@/contexts/AuthContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';

/**
 * Interface para ações de configuração
 */
interface ConfigAction {
  readonly id: string;
  readonly title: string;
  readonly description: string;
  readonly icon: string;
  readonly href: string;
}

/**
 * Página de Configurações do Sistema
 * Acessível apenas para usuários com permissão SYSTEM_ADMIN
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const Settings: React.FC = () => {
  const { hasPermission } = useAuth();
  const [isSidebarCollapsed, setIsSidebarCollapsed] = React.useState<boolean>(false);

  /**
   * Verifica se o usuário tem permissão para acessar configurações
   */
  const canAccessSettings = (): boolean => {
    return hasPermission('SYSTEM_ADMIN');
  };

  /**
   * Ações de configuração disponíveis
   */
  const configActions: ConfigAction[] = [
    {
      id: 'users',
      title: 'Cadastro e Edição de Usuários',
      description: 'Gerenciar usuários do sistema, permissões e funções',
      icon: '👥',
      href: '/settings/users'
    },
    {
      id: 'permissions',
      title: 'Cadastro e Edição de Permissões',
      description: 'Gerenciar permissões e papéis do sistema',
      icon: '🔐',
      href: '/settings/permissions'
    },
    {
      id: 'origin-codes',
      title: 'Cadastro e Edição de Códigos de Origem',
      description: 'Administrar códigos de origem para produtos e operações',
      icon: '🏷️',
      href: '/settings/origin-codes'
    },
    {
      id: 'operations',
      title: 'Cadastro e Edição de Código de Operação',
      description: 'Gerenciar códigos de operação do sistema',
      icon: '⚙️',
      href: '/settings/operations'
    },
    {
      id: 'stores',
      title: 'Cadastro e Edição de Lojas',
      description: 'Gerenciar lojas do sistema e suas configurações',
      icon: '🏪',
      href: '/settings/stores'
    },
    {
      id: 'email-notifiers',
      title: 'Cadastro de Notificações por Email',
      description: 'Gerenciar emails que receberão notificações de vendas',
      icon: '📧',
      href: '/settings/email-notifiers'
    }
  ];

  /**
   * Renderiza um card de ação de configuração
   * @param action - Ação de configuração a ser renderizada
   */
  const renderConfigActionCard = (action: ConfigAction): React.ReactNode => (
    <div
      key={action.id}
      className="bg-white rounded-lg shadow-smart-md p-6 border border-smart-gray-100 hover:shadow-smart-lg transition-all duration-200 cursor-pointer group"
      onClick={() => handleConfigActionClick(action)}
    >
      <div className="text-center">
        <div className="text-4xl mb-4 group-hover:scale-110 transition-transform duration-200">
          {action.icon}
        </div>
        <h3 className="text-lg font-semibold text-smart-gray-800 mb-2 group-hover:text-smart-red-600 transition-colors duration-200">
          {action.title}
        </h3>
        <p className="text-sm text-smart-gray-600 leading-relaxed">
          {action.description}
        </p>
      </div>
    </div>
  );

  /**
   * Manipula o clique em uma ação de configuração
   * @param action - Ação clicada
   */
  const handleConfigActionClick = (action: ConfigAction): void => {
    // Navega para as páginas específicas
    if (action.id === 'users') {
      window.location.href = '/settings/users';
    } else if (action.id === 'permissions') {
      window.location.href = '/settings/permissions';
    } else if (action.id === 'origin-codes') {
      window.location.href = '/settings/origin-codes';
    } else if (action.id === 'operations') {
      window.location.href = '/settings/operations';
    } else if (action.id === 'stores') {
      window.location.href = '/settings/stores';
    } else if (action.id === 'email-notifiers') {
      window.location.href = '/settings/email-notifiers';
    }
  };

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className="mb-8">
      <h1 className="text-3xl font-bold text-smart-gray-800 mb-2">
        Configurações do Sistema ⚙️
      </h1>
      <p className="text-smart-gray-600">
        Gerencie usuários, códigos e configurações do sistema Smart Eletron.
      </p>
    </div>
  );

  /**
   * Renderiza o conteúdo principal da página
   */
  const renderMainContent = (): React.ReactNode => (
    <main className="flex-1 p-6 bg-smart-gray-50 overflow-auto">
      {renderPageHeader()}
      
      {/* Grid de ações de configuração */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {configActions.map(renderConfigActionCard)}
      </div>
    </main>
  );

  // Verifica permissão de acesso
  if (!canAccessSettings()) {
    return (
      <div className="h-screen flex flex-col bg-smart-gray-50">
        <Header isSidebarCollapsed={isSidebarCollapsed} />
        <div className="flex flex-1 overflow-hidden relative">
          <Sidebar onCollapseChange={setIsSidebarCollapsed} />
          <div className="flex-1 flex items-center justify-center">
            <div className="text-center">
              <div className="text-6xl mb-4">🚫</div>
              <h1 className="text-2xl font-bold text-smart-gray-800 mb-2">
                Acesso Negado
              </h1>
              <p className="text-smart-gray-600">
                Você não tem permissão para acessar as configurações do sistema.
              </p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="h-screen flex flex-col bg-smart-gray-50">
      {/* Header */}
      <Header isSidebarCollapsed={isSidebarCollapsed} />
      
      {/* Layout principal */}
      <div className="flex flex-1 overflow-hidden relative">
        {/* Sidebar */}
        <Sidebar onCollapseChange={setIsSidebarCollapsed} />
        
        {/* Conteúdo principal */}
        <div className="flex-1">
          {renderMainContent()}
        </div>
      </div>
    </div>
  );
};

export default Settings;
