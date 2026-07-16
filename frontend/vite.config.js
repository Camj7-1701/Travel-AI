const { createVuePlugin } = require('vite-plugin-vue2')
const path = require('path')

module.exports = {
  plugins: [createVuePlugin()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '/api')
      }
    }
  }
}