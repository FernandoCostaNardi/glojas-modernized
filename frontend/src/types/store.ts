/**
 * Tipos para gerenciamento de lojas
 * Seguindo princípios de Clean Code com tipos bem definidos
 * Context7 - separação de responsabilidades por domínio
 */

// =====================================================
// TIPOS DE LOJA
// =====================================================

/**
 * Interface para loja do sistema
 * Mapeia os campos exatos retornados pela API
 */
export interface Store {
  readonly id: string;
  readonly code: string;
  readonly name: string;
  readonly description?: string;
  readonly address?: string;
  readonly city?: string;
  readonly state?: string;
  readonly zipCode?: string;
  readonly phone?: string;
  readonly email?: string;
  readonly isActive: boolean;
  readonly createdAt: string;
  readonly updatedAt?: string;
  readonly managerName?: string;
  readonly managerEmail?: string;
  readonly managerPhone?: string;
}

/**
 * Interface para loja retornada pela API
 * Mapeia os campos exatos retornados pelo backend
 */
export interface ApiStore {
  readonly id: string;
  readonly code: string;
  readonly name: string;
  readonly city?: string;
  readonly status: boolean; // Status da loja (ativa/inativa)
  readonly createdAt: number[]; // Array de números representando data [ano, mês, dia, hora, minuto, segundo, nanosegundo]
  readonly updatedAt?: number[]; // Array de números representando data [ano, mês, dia, hora, minuto, segundo, nanosegundo]
}

/**
 * Interface para requisição de criação de loja
 * Seguindo a estrutura do backend CreateStoreRequest
 */
export interface CreateStoreRequest {
  readonly code: string;
  readonly name: string;
  readonly address?: string;
  readonly city?: string;
  readonly state?: string;
  readonly zipCode?: string;
  readonly phone?: string;
  readonly email?: string;
  readonly status: boolean;
  readonly managerName?: string;
  readonly managerEmail?: string;
  readonly managerPhone?: string;
}

/**
 * Interface para resposta de criação de loja
 * Seguindo a estrutura do backend CreateStoreResponse
 */
export interface CreateStoreResponse {
  readonly id: string;
  readonly code: string;
  readonly name: string;
  readonly description?: string;
  readonly address?: string;
  readonly city?: string;
  readonly state?: string;
  readonly zipCode?: string;
  readonly phone?: string;
  readonly email?: string;
  readonly status: boolean;
  readonly createdAt: string;
  readonly managerName?: string;
  readonly managerEmail?: string;
  readonly managerPhone?: string;
  readonly message?: string;
}

/**
 * Interface para requisição de atualização de loja
 * Seguindo a estrutura do backend UpdateStoreRequest
 */
export interface UpdateStoreRequest {
  readonly code: string;
  readonly name: string;
  readonly address?: string;
  readonly city?: string;
  readonly state?: string;
  readonly zipCode?: string;
  readonly phone?: string;
  readonly email?: string;
  readonly status: boolean;
  readonly managerName?: string;
  readonly managerEmail?: string;
  readonly managerPhone?: string;
}

/**
 * Interface para resposta de atualização de loja
 */
export interface UpdateStoreResponse {
  readonly id: string;
  readonly code: string;
  readonly name: string;
  readonly description?: string;
  readonly address?: string;
  readonly city?: string;
  readonly state?: string;
  readonly zipCode?: string;
  readonly phone?: string;
  readonly email?: string;
  readonly isActive: boolean;
  readonly createdAt: string;
  readonly updatedAt: string;
  readonly managerName?: string;
  readonly managerEmail?: string;
  readonly managerPhone?: string;
  readonly message?: string;
}

/**
 * Interface para filtros de busca de lojas
 * Usada na busca com filtros e paginação
 */
export interface StoreSearchFilters {
  readonly code?: string;
  readonly name?: string;
  readonly city?: string;
  readonly state?: string;
  readonly isActive?: boolean | undefined;
  readonly managerName?: string;
}

/**
 * Interface para resposta paginada da API de lojas
 * Inclui conteúdo, metadados de paginação e totalizadores
 * Seguindo a estrutura da API v1
 */
export interface StorePageResponse {
  readonly stores: ApiStore[]; // Lista de lojas
  readonly pagination: {
    readonly currentPage: number;
    readonly totalPages: number;
    readonly totalElements: number;
    readonly pageSize: number;
    readonly hasNext: boolean;
    readonly hasPrevious: boolean;
  };
  readonly counts: {
    readonly totalActive: number;    // Lojas ativas
    readonly totalInactive: number;  // Lojas inativas
    readonly totalStores: number;    // Total geral
  };
}

/**
 * Interface para resposta de lojas da API (legacy)
 */
export interface StoreApiResponse {
  readonly content: readonly ApiStore[];
  readonly totalElements: number;
  readonly totalPages: number;
  readonly size: number;
  readonly number: number;
  readonly first: boolean;
  readonly last: boolean;
  readonly numberOfElements: number;
}

// =====================================================
// TIPOS DE FORMULÁRIO DE LOJA
// =====================================================

/**
 * Interface para dados do formulário de loja
 * Usada no modal de criação/edição
 */
export interface StoreFormData {
  readonly code: string;
  readonly name: string;
  readonly address: string;
  readonly city: string;
  readonly state: string;
  readonly zipCode: string;
  readonly phone: string;
  readonly email: string;
  readonly managerName: string;
  readonly managerEmail: string;
  readonly managerPhone: string;
  readonly isActive: boolean;
}

/**
 * Interface para validação de loja
 */
