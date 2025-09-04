import React from 'react';
import { useAuth } from '@/contexts/AuthContext';

/**
 * Interface para as propriedades do ProtectedRoute
 */
interface ProtectedRouteProps {
  readonly children: React.ReactNode;
  readonly requiredPermission?: string;
  readonly fallback?: React.ReactNode;
}

/**
 * Componente ProtectedRoute
 * Protege rotas que requerem autenticação e opcionalmente permissões específicas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredPermission,
  fallback,
}) => {
  const { isAuthenticated, hasPermission } = useAuth();

  /**
   * Renderiza componente de acesso negado
   */
  const renderAccessDenied = (): React.ReactNode => {
    if (fallback) {
      return fallback;
    }

    return (
      <div className="min-h-screen bg-smart-gray-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl shadow-smart-lg p-8 border border-smart-gray-100 max-w-md w-full text-center">
          <div className="mb-6">
            <svg 
              className="mx-auto h-16 w-16 text-error-500" 
              fill="none" 
              viewBox="0 0 24 24" 
              stroke="currentColor"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z" 
              />
            </svg>
          </div>
          
          <h2 className="text-2xl font-bold text-smart-gray-800 mb-4">
            Acesso Negado
          </h2>
          
          <p className="text-smart-gray-600 mb-6">
            {requiredPermission 
              ? `Você não tem permissão necessária (${requiredPermission}) para acessar esta página.`
              : 'Você não tem permissão para acessar esta página.'
            }
          </p>
          
          <button
            onClick={() => window.history.back()}
            className="bg-smart-red-600 hover:bg-smart-red-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200"
          >
            Voltar
          </button>
        </div>
      </div>
    );
  };

  // Verifica se está autenticado
  if (!isAuthenticated) {
    return null; // Não renderiza Login aqui, App.tsx já gerencia isso
  }

  // Verifica permissão específica se requerida
  if (requiredPermission && !hasPermission(requiredPermission)) {
    return renderAccessDenied();
  }

  // Renderiza conteúdo protegido
  return <>{children}</>;
};

export default ProtectedRoute;
