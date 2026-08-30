import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

// 后端地址可用环境变量覆盖（默认本地 8080）；例如后端改跑 8081 时：
// VITE_PROXY_TARGET=http://localhost:8081 npm run dev
const BACKEND_TARGET = process.env.VITE_PROXY_TARGET || 'http://localhost:8080'

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
        // H-07：后端统一版本前缀 /v1，代理匹配带版本号的路径
        proxy: {
            '^/v1/admin/': {
                target: BACKEND_TARGET,
                changeOrigin: true,
            },
            '^/v1/admin$': {
                target: BACKEND_TARGET,
                changeOrigin: true,
            },
            '^/v1/user/': {
                target: BACKEND_TARGET,
                changeOrigin: true,
            },
            // WebSocket 长连接代理（/v1/ws/{sid}），ws:true 开启协议升级
            '^/v1/ws/': {
                target: BACKEND_TARGET,
                changeOrigin: true,
                ws: true,
            },
            '^/v1/user$': {
                target: BACKEND_TARGET,
                changeOrigin: true,
            }
        }
    }
})
