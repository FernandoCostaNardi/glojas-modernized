/**
 * Paleta de cores da Smart Eletron
 * Baseada no logo da empresa com vermelho vibrante e branco
 */

interface ColorScale {
  readonly 50?: string;
  readonly 100?: string;
  readonly 200?: string;
  readonly 300?: string;
  readonly 400?: string;
  readonly 500: string;
  readonly 600: string;
  readonly 700?: string;
  readonly 800?: string;
  readonly 900?: string;
  readonly 950?: string;
}

interface SmartColors {
  readonly primary: ColorScale;
  readonly neutral: ColorScale;
  readonly success: ColorScale;
  readonly error: ColorScale;
  readonly warning: ColorScale;
  readonly white: string;
  readonly black: string;
}

export const SMART_COLORS: SmartColors = {
  // Cores principais baseadas no logo oficial Smart Eletron
  primary: {
    50: '#fef2f2',
    100: '#fee2e2',
    200: '#fecaca',
    300: '#fca5a5',
    400: '#f87171',
    500: '#ef4444',
    600: '#B73C3C', // Cor principal do logo Smart Eletron
    700: '#A12828', // Versão mais escura para hover
    800: '#8B1F1F',
    900: '#7F1D1D',
    950: '#450a0a',
  },
  
  // Cores neutras
  neutral: {
    50: '#f9fafb',
    100: '#f3f4f6',
    200: '#e5e7eb',
    300: '#d1d5db',
    400: '#9ca3af',
    500: '#6b7280',
    600: '#4b5563',
    700: '#374151',
    800: '#1f2937', // Textos secundários
    900: '#111827',
    950: '#030712',
  },
  
  // Cores de estado
  success: {
    50: '#f0fdf4',
    500: '#22c55e',
    600: '#16a34a',
  },
  
  error: {
    50: '#fef2f2',
    500: '#ef4444',
    600: '#dc2626',
  },
  
  warning: {
    50: '#fffbeb',
    500: '#f59e0b',
    600: '#d97706',
  },
  
  // Cores base
  white: '#ffffff',
  black: '#000000',
};

/**
 * Obtém a cor primária da marca
 * @returns Cor primária em formato hexadecimal
 */
export const getPrimaryColor = (): string => SMART_COLORS.primary[600];

/**
 * Obtém a cor de hover para elementos interativos
 * @returns Cor de hover em formato hexadecimal
 */
export const getHoverColor = (): string => SMART_COLORS.primary[700] || SMART_COLORS.primary[600];

/**
 * Obtém a cor de texto secundário
 * @returns Cor de texto secundário em formato hexadecimal
 */
export const getSecondaryTextColor = (): string => SMART_COLORS.neutral[800] || SMART_COLORS.neutral[600];

/**
 * Obtém a cor de fundo sutil
 * @returns {string} Cor de fundo sutil em formato hexadecimal
 */
export const getSubtleBackgroundColor = (): string => SMART_COLORS.neutral[50] || SMART_COLORS.neutral[500];
