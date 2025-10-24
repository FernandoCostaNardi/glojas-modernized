const isDev = import.meta.env.DEV;

export const API_CONFIG = {
    BUSINESS_API_BASE_URL: import.meta.env.VITE_BUSINESS_API_BASE_URL || (isDev ? 'http://localhost:8089/api/business' : '/api/business'),
    LEGACY_API_BASE_URL: import.meta.env.VITE_LEGACY_API_BASE_URL || (isDev ? 'http://localhost:8087/api/legacy' : '/api/legacy'),
    TIMEOUT_MS: 30000,
};

export default API_CONFIG;


