import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref<any>(null)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('travelai_token', newToken)
  }

  const setUserInfo = (info: any) => {
    userInfo.value = info
    localStorage.setItem('travelai_user', JSON.stringify(info))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('travelai_token')
    localStorage.removeItem('travelai_user')
  }

  const initFromStorage = () => {
    const savedToken = localStorage.getItem('travelai_token')
    const savedUser = localStorage.getItem('travelai_user')
    
    if (savedToken) {
      token.value = savedToken
    }
    if (savedUser) {
      try {
        userInfo.value = JSON.parse(savedUser)
      } catch {
        userInfo.value = null
      }
    }
  }

  const isLoggedIn = () => {
    return !!token.value
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    logout,
    initFromStorage,
    isLoggedIn
  }
})
