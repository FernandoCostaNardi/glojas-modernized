/**
 * Tipos para configurações de metas de vendas e comissões
 * Seguindo princípios de Clean Code com tipos bem definidos
 */

/**
 * Interface para dados completos de uma configuração de metas e comissões
 */
export interface SalesTargetConfig {
  readonly id: string;
  readonly storeCode: string;
  readonly competenceDate: string; // Formato MM/YYYY
  readonly storeSalesTarget: number;
  readonly collectiveCommissionPercentage: number;
  readonly individualSalesTarget: number;
  readonly individualCommissionPercentage: number;
  readonly createdAt?: string;
  readonly updatedAt?: string;
}

/**
 * Interface para requisição de criação de configuração
 */
export interface SalesTargetConfigRequest {
  readonly storeCode: string;
  readonly competenceDate: string; // Formato MM/YYYY
  readonly storeSalesTarget: number;
  readonly collectiveCommissionPercentage: number;
  readonly individualSalesTarget: number;
  readonly individualCommissionPercentage: number;
}

/**
 * Interface para requisição de atualização de configuração
 */
export interface UpdateSalesTargetConfigRequest {
  readonly storeCode: string;
  readonly competenceDate: string; // Formato MM/YYYY
  readonly storeSalesTarget: number;
  readonly collectiveCommissionPercentage: number;
  readonly individualSalesTarget: number;
  readonly individualCommissionPercentage: number;
}

/**
 * Interface para dados do formulário de configuração
 */
export interface SalesTargetConfigFormData {
  storeCode: string;
  competenceDate: string; // Formato MM/YYYY
  storeSalesTarget: string; // String para facilitar manipulação no input
  collectiveCommissionPercentage: string; // String para facilitar manipulação no input
  individualSalesTarget: string; // String para facilitar manipulação no input
  individualCommissionPercentage: string; // String para facilitar manipulação no input
}

/**
 * Interface para resultado de validação do formulário
 */
export interface SalesTargetConfigValidationResult {
  readonly isValid: boolean;
  readonly errors: {
    storeCode?: string;
    competenceDate?: string;
    storeSalesTarget?: string;
    collectiveCommissionPercentage?: string;
    individualSalesTarget?: string;
    individualCommissionPercentage?: string;
  };
}

/**
 * Interface para filtros de busca de configurações de metas e comissões
 */
export interface SalesTargetConfigFilters {
  readonly storeCode?: string;
  readonly competenceDate?: string;
}

