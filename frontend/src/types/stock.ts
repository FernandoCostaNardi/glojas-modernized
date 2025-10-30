/**
 * Types para operações de estoque
 * Seguindo padrões de Clean Code com interfaces bem definidas
 */

/**
 * Item de estoque com quantidades por loja
 */
export interface StockItem {
  readonly refplu: string;
  readonly marca: string;
  readonly descricao: string;
  readonly loj1: number | null;
  readonly loj2: number | null;
  readonly loj3: number | null;
  readonly loj4: number | null;
  readonly loj5: number | null;
  readonly loj6: number | null;
  readonly loj7: number | null;
  readonly loj8: number | null;
  readonly loj9: number | null;
  readonly loj10: number | null;
  readonly loj11: number | null;
  readonly loj12: number | null;
  readonly loj13: number | null;
  readonly loj14: number | null;
  readonly total?: number | null;
}

/**
 * Metadados de paginação
 */
export interface PaginationMetadata {
  readonly totalElements: number;
  readonly totalPages: number;
  readonly currentPage: number;
  readonly pageSize: number;
  readonly hasNext: boolean;
  readonly hasPrevious: boolean;
}

/**
 * Informações da fonte de dados
 */
export interface DataSourceInfo {
  readonly source: string;
  readonly version: string;
  readonly endpoint: string;
}

/**
 * Resposta paginada de estoque
 */
export interface StockPageResponse {
  readonly content: StockItem[];
  readonly pagination: PaginationMetadata;
  readonly dataSource: DataSourceInfo;
  readonly timestamp: string;
  readonly status: string;
  readonly message: string;
}

/**
 * Filtros para busca de estoque
 */
export interface StockFilters {
  readonly refplu?: string;
  readonly marca?: string;
  readonly descricao?: string;
  readonly hasStock?: boolean;
  readonly page: number;
  readonly size: number;
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
}

/**
 * Opções de ordenação disponíveis
 */
export const STOCK_SORT_OPTIONS = [
  { value: 'refplu', label: 'RefPLU' },
  { value: 'marca', label: 'Marca' },
  { value: 'descricao', label: 'Descrição' }
] as const;

/**
 * Direções de ordenação
 */
export const SORT_DIRECTIONS = [
  { value: 'asc', label: 'Crescente' },
  { value: 'desc', label: 'Decrescente' }
] as const;

/**
 * Tamanhos de página disponíveis
 */
export const PAGE_SIZES = [
  { value: 15, label: '15 itens' },
  { value: 30, label: '30 itens' },
  { value: 50, label: '50 itens' }
] as const;
