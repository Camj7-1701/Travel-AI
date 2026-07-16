<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="avatar-section">
        <div class="avatar">
          {{ user ? user.nickname.charAt(0) : '?' }}
        </div>
        <div class="user-info">
          <h2>{{ user ? user.nickname : '未登录' }}</h2>
          <p>{{ user ? user.username : '' }}</p>
        </div>
      </div>
    </div>
    
    <div class="profile-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人信息" name="info">
          <div class="info-form">
            <el-form :model="editForm" ref="formRef" label-width="100px">
              <el-form-item label="昵称">
                <el-input v-model="editForm.nickname"></el-input>
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="editForm.phone"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="updateProfile" :loading="saving">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="我的攻略" name="strategies">
          <div class="strategy-list" v-if="strategyList.length">
            <div v-for="item in strategyList" :key="item.id" class="strategy-item">
              <div class="strategy-header">
                <span class="route">{{ item.startPoint }} → {{ item.endPoint }}</span>
                <span class="days">{{ item.travelDays }}天</span>
              </div>
              <div class="strategy-preview">{{ item.aiContent.substring(0, 100) }}...</div>
              <div class="strategy-footer">
                <span class="date">{{ item.createTime }}</span>
                <el-button type="text" @click="viewStrategy(item)">查看完整攻略</el-button>
              </div>
            </div>
          </div>
          <div class="empty-state" v-else>
            <span class="icon">📋</span>
            <p>暂无生成的攻略</p>
            <router-link to="/strategy" class="btn-link">去生成攻略</router-link>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="我的评论" name="comments">
          <div class="comment-list" v-if="commentList.length">
            <div v-for="item in commentList" :key="item.id" class="comment-item">
              <div class="comment-content">{{ item.content }}</div>
              <div class="comment-footer">
                <span class="scenic-name">{{ getScenicName(item.scenicId) }}</span>
                <span class="date">{{ item.createTime }}</span>
                <span class="delete-btn" @click="deleteComment(item.id)">删除</span>
              </div>
            </div>
          </div>
          <div class="empty-state" v-else>
            <span class="icon">💬</span>
            <p>暂无评论</p>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <div class="view-modal" v-if="viewStrategyData" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>旅游攻略</h3>
          <span class="close-btn" @click="closeModal">×</span>
        </div>
        <div class="modal-body" v-html="formatContent(viewStrategyData.aiContent)"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data() {
    return {
      activeTab: 'info',
      user: null,
      editForm: {
        nickname: '',
        phone: ''
      },
      saving: false,
      strategyList: [],
      commentList: [],
      viewStrategyData: null,
      scenicMap: {}
    }
  },
  created() {
    this.loadUser()
    this.loadStrategies()
    this.loadComments()
  },
  methods: {
    async loadUser() {
      const userStr = localStorage.getItem('user')
      this.user = userStr ? JSON.parse(userStr) : null
      if (this.user) {
        this.editForm.nickname = this.user.nickname
        this.editForm.phone = this.user.phone || ''
      }
    },
    async updateProfile() {
      this.saving = true
      try {
        const res = await this.axios.put('/api/user/profile', {
          nickname: this.editForm.nickname,
          phone: this.editForm.phone
        })
        
        if (res.data.code === 200) {
          this.$message.success('更新成功')
          this.user = res.data.data
          localStorage.setItem('user', JSON.stringify(this.user))
        }
      } catch (error) {
        this.$message.error('更新失败')
      } finally {
        this.saving = false
      }
    },
    async loadStrategies() {
      try {
        const res = await this.axios.get('/api/strategy/list')
        if (res.data.code === 200) {
          this.strategyList = res.data.data
        }
      } catch (error) {
        console.error('加载攻略失败', error)
      }
    },
    async loadComments() {
      try {
        const res = await this.axios.get('/api/scenic/list')
        if (res.data.code === 200) {
          res.data.data.forEach(s => {
            this.scenicMap[s.id] = s.scenicName
          })
        }
      } catch (error) {
        console.error('加载景点失败', error)
      }
    },
    getScenicName(scenicId) {
      return this.scenicMap[scenicId] || '未知景点'
    },
    viewStrategy(item) {
      this.viewStrategyData = item
    },
    closeModal() {
      this.viewStrategyData = null
    },
    async deleteComment(commentId) {
      this.$confirm('确定要删除这条评论吗？', '提示').then(async () => {
        try {
          const res = await this.axios.delete(`/api/comment/delete/${commentId}`)
          if (res.data.code === 200) {
            this.$message.success('删除成功')
            this.loadComments()
          }
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    formatContent(content) {
      if (!content) return ''
      return content.replace(/\n/g, '<br/>')
    }
  }
}
</script>

<style scoped>
.profile-page {
  padding: 20px;
}
.profile-header {
  background: white;
  border-radius: 15px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}
.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
}
.user-info h2 {
  font-size: 24px;
  margin-bottom: 5px;
}
.user-info p {
  color: #888;
}
.profile-tabs {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.info-form {
  padding: 20px;
}
.strategy-list {
  padding: 20px;
}
.strategy-item {
  background: #f5f7fa;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 15px;
}
.strategy-header {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
}
.route {
  font-weight: 600;
  color: #667eea;
}
.days {
  padding: 4px 12px;
  background: #667eea;
  color: white;
  border-radius: 15px;
  font-size: 12px;
}
.strategy-preview {
  color: #888;
  font-size: 14px;
  margin-bottom: 10px;
}
.strategy-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #888;
  font-size: 14px;
}
.comment-list {
  padding: 20px;
}
.comment-item {
  background: #f5f7fa;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 15px;
}
.comment-content {
  margin-bottom: 10px;
  color: #333;
}
.comment-footer {
  display: flex;
  gap: 15px;
  align-items: center;
  color: #888;
  font-size: 14px;
}
.scenic-name {
  color: #667eea;
}
.delete-btn {
  color: #ff6b6b;
  cursor: pointer;
}
.empty-state {
  text-align: center;
  padding: 60px;
  color: #888;
}
.empty-state .icon {
  font-size: 48px;
  display: block;
  margin-bottom: 15px;
}
.btn-link {
  display: inline-block;
  margin-top: 15px;
  padding: 10px 25px;
  background: #667eea;
  color: white;
  border-radius: 20px;
  text-decoration: none;
}
.view-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  width: 80%;
  max-height: 80%;
  background: white;
  border-radius: 15px;
  overflow: hidden;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}
.close-btn {
  font-size: 24px;
  cursor: pointer;
  color: #888;
}
.modal-body {
  padding: 20px;
  max-height: 60vh;
  overflow-y: auto;
  line-height: 1.8;
}
</style>