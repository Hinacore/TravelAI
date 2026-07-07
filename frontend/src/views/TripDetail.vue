<template>
  <div class="trip-detail-page">
    <div class="page-header">
      <el-button @click="goBack">
        <i class="el-icon-arrow-left"></i>
        返回
      </el-button>
      <h1>{{ trip?.destination || '行程详情' }}</h1>
      <div class="header-actions">
        <el-button type="primary" @click="editTrip">编辑</el-button>
        <el-button type="warning" @click="shareTrip">分享</el-button>
      </div>
    </div>
    
    <div class="trip-summary" v-if="trip">
      <div class="summary-card">
        <div class="summary-row">
          <div class="summary-item">
            <i class="el-icon-calendar"></i>
            <span>{{ trip.startDate }} - {{ trip.endDate }}</span>
          </div>
          <div class="summary-item">
            <i class="el-icon-money"></i>
            <span>预算：¥{{ trip.budget }}</span>
          </div>
          <div class="summary-item">
            <i class="el-icon-user"></i>
            <span>{{ trip.travelers }}人</span>
          </div>
          <div class="summary-item">
            <i class="el-icon-clock"></i>
            <span>{{ trip.days }}天{{ trip.days - 1 }}晚</span>
          </div>
        </div>
        <div class="summary-row" v-if="trip.description">
          <div class="description">
            <i class="el-icon-info"></i>
            <span>{{ trip.description }}</span>
          </div>
        </div>
        <div class="summary-row">
          <span class="trip-status" :class="getStatusClass(trip.statusText || '')">
            {{ getStatusText(trip.statusText || '') }}
          </span>
        </div>
      </div>
    </div>
    
    <div class="trip-days" v-if="trip?.tripDays">
      <div v-for="(day, index) in trip.tripDays" :key="day.id" class="day-card">
        <div class="day-header">
          <span class="day-number">第{{ Number(index) + 1 }}天</span>
          <span class="day-date">{{ day.date }}</span>
        </div>
        <div class="day-activities">
          <div 
            v-for="activity in day.activities" 
            :key="activity.id" 
            class="activity-item"
          >
            <div class="activity-time">{{ activity.startTime }} - {{ activity.endTime }}</div>
            <div class="activity-info">
              <span class="activity-type">{{ getActivityTypeText(activity.type || '') }}</span>
              <span class="activity-name">{{ activity.name }}</span>
            </div>
            <div class="activity-description">{{ activity.description }}</div>
            <div class="activity-cost" v-if="activity.cost">费用：¥{{ activity.cost }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-else-if="!loading">
      <i class="el-icon-document"></i>
      <p>行程不存在或已删除</p>
      <el-button type="primary" @click="goBack">返回列表</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { tripApi } from '../api/index'
import { ElMessage } from 'element-plus'
import type { Trip } from '../api/types'

interface TripDetail extends Trip {
  statusText: string
  tripDays?: Array<{
    id?: number
    dayNumber?: number
    date?: string
    summary?: string
    activities?: Array<{
      id?: number
      type?: string
      name?: string
      title?: string
      location?: string
      duration?: string
      ticketPrice?: number
      transportation?: string
      description?: string
      budget?: number
      startTime?: string
      endTime?: string
      cost?: number
    }>
  }>
  travelers?: number
  endDate?: string
}

const router = useRouter()
const route = useRoute()
const trip = ref<TripDetail | null>(null)
const loading = ref(true)

const loadTrip = async () => {
  const id = parseInt(route.params.id as string)
  if (!id) return
  
  loading.value = true
  try {
    const response = await tripApi.getTrip(id)
    if (response.code === 200) {
      trip.value = response.data
    }
  } catch (error) {
    console.error('加载行程详情失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/trips')
}

const editTrip = () => {
  if (trip.value) {
    router.push(`/trips/${trip.value.id}/edit`)
  }
}

const shareTrip = async () => {
  if (!trip.value) return
  try {
    const response = await tripApi.shareTrip(trip.value.id)
    if (response.code === 200) {
      const shareCode = response.data
      const shareUrl = `${window.location.origin}/trips/shared/${shareCode}`
      await navigator.clipboard.writeText(shareUrl)
      ElMessage.success(`分享链接已复制到剪贴板：${shareUrl}`)
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '分享失败')
  }
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    ACTIVE: '进行中',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    UNKNOWN: '未知'
  }
  return map[status] || status
}

const getStatusClass = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: 'draft',
    ACTIVE: 'active',
    COMPLETED: 'completed',
    CANCELLED: 'cancelled'
  }
  return map[status] || 'draft'
}

const getActivityTypeText = (type: string) => {
  const map: Record<string, string> = {
    ATTRACTION: '景点',
    FOOD: '美食',
    TRANSPORT: '交通',
    ACCOMMODATION: '住宿',
    SHOPPING: '购物',
    OTHER: '其他'
  }
  return map[type] || type
}

onMounted(() => {
  loadTrip()
})
</script>

<style scoped>
.trip-detail-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
}

.page-header h1 {
  flex: 1;
  font-size: 28px;
  margin: 0;
}

.summary-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 32px;
}

.summary-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
  margin-bottom: 16px;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.summary-item i {
  color: #409EFF;
}

.description {
  flex: 1;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.trip-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 500;
}

.trip-status.draft {
  background: #F5F7FA;
  color: #909399;
}

.trip-status.active {
  background: #E6F7FF;
  color: #1890FF;
}

.trip-status.completed {
  background: #F6FFED;
  color: #52C41A;
}

.trip-status.cancelled {
  background: #FFF1F0;
  color: #FF4D4F;
}

.trip-days {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.day-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #E4E7ED;
}

.day-number {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.day-date {
  font-size: 14px;
  color: #909399;
}

.day-activities {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  background: #F5F7FA;
  border-radius: 8px;
  padding: 16px;
}

.activity-time {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  margin-bottom: 8px;
}

.activity-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.activity-type {
  font-size: 12px;
  padding: 2px 8px;
  background: #E6F7FF;
  color: #1890FF;
  border-radius: 4px;
}

.activity-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.activity-description {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.activity-cost {
  font-size: 14px;
  color: #52C41A;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: #F5F7FA;
  border-radius: 12px;
}

.empty-state i {
  font-size: 64px;
  color: #C0C4CC;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  color: #909399;
  margin: 0 0 24px 0;
}
</style>
