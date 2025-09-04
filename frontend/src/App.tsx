import React from 'react';
import { AuthProvider, useAuth } from '@/contexts/AuthContext';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import Dashboard from '@/pages/Dashboard';
import Settings from '@/pages/Settings';
import UserManagement from '@/pages/settings/UserManagement';
import PermissionManagement from '@/pages/settings/PermissionManagement';
import Login from '@/pages/Login';

/**
 * Componente principal da aplicação Smart Eletron
 * Seguindo princípios de Clean Code com TypeScript
 */
const App: React.FC = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

/**
 * Componente de conteúdo da aplicação
 * Gerencia roteamento baseado no estado de autenticação
 */
const AppContent: React.FC = () => {
  const { isAuthenticated } = useAuth();

  // Renderiza Login ou rota baseada na URL
  if (!isAuthenticated) {
    return <Login />;
  }

  // Roteamento simples baseado na URL atual
  const currentPath = window.location.pathname;
  
  if (currentPath === '/settings') {
    return (
      <ProtectedRoute>
        <Settings />
      </ProtectedRoute>
    );
  }

  if (currentPath === '/settings/users') {
    return (
      <ProtectedRoute>
        <UserManagement />
      </ProtectedRoute>
    );
  }

  if (currentPath === '/settings/permissions') {
    return (
      <ProtectedRoute>
        <PermissionManagement />
      </ProtectedRoute>
    );
  }

  // Rota padrão: Dashboard
  return (
    <ProtectedRoute>
      <Dashboard />
    </ProtectedRoute>
  );
};

export default App;