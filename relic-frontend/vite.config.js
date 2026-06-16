import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src')
        }
    },
    build: {
        target: 'esnext'
    },
    esbuild: {
        target: 'esnext'
    },
    optimizeDeps: {
        esbuildOptions: {
            target: 'esnext'
        }
    },
    server: {
        historyApiFallback: true,
        proxy: {
            '^/admin/': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            '^/admin$': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            '^/user/': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            '^/user$': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            }
        }
    }
})