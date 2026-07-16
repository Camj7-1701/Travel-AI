<template>
  <div class="map-page">
    <div class="map-header">
      <h2>🗺️ 地图规划</h2>
      <div class="route-input">
        <el-input v-model="origin" placeholder="起点" class="input-item"></el-input>
        <span class="arrow">→</span>
        <el-input v-model="destination" placeholder="目的地" class="input-item"></el-input>
        <el-button type="primary" @click="planRoute">规划路线</el-button>
      </div>
    </div>
    <div class="map-content">
      <div class="map-container" ref="mapContainer"></div>
      <div class="route-info" v-if="routeData">
        <h3>路线详情</h3>
        <div class="info-item">
          <span class="label">距离</span>
          <span class="value">{{ routeData.distance }}米</span>
        </div>
        <div class="info-item">
          <span class="label">时长</span>
          <span class="value">{{ formatDuration(routeData.duration) }}</span>
        </div>
        <div class="steps" v-if="routeData.steps">
          <h4>途经点</h4>
          <div v-for="(step, index) in routeData.steps" :key="index" class="step-item">
            <span class="step-num">{{ index + 1 }}</span>
            <span class="step-desc">{{ step.instruction }}</span>
          </div>
        </div>
        <el-button type="success" @click="goStrategy">生成AI攻略</el-button>
      </div>
      <div class="ai-route-info" v-if="aiRouteData">
        <div class="ai-header">
          <h3>🤖 AI智能导航路线</h3>
          <span v-if="aiLoading" class="loading-text">正在生成...</span>
        </div>
        <div v-if="aiLoading" class="ai-loading">
          <div class="spinner"></div>
          <span>AI正在生成导航路线...</span>
        </div>
        <div v-else-if="aiRouteData.steps && aiRouteData.steps.length > 0">
          <div v-for="(step, index) in aiRouteData.steps" :key="index" class="ai-step-item">
            <span :class="['ai-step-num', {start: index === 0, end: index === aiRouteData.steps.length - 1}]">{{ index + 1 }}</span>
            <div class="ai-step-content">
              <span class="ai-step-desc">{{ step.instruction }}</span>
              <span class="ai-step-meta">{{ step.distance || '未知' }} · {{ step.duration || '未知' }}</span>
            </div>
          </div>
          <div class="ai-summary">
            <span>📏 总距离: {{ aiRouteData.totalDistance || '未知' }}</span>
            <span>⏱️ 预计时间: {{ aiRouteData.totalDuration || '未知' }}</span>
          </div>
        </div>
        <div v-else class="ai-empty">
          AI路线生成失败，显示默认路线
        </div>
      </div>
    </div>
    <div class="search-section">
      <h3>🔍 周边搜索</h3>
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索景点" class="search-input"></el-input>
        <el-button type="primary" @click="searchNearby">搜索</el-button>
      </div>
      <div class="search-results" v-if="searchResults.length">
        <div v-for="item in searchResults" :key="item.id" class="result-item">
          <span class="result-name">{{ item.name }}</span>
          <span class="result-address">{{ item.address }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Map',
  data() {
    return {
      origin: '',
      destination: '',
      routeData: null,
      aiRouteData: null,
      aiLoading: false,
      searchKeyword: '',
      searchResults: [],
      map: null,
      markerStart: null,
      markerEnd: null,
      polyline: null
    }
  },
  mounted() {
    this.initMap()
  },
  beforeDestroy() {
    if (this.map) {
      this.map.destroy()
    }
  },
  methods: {
    initMap() {
      this.map = new AMap.Map(this.$refs.mapContainer, {
        zoom: 10,
        center: [116.3972, 39.9163]
      })
    },
    async planRoute() {
      if (!this.origin || !this.destination) {
        this.$message.warning('请输入起点和目的地')
        return
      }
      
      this.aiRouteData = null
      this.aiLoading = true
      
      try {
        const originRes = await this.axios.get(`/api/map/geocode?address=${this.origin}`)
        const destRes = await this.axios.get(`/api/map/geocode?address=${this.destination}`)
        
        if (originRes.data.code === 200 && destRes.data.code === 200) {
          const originLoc = originRes.data.data
          const destLoc = destRes.data.data
          
          const [routeRes, aiRouteRes] = await Promise.all([
            this.axios.get(`/api/map/route?origin=${originLoc.longitude},${originLoc.latitude}&destination=${destLoc.longitude},${destLoc.latitude}`),
            this.axios.get(`/api/map/route/ai?origin=${this.origin}&destination=${this.destination}`)
          ])
          
          if (routeRes.data.code === 200) {
            this.routeData = routeRes.data.data
            this.showRouteOnMap(originLoc, destLoc)
          }
          
          if (aiRouteRes.data.code === 200) {
            this.aiRouteData = aiRouteRes.data.data
          }
          
          this.aiLoading = false
        }
      } catch (error) {
        this.aiLoading = false
        this.$message.error('路线规划失败')
      }
    },
    showRouteOnMap(origin, dest) {
      if (this.markerStart) this.map.remove(this.markerStart)
      if (this.markerEnd) this.map.remove(this.markerEnd)
      if (this.polyline) this.map.remove(this.polyline)
      
      this.markerStart = new AMap.Marker({
        position: [origin.longitude, origin.latitude],
        title: this.origin,
        icon: new AMap.Icon({
          image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
          size: new AMap.Size(30, 30)
        })
      })
      
      this.markerEnd = new AMap.Marker({
        position: [dest.longitude, dest.latitude],
        title: this.destination,
        icon: new AMap.Icon({
          image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png',
          size: new AMap.Size(30, 30)
        })
      })
      
      this.map.add([this.markerStart, this.markerEnd])
      this.map.setCenter([origin.longitude, origin.latitude])
      this.map.setZoom(12)
    },
    formatDuration(seconds) {
      const mins = Math.floor(seconds / 60)
      const hours = Math.floor(mins / 60)
      const remainingMins = mins % 60
      return hours > 0 ? `${hours}小时${remainingMins}分钟` : `${remainingMins}分钟`
    },
    goStrategy() {
      this.$router.push({
        path: '/strategy',
        query: {
          start: this.origin,
          end: this.destination
        }
      })
    },
    async searchNearby() {
      if (!this.destination) {
        this.$message.warning('请先输入目的地')
        return
      }
      
      try {
        const res = await this.axios.get(`/api/map/geocode?address=${this.destination}`)
        if (res.data.code === 200) {
          const loc = res.data.data
          const searchRes = await this.axios.get(`/api/map/search?location=${loc.longitude},${loc.latitude}&keyword=${this.searchKeyword || '景点'}`)
          if (searchRes.data.code === 200) {
            this.searchResults = searchRes.data.data
          }
        }
      } catch (error) {
        this.$message.error('搜索失败')
      }
    }
  }
}
</script>

