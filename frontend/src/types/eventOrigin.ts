/**
 * Tipos para EventOrigin - Códigos de Origem
 * Seguindo princípios de Clean Code com tipos bem definidos
 * Baseado nos DTOs do backend Business API (Java 17)
 */

// =====================================================
// ENUMS E TIPOS BÁSICOS
// =====================================================

/**
 * Enum para fonte do evento
 * Mapeia exatamente os valores do backend EventSource
 */
export type EventSource = 'PDV' | 'EXCHANGE' | 'DANFE';

/**
 * Opções para direção de ordenação
 */
export type SortDirection = 'asc' | 'desc';

// =====================================================
// INTERFACES PRINCIPAIS
// =====================================================

/**
 * Interface principal do EventOrigin
 * Representa um código de origem no sistema
 */
export interface EventOrigin {
  readonly id: string;
  readonly eventSource: EventSource;
  readonly sourceCode: string;
}

/**
 * Interface para resposta da API
 * Mapeia exatamente o EventOriginResponse do backend
 */
export interface EventOriginResponse {
  readonly id: string;
  readonly eventSource: EventSource;
  readonly sourceCode: string;
}

// =====================================================
// DTOs DE REQUEST
// =====================================================

/**
 * DTO para criação de EventOrigin
 * Mapeia o CreateEventOriginRequest do backend
 */
export interface CreateEventOriginRequest {
  readonly eventSource: EventSource;
  readonly sourceCode: string;
}

/**
 * DTO para atualização de EventOrigin
 * Mapeia o UpdateEventOriginRequest do backend
 */
export interface UpdateEventOriginRequest {
  readonly eventSource: EventSource;
  readonly sourceCode: string;
}

// =====================================================
// FILTROS E BUSCA
// =====================================================

/**
 * Interface para filtros de busca
 * Permite filtrar por EventSource e busca parcial por SourceCode
 */
export interface EventOriginFilters {
  readonly eventSource?: EventSource;
  readonly sourceCode?: string;
}

/**
 * Interface para parâmetros de busca
 * Inclui filtros, paginação e ordenação
 */
export interface EventOriginSearchParams {
  readonly eventSource?: EventSource;
  readonly sourceCode?: string;
  readonly page?: number;
  readonly size?: number;
  readonly sortBy?: string;
  readonly sortDir?: SortDirection;
}

// =====================================================
// RESPONSES DE BUSCA
// =====================================================

/**
 * Interface para informações de paginação
 * Mapeia o PaginationInfo do backend
 */
export interface PaginationInfo {
  readonly currentPage: number;
  readonly totalPages: number;
  readonly totalElements: number;
  readonly pageSize: number;
  readonly hasNext: boolean;
  readonly hasPrevious: boolean;
}

/**
 * Interface para totalizadores de EventOrigin
 * Mapeia o EventOriginCounts do backend
 */
export interface EventOriginCounts {
  readonly totalPdv: number;
  readonly totalExchange: number;
  readonly totalDanfe: number;
  readonly totalEventOrigins: number;
}

/**
 * Interface para resposta de busca paginada
 * Mapeia o EventOriginSearchResponse do backend
 */
export interface EventOriginSearchResponse {
  readonly eventOrigins: readonly EventOriginResponse[];
  readonly pagination: PaginationInfo;
  readonly counts: EventOriginCounts;
}

// =====================================================
// FORMULÁRIOS
// =====================================================

/**
 * Interface para dados do formulário de EventOrigin
 * Usado no modal de cadastro/edição
 */
export interface EventOriginFormData {
  readonly eventSource: EventSource;
  readonly sourceCode: string;
}

/**
 * Interface mutável para estado do formulário
 * Usado internamente no modal para gerenciar estado
 */
export interface EventOriginFormState {
  eventSource: EventSource;
  sourceCode: string;
}

/**
 * Interface para erros de validação do formulário
 * Mapeia erros por campo
 */
export interface EventOriginFormErrors {
  readonly eventSource?: string;
  readonly sourceCode?: string;
  readonly general?: string;
}

/**
 * Interface mutável para estado de erros do formulário
 * Usado internamente no modal para gerenciar erros
 */
export interface EventOriginFormErrorsState {
  eventSource?: string;
  sourceCode?: string;
  general?: string;
}

// =====================================================
// ESTADOS DO MODAL
// =====================================================

/**
 * Tipo para modo do modal
 */
export type EventOriginModalMode = 'create' | 'edit';

/**
 * Interface para props do modal
 */
export interface EventOriginModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly mode: EventOriginModalMode;
  readonly eventOrigin?: EventOrigin | null;
  readonly onSuccess: (message: string) => void;
  readonly onSave: (data: CreateEventOriginRequest | UpdateEventOriginRequest) => Promise<void>;
  readonly isSubmitting?: boolean;
}

// =====================================================
// OPÇÕES E CONFIGURAÇÕES
// =====================================================

/**
 * Opções para o campo EventSource
 * Usado em dropdowns e seleções
 */
export const EVENT_SOURCE_OPTIONS: readonly { value: EventSource; label: string }[] = [
  { value: 'PDV', label: 'PDV' },
  { value: 'EXCHANGE', label: 'TROCA' },
  { value: 'DANFE', label: 'DANFE' }
] as const;

/**
 * Configurações padrão para paginação
 */
export const DEFAULT_PAGINATION = {
  page: 0,
  size: 5,
  sortBy: 'sourceCode',
  sortDir: 'asc' as SortDirection
} as const;

/**
 * Configurações de validação
 */
export const VALIDATION_RULES = {
  sourceCode: {
    maxLength: 50,
    required: true
  },
  eventSource: {
    required: true
  }
} as const;

/**
 * Função utilitária para obter o label de exibição de um EventSource
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const getEventSourceDisplayName = (eventSource: EventSource): string => {
  const option = EVENT_SOURCE_OPTIONS.find(opt => opt.value === eventSource);
  return option?.label || eventSource;
};

/**
 * Função utilitária para obter o label compacto de um EventSource (para badges)
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const getEventSourceCompactName = (eventSource: EventSource): string => {
  switch (eventSource) {
    case 'PDV':
      return 'PDV';
    case 'EXCHANGE':
      return 'TROCA';
    case 'DANFE':
      return 'DANFE';
    default:
      return eventSource;
  }
};
