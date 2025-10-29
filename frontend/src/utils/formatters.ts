/**
 * Utilitários para formatação de dados
 * Funções para formatação de moeda, percentual e outros valores
 */

/**
 * Formata um valor numérico como moeda brasileira (R$)
 * @param value - Valor numérico a ser formatado
 * @returns String formatada como moeda brasileira
 */
export const formatCurrency = (value: number): string => {
  if (typeof value !== 'number' || isNaN(value)) {
    return 'R$ 0,00';
  }

  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value);
};

/**
 * Formata um valor numérico como percentual
 * @param value - Valor numérico a ser formatado (ex: 25.5 para 25,5%)
 * @returns String formatada como percentual
 */
export const formatPercentage = (value: number): string => {
  if (typeof value !== 'number' || isNaN(value)) {
    return '0,00%';
  }

  return new Intl.NumberFormat('pt-BR', {
    style: 'percent',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value / 100);
};

/**
 * Formata um valor numérico como número com separadores de milhares
 * @param value - Valor numérico a ser formatado
 * @returns String formatada com separadores de milhares
 */
export const formatNumber = (value: number): string => {
  if (typeof value !== 'number' || isNaN(value)) {
    return '0';
  }

  return new Intl.NumberFormat('pt-BR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(value);
};

/**
 * Formata um valor numérico como número inteiro
 * @param value - Valor numérico a ser formatado
 * @returns String formatada como número inteiro
 */
export const formatInteger = (value: number): string => {
  if (typeof value !== 'number' || isNaN(value)) {
    return '0';
  }

  return new Intl.NumberFormat('pt-BR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(Math.round(value));
};

/**
 * Formata uma data para o padrão brasileiro (DD/MM/YYYY)
 * @param date - Data a ser formatada (Date, string ou timestamp)
 * @returns String formatada como data brasileira
 */
export const formatDate = (date: Date | string | number): string => {
  try {
    const dateObj = typeof date === 'string' || typeof date === 'number' 
      ? new Date(date) 
      : date;

    if (isNaN(dateObj.getTime())) {
      return 'Data inválida';
    }

    return new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    }).format(dateObj);
  } catch (error) {
    return 'Data inválida';
  }
};

/**
 * Formata uma data e hora para o padrão brasileiro (DD/MM/YYYY HH:mm)
 * @param date - Data a ser formatada (Date, string ou timestamp)
 * @returns String formatada como data e hora brasileira
 */
export const formatDateTime = (date: Date | string | number): string => {
  try {
    const dateObj = typeof date === 'string' || typeof date === 'number' 
      ? new Date(date) 
      : date;

    if (isNaN(dateObj.getTime())) {
      return 'Data inválida';
    }

    return new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    }).format(dateObj);
  } catch (error) {
    return 'Data inválida';
  }
};

/**
 * Formata um valor de bytes para uma unidade legível (KB, MB, GB, etc.)
 * @param bytes - Valor em bytes
 * @returns String formatada com unidade apropriada
 */
export const formatBytes = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes';

  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

/**
 * Formata um valor numérico com separadores de milhares e casas decimais opcionais
 * @param value - Valor numérico a ser formatado
 * @param decimals - Número de casas decimais (padrão: 2)
 * @returns String formatada com separadores
 */
export const formatDecimal = (value: number, decimals: number = 2): string => {
  if (typeof value !== 'number' || isNaN(value)) {
    return '0,' + '0'.repeat(decimals);
  }

  return new Intl.NumberFormat('pt-BR', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  }).format(value);
};
