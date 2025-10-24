export const BUSINESS_API_ROUTES = {
    LOGIN: '/auth/login',
    USERS: '/users',
    USER_BY_ID: (id: string | number) => `/users/${id}`,
    SALES: '/sales'
};

export const LEGACY_API_ROUTES = {
    PRODUTOS: '/produtos',
    PRODUTO_BY_ID: (id: string | number) => `/produtos/${id}`
};


