<template>
  <div class="scenic-detail-page">
    <div class="scenic-header" v-if="scenic">
      <div class="image-carousel">
        <img :src="currentImg" class="carousel-img">
        <div class="image-list">
          <img v-for="(img, index) in imgList" :key="index" :src="img" 
               class="thumb-img" :class="{ active: currentIndex === index }"
               @click="switchImg(index)">
          <div v-if="isLoggedIn" class="upload-thumb" @click="triggerUpload">
            <span class="upload-icon">+</span>
            <span class="upload-text">上传</span>
          </div>
          <input type="file" ref="fileInput" accept="image/*" class="file-input" @change="handleFileChange">
        </div>
      </div>
      <div class="scenic-info">
        <h1>{{ scenic.scenicName }}</h1>
        <div class="meta-tags">
          <span class="category">{{ scenic.category }}</span>
          <span class="price">{{ scenic.price === 0 ? '免费' : '¥' + scenic.price }}</span>
          <span class="hot">🔥 {{ scenic.hot }}</span>
        </div>
        <p class="intro">{{ scenic.intro }}</p>
        <div class="detail-info">
          <div class="info-item">
            <span class="icon">📍</span>
            <span>{{ scenic.address }}</span>
          </div>
          <div class="info-item">
            <span class="icon">🕐</span>
            <span>{{ scenic.openTime }}</span>
          </div>
          <div class="info-item">
            <span class="icon">🌍</span>
            <span>{{ scenic.longitude }}, {{ scenic.latitude }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="weather-section" v-if="scenic?.address">
      <div class="weather-card">
        <h3>🌤️ 景点天气查询</h3>
        <div v-if="weatherLoading" class="weather-loading">
          <div class="spinner"></div>
          <span>正在查询天气...</span>
        </div>
        <div v-else-if="weather" class="weather-content">
          <div class="weather-main">
            <div class="weather-icon">{{ getWeatherIcon(weather.weather) }}</div>
            <div class="weather-info">
              <div class="weather-city">{{ weather.city }}</div>
              <div class="weather-condition">{{ weather.weather }}</div>
              <div class="weather-temp">{{ weather.temperature }}°C</div>
            </div>
            <div class="weather-details">
              <span>💨 {{ weather.windDirection }} {{ weather.windPower }}级</span>
              <span>💧 湿度 {{ weather.humidity }}%</span>
            </div>
          </div>
          <div class="clothing-advice">
            <div class="advice-label">👕 穿衣建议</div>
            <div class="advice-content">{{ weather.clothingAdvice }}</div>
          </div>
          <div v-if="weather.forecast && weather.forecast.length > 0" class="forecast">
            <div class="forecast-label">📅 未来天气预报</div>
            <div class="forecast-list">
              <div v-for="(day, index) in weather.forecast" :key="index" class="forecast-item">
                <div class="forecast-date">{{ day.date }}</div>
                <div class="forecast-icon">{{ getWeatherIcon(day.dayweather) }}</div>
                <div class="forecast-condition">{{ day.dayweather }}</div>
                <div class="forecast-temp">{{ day.daytemp }}° ~ {{ day.nighttemp }}°</div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="weather-empty">
          <span>暂无天气信息</span>
        </div>
      </div>
    </div>
    
    <div class="comment-section">
      <h3>💬 用户评论 ({{ comments.length }})</h3>
      
      <div class="comment-form" v-if="isLoggedIn">
        <el-input v-model="commentContent" type="textarea" placeholder="写下你的评论..." :rows="3"></el-input>
        <el-button type="primary" @click="submitComment" :loading="submitting">发布评论</el-button>
      </div>
      <div class="login-tip" v-else>
        <router-link to="/login">登录后可发表评论</router-link>
      </div>
      
      <div class="comment-list" v-if="comments.length">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-avatar">
            {{ getNickname(comment.userId).charAt(0) }}
          </div>
          <div class="comment-content">
            <div class="comment-header">
              <span class="nickname">{{ getNickname(comment.userId) }}</span>
              <span class="time">{{ comment.createTime }}</span>
            </div>
            <p class="text">{{ comment.content }}</p>
            <div class="comment-actions">
              <span class="like-btn" @click="likeComment(comment.id)">
                ❤️ {{ comment.likeNum }}
              </span>
              <span v-if="isOwnComment(comment.userId)" class="delete-btn" @click="deleteComment(comment.id)">
                删除
              </span>
            </div>
          </div>
        </div>
      </div>
      <div class="empty-comments" v-else>
        <span>暂无评论，快来发表第一条评论吧！</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ScenicDetail',
  data() {
    return {
      scenic: null,
      comments: [],
      currentIndex: 0,
      imgList: [],
      commentContent: '',
      submitting: false,
      users: {},
      weather: null,
      weatherLoading: false
    }
  },
  computed: {
    currentImg() {
      return this.imgList[this.currentIndex] || ''
    },
    isLoggedIn() {
      return localStorage.getItem('token') !== null
    }
  },
  created() {
    this.loadScenic()
    this.loadComments()
  },
  watch: {
    scenic(newVal) {
      if (newVal && newVal.address) {
        this.loadWeather(newVal.address)
      }
    }
  },
  methods: {
    async loadScenic() {
      try {
        const res = await this.axios.get(`/api/scenic/detail/${this.$route.params.id}`)
        if (res.data.code === 200) {
          this.scenic = res.data.data
          this.imgList = this.scenic.imgUrls ? this.scenic.imgUrls.split(',') : []
        }
      } catch (error) {
        console.error('加载景点详情失败', error)
      }
    },
    async loadComments() {
      try {
        const res = await this.axios.get(`/api/comment/list/${this.$route.params.id}`)
        if (res.data.code === 200) {
          this.comments = res.data.data
        }
      } catch (error) {
        console.error('加载评论失败', error)
      }
    },
    async loadWeather(address) {
      this.weatherLoading = true
      try {
        const city = this.extractCity(address)
        const res = await this.axios.get('/api/map/weather', { params: { city } })
        this.weather = res.data.data || null
      } catch (e) { 
        console.error('加载天气失败', e) 
      } finally {
        this.weatherLoading = false
      }
    },
    extractCity(address) {
      if (!address) return ''
      const cityRegex = /([\u4e00-\u9fa5]{2,3}市|[\u4e00-\u9fa5]{2,3}地区|[\u4e00-\u9fa5]{2,3}州)/
      const match = address.match(cityRegex)
      if (match) {
        return match[1]
      }
      const provinceRegex = /([\u4e00-\u9fa5]{2,3}省)/
      const provinceMatch = address.match(provinceRegex)
      if (provinceMatch) {
        return provinceMatch[1]
      }
      return address.substring(0, 6)
    },
    getWeatherIcon(weather) {
      if (!weather) return '🌤️'
      weather = weather.toLowerCase()
      if (weather.includes('晴')) return '☀️'
      if (weather.includes('阴')) return '☁️'
      if (weather.includes('雨')) {
        if (weather.includes('雷')) return '⛈️'
        if (weather.includes('大')) return '🌧️'
        return '🌦️'
      }
      if (weather.includes('雪')) return '❄️'
      if (weather.includes('雾')) return '🌫️'
      if (weather.includes('风')) return '💨'
      return '🌤️'
    },
    switchImg(index) {
      this.currentIndex = index
    },
    async submitComment() {
      if (!this.commentContent.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }
      
      this.submitting = true
      try {
        const res = await this.axios.post('/api/comment/add', {
          scenicId: this.$route.params.id,
          content: this.commentContent
        })
        
        if (res.data.code === 200) {
          this.$message.success('评论成功')
          this.commentContent = ''
          this.loadComments()
        }
      } catch (error) {
        this.$message.error('评论失败')
      } finally {
        this.submitting = false
      }
    },
    async likeComment(commentId) {
      try {
        const res = await this.axios.post(`/api/comment/like/${commentId}`)
        if (res.data.code === 200) {
          this.$message.success('点赞成功')
          this.loadComments()
        }
      } catch (error) {
        console.error('点赞失败', error)
      }
    },
    async deleteComment(commentId) {
      this.$confirm('确定要删除这条评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
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
    getNickname(userId) {
      return this.users[userId] || '用户' + userId
    },
    isOwnComment(userId) {
      const user = JSON.parse(localStorage.getItem('user'))
      return user && user.id === userId
    },
    triggerUpload() {
      this.$refs.fileInput.click()
    },
    async handleFileChange(event) {
      const file = event.target.files[0]
      if (!file) return

      const formData = new FormData()
      formData.append('file', file)

      try {
        const res = await this.axios.post(`/api/upload/scenic/${this.$route.params.id}`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })

        if (res.data.code === 200) {
          const newUrl = res.data.data.url
          this.imgList.push(newUrl)
          this.currentIndex = this.imgList.length - 1
          this.$message.success('图片上传成功')
        }
      } catch (error) {
        this.$message.error('图片上传失败')
      } finally {
        event.target.value = ''
      }
    }
  }
}
</script>

<style scoped>
.scenic-detail-page {
  padding: 20px;
}
.scenic-header {
  background: white;
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.image-carousel {
  margin-bottom: 20px;
}
.carousel-img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 10px;
}
.image-list {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  overflow-x: auto;
}
.thumb-img {
  width: 100px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.3s;
}
.thumb-img.active {
  opacity: 1;
  border: 3px solid #667eea;
}
.upload-thumb {
  width: 100px;
  height: 60px;
  border-radius: 8px;
  border: 2px dashed #ddd;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  flex-shrink: 0;
}
.upload-thumb:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}
.upload-icon {
  font-size: 24px;
  color: #ddd;
  line-height: 1;
}
.upload-text {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}
.upload-thumb:hover .upload-icon,
.upload-thumb:hover .upload-text {
  color: #667eea;
}
.file-input {
  display: none;
}
.scenic-info h1 {
  font-size: 28px;
  margin-bottom: 15px;
}
.meta-tags {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}
.category {
  padding: 6px 15px;
  background: #667eea;
  color: white;
  border-radius: 20px;
}
.price {
  color: #ff6b6b;
  font-weight: 600;
  font-size: 18px;
}
.hot {
  color: #ffa502;
}
.intro {
  font-size: 16px;
  line-height: 1.8;
  color: #555;
  margin-bottom: 20px;
}
.detail-info {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 10px;
}
.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 15px;
}
.info-item:last-child {
  margin-bottom: 0;
}
.icon {
  font-size: 18px;
}
.weather-section {
  margin-bottom: 20px;
}
.weather-card {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.weather-card h3 {
  font-size: 20px;
  margin-bottom: 20px;
}
.weather-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
  color: #888;
}
.weather-loading .spinner {
  width: 32px;
  height: 32px;
  border: 4px solid #f0f0f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.weather-content {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.05), rgba(139, 92, 246, 0.05));
  border-radius: 12px;
  padding: 24px;
}
.weather-main {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
}
.weather-icon {
  font-size: 64px;
}
.weather-info {
  flex: 1;
}
.weather-city {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}
.weather-condition {
  font-size: 16px;
  color: #888;
}
.weather-temp {
  font-size: 48px;
  font-weight: 700;
  color: #667eea;
  margin-top: 8px;
}
.weather-details {
  text-align: right;
}
.weather-details span {
  display: block;
  font-size: 14px;
  color: #888;
  margin-bottom: 8px;
}
.clothing-advice {
  background: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}
