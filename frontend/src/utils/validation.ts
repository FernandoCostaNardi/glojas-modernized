import { LoginCredentials, FormErrors } from '@/types';

/**
 * Utilitários de validação para formulários
 * Seguindo princípios de Clean Code com funções pequenas e focadas
 */

/**
 * Valida se um campo está preenchido
 * @param value - Valor a ser validado
 * @param fieldName - Nome do campo para mensagem de erro
 * @returns Mensagem de erro ou null se válido
 */
export const validateRequired = (value: string, fieldName: string): string | null => {
  if (!value || value.trim().length === 0) {
    return `${fieldName} é obrigatório`;
  }
  return null;
};

/**
 * Valida formato de email
 * @param email - Email a ser validado
 * @returns Mensagem de erro ou null se válido
 */
export const validateEmail = (email: string): string | null => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  
  if (!email) {
    return 'Email é obrigatório';
  }
  
  if (!emailRegex.test(email)) {
    return 'Email deve ter um formato válido';
  }
  
  return null;
};

/**
 * Valida comprimento mínimo de senha
 * @param password - Senha a ser validada
 * @param minLength - Comprimento mínimo (padrão: 6)
 * @returns Mensagem de erro ou null se válido
 */
export const validatePassword = (password: string, minLength = 6): string | null => {
  if (!password) {
    return 'Senha é obrigatória';
  }
  
  if (password.length < minLength) {
    return `Senha deve ter pelo menos ${minLength} caracteres`;
  }
  
  return null;
};

/**
 * Valida se dois campos são iguais (ex: confirmação de senha)
 * @param value1 - Primeiro valor
 * @param value2 - Segundo valor
 * @param fieldName - Nome do campo para mensagem de erro
 * @returns Mensagem de erro ou null se válido
 */
export const validateMatch = (value1: string, value2: string, fieldName: string): string | null => {
  if (value1 !== value2) {
    return `${fieldName} não confere`;
  }
  return null;
};

/**
 * Valida comprimento mínimo e máximo
 * @param value - Valor a ser validado
 * @param min - Comprimento mínimo
 * @param max - Comprimento máximo
 * @param fieldName - Nome do campo para mensagem de erro
 * @returns Mensagem de erro ou null se válido
 */
export const validateLength = (value: string, min: number, max: number, fieldName: string): string | null => {
  if (!value) {
    return `${fieldName} é obrigatório`;
  }
  
  if (value.length < min) {
    return `${fieldName} deve ter pelo menos ${min} caracteres`;
  }
  
  if (value.length > max) {
    return `${fieldName} deve ter no máximo ${max} caracteres`;
  }
  
  return null;
};



/**
 * Tipo para definir um validador de campo
 * Seguindo princípio Single Responsibility
 */
type FieldValidator = {
  readonly field: keyof LoginCredentials;
  readonly validator: (value: string) => string | null;
  readonly fieldName: string;
};

/**
 * Configuração declarativa dos validadores de login
 * Seguindo princípio DRY e Open/Closed
 */
const LOGIN_VALIDATORS: readonly FieldValidator[] = [
  {
    field: 'email',
    validator: validateEmail,
    fieldName: 'Email',
  },
  {
    field: 'password',
    validator: validatePassword,
    fieldName: 'Senha',
  },
] as const;

/**
 * Valida dados de login usando abordagem funcional
 * Seguindo princípios Clean Code: DRY, Single Responsibility, Functional
 * 
 * @param formData - Credenciais de login a serem validadas
 * @returns Objeto com erros encontrados (vazio se válido)
 */
export const validateLoginForm = (formData: LoginCredentials): FormErrors => {
  return LOGIN_VALIDATORS.reduce((errors: FormErrors, { field, validator }) => {
    const error = validator(formData[field]);
    
    if (error) {
      return { ...errors, [field]: error };
    }
    
    return errors;
  }, {});
};

/**
 * Cria um validador genérico para formulários
 * Seguindo princípio de Higher Order Functions
 * 
 * @param validators - Array de validadores a serem aplicados
 * @returns Função de validação que pode ser reutilizada
 */
export const createFormValidator = <T extends Record<string, string>>(
  validators: readonly FieldValidator[]
) => {
  return (formData: T): FormErrors => {
    return validators.reduce((errors: FormErrors, { field, validator }) => {
      const fieldValue = formData[field as string] || '';
      const error = validator(fieldValue);
      
      if (error) {
        return { ...errors, [field]: error };
      }
      
      return errors;
    }, {});
  };
};

/**
 * Remove caracteres especiais e espaços extras
 * Seguindo princípio de função pura
 * 
 * @param value - Valor a ser limpo
 * @returns Valor limpo e sanitizado
 */
export const sanitizeInput = (value: string): string => {
  if (!value) return '';
  return value.trim().replace(/\s+/g, ' ');
};
