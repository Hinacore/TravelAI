export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface UserInfo {
  id: number
  username: string
  email: string
  nickname?: string
  phone?: string
  gender?: string
  birthDate?: string
  avatar?: string
  createdAt?: string
  updatedAt?: string
}

export interface LoginResponse {
  token: string
  user: UserInfo
}

export interface Trip {
  id: number
  name: string
  destination: string
  startDate: string
  endDate?: string
  days: number
  budget: number
  totalBudget?: number
  status: number
  statusText: string
  description?: string
  createdAt: string
  updatedAt?: string
}

export interface Message {
  id: number
  content: string
  sender: 'USER' | 'AI'
  createdAt: string
}

export interface Conversation {
  id: number
  title: string
  lastMessage?: string
  messages?: Message[]
  createdAt: string
  updatedAt: string
}

export interface RecommendForm {
  destination: string
  budget: number
  days: number
  startDate: string
  travelers?: number
  preferences: string[]
  specialRequirements?: string
}

export interface DailyActivity {
  spot: string
  duration: string
  ticket: string
  transportation: string
  description: string
}

export interface DailyItinerary {
  day: number
  date: string
  morning?: DailyActivity
  afternoon?: DailyActivity
  evening?: DailyActivity
}

export interface BudgetBreakdown {
  accommodation: number
  food: number
  transportation: number
  tickets: number
  other: number
}

export interface AITripResponse {
  success: boolean
  city: string
  days: number
  totalBudget: number
  dailyItinerary: DailyItinerary[]
  budgetBreakdown: BudgetBreakdown
  tips: string[]
  warnings: string[]
}