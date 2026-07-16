<template>
  <div class="scenic-page">
    <div class="scenic-header">
      <h2>🏞️ 景点大全</h2>
      <div class="filter-bar">
        <el-select v-model="category" placeholder="分类筛选" @change="loadScenic">
          <el-option label="全部" value=""></el-option>
          <el-option label="山水" value="山水"></el-option>
          <el-option label="古镇" value="古镇"></el-option>
          <el-option label="乐园" value="乐园"></el-option>
          <el-option label="人文" value="人文"></el-option>
          <el-option label="美食" value="美食"></el-option>
        </el-select>
      </div>
    </div>
    
    <div class="scenic-grid">
      <div class="scenic-card" v-for="scenic in scenicList" :key="scenic.id" @click="goDetail(scenic.id)">
        <div class="scenic-img">
          <img :src="getFirstImg(scenic.imgUrls)" :alt="scenic.scenicName">
          <div class="category-tag">{{ scenic.category }}</div>
          <div v-if="scenic.hot > 100" class="hot-badge">热门</div>
        </div>
        <div class="scenic-info">
          <div class="scenic-title">{{ scenic.scenicName }}</div>
          <div class="scenic-meta">
            <span class="price">{{ scenic.price === 0 ? '免费' : '¥' + scenic.price }}</span>
            <span class="open-time">⏰ {{ scenic.openTime }}</span>
          </div>
          <p class="scenic-intro">{{ scenic.intro }}</p>
          <div v-if="scenic.aiInfo" class="ai-features">
            <span class="feature-label">🌟 特色亮点</span>
            <span v-for="(feature, idx) in scenic.aiInfo.features" :key="idx" class="feature-tag">{{ feature }}</span>
          </div>
          <div class="scenic-footer">
            <span class="address">{{ scenic.address }}</span>
            <span class="hot">🔥 {{ scenic.hot }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-if="scenicList.length === 0">
      <span class="empty-icon">🌄</span>
      <p>暂无景点数据</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Scenic',
  data() {
    return {
      category: '',
      scenicList: []
    }
  },
  created() {
    this.loadScenic()
  },
  methods: {
    async loadScenic() {
      try {
        const url = this.category ? `/api/scenic/list?category=${this.category}` : '/api/scenic/list'
        const res = await this.axios.get(url)
        if (res.data.code === 200) {
          this.scenicList = res.data.data
          this.scenicList.forEach(scenic => {
            this.fetchAiInfo(scenic)
          })
        }
      } catch (error) {
        console.error('加载景点失败', error)
      }
    },
    async fetchAiInfo(scenic) {
      try {
        const res = await this.axios.get('/api/scenic/ai-info', { params: { scenicName: scenic.scenicName } })
        scenic.aiInfo = res.data.data
      } catch (e) {
        console.error('获取AI信息失败:', e)
      }
    },
    getFirstImg(imgUrls) {
      if (!imgUrls) return 'https://via.placeholder.com/400x250'
      return imgUrls.split(',')[0]
    },
    goDetail(id) {
      this.$router.push(`/scenic/detail/${id}`)
    }
  }
}
</script>

<style scoped>
.scenic-page {
  padding: 20px;
}
.scenic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.scenic-header h2 {
  font-size: 24px;
}
.filter-bar {
  width: 200px;
}
.scenic-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}
.scenic-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
}
.scenic-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}
.scenic-img {
  position: relative;
  width: 250px;
  height: 200px;
  flex-shrink: 0;
  overflow: hidden;
}
.scenic-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.scenic-card:hover .scenic-img img {
  transform: scale(1.08);
}
.category-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(255,255,255,0.95);
  color: #667eea;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
.hot-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #ef4444;
  color: white;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
.scenic-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}
.scenic-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
}
.scenic-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
}
.price {
  color: #ff6b6b;
  font-weight: 600;
  font-size: 16px;
}
.open-time {
  font-size: 13px;
  color: #888;
  padding: 4px 10px;
  background: rgba(99, 102, 241, 0.05);
  border-radius: 6px;
}
.scenic-intro {
  flex: 1;
  color: #888;
  font-size: 14px;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  margin-bottom: 12px;
}
.ai-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  margin-bottom: 12px;
}
.feature-label {
  font-size: 12px;
  font-weight: 600;
  color: #10b981;
  margin-right: 4px;
}
.feature-tag {
  font-size: 12px;
  color: #333;
  padding: 4px 10px;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1), rgba(5, 150, 105, 0.1));
  border-radius: 20px;
  border: 1px solid rgba(16, 185, 129, 0.2);
}
.scenic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.address {
  color: #888;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}
.hot {
  color: #ffa502;
  font-size: 13px;
}
.empty-state {
  text-align: center;
  padding: 60px;
  color: #888;
}
.empty-icon {
  font-size: 60px;
  display: block;
  margin-bottom: 20px;
}
</style>