.advice-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}
.advice-content {
  font-size: 14px;
  color: #888;
  line-height: 1.6;
}
.forecast {
  background: white;
  border-radius: 8px;
  padding: 16px;
}
.forecast-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}
.forecast-list {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}
.forecast-item {
  flex-shrink: 0;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  min-width: 100px;
}
.forecast-date {
  font-size: 12px;
  color: #888;
}
.forecast-icon {
  font-size: 28px;
  margin: 8px 0;
}
.forecast-condition {
  font-size: 13px;
  color: #333;
}
.forecast-temp {
  font-size: 13px;
  color: #888;
  margin-top: 4px;
}
.weather-empty {
  text-align: center;
  padding: 30px;
  color: #888;
}
.comment-section {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.comment-section h3 {
  font-size: 20px;
  margin-bottom: 20px;
}
.comment-form {
  margin-bottom: 20px;
}
.comment-form textarea {
  margin-bottom: 10px;
}
.login-tip {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 10px;
  margin-bottom: 20px;
}
.login-tip a {
  color: #667eea;
}
.comment-list {
  max-height: 500px;
  overflow-y: auto;
}
.comment-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}
.comment-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.comment-content {
  flex: 1;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}
.nickname {
  font-weight: 600;
}
.time {
  color: #888;
  font-size: 14px;
}
.text {
  color: #555;
  line-height: 1.6;
  margin-bottom: 10px;
}
.comment-actions {
  display: flex;
  gap: 20px;
}
.like-btn {
  cursor: pointer;
  color: #888;
  transition: color 0.3s;
}
.like-btn:hover {
  color: #ff6b6b;
}
.delete-btn {
  cursor: pointer;
  color: #ff6b6b;
  font-size: 14px;
}
.empty-comments {
  text-align: center;
  padding: 40px;
  color: #888;
}
</style>