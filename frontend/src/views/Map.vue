<template>
  <div class="map-page">
    <div class="map-header">
      <h2>🗺️ 景点地图</h2>
      <div class="search-input-group">
        <el-input v-model="city" placeholder="输入城市名称" class="city-input"></el-input>
        <el-input v-model="searchKeyword" placeholder="搜索景点" class="keyword-input"></el-input>
        <el-button type="primary" @click="searchScenic">搜索景点</el-button>
      </div>
    </div>
    <div class="map-content">
      <div class="map-container" ref="mapContainer"></div>
      <div class="scenic-list" v-if="scenicList.length > 0">
        <h3>📍 搜索结果</h3>
        <div class="scenic-items">
          <div v-for="(item, index) in scenicList" :key="index" 
               class="scenic-item" 
               @click="focusOnMarker(index)">
            <div class="scenic-name">{{ item.name }}</div>
            <div class="scenic-address">{{ item.address }}</div>
            <div class="scenic-type">{{ item.type }}</div>
          </div>
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
      city: '',
      searchKeyword: '',
      scenicList: [],
      map: null,
      markers: []
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
    searchScenic() {
      if (!this.city) {
        this.$message.warning('请输入城市名称')
        return
      }

      AMap.plugin(['AMap.Geocoder', 'AMap.PlaceSearch'], () => {
        const geocoder = new AMap.Geocoder({})
        
        geocoder.getLocation(this.city, (status, result) => {
          if (status === 'complete' && result.info === 'OK') {
            const loc = result.geocodes[0].location
            this.map.setCenter([loc.lng, loc.lat])
            this.map.setZoom(12)

            const placeSearch = new AMap.PlaceSearch({
              pageSize: 20,
              pageIndex: 1,
              map: this.map,
              panel: false
            })

            const keyword = this.searchKeyword || '景点'
            placeSearch.searchNearBy(keyword, [loc.lng, loc.lat], 5000, (status, result) => {
              if (status === 'complete' && result.info === 'OK') {
                this.scenicList = result.poiList.pois.map(poi => ({
                  name: poi.name,
                  address: poi.address,
                  location: poi.location.lng + ',' + poi.location.lat,
                  type: poi.type
                }))
                this.addMarkersToMap()
                this.autoFitView()
              } else {
                this.$message.error('搜索失败，请重试')
              }
            })
          } else {
            this.$message.error('城市定位失败，请输入正确的城市名称')
          }
        })
      })
    },
    addMarkersToMap() {
      this.markers.forEach(marker => {
        this.map.remove(marker)
      })
      this.markers = []

      this.scenicList.forEach((item, index) => {
        const coords = item.location.split(',')
        const marker = new AMap.Marker({
          position: [coords[0], coords[1]],
          title: item.name,
          label: {
            content: `${index + 1}`,
            direction: 'bottom',
            offset: new AMap.Pixel(-6, -5),
            style: {
              fontSize: '12px',
              fontWeight: 'bold',
              color: '#fff',
              backgroundColor: '#667eea',
              borderRadius: '50%',
              width: '20px',
              height: '20px',
              textAlign: 'center',
              lineHeight: '20px'
            }
          },
          icon: new AMap.Icon({
            image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
            size: new AMap.Size(30, 30)
          })
        })

        marker.on('click', () => {
          this.showInfoWindow(marker, item)
        })

        this.markers.push(marker)
      })

      this.map.add(this.markers)
    },
    autoFitView() {
      if (this.scenicList.length > 0) {
        const bounds = new AMap.Bounds()
        this.markers.forEach(marker => {
          bounds.extend(marker.getPosition())
        })
        this.map.setBounds(bounds, {
          padding: [50, 50, 50, 370],
          delay: 500
        })

        setTimeout(() => {
          if (this.markers[0]) {
            const position = this.markers[0].getPosition()
            this.map.setCenter(position)
            this.showInfoWindow(this.markers[0], this.scenicList[0])
          }
        }, 600)
      }
    },
    showInfoWindow(marker, item) {
      const infoWindow = new AMap.InfoWindow({
        content: `
          <div style="padding: 10px;">
            <h4 style="margin: 0 0 8px 0;">${item.name}</h4>
            <p style="margin: 0 0 4px 0; font-size: 14px;">📍 ${item.address}</p>
            <p style="margin: 0; font-size: 12px; color: #888;">${item.type}</p>
          </div>
        `,
        offset: new AMap.Pixel(0, -30)
      })
      infoWindow.open(this.map, marker.getPosition())
    },
    focusOnMarker(index) {
      if (this.markers[index]) {
        const position = this.markers[index].getPosition()
        this.map.setCenter(position)
        this.map.setZoom(15)
        this.showInfoWindow(this.markers[index], this.scenicList[index])
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
.search-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}
.city-input {
  width: 200px;
}
.keyword-input {
  flex: 1;
}
.map-content {
  display: flex;
  gap: 20px;
}
.map-container {
  flex: 1;
  height: 500px;
  border-radius: 15px;
  overflow: hidden;
}
.scenic-list {
  width: 320px;
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
  overflow-y: auto;
  max-height: 500px;
}
.scenic-list h3 {
  font-size: 18px;
  margin-bottom: 15px;
}
.scenic-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.scenic-item {
  padding: 12px;
  border-radius: 10px;
  background: #f8fafc;
  cursor: pointer;
  transition: all 0.3s;
}
.scenic-item:hover {
  background: #e0e7ff;
  transform: translateX(5px);
}
.scenic-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}
.scenic-address {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}
.scenic-type {
  font-size: 12px;
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}
</style>
