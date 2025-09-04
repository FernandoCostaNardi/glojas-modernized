/**
 * Utilitários para gerenciamento de tokens JWT
 * Seguindo princípios de Clean Code com responsabilidade única
 */

/**
 * Interface para o payload decodificado do JWT
 */
interface JWTPayload {
  readonly sub: string; // subject (username)
  readonly iat: number; // issued at
  readonly exp: number; // expiration time
  readonly [key: string]: unknown;
}

/**
 * Decodifica um token JWT sem verificar a assinatura
 * @param token - Token JWT para decodificar
 * @returns Payload decodificado ou null se inválido
 */
export const decodeJWT = (token: string): JWTPayload | null => {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) {
      return null;
    }

    const payload = parts[1];
    if (!payload) {
      return null;
    }
    const decodedPayload = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decodedPayload) as JWTPayload;
  } catch (error) {
    console.error('Erro ao decodificar JWT:', error);
    return null;
  }
};

/**
 * Verifica se um token JWT está expirado
 * @param token - Token JWT para verificar
 * @returns True se expirado, false caso contrário
 */
export const isTokenExpired = (token: string): boolean => {
  const payload = decodeJWT(token);
  if (!payload) {
    return true;
  }

  const currentTime = Math.floor(Date.now() / 1000);
  return payload.exp < currentTime;
};

/**
 * Obtém o tempo de expiração de um token em milliseconds
 * @param token - Token JWT
 * @returns Timestamp de expiração em milliseconds ou null se inválido
 */
export const getTokenExpiration = (token: string): number | null => {
  const payload = decodeJWT(token);
  if (!payload) {
    return null;
  }

  return payload.exp * 1000; // Converte para milliseconds
};

/**
 * Calcula o tempo restante até a expiração do token
 * @param token - Token JWT
 * @returns Tempo restante em milliseconds ou 0 se expirado
 */
export const getTimeUntilExpiration = (token: string): number => {
  const expiration = getTokenExpiration(token);
  if (!expiration) {
    return 0;
  }

  const timeRemaining = expiration - Date.now();
  return Math.max(0, timeRemaining);
};

/**
 * Formata o tempo restante em uma string legível
 * @param milliseconds - Tempo em milliseconds
 * @returns String formatada (ex: "15min", "2h 30min")
 */
export const formatTimeRemaining = (milliseconds: number): string => {
  if (milliseconds <= 0) {
    return 'Expirado';
  }

  const minutes = Math.floor(milliseconds / (1000 * 60));
  const hours = Math.floor(minutes / 60);
  const remainingMinutes = minutes % 60;

  if (hours > 0) {
    return remainingMinutes > 0 
      ? `${hours}h ${remainingMinutes}min`
      : `${hours}h`;
  }

  return `${minutes}min`;
};

/**
 * Storage keys para persistência
 */
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'smart_auth_token',
  USER_DATA: 'smart_user_data',
} as const;

/**
 * Salva o token no localStorage
 * @param token - Token para salvar
 */
export const saveToken = (token: string): void => {
  try {
    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
  } catch (error) {
    console.error('Erro ao salvar token:', error);
  }
};

/**
 * Recupera o token do localStorage
 * @returns Token salvo ou null se não existir
 */
export const getStoredToken = (): string | null => {
  try {
    return localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
  } catch (error) {
    console.error('Erro ao recuperar token:', error);
    return null;
  }
};

/**
 * Remove o token do localStorage
 */
export const removeToken = (): void => {
  try {
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
  } catch (error) {
    console.error('Erro ao remover token:', error);
  }
};

/**
 * Valida se um token é válido e não expirado
 * @param token - Token para validar
 * @returns True se válido, false caso contrário
 */
export const isValidToken = (token: string | null): boolean => {
  if (!token) {
    return false;
  }

  return !isTokenExpired(token);
};

/**
 * Obtém o token do localStorage
 * @returns Token salvo ou null se não existir
 */
export const getToken = (): string | null => {
  return getStoredToken();
};
