import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 将 /api 前缀重写为 /admin 前缀
        // 例如：/api/login -> http://localhost:8080/admin/login
        rewrite: (path) => path.replace(/^\/api/, '/admin')
      }
    }
  }
})
