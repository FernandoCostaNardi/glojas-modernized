/**
 * Tipos globais da aplicação Smart Eletron
 * Seguindo princípios de Clean Code com tipos bem definidos
 */

// =====================================================
// TIPOS DE COMPONENTES UI
// =====================================================

export type ButtonVariant = 'primary' | 'secondary' | 'danger' | 'ghost';
export type ButtonSize = 'sm' | 'md' | 'lg' | 'xl';
export type InputType = 'text' | 'email' | 'password' | 'tel' | 'url';
export type LogoVariant = 'color' | 'white' | 'dark';
export type LogoSize = 'sm' | 'md' | 'lg' | 'xl';

// =====================================================
// TIPOS DE AUTENTICAÇÃO
// =====================================================

export interface LoginCredentials {
  readonly email: string;
  readonly password: string;
}

export interface LoginResponse {
  readonly token: string;
  readonly username: string;
  readonly name: string;
  readonly roles: readonly string[];
  readonly permissions: readonly string[];
}

export interface LoginError {
  readonly message: string;
  readonly code?: string;
  readonly details?: unknown;
}

export interface AuthUser {
  readonly username: string;
  readonly name: string;
  readonly roles: readonly string[];
  readonly permissions: readonly string[];
}

export interface AuthContextType {
  readonly isAuthenticated: boolean;
  readonly user: AuthUser | null;
  readonly token: string | null;
  readonly login: (response: LoginResponse) => void;
  readonly logout: () => void;
  readonly hasPermission: (permission: string) => boolean;
}

export interface User {
  readonly id: string;
  readonly username: string;
  readonly email: string;
  readonly name: string;
  readonly roles: readonly string[];
  readonly isActive: boolean;
  readonly createdAt: string;
  readonly lastLoginAt?: string;
}

/**
 * Interface para usuário do sistema (usado no UserModal)
 */
export interface SystemUser {
  readonly id: string;
  readonly name: string;
  readonly username: string;
  readonly email: string;
  readonly isActive: boolean;
  readonly isNotLocked: boolean;
  readonly joinDate: string;
  readonly lastLoginDate?: string;
  readonly roles: string[];
}

/**
 * Interface para usuário retornado pela API
 * Mapeia os campos exatos retornados pelo backend
 */
export interface ApiSystemUser {
  readonly id: string;
  readonly name: string;
  readonly username: string;
  readonly email: string;
  readonly active: boolean; // Campo retornado pela API
  readonly notLocked: boolean; // Campo retornado pela API
  readonly joinDate: string;
  readonly lastLoginDate?: string;
  readonly roles: string[];
  readonly profileImageUrl?: string;
  readonly message?: string;
  readonly updatedAt?: string;
}

/**
 * Interface para requisição de criação de usuário
 * Seguindo a estrutura do backend CreateUserRequest
 */
export interface CreateUserRequest {
  readonly name: string;
  readonly username: string;
  readonly email: string;
  readonly password: string;
  readonly profileImageUrl?: string;
  readonly roles: string[];
}

/**
 * Interface para resposta de criação de usuário
 * Seguindo a estrutura do backend CreateUserResponse
 */
export interface CreateUserResponse {
  readonly id: string;
  readonly name: string;
  readonly username: string;
  readonly email: string;
  readonly profileImageUrl?: string;
  readonly joinDate: string;
  readonly isActive: boolean;
  readonly isNotLocked: boolean;
  readonly roles: string[];
  readonly message?: string;
}

/**
 * Interface para requisição de atualização de usuário
 * Seguindo a estrutura do backend UpdateUserRequest
 */
export interface UpdateUserRequest {
  readonly name: string;
  readonly email: string;
  readonly profileImageUrl?: string;
  readonly roles: string[];
  readonly isActive: boolean;
  readonly isNotLocked: boolean;
}

/**
 * Interface para resposta de atualização de usuário
 */