export interface StoreValidationResult {
  readonly isValid: boolean;
  readonly errors: {
    readonly code?: string;
    readonly name?: string;
    readonly city?: string;
    readonly state?: string;
    readonly email?: string;
    readonly phone?: string;
    readonly zipCode?: string;
    readonly managerEmail?: string;
    readonly managerPhone?: string;
  };
}

// =====================================================
// TIPOS DE ESTADOS E OPERAÇÕES
// =====================================================

/**
 * Tipo para modo do modal de loja
 */
export type StoreModalMode = 'create' | 'edit';

/**
 * Interface para estado do modal de loja
 */
export interface StoreModalState {
  readonly isOpen: boolean;
  readonly mode: StoreModalMode;
  readonly store: ApiStore | null;
  readonly isLoading: boolean;
  readonly errors: StoreValidationResult['errors'];
}

/**
 * Interface para confirmação de status de loja
 */
export interface StoreStatusConfirmation {
  readonly isOpen: boolean;
  readonly store: ApiStore | null;
  readonly action: 'toggleActive';
}

// =====================================================
// TIPOS DE HOOK DE GERENCIAMENTO
// =====================================================

/**
 * Interface para retorno do hook de gerenciamento de lojas
 * Context7 - encapsulamento de lógica de negócio
 */
export interface UseStoreManagementReturn {
  // Estados
  readonly stores: ApiStore[];
  readonly isLoading: boolean;
  readonly filters: StoreSearchFilters;
  readonly currentPage: number;
  readonly pageSize: number;
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
  readonly totalElements: number;
  readonly totalPages: number;
  readonly storeCounts: StorePageResponse['counts'];
  readonly showModal: boolean;
  readonly editingStore: ApiStore | null;
  readonly modalMode: StoreModalMode;
  readonly showStatusConfirmation: boolean;
  readonly confirmingStore: ApiStore | null;
  readonly confirmationAction: StoreStatusConfirmation['action'];

  // Ações
  readonly setIsLoading: (loading: boolean) => void;
  readonly setFilters: (filters: StoreSearchFilters) => void;
  readonly refreshStoreList: () => Promise<void>;
  readonly clearFilters: () => void;
  readonly changePage: (page: number) => void;
  readonly handleSort: (field: string) => void;
  readonly openCreateModal: () => void;
  readonly openEditModal: (store: ApiStore) => void;
  readonly closeModal: () => void;
  readonly onModalSuccess: () => Promise<void>;
  readonly openStatusConfirmation: (store: ApiStore, action: StoreStatusConfirmation['action']) => void;
  readonly closeStatusConfirmation: () => void;
  readonly confirmStatusChange: () => Promise<void>;
}

// =====================================================
// TIPOS DE COMPONENTES
// =====================================================

/**
 * Interface para props do componente de tabela de lojas
 */
export interface StoreTableProps {
  readonly stores: ApiStore[];
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
  readonly onSort: (field: string) => void;
  readonly onEditStore: (store: ApiStore) => void;
  readonly onToggleStoreStatus: (store: ApiStore) => void;
}

/**
 * Interface para props do componente de filtros de lojas
 */
export interface StoreFiltersProps {
  readonly filters: StoreSearchFilters;
  readonly totalElements: number;
  readonly onFiltersChange: (filters: StoreSearchFilters) => void;
  readonly onClearFilters: () => void;
}

/**
 * Interface para props do modal de loja
 */
export interface StoreModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly mode: StoreModalMode;
  readonly store: ApiStore | null;
  readonly onSuccess: () => Promise<void>;
}

// =====================================================
// TIPOS UTILITÁRIOS
// =====================================================

/**
 * Tipo para estados brasileiros
 */
export type BrazilianState = 
  | 'AC' | 'AL' | 'AP' | 'AM' | 'BA' | 'CE' | 'DF' | 'ES' | 'GO' 
  | 'MA' | 'MT' | 'MS' | 'MG' | 'PA' | 'PB' | 'PR' | 'PE' | 'PI' 
  | 'RJ' | 'RN' | 'RS' | 'RO' | 'RR' | 'SC' | 'SP' | 'SE' | 'TO';

/**
 * Interface para opções de estado
 */
export interface StateOption {
  readonly value: BrazilianState;
  readonly label: string;
}

/**
 * Lista de estados brasileiros para uso em selects
 */
export const BRAZILIAN_STATES: StateOption[] = [
  { value: 'AC', label: 'Acre' },
  { value: 'AL', label: 'Alagoas' },
  { value: 'AP', label: 'Amapá' },
  { value: 'AM', label: 'Amazonas' },
  { value: 'BA', label: 'Bahia' },
  { value: 'CE', label: 'Ceará' },
  { value: 'DF', label: 'Distrito Federal' },
  { value: 'ES', label: 'Espírito Santo' },
  { value: 'GO', label: 'Goiás' },
  { value: 'MA', label: 'Maranhão' },
  { value: 'MT', label: 'Mato Grosso' },
  { value: 'MS', label: 'Mato Grosso do Sul' },
  { value: 'MG', label: 'Minas Gerais' },
  { value: 'PA', label: 'Pará' },
  { value: 'PB', label: 'Paraíba' },
  { value: 'PR', label: 'Paraná' },
  { value: 'PE', label: 'Pernambuco' },
  { value: 'PI', label: 'Piauí' },
  { value: 'RJ', label: 'Rio de Janeiro' },
  { value: 'RN', label: 'Rio Grande do Norte' },
  { value: 'RS', label: 'Rio Grande do Sul' },
  { value: 'RO', label: 'Rondônia' },
  { value: 'RR', label: 'Roraima' },
  { value: 'SC', label: 'Santa Catarina' },
  { value: 'SP', label: 'São Paulo' },
  { value: 'SE', label: 'Sergipe' },
  { value: 'TO', label: 'Tocantins' }
];
