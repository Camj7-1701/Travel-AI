<template>
  <div class="strategy-page">
    <div class="strategy-form">
      <h2>🤖 AI旅游攻略生成</h2>
      <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出发地" prop="startPoint">
              <el-input v-model="form.startPoint" placeholder="例如：北京"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的地" prop="endPoint">
              <el-input v-model="form.endPoint" placeholder="例如：杭州"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="出行天数" prop="days">
              <el-select v-model="form.days" placeholder="选择天数">
                <el-option label="1天" :value="1"></el-option>
                <el-option label="2天" :value="2"></el-option>
                <el-option label="3天" :value="3"></el-option>
                <el-option label="4天" :value="4"></el-option>
                <el-option label="5天" :value="5"></el-option>
                <el-option label="6天" :value="6"></el-option>
                <el-option label="7天" :value="7"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预算" prop="budget">
              <el-select v-model="form.budget" placeholder="选择预算">
                <el-option label="经济型" value="经济型"></el-option>
                <el-option label="舒适型" value="舒适型"></el-option>
                <el-option label="豪华型" value="豪华型"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="出行人数" prop="people">
              <el-select v-model="form.people" placeholder="选择人数">
                <el-option label="1人" value="1人"></el-option>
                <el-option label="2人" value="2人"></el-option>
                <el-option label="3-5人" value="3-5人"></el-option>
                <el-option label="5人以上" value="5人以上"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="游玩偏好" prop="preference">
          <el-checkbox-group v-model="form.preference">
            <el-checkbox label="美食"></el-checkbox>
            <el-checkbox label="徒步"></el-checkbox>
            <el-checkbox label="亲子"></el-checkbox>
            <el-checkbox label="购物"></el-checkbox>
            <el-checkbox label="摄影"></el-checkbox>
            <el-checkbox label="文化"></el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="generate-btn" @click="generate" :loading="loading">
            🎯 生成攻略
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div class="strategy-result" v-if="strategy">
      <h3>📋 旅游攻略</h3>
      <div class="strategy-header">
        <span class="route">{{ strategy.startPoint }} → {{ strategy.endPoint }}</span>
        <span class="days">{{ strategy.travelDays }}天</span>
        <span class="budget">{{ strategy.budget }}</span>
      </div>
      <div class="strategy-content" v-html="formatContent(strategy.aiContent)"></div>
    </div>
    
    <div class="history-section">
      <h3>📜 我的攻略历史</h3>
      <div class="history-list" v-if="historyList.length">
        <div v-for="item in historyList" :key="item.id" class="history-item">
          <div class="history-info">
            <span class="history-route">{{ item.startPoint }} → {{ item.endPoint }}</span>
            <span class="history-date">{{ item.createTime }}</span>
          </div>
          <el-button type="text" @click="viewStrategy(item)">查看</el-button>
        </div>
      </div>
      <p class="empty-hint" v-else>暂无攻略记录</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Strategy',
  data() {
    return {
      form: {
        startPoint: '',
        endPoint: '',
        days: 3,
        budget: '',
        people: '1人',
        preference: []
      },
      rules: {
        startPoint: [{ required: true, message: '请输入出发地', trigger: 'blur' }],
        endPoint: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
        days: [{ required: true, message: '请选择出行天数', trigger: 'change' }],
        budget: [{ required: true, message: '请选择预算', trigger: 'change' }],
        preference: [{ required: true, message: '请选择游玩偏好', trigger: 'change' }]
      },
      loading: false,
      strategy: null,
      historyList: []
    }
  },
  created() {
    this.loadHistory()
    if (this.$route.query.start) {
      this.form.startPoint = this.$route.query.start
    }
    if (this.$route.query.end) {
      this.form.endPoint = this.$route.query.end
    }
  },
  methods: {
    async generate() {
      const valid = await this.$refs.formRef.validate()
      if (!valid) return
      
      this.loading = true
      try {
        const res = await this.axios.post('/api/strategy/generate', {
          startPoint: this.form.startPoint,
          endPoint: this.form.endPoint,
          days: this.form.days,
          budget: this.form.budget,
          preference: this.form.preference.join(',')
        })
        
        if (res.data.code === 200) {
          this.strategy = res.data.data
          this.$message.success('攻略生成成功')
          this.loadHistory()
        } else {
          this.$message.error(res.data.message)
        }
      } catch (error) {
        this.$message.error('生成失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    async loadHistory() {
      try {
        const res = await this.axios.get('/api/strategy/list')
        if (res.data.code === 200) {
          this.historyList = res.data.data
        }
      } catch (error) {
        console.error('加载历史失败', error)
      }
    },
    viewStrategy(item) {
      this.strategy = item
    },
    formatContent(content) {
      if (!content) return ''
      return content
        .replace(/\n/g, '<br/>')
        .replace(/### (.*?)\n/g, '<h4>$1</h4>')
        .replace(/## (.*?)\n/g, '<h3>$1</h3>')
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\d+\.\s/g, '<span class="list-num">')
        .replace(/(\d+\.\s.*?)(?=\d+\.\s|$)/g, '<span class="list-item">$1</span>')
    }
  }
}
</script>

<style scoped>
.strategy-page {
  padding: 20px;
}
.strategy-form {
  background: white;
  border-radius: 15px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.strategy-form h2 {
  font-size: 24px;
  margin-bottom: 20px;
}
.generate-btn {
  width: 100%;
  padding: 15px;
  font-size: 18px;
  border-radius: 30px;
}
.strategy-result {
  background: white;
  border-radius: 15px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.strategy-result h3 {
  font-size: 22px;
  margin-bottom: 20px;
}
.strategy-header {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #667eea;
}
.route, .days, .budget {
  padding: 8px 20px;
  background: #f5f7fa;
  border-radius: 20px;
  font-size: 14px;
}
.strategy-content {
  line-height: 2;
  color: #333;
}
.strategy-content h3 {
  color: #667eea;
  font-size: 18px;
  margin: 20px 0 10px;
}
.strategy-content h4 {
  color: #333;
  font-size: 16px;
  margin: 15px 0 8px;
}
.strategy-content strong {
  color: #667eea;
}
.history-section {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.history-section h3 {
  font-size: 20px;
  margin-bottom: 20px;
}
.history-list {
  max-height: 400px;
  overflow-y: auto;
}
.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}
.history-info {
  display: flex;
  flex-direction: column;
}
.history-route {
  font-weight: 600;
  margin-bottom: 5px;
}
.history-date {
  color: #888;
  font-size: 14px;
}
.empty-hint {
  text-align: center;
  color: #888;
  padding: 40px;
}
</style>