export interface UpdateUserResponse {
  readonly id: string;
  readonly name: string;
  readonly username: string;
  readonly email: string;
  readonly profileImageUrl?: string;
  readonly joinDate: string;
  readonly isActive: boolean;
  readonly isNotLocked: boolean;
  readonly roles: string[];
  readonly message?: string;
}

/**
 * Interface para roles disponíveis no sistema
 * Usada no UserModal para seleção de permissões
 * Mapeia os campos exatos retornados pela API
 */
export interface AvailableRole {
  readonly id: string;
  readonly name: string;
  readonly description: string;
  readonly active: boolean; // Campo retornado pela API (corrigido para 'active')
  readonly createdAt: string;
  readonly updatedAt: string;
  readonly permissionNames: string[];
}

/**
 * Interface para filtros de busca de usuários
 * Usada na busca com filtros e paginação
 */
export interface UserSearchFilters {
  name?: string;
  roles?: string[];
  isActive?: boolean | undefined;
  isNotLocked?: boolean | undefined;
}

/**
 * Interface para resposta paginada da API de usuários
 * Inclui conteúdo, metadados de paginação e totalizadores
 * Seguindo a nova estrutura da API v1
 */
export interface UserPageResponse {
  readonly users: ApiSystemUser[]; // Lista de usuários (antes era "content")
  readonly pagination: {
    readonly currentPage: number;
    readonly totalPages: number;
    readonly totalElements: number;
    readonly pageSize: number;
    readonly hasNext: boolean;
    readonly hasPrevious: boolean;
  };
  readonly counts: {
    readonly totalActive: number;    // Usuários ativos
    readonly totalInactive: number;  // Usuários inativos
    readonly totalBlocked: number;   // Usuários bloqueados
    readonly totalUsers: number;     // Total geral
  };
}

// =====================================================
// TIPOS DE FORMULÁRIOS
// =====================================================

export interface FormError {
  readonly field: string;
  readonly message: string;
}

export interface ValidationResult {
  readonly isValid: boolean;
  readonly errors: readonly FormError[];
}

export interface FormErrors {
  readonly [key: string]: string | undefined;
}

export interface FormState<T> {
  readonly data: T;
  readonly errors: Record<keyof T, string>;
  readonly isSubmitting: boolean;
  readonly isDirty: boolean;
}

// =====================================================
// TIPOS DE API
// =====================================================

export interface ApiResponse<T> {
  readonly success: boolean;
  readonly data?: T;
  readonly message?: string;
  readonly errors?: readonly string[];
}

export interface ApiError {
  readonly status: number;
  readonly message: string;
  readonly details?: unknown;
}

export interface PaginationInfo {
  readonly page: number;
  readonly size: number;
  readonly totalElements: number;
  readonly totalPages: number;
  readonly hasNext: boolean;
  readonly hasPrevious: boolean;
}

export interface PagedResponse<T> {
  readonly content: readonly T[];
  readonly pagination: PaginationInfo;
}

// =====================================================
// TIPOS DE PRODUTOS (Legacy API)
// =====================================================

export interface Product {
  readonly codigo: string;
  readonly descricao: string;
  readonly secao?: string;
  readonly grupo?: string;
  readonly subgrupo?: string;
  readonly marca?: string;
  readonly partNumberCodigo?: string;
  readonly refplu?: string;
  readonly ncm?: string;
  readonly isActive: boolean;
}

export interface ProductFilters {
  readonly secao?: string;
  readonly grupo?: string;
  readonly marca?: string;
  readonly descricao?: string;
}

// =====================================================
// TIPOS DE ESTADOS DA APLICAÇÃO
// =====================================================

export type LoadingState = 'idle' | 'loading' | 'succeeded' | 'failed';

export interface AppError {
  readonly id: string;
  readonly message: string;
  readonly timestamp: Date;
  readonly severity: 'error' | 'warning' | 'info';
}

