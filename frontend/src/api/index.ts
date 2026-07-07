import axios from 'axios'
import type { AxiosInstance } from 'axios'
import type { ApiResponse, UserInfo, LoginResponse, Trip, Conversation, Message, AITripResponse } from './types'

const createApi = (baseURL: string): AxiosInstance => {
  const api = axios.create({
    baseURL,
    timeout: 30000,
    headers: {
      'Content-Type': 'application/json'
    }
  })

  api.interceptors.request.use((config) => {
    const token = localStorage.getItem('travelai_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  }, (error) => {
    return Promise.reject(error)
  })

  api.interceptors.response.use((response) => {
    const data = response.data as ApiResponse<any>
    if (data.code === 401) {
      localStorage.removeItem('travelai_token')
      localStorage.removeItem('travelai_user')
      window.location.href = '/login'
      return Promise.reject(new Error('登录失效，请重新登录'))
    }
    return response
  }, (error) => {
    return Promise.reject(error)
  })

  return api
}

const userApiClient = createApi('http://localhost:8091')
const tripApiClient = createApi('http://localhost:8092')
const recommendApiClient = createApi('http://localhost:8093')
const chatApiClient = createApi('http://localhost:8094')

export const userApi = {
  register: async (data: { username: string; email: string; password: string }): Promise<ApiResponse<UserInfo>> => {
    const res = await userApiClient.post('/api/v1/users/register', data)
    return res.data as ApiResponse<UserInfo>
  },
  login: async (data: { username: string; password: string }): Promise<ApiResponse<LoginResponse>> => {
    const res = await userApiClient.post('/api/v1/users/login', data)
    return res.data as ApiResponse<LoginResponse>
  },
  getUserInfo: async (): Promise<ApiResponse<UserInfo>> => {
    const res = await userApiClient.get('/api/v1/users/info')
    return res.data as ApiResponse<UserInfo>
  },
  updateUserInfo: async (data: Partial<UserInfo>): Promise<ApiResponse<UserInfo>> => {
    const res = await userApiClient.put('/api/v1/users/info', data)
    return res.data as ApiResponse<UserInfo>
  },
  changePassword: async (oldPassword: string, newPassword: string): Promise<ApiResponse> => {
    const res = await userApiClient.put('/api/v1/users/password', {}, { params: { oldPassword, newPassword } })
    return res.data as ApiResponse
  }
}

export const tripApi = {
  createTrip: async (data: any): Promise<ApiResponse<Trip>> => {
    const res = await tripApiClient.post('/api/v1/trips', data)
    return res.data as ApiResponse<Trip>
  },
  getTrips: async (params?: any): Promise<ApiResponse<Trip[]>> => {
    const res = await tripApiClient.get('/api/v1/trips', { params })
    return res.data as ApiResponse<Trip[]>
  },
  getTrip: async (id: number): Promise<ApiResponse<Trip>> => {
    const res = await tripApiClient.get(`/api/v1/trips/${id}`)
    return res.data as ApiResponse<Trip>
  },
  updateTrip: async (id: number, data: any): Promise<ApiResponse<Trip>> => {
    const res = await tripApiClient.put(`/api/v1/trips/${id}`, data)
    return res.data as ApiResponse<Trip>
  },
  deleteTrip: async (id: number): Promise<ApiResponse> => {
    const res = await tripApiClient.delete(`/api/v1/trips/${id}`)
    return res.data as ApiResponse
  },
  shareTrip: async (id: number): Promise<ApiResponse<{ shareToken: string }>> => {
    const res = await tripApiClient.post(`/api/v1/trips/${id}/share`)
    return res.data as ApiResponse<{ shareToken: string }>
  },
  getSharedTrip: async (token: string): Promise<ApiResponse<Trip>> => {
    const res = await tripApiClient.get(`/api/v1/trips/share/${token}`)
    return res.data as ApiResponse<Trip>
  },
  updateTripStatus: async (id: number, status: number): Promise<ApiResponse<Trip>> => {
    const res = await tripApiClient.patch(`/api/v1/trips/${id}/status`, null, { params: { status } })
    return res.data as ApiResponse<Trip>
  }
}

export const recommendApi = {
  generateTrip: async (data: any): Promise<ApiResponse<AITripResponse>> => {
    const res = await recommendApiClient.post('/api/v1/recommend/trip', data)
    return res.data as ApiResponse<AITripResponse>
  },
  optimizeTrip: async (tripId: number, data: any): Promise<ApiResponse<AITripResponse>> => {
    const res = await recommendApiClient.post(`/api/v1/recommend/trip/${tripId}/optimize`, data)
    return res.data as ApiResponse<AITripResponse>
  }
}

export const chatApi = {
  createConversation: async (): Promise<ApiResponse<Conversation>> => {
    const res = await chatApiClient.post('/api/v1/chat/conversations')
    return res.data as ApiResponse<Conversation>
  },
  getConversations: async (): Promise<ApiResponse<Conversation[]>> => {
    const res = await chatApiClient.get('/api/v1/chat/conversations')
    return res.data as ApiResponse<Conversation[]>
  },
  getConversation: async (id: number): Promise<ApiResponse<Conversation>> => {
    const res = await chatApiClient.get(`/api/v1/chat/conversations/${id}`)
    return res.data as ApiResponse<Conversation>
  },
  sendMessage: async (conversationId: number, data: { content: string }): Promise<ApiResponse<Message>> => {
    const res = await chatApiClient.post(`/api/v1/chat/conversations/${conversationId}/messages`, data)
    return res.data as ApiResponse<Message>
  },
  sendMessageAuto: async (data: any): Promise<ApiResponse<Message>> => {
    const res = await chatApiClient.post('/api/v1/chat/messages', data)
    return res.data as ApiResponse<Message>
  },
  deleteConversation: async (id: number): Promise<ApiResponse> => {
    const res = await chatApiClient.delete(`/api/v1/chat/conversations/${id}`)
    return res.data as ApiResponse
  }
}

export default userApiClient