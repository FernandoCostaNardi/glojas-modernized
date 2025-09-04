import React from 'react';

/**
 * Props das estatísticas do sistema
 */
interface SystemStatsCardsProps {
  readonly systemCounts: {
    totalActive: number;
    totalInactive: number;
    totalBlocked: number;
    totalUsers: number;
  };
}

/**
 * Componente de estatísticas rápidas do sistema
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const SystemStatsCards: React.FC<SystemStatsCardsProps> = ({ systemCounts }) => {
  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
      <div className="bg-white rounded-lg shadow-sm border border-smart-gray-100 p-4">
        <div className="flex items-center">
          <div className="bg-smart-blue-500 rounded-lg p-2 text-white text-lg mr-3">
            👥
          </div>
          <div>
            <h3 className="text-xs font-medium text-smart-gray-600">Total de Usuários</h3>
            <p className="text-xl font-bold text-smart-gray-800">{systemCounts.totalUsers}</p>
          </div>
        </div>
      </div>
      
      <div className="bg-white rounded-lg shadow-sm border border-smart-gray-100 p-4">
        <div className="flex items-center">
          <div className="bg-green-500 rounded-lg p-2 text-white text-lg mr-3">
            ✅
          </div>
          <div>
            <h3 className="text-xs font-medium text-smart-gray-600">Usuários Ativos</h3>
            <p className="text-xl font-bold text-smart-gray-800">
              {systemCounts.totalActive}
            </p>
          </div>
        </div>
      </div>
      
      <div className="bg-white rounded-lg shadow-sm border border-smart-gray-100 p-4">
        <div className="flex items-center">
          <div className="bg-red-500 rounded-lg p-2 text-white text-lg mr-3">
            ❌
          </div>
          <div>
            <h3 className="text-xs font-medium text-smart-gray-600">Usuários Inativos</h3>
            <p className="text-xl font-bold text-smart-gray-800">
              {systemCounts.totalInactive}
            </p>
          </div>
        </div>
      </div>
      
      <div className="bg-white rounded-lg shadow-sm border border-smart-gray-100 p-4">
        <div className="flex items-center">
          <div className="bg-orange-500 rounded-lg p-2 text-white text-lg mr-3">
            🔒
          </div>
          <div>
            <h3 className="text-xs font-medium text-smart-gray-600">Usuários Bloqueados</h3>
            <p className="text-xl font-bold text-smart-gray-800">
              {systemCounts.totalBlocked}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
