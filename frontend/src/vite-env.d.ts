/// <reference types="vite/client" />

interface ImportMetaEnv {
	readonly VITE_BUSINESS_API_BASE_URL?: string;
	readonly VITE_LEGACY_API_BASE_URL?: string;
}

interface ImportMeta {
	readonly env: ImportMetaEnv;
}


