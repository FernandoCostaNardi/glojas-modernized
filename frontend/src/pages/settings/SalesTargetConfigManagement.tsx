import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { salesTargetConfigService, storeService } from '@/services/api';
import { SalesTargetConfig, SalesTargetConfigFilters, ApiStore } from '@/types';
import SalesTargetConfigTable from '@/components/tables/SalesTargetConfigTable';
import SalesTargetConfigModal from '@/components/modals/SalesTargetConfigModal';
import { SalesTargetConfigFilters as SalesTargetConfigFiltersComponent } from '@/components/filters/SalesTargetConfigFilters';

/**
 * Página de Gerenciamento de Metas de Vendas e Comissões
 * Permite cadastrar, editar e gerenciar configurações de metas e comissões
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const SalesTargetConfigManagement: React.FC = () => {
  const { hasPermission } = useAuth();
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState<boolean>(false);
  
  // Estados para dados
  const [configs, setConfigs] = useState<SalesTargetConfig[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  
  // Estados para filtros
  const [filters, setFilters] = useState<SalesTargetConfigFilters>({});
  const [availableStores, setAvailableStores] = useState<ApiStore[]>([]);
  
  // Estados para modais
  const [showModal, setShowModal] = useState<boolean>(false);
  const [editingConfig, setEditingConfig] = useState<SalesTargetConfig | null>(null);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  
  // Estado para confirmação de exclusão
  const [showDeleteConfirm, setShowDeleteConfirm] = useState<boolean>(false);
  const [configToDelete, setConfigToDelete] = useState<SalesTargetConfig | null>(null);

  /**
   * Verifica se o usuário tem permissão para gerenciar configurações
   */
  const canManageConfigs = (): boolean => {
    return hasPermission('SYSTEM_ADMIN');
  };

  /**
   * Carrega configurações do servidor com filtros opcionais
   */
  const loadConfigs = async (): Promise<void> => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await salesTargetConfigService.getAllSalesTargetConfigs(
        filters.storeCode,
        filters.competenceDate
      );
      setConfigs(data);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao carregar configurações';
      setError(errorMessage);
      console.error('❌ Erro ao carregar configurações:', err);
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Carrega lojas disponíveis
   */
  const loadStores = async (): Promise<void> => {
    try {
      const stores = await storeService.getAllStores();
      setAvailableStores(Array.isArray(stores) ? stores : []);
    } catch (err) {
      console.error('❌ Erro ao carregar lojas:', err);
      setAvailableStores([]);
    }
  };

  /**
   * Carrega lojas disponíveis ao montar o componente
   */
  useEffect(() => {
    if (canManageConfigs()) {
      loadStores();
    }
  }, []);

  /**
   * Carrega configurações quando os filtros mudarem ou ao montar o componente
   */
  useEffect(() => {
    if (canManageConfigs()) {
      loadConfigs();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters]);

  /**
   * Abre modal para criar nova configuração
   */
  const handleCreateConfig = (): void => {
    setModalMode('create');
    setEditingConfig(null);
    setShowModal(true);
  };

  /**
   * Abre modal para editar configuração
   */
  const handleEditConfig = (config: SalesTargetConfig): void => {
    setModalMode('edit');
    setEditingConfig(config);
    setShowModal(true);
  };

  /**
   * Abre modal de confirmação para deletar
   */
  const handleDeleteClick = (config: SalesTargetConfig): void => {
    setConfigToDelete(config);
    setShowDeleteConfirm(true);
  };

  /**
   * Confirma e executa a exclusão
   */
  const handleConfirmDelete = async (): Promise<void> => {
    if (!configToDelete) return;

    try {
      await salesTargetConfigService.deleteSalesTargetConfig(configToDelete.id);
      await loadConfigs();
      setShowDeleteConfirm(false);
      setConfigToDelete(null);
      // TODO: Implementar notificação de sucesso
      console.log('Configuração deletada com sucesso');
    } catch (err) {
      console.error('❌ Erro ao deletar configuração:', err);
      // TODO: Implementar notificação de erro
    }
  };

  /**
   * Manipula sucesso de operação (criação/edição)
   */
  const handleSuccess = async (): Promise<void> => {
    await loadConfigs();
    setShowModal(false);
    setEditingConfig(null);
    // TODO: Implementar notificação de sucesso
    console.log('Operação realizada com sucesso');
  };

  /**
   * Manipula mudança nos filtros
   */
  const handleFiltersChange = (newFilters: SalesTargetConfigFilters): void => {
    setFilters(newFilters);
  };

  /**
   * Limpa todos os filtros
   */
  const handleClearFilters = (): void => {
    setFilters({});
  };

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className="mb-4 md:mb-8">
      <h1 className="text-xl md:text-3xl font-bold text-smart-gray-800 mb-2">
        Metas de Vendas e Comissões 🎯
      </h1>
      <p className="text-sm md:text-base text-smart-gray-600">
        Gerencie metas de vendas e percentuais de comissão por loja e competência.
      </p>
    </div>
  );

  /**
   * Renderiza a seção de filtros
   */
  const renderFiltersSection = (): React.ReactNode => (
    <SalesTargetConfigFiltersComponent
      filters={filters}
      availableStores={availableStores}
      totalElements={configs.length}
      onFiltersChange={handleFiltersChange}
      onClearFilters={handleClearFilters}
    />
  );

  /**
   * Renderiza a seção de configurações
   */
  const renderConfigsSection = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-smart-md p-3 md:p-6 w-full max-w-full box-border">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-3 md:gap-0 mb-4 md:mb-6">
        <h2 className="text-lg md:text-xl font-semibold text-smart-gray-800">
          Configurações de Metas e Comissões
        </h2>
        <button
          onClick={handleCreateConfig}
          className="w-full md:w-auto px-4 py-2 bg-smart-red-600 text-white rounded-md hover:bg-smart-red-700 transition-colors duration-200 text-sm md:text-base"
        >
          + Nova Configuração
        </button>
      </div>

      <div className="w-full max-w-full overflow-x-auto">
        <SalesTargetConfigTable
          configs={configs}
          isLoading={isLoading}
          error={error}
          onEditConfig={handleEditConfig}
          onDeleteConfig={handleDeleteClick}
        />
      </div>
    </div>
  );

  /**
   * Renderiza o conteúdo principal da página
   */
  const renderMainContent = (): React.ReactNode => (
    <main className="flex-1 bg-smart-gray-50 overflow-x-hidden overflow-y-auto w-full max-w-full">
      <div className="p-3 md:p-6 w-full max-w-full box-border">
        {renderPageHeader()}
        {renderFiltersSection()}
        {renderConfigsSection()}
      </div>
    </main>
  );

  /**
   * Renderiza modal de confirmação de exclusão
   */
  const renderDeleteConfirmModal = (): React.ReactNode => {
    if (!showDeleteConfirm || !configToDelete) return null;

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
          <h3 className="text-lg font-semibold text-smart-gray-800 mb-4">
            Confirmar Exclusão
          </h3>
          <p className="text-smart-gray-600 mb-6">
            Tem certeza que deseja excluir a configuração da loja{' '}
            <strong>{configToDelete.storeCode}</strong> para a competência{' '}
            <strong>{configToDelete.competenceDate}</strong>?
          </p>
          <div className="flex justify-end gap-3">
            <button
              onClick={() => {
                setShowDeleteConfirm(false);
                setConfigToDelete(null);
              }}
              className="px-4 py-2 border border-smart-gray-300 text-smart-gray-700 rounded-md hover:bg-smart-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              onClick={handleConfirmDelete}
              className="px-4 py-2 bg-smart-red-600 text-white rounded-md hover:bg-smart-red-700 transition-colors"
            >
              Excluir
            </button>
          </div>
        </div>
      </div>
    );
  };

  // Verifica permissão de acesso
  if (!canManageConfigs()) {
    return (
      <div className="min-h-screen flex flex-col bg-smart-gray-50 w-full max-w-full overflow-x-hidden">
        <Header isSidebarCollapsed={isSidebarCollapsed} />
        <div className="flex flex-1 relative w-full max-w-full overflow-x-hidden">
          <Sidebar onCollapseChange={setIsSidebarCollapsed} />
          <div className="flex-1 flex items-center justify-center overflow-y-auto">
            <div className="text-center p-4">
              <div className="text-6xl mb-4">🚫</div>
              <h1 className="text-2xl font-bold text-smart-gray-800 mb-2">
                Acesso Negado
              </h1>
              <p className="text-smart-gray-600">
                Você não tem permissão para acessar o gerenciamento de metas e comissões.
              </p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-smart-gray-50 w-full max-w-full overflow-x-hidden">
      {/* Header */}
      <Header isSidebarCollapsed={isSidebarCollapsed} />
      
      {/* Layout principal */}
      <div className="flex flex-1 relative w-full max-w-full overflow-x-hidden">
        {/* Sidebar */}
        <Sidebar onCollapseChange={setIsSidebarCollapsed} />
        
        {/* Conteúdo principal */}
        <div className="flex-1 min-w-0 w-full max-w-full overflow-x-hidden">
          {renderMainContent()}
        </div>
      </div>

      {/* Modais */}
      <SalesTargetConfigModal
        isOpen={showModal}
        onClose={() => {
          setShowModal(false);
          setEditingConfig(null);
        }}
        mode={modalMode}
        config={editingConfig}
        onSuccess={handleSuccess}
      />

      {/* Modal de confirmação de exclusão */}
      {renderDeleteConfirmModal()}
    </div>
  );
};

export default SalesTargetConfigManagement;

