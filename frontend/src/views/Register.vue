<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-form">
        <div class="form-header">
          <span class="logo-icon">🌍</span>
          <h1>智旅AI</h1>
          <p>创建账号，开启智能旅行</p>
        </div>
        <el-form :model="form" ref="formRef" :rules="rules" label-width="80px">
          <el-form-item label="账号" prop="username">
            <el-input v-model="form.username" placeholder="请输入账号"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码"></el-input>
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="请输入昵称"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="submit-btn" @click="register" :loading="loading">
              注册
            </el-button>
          </el-form-item>
        </el-form>
        <div class="form-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        nickname: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度3-20位', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: this.confirmPasswordValidator, trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { max: 20, message: '昵称长度不超过20位', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    confirmPasswordValidator(rule, value, callback) {
      if (value !== this.form.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    },
    async register() {
      const valid = await this.$refs.formRef.validate()
      if (!valid) return
      
      this.loading = true
      try {
        const res = await this.axios.post('/api/auth/register', {
          username: this.form.username,
          password: this.form.password,
          nickname: this.form.nickname
        })
        
        if (res.data.code === 200) {
          localStorage.setItem('token', res.data.data.token)
          localStorage.setItem('user', JSON.stringify(res.data.data.user))
          this.$message.success('注册成功')
          this.$router.push('/')
        } else {
          this.$message.error(res.data.message)
        }
      } catch (error) {
        this.$message.error('注册失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.register-container {
  width: 400px;
}
.register-form {
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