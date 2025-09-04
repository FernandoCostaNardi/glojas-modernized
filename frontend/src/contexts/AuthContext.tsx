import React, { createContext, useContext, useEffect, useState, useCallback, useRef } from 'react';
import type { AuthContextType, AuthUser, LoginResponse } from '@/types';
import { 
  saveToken, 
  getStoredToken, 
  removeToken, 
  isValidToken, 
  getTimeUntilExpiration,
  STORAGE_KEYS 
} from '@/utils/token';

/**
 * Context para gerenciamento de autenticação
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const AuthContext = createContext<AuthContextType | undefined>(undefined);

/**
 * Interface para as propriedades do AuthProvider
 */
interface AuthProviderProps {
  readonly children: React.ReactNode;
}

/**
 * Provider do contexto de autenticação
 * Gerencia estado global de autenticação, token e auto-logout
 */
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(null);
  
  // Refs para evitar dependências circulares
  const logoutRef = useRef<() => void>();
  const navigateToLoginRef = useRef<() => void>();

  /**
   * Verifica se o usuário tem uma permissão específica
   * @param permission - Nome da permissão a verificar
   * @returns True se o usuário tem a permissão
   */
  const hasPermission = useCallback((permission: string): boolean => {
    if (!user || !user.permissions) {
      return false;
    }
    return user.permissions.includes(permission);
  }, [user]);

  /**
   * Realiza o login salvando dados do usuário e token
   * @param response - Resposta da API de login
   */
  const login = useCallback((response: LoginResponse): void => {
    const { token: authToken, username, name, roles, permissions } = response;
    
    // Salva token no localStorage
    saveToken(authToken);
    
    // Cria objeto do usuário
    const userData: AuthUser = {
      username,
      name,
      roles,
      permissions,
    };
    
    // Salva dados do usuário
    try {
      localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(userData));
    } catch (error) {
      console.error('Erro ao salvar dados do usuário:', error);
    }
    
    // Atualiza estado
    setToken(authToken);
    setUser(userData);
    setIsAuthenticated(true);
  }, []);

  /**
   * Limpa dados de autenticação sem navegação
   */
  const clearAuth = useCallback((): void => {
    // Remove dados do localStorage
    removeToken();
    
    // Limpa estado
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
  }, []);

  /**
   * Navega para a página de login
   */
  const navigateToLogin = useCallback((): void => {
    window.location.href = '/';
  }, []);

  /**
   * Realiza o logout limpando dados de autenticação e navegando
   */
  const logout = useCallback((): void => {
    clearAuth();
    navigateToLogin();
  }, [clearAuth, navigateToLogin]);

  /**
   * Inicializa autenticação verificando dados salvos
   */
  const initializeAuth = useCallback((): void => {
    try {
      const storedToken = getStoredToken();
      const storedUserData = localStorage.getItem(STORAGE_KEYS.USER_DATA);
      
      if (storedToken && isValidToken(storedToken) && storedUserData) {
        const userData = JSON.parse(storedUserData) as AuthUser;
        
        setToken(storedToken);
        setUser(userData);
        setIsAuthenticated(true);
      } else {
        // Token inválido ou expirado
        clearAuth();
      }
    } catch (error) {
      console.error('Erro ao inicializar autenticação:', error);
      clearAuth();
    }
  }, [clearAuth]);

  /**
   * Verifica periodicamente se o token ainda é válido
   */
  const checkTokenValidity = useCallback((): void => {
    if (token && !isValidToken(token)) {
      clearAuth();
      navigateToLogin();
    }
  }, [token, clearAuth, navigateToLogin]);

  // Armazena referências estáveis para evitar re-renders
  logoutRef.current = logout;
  navigateToLoginRef.current = navigateToLogin;

  /**
   * Configura verificação automática de expiração do token
   */
  useEffect(() => {
    if (!token || !isAuthenticated) {
      return;
    }

    // Verifica imediatamente
    checkTokenValidity();
    
    // Verifica a cada minuto
    const interval = setInterval(checkTokenValidity, 60000);
    
    // Agenda logout automático baseado na expiração
    const timeUntilExpiration = getTimeUntilExpiration(token);
    if (timeUntilExpiration > 0) {
      const timeout = setTimeout(() => {
        clearAuth();
        navigateToLogin();
      }, timeUntilExpiration);
      
      return () => {
        clearInterval(interval);
        clearTimeout(timeout);
      };
    }
    
    return () => clearInterval(interval);
  }, [token, isAuthenticated]); // Removidas dependências que causavam loops

  /**
   * Inicializa autenticação na montagem do componente
   */
  useEffect(() => {
    initializeAuth();
  }, []); // Sem dependências para executar apenas uma vez

  /**
   * Valor do contexto
   */
  const contextValue: AuthContextType = {
    isAuthenticated,
    user,
    token,
    login,
    logout,
    hasPermission,
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};

/**
 * Hook personalizado para usar o contexto de autenticação
 * @returns Contexto de autenticação
 * @throws Error se usado fora do AuthProvider
 */
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  
  if (context === undefined) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  
  return context;
};

export default AuthContext;
