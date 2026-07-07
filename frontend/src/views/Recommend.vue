<template>
  <div class="recommend-page">
    <div class="page-header">
      <h1>AI智能行程规划</h1>
      <p>填写您的旅行需求，让AI为您生成完美的行程规划</p>
    </div>
    
    <div class="form-section">
      <div class="form-card">
        <el-form :model="recommendForm" :rules="rules" ref="formRef" label-width="120px">
          <el-form-item label="目的地" prop="destination">
            <el-input 
              v-model="recommendForm.destination" 
              placeholder="例如：北京、上海、东京"
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="旅行天数" prop="days">
            <el-input-number 
              v-model="recommendForm.days" 
              :min="1" 
              :max="30" 
              size="large"
              placeholder="请输入天数"
            />
          </el-form-item>
          
          <el-form-item label="预算范围" prop="budget">
            <el-input-number 
              v-model="recommendForm.budget" 
              :min="100" 
              :max="999999" 
              size="large"
              placeholder="请输入预算金额（元）"
              :controls="false"
            />
          </el-form-item>
          
          <el-form-item label="出发日期" prop="startDate">
            <el-date-picker 
              v-model="recommendForm.startDate" 
              type="date" 
              placeholder="选择出发日期"
              size="large"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="出行人数" prop="travelers">
            <el-input-number 
              v-model="recommendForm.travelers" 
              :min="1" 
              :max="20" 
              size="large"
              placeholder="请输入人数"
            />
          </el-form-item>
          
          <el-form-item label="旅行偏好">
            <el-checkbox-group v-model="recommendForm.preferences">
              <el-checkbox label="自然风光" />
              <el-checkbox label="历史文化" />
              <el-checkbox label="美食探店" />
              <el-checkbox label="休闲度假" />
              <el-checkbox label="购物娱乐" />
              <el-checkbox label="亲子游玩" />
            </el-checkbox-group>
          </el-form-item>
          
          <el-form-item label="特殊需求">
            <el-input 
              v-model="recommendForm.specialRequirements" 
              type="textarea"
              :rows="3"
              placeholder="例如：希望避开人流高峰、偏好特色民宿、需要安排轮椅通道等"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              :loading="loading"
              @click="generateTrip"
            >
              <i class="el-icon-magic-stick"></i>
              AI生成行程
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <div class="result-section" v-if="generatedTrip">
      <div class="result-card">
        <div class="result-header">
          <h2>{{ generatedTrip.tripName }}</h2>
          <div class="result-actions">
            <el-button @click="regenerate">重新生成</el-button>
            <el-button type="primary" @click="saveTrip">保存行程</el-button>
          </div>
        </div>
        
        <div class="trip-summary">
          <div class="summary-item">
            <i class="el-icon-location"></i>
            <span>{{ recommendForm.destination }}</span>
          </div>
          <div class="summary-item">
            <i class="el-icon-date"></i>
            <span>{{ recommendForm.startDate }}</span>
          </div>
          <div class="summary-item">
            <i class="el-icon-money"></i>
            <span>预算：¥{{ recommendForm.budget }}</span>
          </div>
          <div class="summary-item">
            <i class="el-icon-user"></i>
            <span>{{ recommendForm.travelers }}人</span>
          </div>
        </div>
        
        <div class="trip-days">
          <div v-for="dayPlan in (generatedTrip.dayPlans || generatedTrip.days)" :key="dayPlan.dayNumber || dayPlan.day" class="day-card">
            <div class="day-header">
              <span class="day-number">第{{ dayPlan.dayNumber || dayPlan.day }}天</span>
              <span class="day-summary">{{ dayPlan.summary || dayPlan.date }}</span>
            </div>
            <div class="day-activities">
              <div v-if="dayPlan.morning" class="activity-item">
                <div class="activity-time">上午</div>
                <div class="activity-info">
                  <span class="activity-type">上午活动</span>
                  <span class="activity-name">{{ dayPlan.morning.title || dayPlan.morning.spot }}</span>
                </div>
                <div class="activity-description">{{ dayPlan.morning.description }}</div>
                <div class="activity-meta">
                  <span>📍 {{ dayPlan.morning.location || '' }}</span>
                  <span>⏱️ {{ dayPlan.morning.duration }}</span>
                  <span>🚗 {{ dayPlan.morning.transportation }}</span>
                  <span>💰 ¥{{ dayPlan.morning.ticketPrice || dayPlan.morning.ticket || 0 }}</span>
                </div>
              </div>
              <div v-if="dayPlan.afternoon" class="activity-item">
                <div class="activity-time">下午</div>
                <div class="activity-info">
                  <span class="activity-type">下午活动</span>
                  <span class="activity-name">{{ dayPlan.afternoon.title || dayPlan.afternoon.spot }}</span>
                </div>
                <div class="activity-description">{{ dayPlan.afternoon.description }}</div>
                <div class="activity-meta">
                  <span>📍 {{ dayPlan.afternoon.location || '' }}</span>
                  <span>⏱️ {{ dayPlan.afternoon.duration }}</span>
                  <span>🚗 {{ dayPlan.afternoon.transportation }}</span>
                  <span>💰 ¥{{ dayPlan.afternoon.ticketPrice || dayPlan.afternoon.ticket || 0 }}</span>
                </div>
              </div>
              <div v-if="dayPlan.evening" class="activity-item">
                <div class="activity-time">晚上</div>
                <div class="activity-info">
                  <span class="activity-type">晚上活动</span>
                  <span class="activity-name">{{ dayPlan.evening.title || dayPlan.evening.spot }}</span>
                </div>
                <div class="activity-description">{{ dayPlan.evening.description }}</div>
                <div class="activity-meta">
                  <span>📍 {{ dayPlan.evening.location || '' }}</span>
                  <span>⏱️ {{ dayPlan.evening.duration }}</span>
                  <span>🚗 {{ dayPlan.evening.transportation }}</span>
                  <span>💰 ¥{{ dayPlan.evening.ticketPrice || dayPlan.evening.ticket || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="budget-details" v-if="generatedTrip.budgetDetails && generatedTrip.budgetDetails.length > 0">
          <h3>预算明细</h3>
          <div class="budget-list">
            <div v-for="detail in generatedTrip.budgetDetails" :key="detail.category" class="budget-item">
              <span class="budget-category">{{ detail.category }}</span>
              <span class="budget-amount">¥{{ detail.amount }}</span>
              <span class="budget-desc">{{ detail.description }}</span>
            </div>
          </div>
        </div>
        
        <div class="tips" v-if="generatedTrip.tips && generatedTrip.tips.length > 0">
          <h3>温馨提示</h3>
          <ul>
            <li v-for="(tip, index) in generatedTrip.tips" :key="index">{{ tip }}</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { recommendApi, tripApi } from '../api/index'
import { ElMessage } from 'element-plus'

const formRef = ref()
const loading = ref(false)
const generatedTrip = ref<any>(null)

const recommendForm = reactive({
  destination: '',
  days: 3,
  budget: 3000,
  startDate: '',
  travelers: 1,
  preferences: [],
  specialRequirements: ''
})

const rules = {
  destination: [
    { required: true, message: '请输入目的地', trigger: 'blur' }
  ],
  days: [
    { required: true, message: '请输入旅行天数', trigger: 'blur' },
    { type: 'number', min: 1, message: '天数至少为1天', trigger: 'blur' }
  ],
  budget: [
    { required: true, message: '请输入预算', trigger: 'blur' },
    { type: 'number', min: 100, message: '预算至少100元', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择出发日期', trigger: 'blur' }
  ],
  travelers: [
    { required: true, message: '请输入出行人数', trigger: 'blur' },
    { type: 'number', min: 1, message: '人数至少为1人', trigger: 'blur' }
  ]
}

const generateTrip = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    loading.value = true
    try {
      const response = await recommendApi.generateTrip(recommendForm)
      if (response.code === 200) {
        generatedTrip.value = response.data
        ElMessage.success('行程生成成功')
      } else {
        ElMessage.error(response.message || '生成失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '生成失败，请重试')
    } finally {
      loading.value = false
    }
  })
}

const saveTrip = async () => {
  if (!generatedTrip.value) return
  
  try {
    const dayPlans = generatedTrip.value.dayPlans || generatedTrip.value.days || []
    const tripDays = dayPlans.map((dayPlan: any) => {
      const activities: any[] = []
      
      if (dayPlan.morning) {
        activities.push({
          type: 'morning',
          title: dayPlan.morning.title || dayPlan.morning.spot,
          location: dayPlan.morning.location || '',
          duration: dayPlan.morning.duration || '',
          ticketPrice: dayPlan.morning.ticketPrice || dayPlan.morning.ticket || 0,
          transportation: dayPlan.morning.transportation || '',
          description: dayPlan.morning.description || '',
          budget: 0
        })
      }
      
      if (dayPlan.afternoon) {
        activities.push({
          type: 'afternoon',
          title: dayPlan.afternoon.title || dayPlan.afternoon.spot,
          location: dayPlan.afternoon.location || '',
          duration: dayPlan.afternoon.duration || '',
          ticketPrice: dayPlan.afternoon.ticketPrice || dayPlan.afternoon.ticket || 0,
          transportation: dayPlan.afternoon.transportation || '',
          description: dayPlan.afternoon.description || '',
          budget: 0
        })
      }
      
      if (dayPlan.evening) {
        activities.push({
          type: 'evening',
          title: dayPlan.evening.title || dayPlan.evening.spot,
          location: dayPlan.evening.location || '',
          duration: dayPlan.evening.duration || '',
          ticketPrice: dayPlan.evening.ticketPrice || dayPlan.evening.ticket || 0,
          transportation: dayPlan.evening.transportation || '',
          description: dayPlan.evening.description || '',
          budget: 0
        })
      }
      
      return {
        dayNumber: dayPlan.dayNumber || dayPlan.day,
        summary: dayPlan.summary || dayPlan.date || '',
        activities
      }
    })
    
    const budgetDetails = (generatedTrip.value.budgetDetails || []).map((detail: any) => ({
      category: detail.category || '',
      amount: detail.amount || 0,
      description: detail.description || ''
    }))
    
    const saveData = {
      name: generatedTrip.value.tripName || `${recommendForm.destination}${recommendForm.days}日游`,
      destination: recommendForm.destination,
      budget: recommendForm.budget,
      days: recommendForm.days,
      startDate: recommendForm.startDate,
      description: generatedTrip.value.tips?.join('\n') || '',
      tripDays,
      budgetDetails
    }
    
    const response = await tripApi.createTrip(saveData)
    if (response.code === 200) {
      ElMessage.success('行程保存成功')
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error: any) {
    console.error('保存行程失败:', error)
    ElMessage.error(error.response?.data?.message || '保存失败，请重试')
  }
}

const regenerate = () => {
  generatedTrip.value = null
}
</script>

<style scoped>
.recommend-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 32px;
  margin: 0 0 8px 0;
}

.page-header p {
  color: #606266;
  margin: 0;
}

.form-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 40px;
}

.result-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.result-header h2 {
  font-size: 24px;
  margin: 0;
}

.trip-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #E4E7ED;
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

.trip-days {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.day-card {
  background: #F5F7FA;
  border-radius: 8px;
  padding: 20px;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #E4E7ED;
}

.day-number {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.day-date {
  font-size: 14px;
  color: #909399;
}

.day-summary {
  font-size: 14px;
  color: #606266;
}

.day-activities {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  background: #fff;
  border-radius: 6px;
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

.activity-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.budget-details {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #E4E7ED;
}

.budget-details h3 {
  font-size: 18px;
  margin: 0 0 16px 0;
  color: #303133;
}

.budget-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.budget-item {
  display: flex;
  flex-direction: column;
  padding: 12px 16px;
  background: #F5F7FA;
  border-radius: 8px;
  min-width: 140px;
}

.budget-category {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.budget-amount {
  font-size: 18px;
  font-weight: 600;
  color: #409EFF;
}

.budget-desc {
  font-size: 12px;
  color: #909399;
}

.tips {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #E4E7ED;
}

.tips h3 {
  font-size: 18px;
  margin: 0 0 16px 0;
  color: #303133;
}

.tips ul {
  margin: 0;
  padding-left: 20px;
}

.tips li {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}
</style>
