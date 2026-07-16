import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import VueAxios from 'vue-axios'

Vue.use(ElementUI)
Vue.use(VueAxios, axios)
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
}, error => {
  return Promise.reject(error)
})

axios.interceptors.response.use(response => {
  return response
}, error => {
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }
  return Promise.reject(error)
})

Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')