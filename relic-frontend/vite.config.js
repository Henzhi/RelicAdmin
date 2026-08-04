import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src')
        }
    },
    build: {
        // 兼顾兼容性（esnext 会产出低版本浏览器无法解析的语法）
        target: 'es2020',
        chunkSizeWarningLimit: 1500,
        rollupOptions: {
            output: {
                // 三方库分包：充分利用浏览器长期缓存，避免单文件过大
                manualChunks: {
                    vue: ['vue', 'vue-router', 'pinia'],
                    element: ['element-plus', '@element-plus/icons-vue'],
                    echarts: ['echarts'],
                    axios: ['axios']
                }
            }
        }
    },
    esbuild: {
        target: 'es2020'
    },
    optimizeDeps: {
        esbuildOptions: {
            target: 'es2020'
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
