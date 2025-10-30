import React from 'react';
import { AuthProvider, useAuth } from '@/contexts/AuthContext';
import { LayoutProvider } from '@/contexts/LayoutContext';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import Dashboard from '@/pages/Dashboard';
import Settings from '@/pages/Settings';
import UserManagement from '@/pages/settings/UserManagement';
import PermissionManagement from '@/pages/settings/PermissionManagement';
import OperationManagement from '@/pages/settings/OperationManagement';
import EventOriginManagement from '@/pages/settings/EventOriginManagement';
import StoreManagement from '@/pages/settings/StoreManagement';
import EmailNotifierManagement from '@/pages/settings/EmailNotifierManagement';
import VendasPage from '@/pages/vendas/VendasPage';
import Estoque from '@/pages/estoque/Estoque';
import AnaliseCompras from '@/pages/analise-compras/AnaliseCompras';
import Login from '@/pages/Login';

/**
 * Componente principal da aplicação Smart Eletron
 * Seguindo princípios de Clean Code com TypeScript
 */
const App: React.FC = () => {
  return (
    <AuthProvider>
      <LayoutProvider>
        <AppContent />
      </LayoutProvider>
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

  if (currentPath === '/settings/operations') {
    return (
      <ProtectedRoute>
        <OperationManagement />
      </ProtectedRoute>
    );
  }

  if (currentPath === '/settings/origin-codes') {
    return (
      <ProtectedRoute>
        <EventOriginManagement />
      </ProtectedRoute>
    );
  }

  if (currentPath === '/settings/stores') {
    return (
      <ProtectedRoute>
        <StoreManagement />
      </ProtectedRoute>
    );
  }

  if (currentPath === '/settings/email-notifiers') {
    return (
      <ProtectedRoute>
        <EmailNotifierManagement />
      </ProtectedRoute>
    );
  }

  if (currentPath === '/vendas') {
    return (
      <ProtectedRoute requiredPermission="sell:read">
        <VendasPage />
      </ProtectedRoute>
    );
  }

  if (currentPath === '/estoque') {
    return (
      <ProtectedRoute requiredPermission="stock:read">
        <Estoque />
      </ProtectedRoute>
    );
  }

  if (currentPath === '/analise-compras') {
    return (
      <ProtectedRoute requiredPermission="buy:read">
        <AnaliseCompras />
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