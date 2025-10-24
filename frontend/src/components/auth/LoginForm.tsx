import React, { useState } from 'react';
import { businessApi } from '@/services/api';
import { BUSINESS_API_ROUTES } from '@/constants/apiRoutes';
import { LoginResponse, LoginError, LoginCredentials, FormEventHandler } from '@/types';
import { useAuth } from '@/contexts/AuthContext';
import Button from '../ui/Button';
import Input from '../ui/Input';
import { validateLoginForm, sanitizeInput } from '../../utils/validation';

/**
 * Interface para as propriedades do componente LoginForm
 * Seguindo princípios de Clean Code com tipos bem definidos
 */
interface LoginFormProps {
  readonly onLoginSuccess: (response: LoginResponse) => void;
  readonly onLoginError: (error: LoginError) => void;
  readonly className?: string;
}



/**
 * Interface para erros do formulário
 */
interface FormErrors {
  readonly email?: string;
  readonly password?: string;
  readonly general?: string;
}

/**
 * Componente LoginForm com TypeScript
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const LoginForm: React.FC<LoginFormProps> = ({
  onLoginSuccess,
  onLoginError,
  className = '',
}) => {
  const { login } = useAuth();
  // Estados do formulário tipados
  const [formData, setFormData] = useState<LoginCredentials>({
    email: '',
    password: '',
  });
  
  const [errors, setErrors] = useState<FormErrors>({});
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [showPassword, setShowPassword] = useState<boolean>(false);

  /**
   * Atualiza o valor de um campo do formulário
   * @param field - Nome do campo
   * @param value - Novo valor
   */
  const handleInputChange = (field: keyof LoginCredentials, value: string): void => {
    const sanitizedValue = sanitizeInput(value);
    
    setFormData(prev => ({
      ...prev,
      [field]: sanitizedValue,
    }));
    
    // Limpa erro do campo quando usuário começa a digitar
    if (errors[field as keyof FormErrors]) {
      setErrors(prev => ({
        ...prev,
        [field]: '',
      }));
    }
  };

  /**
   * Valida o formulário antes do envio
   * @returns {boolean} True se válido, false caso contrário
   */
  const validateForm = () => {
    const validationErrors = validateLoginForm(formData);
    setErrors(validationErrors);
    return Object.keys(validationErrors).length === 0;
  };

  /**
   * Processa o login do usuário
   * @param {Object} loginData - Dados de login
   * @returns {Promise<Object>} Resposta da API
   */
  const processLogin = async (loginData: LoginCredentials): Promise<LoginResponse> => {
    try {
      const response = await businessApi.post(BUSINESS_API_ROUTES.LOGIN, loginData, { timeout: 10000 });
      
      return response.data;
    } catch (error: any) {
      throw new Error(
        error.response?.data?.message || 
        error.response?.status === 401 ? 'Credenciais inválidas' :
        error.response?.status === 403 ? 'Acesso negado' :
        'Erro ao fazer login. Tente novamente.'
      );
    }
  };

  /**
   * Manipula o envio do formulário
   * @param e - Evento do formulário
   */
  const handleSubmit: FormEventHandler = async (e) => {
    e.preventDefault();
    
    // Valida formulário
    if (!validateForm()) {
      return;
    }
    
    setIsLoading(true);
    
    try {
      const response = await processLogin(formData);
      
      // Realiza login no contexto
      login(response);
      
      // Chama callback de sucesso
      if (onLoginSuccess) {
        onLoginSuccess(response);
      }
      
    } catch (error: any) {
      console.error('Erro no login:', error);
      
      // Define erro geral
      setErrors({
        general: error.message || 'Erro inesperado',
      });
      
      // Chama callback de erro
      if (onLoginError) {
        const loginError: LoginError = {
          message: error.message || 'Erro inesperado',
          code: error.code,
          details: error,
        };
        onLoginError(loginError);
      }
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Alterna a visibilidade da senha
   */
  const togglePasswordVisibility = (): void => {
    setShowPassword(!showPassword);
  };

  /**
   * Renderiza o ícone de visibilidade da senha
   * @returns Ícone de visibilidade
   */
  const renderPasswordToggle = (): React.ReactNode => {
    return (
      <button
        type="button"
        onClick={togglePasswordVisibility}
        className="absolute top-11 right-3 flex items-center justify-center z-10 w-6 h-6"
      >
        {showPassword ? (
          <svg className="h-5 w-5 text-smart-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
          </svg>
        ) : (
          <svg className="h-5 w-5 text-smart-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
          </svg>
        )}
      </button>
    );
  };

  return (
    <form onSubmit={handleSubmit} className={`space-y-6 ${className}`}>
      {/* Mensagem de erro geral */}
      {errors.general && (
        <div className="bg-error-50 border border-error-200 rounded-lg p-4">
          <div className="flex">
            <div className="flex-shrink-0">
              <svg className="h-5 w-5 text-error-400" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </div>
            <div className="ml-3">
              <p className="text-sm text-error-800">{errors.general}</p>
            </div>
          </div>
        </div>
      )}

      {/* Campo Email */}
      <Input
        type="email"
        label="Email"
        placeholder="Digite seu email"
        value={formData.email}
        onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleInputChange('email', e.target.value)}
        error={errors.email}
        required
        autoComplete="email"
      />

      {/* Campo Senha */}
      <div className="relative">
        <Input
          type={showPassword ? 'text' : 'password'}
          label="Senha"
          placeholder="Digite sua senha"
          value={formData.password}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleInputChange('password', e.target.value)}
          error={errors.password}
          required
          autoComplete="current-password"
        />
        {renderPasswordToggle()}
      </div>

      {/* Botão de Login */}
      <Button
        type="submit"
        variant="primary"
        size="lg"
        loading={isLoading}
        disabled={isLoading}
        className="w-full"
      >
        {isLoading ? 'Entrando...' : 'Entrar'}
      </Button>

      {/* Links de ajuda */}
      <div className="text-center space-y-2">
        <a
          href="#esqueci-senha"
          className="text-sm text-smart-red-600 hover:text-smart-red-700 transition-colors duration-200"
        >
          Esqueci minha senha
        </a>
        <div className="text-sm text-smart-gray-500">
          Não tem uma conta?{' '}
          <a
            href="#cadastro"
            className="text-smart-red-600 hover:text-smart-red-700 font-medium transition-colors duration-200"
          >
            Cadastre-se
          </a>
        </div>
      </div>
    </form>
  );
};

// TypeScript já fornece validação de tipos em tempo de compilação

export default LoginForm;
