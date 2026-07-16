<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-form">
        <div class="form-header">
          <span class="logo-icon">🌍</span>
          <h1>智旅AI</h1>
          <p>欢迎回来，开始你的旅行规划</p>
        </div>
        <el-form :model="form" ref="formRef" :rules="rules" label-width="80px">
          <el-form-item label="账号" prop="username">
            <el-input v-model="form.username" placeholder="请输入账号"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="submit-btn" @click="login" :loading="loading">
              登录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="form-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度3-20位', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    async login() {
      const valid = await this.$refs.formRef.validate()
      if (!valid) return
      
      this.loading = true
      try {
        const res = await this.axios.post('/api/auth/login', {
          username: this.form.username,
          password: this.form.password
        })
        
        if (res.data.code === 200) {
          localStorage.setItem('token', res.data.data.token)
          localStorage.setItem('user', JSON.stringify(res.data.data.user))
          this.$message.success('登录成功')
          this.$router.push('/')
        } else {
          this.$message.error(res.data.message)
        }
      } catch (error) {
        this.$message.error('登录失败，请检查账号密码')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-container {
  width: 400px;
}
.login-form {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.2);
}
.form-header {
  text-align: center;
  margin-bottom: 30px;
}
.logo-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 10px;
}
.form-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 5px;
}
.form-header p {
  color: #888;
}
.submit-btn {
  width: 100%;
  padding: 12px;
  border-radius: 30px;
  font-size: 16px;
}
.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #888;
}
.form-footer a {
  color: #667eea;
  text-decoration: none;
}
</style>