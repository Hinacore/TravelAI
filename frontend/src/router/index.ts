import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Recommend from '../views/Recommend.vue'
import Chat from '../views/Chat.vue'
import TripList from '../views/TripList.vue'
import TripDetail from '../views/TripDetail.vue'
import Profile from '../views/Profile.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'Home', component: Home },
    { path: '/login', name: 'Login', component: Login },
    { path: '/register', name: 'Register', component: Register },
    { path: '/recommend', name: 'Recommend', component: Recommend },
    { path: '/chat', name: 'Chat', component: Chat },
    { path: '/trips', name: 'TripList', component: TripList },
    { path: '/trips/:id', name: 'TripDetail', component: TripDetail },
    { path: '/profile', name: 'Profile', component: Profile },
    { path: '/:pathMatch(.*)*', redirect: '/' }
  ]
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('travelai_token')
  const requiresAuth = ['/recommend', '/chat', '/trips', '/profile']
  
  if (requiresAuth.includes(to.path) && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
