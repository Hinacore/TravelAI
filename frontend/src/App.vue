<template>
  <div class="app-container">
    <header class="app-header" v-if="showHeader">
      <div class="header-left">
        <router-link to="/" class="logo">TravelAI</router-link>
        <nav class="nav-menu">
          <router-link to="/" class="nav-item" :class="{ active: currentRoute === '/' }">首页</router-link>
          <router-link to="/trips" class="nav-item" :class="{ active: currentRoute === '/trips' }">行程管理</router-link>
          <router-link to="/recommend" class="nav-item" :class="{ active: currentRoute === '/recommend' }">AI推荐</router-link>
          <router-link to="/chat" class="nav-item" :class="{ active: currentRoute === '/chat' }">AI助手</router-link>
        </nav>
      </div>
      <div class="header-right">
        <template v-if="isLoggedIn">
          <router-link to="/profile" class="user-info">
            <span>{{ userStore.userInfo?.username || '用户' }}</span>
            <i class="el-icon-user"></i>
          </router-link>
          <button class="logout-btn" @click="handleLogout">退出登录</button>
        </template>
        <template v-else>
          <router-link to="/login" class="login-btn">登录</router-link>
          <router-link to="/register" class="register-btn">注册</router-link>
        </template>
      </div>
    </header>
    
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from './stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const currentRoute = computed(() => route.path)
const isLoggedIn = computed(() => userStore.isLoggedIn())
const showHeader = computed(() => {
  return route.path !== '/login' && route.path !== '/register'
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.initFromStorage()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  background: #F5F7FA;
}

#app {
  min-height: 100vh;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: linear-gradient(135deg, #409EFF 0%, #667EEA 100%);
  color: #fff;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 30px;
}

.logo {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  text-decoration: none;
  cursor: pointer;
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-item {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.nav-item:hover,
.nav-item.active {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.2);
}

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  padding: 6px 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.login-btn {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  padding: 6px 16px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.register-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  text-decoration: none;
  padding: 6px 16px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.register-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.app-main {
  flex: 1;
  padding: 20px;
}
</style>
