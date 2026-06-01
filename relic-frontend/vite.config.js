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
    server: {
        historyApiFallback: true,
        proxy: {
            // 只代理 /admin/ 开头的（注意有斜杠），不代理 /admin- 开头的
            '^/admin/': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            // 单独处理 /admin 本身（如果后端有这个路径）
            '^/admin$': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            // 只代理 /user/ 开头的，不代理 /users
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