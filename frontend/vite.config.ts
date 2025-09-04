import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    // React plugin
    react(),
    // Plugin para resolver paths do tsconfig
    tsconfigPaths(),
  ],
  
  // Configuração do servidor de desenvolvimento
  server: {
    port: 3000,
    open: true,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api/business'),
      },
    },
  },
  
  // Configurações de build
  build: {
    target: 'es2020',
    minify: 'esbuild',
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom'],
          utils: ['axios']
        }
      }
    }
  },
  
  // Otimizações
  optimizeDeps: {
    include: ['react', 'react-dom', 'axios']
  }
});