<style scoped>
.map-page {
  padding: 20px;
}
.map-header {
  background: white;
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.map-header h2 {
  font-size: 22px;
  margin-bottom: 15px;
}
.route-input {
  display: flex;
  align-items: center;
  gap: 10px;
}
.input-item {
  flex: 1;
}
.arrow {
  font-size: 20px;
  color: #667eea;
}
.map-content {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}
.map-container {
  flex: 1;
  height: 500px;
  border-radius: 15px;
  overflow: hidden;
}
.route-info {
  width: 300px;
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.route-info h3 {
  font-size: 18px;
  margin-bottom: 15px;
}
.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}
.label {
  color: #888;
}
.value {
  font-weight: 600;
  color: #667eea;
}
.steps h4 {
  font-size: 16px;
  margin: 15px 0 10px;
}
.step-item {
  display: flex;
  gap: 10px;
  padding: 8px 0;
  font-size: 14px;
  color: #555;
}
.step-num {
  width: 24px;
  height: 24px;
  background: #667eea;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}
.ai-route-info {
  width: 350px;
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.ai-header {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.ai-header h3 {
  font-size: 16px;
  margin: 0;
}
.loading-text {
  font-size: 12px;
  opacity: 0.8;
}
.ai-loading {
  padding: 40px;
  text-align: center;
  color: #888;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.ai-loading .spinner {
  width: 32px;
  height: 32px;
  border: 4px solid #f0f0f0;
  border-top-color: #10b981;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.ai-step-item {
  display: flex;
  gap: 12px;
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
}
.ai-step-num {
  width: 28px;
  height: 28px;
  background: #6366f1;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}
.ai-step-num.start {
  background: #10b981;
}
.ai-step-num.end {
  background: #ef4444;
}
.ai-step-content {
  flex: 1;
}
.ai-step-desc {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  display: block;
}
.ai-step-meta {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
}
.ai-summary {
  padding: 12px 20px;
  background: rgba(16, 185, 129, 0.05);
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}
.ai-summary span {
  color: #333;
}
.ai-summary span:first-child {
  color: #10b981;
  font-weight: 600;
}
.ai-empty {
  padding: 20px;
  text-align: center;
  color: #888;
  font-size: 14px;
}
.search-section {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.search-section h3 {
  font-size: 18px;
  margin-bottom: 15px;
}
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.search-input {
  flex: 1;
}
.search-results {
  max-height: 300px;
  overflow-y: auto;
}
.result-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.result-name {
  font-weight: 600;
}
.result-address {
  color: #888;
  font-size: 14px;
}
</style>