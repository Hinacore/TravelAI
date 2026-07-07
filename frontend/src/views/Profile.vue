<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="avatar-section">
        <div class="avatar">
          <i class="el-icon-user"></i>
        </div>
        <h2>{{ userStore.userInfo?.username }}</h2>
        <p>{{ userStore.userInfo?.email }}</p>
      </div>
    </div>
    
    <div class="profile-content">
      <div class="tabs-container">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="个人信息" name="info">
            <div class="form-card">
              <h3>编辑个人信息</h3>
              <el-form :model="userForm" label-width="100px">
                <el-form-item label="昵称">
                  <el-input v-model="userForm.nickname" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="userForm.email" disabled />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="userForm.phone" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-select v-model="userForm.gender">
                    <el-option label="男" value="MALE" />
                    <el-option label="女" value="FEMALE" />
                    <el-option label="保密" value="OTHER" />
                  </el-select>
                </el-form-item>
                <el-form-item label="出生日期">
                  <el-date-picker 
                    v-model="userForm.birthDate" 
                    type="date" 
                    placeholder="选择日期"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateProfile">保存修改</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="修改密码" name="password">
            <div class="form-card">
              <h3>修改密码</h3>
              <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="changePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { userApi } from '../api/index'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('info')
const passwordFormRef = ref()

const userForm = reactive({
  nickname: '',
  email: '',
  phone: '',
  gender: 'OTHER',
  birthDate: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (_rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loadUserInfo = async () => {
  try {
    const response = await userApi.getUserInfo()
    if (response.code === 200) {
      const user = response.data
      userForm.nickname = user.nickname || ''
      userForm.email = user.email || ''
      userForm.phone = user.phone || ''
      userForm.gender = user.gender || 'OTHER'
      userForm.birthDate = user.birthDate || ''
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const updateProfile = async () => {
  try {
    const response = await userApi.updateUserInfo(userForm)
    if (response.code === 200) {
      ElMessage.success('更新成功')
      userStore.setUserInfo({ ...userStore.userInfo, ...userForm })
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  }
}

const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    try {
      const response = await userApi.changePassword(passwordForm.oldPassword, passwordForm.newPassword)
      if (response.code === 200) {
        ElMessage.success('密码修改成功，请重新登录')
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        userStore.logout()
        window.location.href = '/login'
      } else {
        ElMessage.error(response.message || '修改失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '修改失败')
    }
  })
}

const handleTabClick = () => {
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  color: #fff;
  padding: 40px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.avatar-section {
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px auto;
  font-size: 48px;
}

.avatar-section h2 {
  font-size: 24px;
  margin: 0 0 8px 0;
}

.avatar-section p {
  opacity: 0.8;
  margin: 0;
}

.form-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.form-card h3 {
  font-size: 20px;
  margin: 0 0 24px 0;
}

.tabs-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}
</style>
