export default {
    server: {
        proxy: {
            '/admin': 'http://localhost:8080',
            '/user': 'http://localhost:8080',
        }
    }
}