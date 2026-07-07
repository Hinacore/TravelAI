<template>
  <div class="trips-page">
    <div class="page-header">
      <h1>行程管理</h1>
      <div class="header-actions">
        <router-link to="/recommend" class="btn-primary">
          <i class="el-icon-magic-stick"></i>
          AI生成行程
        </router-link>
      </div>
    </div>
    
    <div class="search-bar">
      <el-input 
        v-model="searchKeyword" 
        placeholder="搜索目的地" 
        prefix-icon="el-icon-search"
        @keyup.enter="loadTrips"
      />
      <el-select v-model="sortBy" placeholder="排序方式">
        <el-option label="创建时间" value="createdAt" />
        <el-option label="出发时间" value="startDate" />
      </el-select>
      <el-button type="primary" @click="loadTrips">搜索</el-button>
    </div>
    
    <div class="trips-list" v-if="trips.length > 0">
      <div 
        v-for="trip in trips" 
        :key="trip.id" 
        class="trip-card"
      >
        <div class="trip-header" @click="goToTrip(trip.id)">
          <h3>{{ trip.name }}</h3>
          <div class="status-dropdown">
            <el-dropdown @command="handleStatusChange(trip.id)">
              <span class="trip-status" :class="getStatusClass(trip.statusText || '')">
                {{ getStatusText(trip.statusText || '') }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="0">草稿</el-dropdown-item>
                  <el-dropdown-item :command="1">进行中</el-dropdown-item>
                  <el-dropdown-item :command="2">已完成</el-dropdown-item>
                  <el-dropdown-item :command="3">已取消</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div class="trip-info">
          <div class="info-item">
            <i class="el-icon-map-location"></i>
            <span>{{ trip.destination }}</span>
          </div>
          <div class="info-item">
            <i class="el-icon-date"></i>
            <span>{{ formatDates(trip.startDate, trip.endDate || '') }}</span>
          </div>
          <div class="info-item">
            <i class="el-icon-money"></i>
            <span>¥{{ trip.budget }}</span>
          </div>
          <div class="info-item">
            <i class="el-icon-clock"></i>
            <span>{{ trip.days }}天{{ trip.days - 1 }}晚</span>
          </div>
        </div>
        <div class="trip-description">{{ trip.description || '暂无描述' }}</div>
        <div class="trip-actions">
          <el-button size="small" @click="goToTrip(trip.id)">查看</el-button>
          <el-button size="small" type="primary" @click="editTrip(trip.id)">编辑</el-button>
          <el-button size="small" type="warning" @click="shareTrip(trip.id)">分享</el-button>
          <el-button size="small" type="danger" @click="deleteTrip(trip.id)">删除</el-button>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-else>
      <i class="el-icon-map-location"></i>
      <p>还没有行程，快去创建一个吧！</p>
      <router-link to="/recommend" class="btn-primary">
        <i class="el-icon-plus"></i>
        创建行程
      </router-link>
    </div>
    
    <div class="pagination" v-if="total > 0">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50]"
        :page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { tripApi } from '../api/index'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Trip } from '../api/types'

interface TripItem extends Trip {
  statusText: string
}

const router = useRouter()
const trips = ref<TripItem[]>([])
const searchKeyword = ref('')
const sortBy = ref('createdAt')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadTrips = async () => {
  try {
    const response = await tripApi.getTrips({
      keyword: searchKeyword.value,
      sortBy: sortBy.value,
      page: currentPage.value - 1,
      size: pageSize.value
    })
    if (response.code === 200) {
      const data = response.data as any
      trips.value = data.content || data
      total.value = data.totalElements || trips.value.length
    }
  } catch (error) {
    console.error('加载行程失败:', error)
  }
}

const goToTrip = (id: number) => {
  router.push(`/trips/${id}`)
}

const editTrip = (id: number) => {
  router.push(`/trips/${id}/edit`)
}

const shareTrip = async (id: number) => {
  try {
    const response = await tripApi.shareTrip(id)
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

const deleteTrip = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个行程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await tripApi.deleteTrip(id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadTrips()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

const formatDates = (start: string, end: string) => {
  if (!start || !end) return ''
  return `${start} - ${end}`
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

const handleStatusChange = (id: number) => (status: string | number) => {
  updateStatus(id, Number(status))
}

const updateStatus = async (id: number, status: number) => {
  try {
    const response = await tripApi.updateTripStatus(id, status)
    if (response.code === 200) {
      ElMessage.success('状态更新成功')
      loadTrips()
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '状态更新失败')
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadTrips()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadTrips()
}

onMounted(() => {
  loadTrips()
})
</script>

<style scoped>
.trips-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  margin: 0;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  align-items: center;
}

.search-bar .el-input {
  flex: 1;
  max-width: 300px;
}

.trips-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
}

.trip-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.trip-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  cursor: pointer;
}

.trip-header h3 {
  font-size: 20px;
  margin: 0;
}

.trip-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.trip-status:hover {
  opacity: 0.8;
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

.trip-info {
  display: flex;
  gap: 24px;
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
}

.trip-description {
  color: #909399;
  font-size: 14px;
  margin-bottom: 16px;
  display: -webkit-box;
  line-clamp: 3;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.trip-actions {
  display: flex;
  gap: 8px;
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

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
</style>
