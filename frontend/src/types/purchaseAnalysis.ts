/**
 * Types e interfaces para Análise de Compras
 */

/**
 * Interface para item de análise de compras
 */
export interface PurchaseAnalysisItem {
  readonly descricaoGrupo: string;
  readonly codigoPartNumber: string | null;
  readonly descricaoMarca: string;
  readonly refplu: string;
  readonly descricaoProduto: string;
  readonly custoReposicao: number;
  readonly precoVenda: number;
  readonly vendas90Dias: number;
  readonly vendas60Dias: number;
  readonly vendas30Dias: number;
  readonly vendasMesAtual: number;
  readonly estoque: number;
  readonly mediaMensal?: number;
  readonly diferenca?: number;
}

/**
 * Interface para informações de paginação
 */
export interface PaginationInfo {
  readonly currentPage: number;
  readonly pageSize: number;
  readonly totalElements: number;
  readonly totalPages: number;
  readonly first: boolean;
  readonly last: boolean;
}

/**
 * Interface para resposta paginada
 */
export interface PurchaseAnalysisPageResponse {
  readonly content: PurchaseAnalysisItem[];
  readonly pagination: PaginationInfo;
}

/**
 * Interface para filtros de busca
 */
export interface PurchaseAnalysisFilters {
  refplu?: string;
  hideNoSales?: boolean;
  page: number;
  size: number;
  sortBy: string;
  sortDir: 'asc' | 'desc';
}

/**
 * Opções de ordenação disponíveis
 */
export const PURCHASE_ANALYSIS_SORT_OPTIONS = [
  { value: 'refplu', label: 'REFPLU' },
  { value: 'descricaoProduto', label: 'Descrição' },
  { value: 'descricaoGrupo', label: 'Grupo' },
  { value: 'descricaoMarca', label: 'Marca' },
  { value: 'codigoPartNumber', label: 'Part Number' },
  { value: 'custoReposicao', label: 'Custo Reposição' },
  { value: 'precoVenda', label: 'Preço Venda' },
  { value: 'vendas90Dias', label: 'Vendas 90 Dias' },
  { value: 'vendas60Dias', label: 'Vendas 60 Dias' },
  { value: 'vendas30Dias', label: 'Vendas 30 Dias' },
  { value: 'vendasMesAtual', label: 'Vendas Mês Atual' },
  { value: 'estoque', label: 'Estoque' }
] as const;

/**
 * Direções de ordenação disponíveis
 */
export const SORT_DIRECTIONS = [
  { value: 'asc', label: 'Crescente' },
  { value: 'desc', label: 'Decrescente' }
] as const;

/**
 * Tamanhos de página disponíveis
 */
export const PAGE_SIZES = [15, 20, 30, 50, 100] as const;

/**
 * Tipos de abas disponíveis na página de análise de compras
 */
export type PurchaseAnalysisTabType = 'geral' | 'estoque-critico';

/**
 * Interface para abas da página de análise de compras
 */
export interface PurchaseAnalysisTab {
  readonly id: PurchaseAnalysisTabType;
  readonly label: string;
  readonly description: string;
}

