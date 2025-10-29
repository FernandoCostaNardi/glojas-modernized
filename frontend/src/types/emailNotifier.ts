// =====================================================
// TIPOS PRINCIPAIS
// =====================================================

/**
 * Interface para EmailNotifier
 * Representa um notificador de email no sistema
 */
export interface EmailNotifier {
  readonly id: string;
  readonly email: string;
  readonly dailySellNotifier: boolean;
  readonly dailyMonthNotifier: boolean;
  readonly monthYearNotifier: boolean;
  readonly createdAt: string;
  readonly updatedAt: string;
}

/**
 * Interface para EmailNotifierResponse (resposta da API)
 * Dados retornados pela API para EmailNotifier
 */
export interface EmailNotifierResponse {
  readonly id: string;
  readonly email: string;
  readonly dailySellNotifier: boolean;
  readonly dailyMonthNotifier: boolean;
  readonly monthYearNotifier: boolean;
  readonly createdAt: string;
  readonly updatedAt: string;
}

// =====================================================
// REQUESTS
// =====================================================

/**
 * Interface para criação de EmailNotifier
 */
export interface CreateEmailNotifierRequest {
  readonly email: string;
  readonly dailySellNotifier: boolean;
  readonly dailyMonthNotifier: boolean;
  readonly monthYearNotifier: boolean;
}

/**
 * Interface para atualização de EmailNotifier
 */
export interface UpdateEmailNotifierRequest {
  readonly dailySellNotifier: boolean;
  readonly dailyMonthNotifier: boolean;
  readonly monthYearNotifier: boolean;
}

// =====================================================
// RESPONSES
// =====================================================

/**
 * Interface para resposta paginada de EmailNotifier
 */
export interface EmailNotifierPageResponse {
  readonly content: readonly EmailNotifierResponse[];
  readonly page: number;
  readonly size: number;
  readonly totalPages: number;
  readonly totalElements: number;
  readonly hasNext: boolean;
  readonly hasPrevious: boolean;
}

// =====================================================
// FILTROS E BUSCA
// =====================================================

/**
 * Interface para filtros de busca de EmailNotifier
 */
export interface EmailNotifierFilters {
  readonly email?: string;
}

/**
 * Interface para parâmetros de busca de EmailNotifier
 */
export interface EmailNotifierSearchParams {
  readonly page: number;
  readonly size: number;
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
  readonly filters?: EmailNotifierFilters;
}

/**
 * Interface para resposta de busca de EmailNotifier
 */
export interface EmailNotifierSearchResponse {
  readonly content: readonly EmailNotifierResponse[];
  readonly page: number;
  readonly size: number;
  readonly totalPages: number;
  readonly totalElements: number;
  readonly hasNext: boolean;
  readonly hasPrevious: boolean;
}

// =====================================================
// FORMULÁRIOS
// =====================================================

/**
 * Interface para estado do formulário de EmailNotifier
 */
export interface EmailNotifierFormState {
  readonly email: string;
  readonly dailySellNotifier: boolean;
  readonly dailyMonthNotifier: boolean;
  readonly monthYearNotifier: boolean;
}

/**
 * Interface para erros do formulário de EmailNotifier
 */
export interface EmailNotifierFormErrorsState {
  email?: string;
  dailySellNotifier?: string;
  dailyMonthNotifier?: string;
  monthYearNotifier?: string;
  general?: string;
}

// =====================================================
// ESTADOS DO MODAL
// =====================================================

/**
 * Tipo para modo do modal
 */
export type EmailNotifierModalMode = 'create' | 'edit';

/**
 * Interface para props do modal
 */
export interface EmailNotifierModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly mode: EmailNotifierModalMode;
  readonly emailNotifier?: EmailNotifier | null;
  readonly onSuccess: (message: string) => void;
  readonly onSave: (data: CreateEmailNotifierRequest | UpdateEmailNotifierRequest) => Promise<void>;
  readonly isSubmitting?: boolean;
}

// =====================================================
// OPÇÕES E CONFIGURAÇÕES
// =====================================================

/**
 * Opções para tipos de notificação
 * Usado em dropdowns e seleções
 */
export const EMAIL_NOTIFIER_TYPES = [
  {
    value: 'dailySellNotifier',
    label: 'Email com diário de vendas do dia anterior',
    description: 'Recebe relatório diário das vendas do dia anterior'
  },
  {
    value: 'dailyMonthNotifier',
    label: 'Diário com atualização do valor de vendas mensais',
    description: 'Recebe atualizações diárias do progresso das vendas mensais'
  },
  {
    value: 'monthYearNotifier',
    label: 'Mensal com valor atualizado das vendas do ano',
    description: 'Recebe relatório mensal com progresso das vendas anuais'
  }
] as const;

/**
 * Regras de validação para EmailNotifier
 */
export const EMAIL_NOTIFIER_VALIDATION_RULES = {
  email: {
    required: true,
    maxLength: 255,
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  }
} as const;

/**
 * Configuração padrão de paginação para EmailNotifier
 */
export const EMAIL_NOTIFIER_DEFAULT_PAGINATION = {
  page: 0,
  size: 20,
  sortBy: 'email',
  sortDir: 'asc' as const
} as const;

/**
 * Configuração de colunas da tabela EmailNotifier
 */
export const EMAIL_NOTIFIER_TABLE_COLUMNS = [
  {
    key: 'email',
    label: 'Email',
    sortable: true,
    width: '30%'
  },
  {
    key: 'dailySellNotifier',
    label: 'Vendas Diárias',
    sortable: false,
    width: '15%'
  },
  {
    key: 'dailyMonthNotifier',
    label: 'Vendas Mensais',
    sortable: false,
    width: '15%'
  },
  {
    key: 'monthYearNotifier',
    label: 'Vendas Anuais',
    sortable: false,
    width: '15%'
  },
  {
    key: 'actions',
    label: 'Ações',
    sortable: false,
    width: '25%'
  }
] as const;
