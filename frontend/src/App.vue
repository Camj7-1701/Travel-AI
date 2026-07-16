<template>
  <div class="app-container">
    <header class="app-header" v-if="showHeader">
      <div class="header-content">
        <div class="logo" @click="goHome">
          <span class="logo-icon">🌍</span>
          <span class="logo-text">智旅AI</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/map" class="nav-item">地图规划</router-link>
          <router-link to="/strategy" class="nav-item">AI攻略</router-link>
          <router-link to="/scenic" class="nav-item">景点大全</router-link>
        </nav>
        <div class="user-area">
          <template v-if="user">
            <router-link to="/profile" class="user-info">
              <span class="avatar">{{ user.nickname.charAt(0) }}</span>
              <span class="nickname">{{ user.nickname }}</span>
            </router-link>
            <button class="logout-btn" @click="logout">退出</button>
          </template>
          <template v-else>
            <router-link to="/login" class="login-btn">登录</router-link>
            <router-link to="/register" class="register-btn">注册</router-link>
          </template>
        </div>
      </div>
    </header>
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      user: null,
      showHeader: true
    }
  },
  created() {
    this.loadUser()
  },
  watch: {
    $route() {
      this.loadUser()
      this.showHeader = this.$route.path !== '/login' && this.$route.path !== '/register'
    }
  },
  methods: {
    loadUser() {
      const userStr = localStorage.getItem('user')
      this.user = userStr ? JSON.parse(userStr) : null
    },
    goHome() {
      this.$router.push('/')
    },
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.user = null
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background: #f5f7fa;
}
.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}
.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.logo-icon {
  font-size: 28px;
  margin-right: 8px;
}
.logo-text {
  font-size: 22px;
  font-weight: bold;
  color: white;
}
.nav-menu {
  display: flex;
  gap: 30px;
}
.nav-item {
  color: rgba(255,255,255,0.9);
  text-decoration: none;
  font-size: 16px;
  transition: color 0.3s;
}
.nav-item:hover {
  color: white;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 15px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  text-decoration: none;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}
.nickname {
  font-size: 14px;
}
.login-btn, .register-btn, .logout-btn {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  border: none;
}
.login-btn {
  background: rgba(255,255,255,0.2);
  color: white;
}
.login-btn:hover {
  background: rgba(255,255,255,0.3);
}
.register-btn {
  background: white;
  color: #667eea;
}
.register-btn:hover {
  background: rgba(255,255,255,0.9);
}
.logout-btn {
  background: rgba(255,255,255,0.2);
  color: white;
}
.logout-btn:hover {
  background: rgba(255,255,255,0.3);
}
.app-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>