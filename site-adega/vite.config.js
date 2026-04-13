import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    https: false,
    proxy: {
      '/api': { // Proxy para requisições que começam com /api
        target: 'http://api:8080', // Redireciona para o container da API na rede interna do Docker
        changeOrigin: true, // Altera o origin da requisição para evitar problemas de CORS
        // rewrite: (path) => path.replace(/^\/api/, '') // Removido para manter o /api que o Controller espera
      }
    }
  }
})
