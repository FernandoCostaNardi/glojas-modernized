import React, { useState } from 'react';
import { LoginResponse, LoginError } from '@/types';
import Logo from '../components/ui/Logo';
import LoginForm from '../components/auth/LoginForm';

/**
 * Interface para o estado de login
 */
interface LoginStatus {
  readonly type: 'success' | 'error' | null;
  readonly message: string;
}

/**
 * Página de Login da Smart Eletron
 * Seguindo princípios de Clean Code com TypeScript
 */
const Login: React.FC = () => {
  const [loginStatus, setLoginStatus] = useState<LoginStatus>({
    type: null,
    message: '',
  });

  /**
   * Manipula o sucesso do login
   * @param response - Resposta da API tipada
   */
  const handleLoginSuccess = (_response: LoginResponse): void => {
    setLoginStatus({
      type: 'success',
      message: 'Login realizado com sucesso! Carregando dashboard...',
    });
    
    // O AuthContext já gerencia o token e redirecionamento
  };

  /**
   * Manipula erro no login
   * @param error - Erro tipado
   */
  const handleLoginError = (error: LoginError): void => {
    console.error('Erro no login:', error);
    
    setLoginStatus({
      type: 'error',
      message: error.message || 'Erro ao fazer login. Tente novamente.',
    });
    
    // Limpa status de erro após 5 segundos
    setTimeout(() => {
      setLoginStatus({ type: null, message: '' });
    }, 5000);
  };

  /**
   * Renderiza mensagem de status
   * @returns {React.ReactNode} Mensagem de status
   */
  const renderStatusMessage = () => {
    if (!loginStatus.type) return null;

    const isSuccess = loginStatus.type === 'success';
    const bgColor = isSuccess ? 'bg-success-50' : 'bg-error-50';
    const borderColor = isSuccess ? 'border-success-200' : 'border-error-200';
    const textColor = isSuccess ? 'text-success-800' : 'text-error-800';
    const iconColor = isSuccess ? 'text-success-400' : 'text-error-400';

    return (
      <div className={`${bgColor} border ${borderColor} rounded-lg p-4 mb-6 animate-fade-in`}>
        <div className="flex">
          <div className="flex-shrink-0">
            {isSuccess ? (
              <svg className={`h-5 w-5 ${iconColor}`} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
              </svg>
            ) : (
              <svg className={`h-5 w-5 ${iconColor}`} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            )}
          </div>
          <div className="ml-3">
            <p className={`text-sm ${textColor}`}>{loginStatus.message}</p>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-black flex items-center justify-center p-4">
      {/* Background decorativo */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-smart-red-100 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-pulse-slow"></div>
        <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-smart-gray-100 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-pulse-slow"></div>
      </div>

      {/* Container principal */}
      <div className="relative w-full max-w-md">
        {/* Card de login */}
        <div className="bg-white rounded-2xl shadow-smart-lg p-8 border border-smart-gray-100 animate-slide-up">
          {/* Header */}
          <div className="text-center mb-8">
            <div className="flex justify-center mb-6">
              <Logo size="xl" variant="color" />
            </div>
            <h1 className="text-2xl font-bold text-smart-gray-800 mb-2">
              Bem-vindo de volta
            </h1>
            <p className="text-smart-gray-600">
              Faça login para acessar sua conta
            </p>
          </div>

          {/* Mensagem de status */}
          {renderStatusMessage()}

          {/* Formulário de login */}
          <LoginForm
            onLoginSuccess={handleLoginSuccess}
            onLoginError={handleLoginError}
          />
        </div>

        {/* Footer */}
        <div className="text-center mt-8">
          <p className="text-sm text-white">
            © 2025 Smart Eletron. Todos os direitos reservados.
          </p>
        </div>
      </div>

      {/* Elementos decorativos */}
      <div className="fixed top-8 left-8 opacity-10">
        <div className="w-32 h-32 bg-smart-red-200 rounded-full"></div>
      </div>
      <div className="fixed bottom-8 right-8 opacity-10">
        <div className="w-24 h-24 bg-smart-gray-200 rounded-full"></div>
      </div>
    </div>
  );
};

export default Login;