export interface NotificationState {
  readonly type: 'success' | 'error' | 'warning' | 'info';
  readonly message: string;
  readonly duration?: number;
}

// =====================================================
// TIPOS DE CONFIGURAÇÃO
// =====================================================

export interface AppConfig {
  readonly apiBaseUrl: string;
  readonly legacyApiBaseUrl: string;
  readonly environment: 'development' | 'staging' | 'production';
  readonly features: {
    readonly useMockData: boolean;
    readonly enableLogging: boolean;
    readonly enableAnalytics: boolean;
  };
}

// =====================================================
// TIPOS UTILITÁRIOS
// =====================================================

// Torna todas as propriedades opcionais exceto as especificadas
export type PartialExcept<T, K extends keyof T> = Partial<T> & Pick<T, K>;

// Torna todas as propriedades obrigatórias exceto as especificadas
export type RequiredExcept<T, K extends keyof T> = Required<T> & Partial<Pick<T, K>>;

// Remove propriedades readonly
export type Mutable<T> = {
  -readonly [P in keyof T]: T[P];
};

// Tipo para chaves de string de um objeto
export type StringKeys<T> = Extract<keyof T, string>;

// Tipo para valores de um objeto
export type ValueOf<T> = T[keyof T];

// =====================================================
// TIPOS DE EVENTOS
// =====================================================

export type FormEventHandler<T = HTMLFormElement> = (event: React.FormEvent<T>) => void;
export type ChangeEventHandler<T = HTMLInputElement> = (event: React.ChangeEvent<T>) => void;
export type ClickEventHandler<T = HTMLButtonElement> = (event: React.MouseEvent<T>) => void;
export type KeyboardEventHandler<T = HTMLElement> = (event: React.KeyboardEvent<T>) => void;

/**
 * Interface para permissão do sistema
 */
export interface Permission {
  readonly id: string;
  readonly name: string;
  readonly resource: string;
  readonly action: string;
  readonly description?: string;
  readonly createdAt: string;
  readonly updatedAt?: string;
}

/**
 * Interface para role do sistema
 * Mapeia os campos exatos retornados pela API
 */
export interface Role {
  readonly id: string;
  readonly name: string;
  readonly description?: string;
  readonly active: boolean; // Campo retornado pela API (corrigido para 'active')
  readonly createdAt: string;
  readonly updatedAt?: string;
  readonly permissionNames: readonly string[];
}

/**
 * Interface para criação/edição de role
 */
export interface RoleFormData {
  readonly name: string;
  readonly description: string;
  readonly isActive: boolean; // Campo para envio à API (corrigido)
  readonly selectedPermissions: readonly string[];
}

/**
 * Interface para criação/edição de permissão
 */
export interface PermissionFormData {
  readonly name: string;
  readonly resource: string;
  readonly action: string;
  readonly description: string;
}

/**
 * Interface para resposta de roles da API
 */
export interface RolePageResponse {
  readonly content: readonly Role[];
  readonly totalElements: number;
  readonly totalPages: number;
  readonly size: number;
  readonly number: number;
  readonly first: boolean;
  readonly last: boolean;
  readonly numberOfElements: number;
}

/**
 * Interface para resposta de permissões da API
 */
export interface PermissionPageResponse {
  readonly content: readonly Permission[];
  readonly totalElements: number;
  readonly totalPages: number;
  readonly size: number;
  readonly number: number;
  readonly first: boolean;
  readonly last: boolean;
  readonly numberOfElements: number;
}

// =====================================================
// TIPOS DE TOGGLE DE ROLES
// =====================================================

// Re-exportar tipos de toggle de roles
export type {
  RoleToggleOperation,
  RoleToggleResult,
  RoleToggleState,
  UseRoleToggleReturn,
  RoleToggleConfig,
  RoleToggleEvent,
  RoleToggleService,
  RoleToggleNotification
} from './roleToggle';