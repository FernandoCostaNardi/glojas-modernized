/**
 * Tipos específicos para operações de toggle de roles
 * Seguindo princípios de Clean Code com responsabilidade única
 */

import { Role } from './index';

/**
 * Interface para operação de toggle de role
 * Define os dados necessários para alterar o status de uma role
 */
export interface RoleToggleOperation {
  readonly roleId: string;
  readonly currentStatus: boolean;
  readonly newStatus: boolean;
  readonly roleName: string;
}

/**
 * Interface para resultado da operação de toggle
 * Contém informações sobre o sucesso ou falha da operação
 */
export interface RoleToggleResult {
  readonly success: boolean;
  readonly role: Role;
  readonly message: string;
  readonly error?: string;
  readonly timestamp: Date;
}

/**
 * Interface para estado do hook de toggle
 * Gerencia estados de loading e erro de forma granular
 */
export interface RoleToggleState {
  readonly isToggling: boolean;
  readonly togglingRoleId: string | null;
  readonly error: string | null;
  readonly lastOperation?: RoleToggleResult;
}

/**
 * Interface para retorno do hook useRoleToggle
 * Expõe apenas as funcionalidades necessárias
 */
export interface UseRoleToggleReturn {
  readonly state: RoleToggleState;
  readonly toggleRole: (role: Role) => Promise<void>;
  readonly clearError: () => void;
  readonly canToggleRole: (role: Role) => boolean;
}

/**
 * Interface para configuração do hook
 * Permite customização do comportamento
 */
export interface RoleToggleConfig {
  readonly enableRetry?: boolean;
  readonly maxRetries?: number;
  readonly retryDelay?: number;
  readonly enableLogging?: boolean;
}

/**
 * Tipo para eventos de toggle
 * Usado para logging e auditoria
 */
export type RoleToggleEvent = 
  | { type: 'TOGGLE_STARTED'; roleId: string; roleName: string }
  | { type: 'TOGGLE_SUCCESS'; roleId: string; roleName: string; newStatus: boolean }
  | { type: 'TOGGLE_ERROR'; roleId: string; roleName: string; error: string }
  | { type: 'TOGGLE_RETRY'; roleId: string; roleName: string; attempt: number };

/**
 * Interface para serviço de toggle
 * Abstração para permitir diferentes implementações
 */
export interface RoleToggleService {
  readonly toggleRoleStatus: (roleId: string, isActive: boolean) => Promise<Role>;
  readonly validateTogglePermission: (role: Role) => boolean;
}

/**
 * Interface para notificações
 * Abstração para feedback visual
 */
export interface RoleToggleNotification {
  readonly showSuccess: (message: string) => void;
  readonly showError: (message: string) => void;
  readonly showLoading: (message: string) => void;
}
