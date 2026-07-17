import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/map',
      name: 'Map',
      component: () => import('../views/Map.vue')
    },
    {
      path: '/strategy',
      name: 'Strategy',
      component: () => import('../views/Strategy.vue')
    },
    {
      path: '/scenic',
      name: 'Scenic',
      component: () => import('../views/Scenic.vue')
    },
    {
      path: '/scenic/detail/:id',
      name: 'ScenicDetail',
      component: () => import('../views/ScenicDetail.vue')
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('../views/Profile.vue')
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const publicPaths = ['/login', '/register', '/', '/scenic', '/scenic/detail', '/map']
  if (!publicPaths.includes(to.path) && !publicPaths.some(path => to.path.startsWith(path)) && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router