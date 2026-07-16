<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-content">
        <h1>🌍 智旅AI</h1>
        <p>AI驱动的智能旅游规划平台</p>
        <div class="hero-buttons">
          <router-link to="/strategy" class="btn-primary">🎯 AI生成攻略</router-link>
          <router-link to="/map" class="btn-secondary">🗺️ 地图规划</router-link>
        </div>
      </div>
    </section>
    
    <section class="hot-scenic">
      <h2>🔥 热门景点推荐</h2>
      <div class="scenic-grid">
        <div class="scenic-card" v-for="scenic in hotScenicList" :key="scenic.id" @click="goDetail(scenic.id)">
          <div class="scenic-img">
            <img :src="getFirstImg(scenic.imgUrls)" :alt="scenic.scenicName">
          </div>
          <div class="scenic-info">
            <h3>{{ scenic.scenicName }}</h3>
            <p class="scenic-category">{{ scenic.category }}</p>
            <p class="scenic-address">{{ scenic.address }}</p>
            <div class="scenic-footer">
              <span class="price">{{ scenic.price === 0 ? '免费' : '¥' + scenic.price }}</span>
              <span class="hot">🔥 {{ scenic.hot }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <section class="weather-section">
      <div class="weather-card">
        <div class="weather-header">
          <h2>🌤️ 目的地天气查询</h2>
          <div class="weather-search">
            <el-input v-model="weatherCity" placeholder="输入城市名称" style="width: 250px;" @keyup.enter="queryWeather"></el-input>
            <el-button type="primary" @click="queryWeather" :loading="weatherLoading">查询</el-button>
          </div>
        </div>
        <div v-if="weather" class="weather-result">
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
        </div>
        <div v-else-if="weatherError" class="weather-error">
          {{ weatherError }}
        </div>
      </div>
    </section>
    
    <section class="features">
      <h2>✨ 核心功能</h2>
      <div class="feature-grid">
        <div class="feature-card">
          <div class="feature-icon">🤖</div>
          <h3>AI智能攻略</h3>
          <p>根据你的出行天数、预算、偏好，自动生成完整旅游方案</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🗺️</div>
          <h3>地图导航</h3>
          <p>接入高德地图，路线规划、景点定位、周边搜索</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📸</div>
          <h3>实景展示</h3>
          <p>海量景点实地实拍图片，真实感受目的地美景</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">💬</div>
          <h3>评论社区</h3>
          <p>分享旅游体验，查看他人真实评价</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      hotScenicList: [],
      weatherCity: '',
      weather: null,
      weatherLoading: false,
      weatherError: ''
    }
  },
  created() {
    this.loadHotScenic()
  },
  methods: {
    async loadHotScenic() {
      try {
        const res = await this.axios.get('/api/scenic/hot?limit=6')
        if (res.data.code === 200) {
          this.hotScenicList = res.data.data
        }
      } catch (error) {
        console.error('加载热门景点失败', error)
      }
    },
    async queryWeather() {
      if (!this.weatherCity.trim()) {
        this.$message.warning('请输入城市名称')
        return
      }
      this.weatherLoading = true
      this.weatherError = ''
      try {
        const res = await this.axios.get('/api/map/weather', { params: { city: this.weatherCity } })
        if (res.data.code === 200) {
          this.weather = res.data.data
        } else {
          this.weatherError = '查询失败，请重试'
        }
      } catch (e) {
        this.weatherError = '查询失败，请检查网络连接'
      } finally {
        this.weatherLoading = false
      }
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
    getFirstImg(imgUrls) {
      if (!imgUrls) return 'https://via.placeholder.com/300x200'
      return imgUrls.split(',')[0]
    },
    goDetail(id) {
      this.$router.push(`/scenic/detail/${id}`)
    }
  }
}
</script>

<style scoped>
.home-page {
  padding: 20px 0;
}
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 80px 40px;
  text-align: center;
  color: white;
  margin-bottom: 40px;
}
.hero-content h1 {
  font-size: 48px;
  margin-bottom: 10px;
}
.hero-content p {
  font-size: 20px;
  opacity: 0.9;
  margin-bottom: 30px;
}
.hero-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}
.btn-primary, .btn-secondary {
  padding: 15px 40px;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s;
}
.btn-primary {
  background: white;
  color: #667eea;
}
.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255,255,255,0.3);
}
.btn-secondary {
  background: rgba(255,255,255,0.2);
  color: white;
  border: 2px solid white;
}
.btn-secondary:hover {
  background: rgba(255,255,255,0.3);
}
.hot-scenic {
  margin-bottom: 40px;
}
.hot-scenic h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}
.scenic-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.scenic-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}
.scenic-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}
.scenic-img {
  height: 200px;
  overflow: hidden;
}
.scenic-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.scenic-info {
  padding: 15px;
}
.scenic-info h3 {
  font-size: 18px;
  margin-bottom: 5px;
  color: #333;
}
.scenic-category {
  color: #667eea;
  font-size: 12px;
  margin-bottom: 8px;
}
.scenic-address {
  color: #888;
  font-size: 13px;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.scenic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: #ff6b6b;
  font-weight: 600;
}
.hot {
  color: #ffa502;
  font-size: 12px;
}
.weather-section {
  margin-bottom: 40px;
}
.weather-card {
  background: white;
  border-radius: 15px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.weather-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.weather-header h2 {
  font-size: 24px;
  margin: 0;
}
.weather-search {
  display: flex;
  gap: 12px;
}
.weather-result {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.05), rgba(139, 92, 246, 0.05));
  border-radius: 12px;
  padding: 20px;
}
.weather-main {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
}
.weather-icon {
  font-size: 56px;
}
.weather-info {
  flex: 1;
}
.weather-city {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}
.weather-condition {
  font-size: 14px;
  color: #888;
}
.weather-temp {
  font-size: 40px;
  font-weight: 700;
  color: #667eea;
  margin-top: 4px;
}
.weather-details {
  text-align: right;
}
.weather-details span {
  display: block;
  font-size: 13px;
  color: #888;
  margin-bottom: 6px;
}
.clothing-advice {
  background: white;
  border-radius: 8px;
  padding: 14px;
}
.advice-label {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}
.advice-content {
  font-size: 13px;
  color: #888;
  line-height: 1.6;
}
.weather-error {
  text-align: center;
  padding: 20px;
  color: #ef4444;
}
.features {
  margin-bottom: 40px;
}
.features h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}
.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.feature-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.feature-icon {
  font-size: 40px;
  margin-bottom: 15px;
}
.feature-card h3 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}
.feature-card p {
  color: #888;
  font-size: 14px;
  line-height: 1.5;
}
</style>