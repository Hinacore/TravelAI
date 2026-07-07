<template>
  <div class="home-page">
    <div class="hero-section">
      <div class="hero-content">
        <h1>智能旅游推荐与规划系统</h1>
        <p>让AI帮您规划完美旅程</p>
        <div class="hero-buttons">
          <router-link to="/recommend" class="btn-primary">
            <i class="el-icon-magic-stick"></i>
            AI生成行程
          </router-link>
          <router-link to="/chat" class="btn-secondary">
            <i class="el-icon-chat-dot-round"></i>
            咨询AI助手
          </router-link>
        </div>
      </div>
    </div>
    
    <div class="features-section">
      <div class="container">
        <h2>核心功能</h2>
        <div class="features-grid">
          <router-link to="/recommend" class="feature-card">
            <div class="feature-icon">
              <i class="el-icon-lightbulb"></i>
            </div>
            <h3>智能行程规划</h3>
            <p>基于目的地、预算、天数，AI自动生成详细行程安排</p>
          </router-link>
          
          <router-link to="/chat" class="feature-card">
            <div class="feature-icon">
              <i class="el-icon-chat-line-round"></i>
            </div>
            <h3>AI助手对话</h3>
            <p>自然语言交互，获取个性化旅游建议和常见问题解答</p>
          </router-link>
          
          <router-link to="/trips" class="feature-card">
            <div class="feature-icon">
              <i class="el-icon-document"></i>
            </div>
            <h3>行程管理</h3>
            <p>创建、编辑、保存、分享您的旅行计划</p>
          </router-link>
          
          <router-link to="/trips" class="feature-card">
            <div class="feature-icon">
              <i class="el-icon-share"></i>
            </div>
            <h3>行程分享</h3>
            <p>一键分享您的精彩旅程给好友</p>
          </router-link>
        </div>
      </div>
    </div>
    
    <div class="recent-trips-section" v-if="recentTrips.length > 0">
      <div class="container">
        <h2>最近行程</h2>
        <div class="trips-list">
          <div 
            v-for="trip in recentTrips" 
            :key="trip.id" 
            class="trip-card"
            @click="goToTrip(trip.id)"
          >
            <div class="trip-header">
              <h3>{{ trip.destination }}</h3>
              <span class="trip-dates">{{ formatDates(trip.startDate, trip.endDate) }}</span>
            </div>
            <div class="trip-info">
              <span class="trip-budget">预算：¥{{ trip.budget }}</span>
              <span class="trip-days">{{ trip.days }}天</span>
            </div>
            <div class="trip-actions">
              <span class="view-detail">查看详情 →</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-else>
      <div class="container">
        <div class="empty-content">
          <i class="el-icon-map-location"></i>
          <p>还没有行程，快去创建一个吧！</p>
          <router-link to="/recommend" class="btn-primary">
            <i class="el-icon-plus"></i>
            创建行程
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { tripApi } from '../api/index'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const recentTrips = ref<any[]>([])

const loadRecentTrips = async () => {
  if (!userStore.isLoggedIn()) {
    return
  }
  try {
    const response = await tripApi.getTrips({ page: 0, size: 4 })
    if (response.code === 200) {
      const data = response.data as any
      recentTrips.value = data.content || data
    }
  } catch (error) {
    console.error('加载行程失败:', error)
  }
}

const goToTrip = (id: number) => {
  router.push(`/trips/${id}`)
}

const formatDates = (start: string, end: string) => {
  if (!start || !end) return ''
  const startDate = new Date(start)
  const endDate = new Date(end)
  return `${startDate.getMonth() + 1}月${startDate.getDate()}日 - ${endDate.getMonth() + 1}月${endDate.getDate()}日`
}

onMounted(() => {
  loadRecentTrips()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  color: #fff;
  padding: 80px 20px;
  text-align: center;
}

.hero-content h1 {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 16px 0;
}

.hero-content p {
  font-size: 20px;
  opacity: 0.9;
  margin: 0 0 32px 0;
}

.hero-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn-primary, .btn-secondary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #fff;
  color: #667EEA;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.3);
}

.features-section {
  padding: 60px 20px;
}

.features-section h2 {
  text-align: center;
  font-size: 32px;
  margin: 0 0 40px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  text-decoration: none;
  color: inherit;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.feature-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px auto;
  font-size: 28px;
  color: #fff;
}

.feature-card h3 {
  font-size: 18px;
  margin: 0 0 8px 0;
}

.feature-card p {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.recent-trips-section {
  padding: 60px 20px;
  background: #F5F7FA;
}

.recent-trips-section h2 {
  font-size: 32px;
  margin: 0 0 40px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.trips-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.trip-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.trip-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.trip-header h3 {
  font-size: 20px;
  margin: 0;
  color: #303133;
}

.trip-dates {
  font-size: 14px;
  color: #909399;
}

.trip-info {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.trip-budget, .trip-days {
  font-size: 14px;
  color: #606266;
}

.trip-actions {
  text-align: right;
}

.view-detail {
  color: #409EFF;
  font-size: 14px;
}

.empty-state {
  padding: 80px 20px;
  background: #F5F7FA;
}

.empty-content {
  text-align: center;
}

.empty-content i {
  font-size: 64px;
  color: #C0C4CC;
  margin-bottom: 16px;
}

.empty-content p {
  font-size: 16px;
  color: #909399;
  margin: 0 0 24px 0;
}
</style>
