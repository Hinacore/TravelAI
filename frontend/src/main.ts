import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import App from './App.vue'
import { useUserStore } from './stores/user'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)

const userStore = useUserStore()
userStore.initFromStorage()

app.use(router)
app.use(ElementPlus, {
  locale: zhCn
})

app.mount('#app')
