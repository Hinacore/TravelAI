<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="chat-sidebar">
        <div class="sidebar-header">
          <h2>AI助手</h2>
          <el-button type="primary" size="small" @click="createNewConversation">
            <i class="el-icon-plus"></i>
            新建对话
          </el-button>
        </div>
        
        <div class="conversation-list">
          <div 
            v-for="conv in conversations" 
            :key="conv.id" 
            class="conversation-item"
            :class="{ active: currentConversationId === conv.id }"
            @click="selectConversation(conv.id)"
          >
            <div class="conv-avatar">
              <i class="el-icon-chat-dot-round"></i>
            </div>
            <div class="conv-info">
              <div class="conv-title">{{ conv.title || '未命名对话' }}</div>
              <div class="conv-preview">{{ conv.lastMessage || '暂无消息' }}</div>
            </div>
            <div class="conv-delete" @click.stop="deleteConversation(conv.id)">
              <i class="el-icon-delete"></i>
            </div>
          </div>
        </div>
      </div>
      
      <div class="chat-main">
        <div class="chat-header" v-if="currentConversation">
          <h3>{{ currentConversation.title || 'AI助手' }}</h3>
          <span class="online-status">在线</span>
        </div>
        
        <div class="chat-messages" ref="messagesContainer">
          <div 
            v-for="msg in messages" 
            :key="msg.id" 
            class="message-item"
            :class="{ user: msg.sender === 'USER' }"
          >
            <div class="message-avatar">
              <i v-if="msg.sender === 'USER'" class="el-icon-user"></i>
              <i v-else class="el-icon-ai"></i>
            </div>
            <div class="message-content">
              <div class="message-text">{{ msg.content }}</div>
              <div class="message-time">{{ msg.createdAt }}</div>
            </div>
          </div>
          <div v-if="isTyping" class="message-item">
            <div class="message-avatar">
              <i class="el-icon-ai"></i>
            </div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="chat-input" v-if="currentConversation">
          <el-input 
            v-model="messageInput" 
            placeholder="输入您的问题..."
            @keyup.enter="sendMessage"
            :disabled="isTyping"
          />
          <el-button type="primary" @click="sendMessage" :disabled="isTyping">
            <i class="el-icon-send"></i>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { chatApi } from '../api/index'
import { ElMessage } from 'element-plus'

const conversations = ref<any[]>([])
const messages = ref<any[]>([])
const currentConversationId = ref<number | null>(null)
const messageInput = ref('')
const isTyping = ref(false)
const messagesContainer = ref()

const currentConversation = computed(() => {
  return conversations.value.find(c => c.id === currentConversationId.value) || null
})

const loadConversations = async () => {
  try {
    const response = await chatApi.getConversations()
    if (response.code === 200) {
      conversations.value = response.data
      if (conversations.value.length > 0 && !currentConversationId.value) {
        selectConversation(conversations.value[0].id)
      }
    }
  } catch (error) {
    console.error('加载对话列表失败:', error)
  }
}

const createNewConversation = async () => {
  try {
    const response = await chatApi.createConversation()
    if (response.code === 200) {
      const conv = response.data
      conversations.value.unshift(conv)
      currentConversationId.value = conv.id
      messages.value = []
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '创建对话失败')
  }
}

const selectConversation = async (id: number) => {
  currentConversationId.value = id
  try {
    const response = await chatApi.getConversation(id)
    if (response.code === 200) {
      messages.value = response.data.messages || []
      scrollToBottom()
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载对话失败')
  }
}

const deleteConversation = async (id: number) => {
  try {
    await chatApi.deleteConversation(id)
    conversations.value = conversations.value.filter(c => c.id !== id)
    if (currentConversationId.value === id) {
      currentConversationId.value = null
      messages.value = []
    }
    ElMessage.success('删除成功')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

const sendMessage = async () => {
  if (!messageInput.value.trim() || !currentConversationId.value) return
  
  const content = messageInput.value.trim()
  messageInput.value = ''
  
  messages.value.push({
    id: Date.now(),
    content,
    sender: 'USER',
    createdAt: new Date().toLocaleTimeString()
  })
  
  scrollToBottom()
  isTyping.value = true
  
  try {
    const response = await chatApi.sendMessage(currentConversationId.value, { content })
    if (response.code === 200) {
      const aiMessage = response.data
      aiMessage.createdAt = new Date().toLocaleTimeString()
      messages.value.push(aiMessage)
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '发送失败')
  } finally {
    isTyping.value = false
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

watch(currentConversationId, () => {
  scrollToBottom()
})

onMounted(() => {
  loadConversations()
})
</script>

<style scoped>
.chat-page {
  height: calc(100vh - 80px);
  display: flex;
  justify-content: center;
}

.chat-container {
  width: 100%;
  max-width: 1200px;
  display: flex;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.chat-sidebar {
  width: 320px;
  border-right: 1px solid #E4E7ED;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #E4E7ED;
}

.sidebar-header h2 {
  font-size: 18px;
  margin: 0;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.3s ease;
  border-bottom: 1px solid #F2F6FC;
}

.conversation-item:hover {
  background: #F5F7FA;
}

.conversation-item.active {
  background: #E6F7FF;
}

.conv-avatar {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  margin-right: 12px;
}

.conv-info {
  flex: 1;
  overflow: hidden;
}

.conv-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-preview {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-delete {
  color: #C0C4CC;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.conv-delete:hover {
  color: #F56C6C;
  background: #FEF0F0;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #E4E7ED;
}

.chat-header h3 {
  font-size: 18px;
  margin: 0;
}

.online-status {
  font-size: 12px;
  padding: 2px 8px;
  background: #F6FFED;
  color: #52C41A;
  border-radius: 20px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  background: #E4E7ED;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 18px;
  flex-shrink: 0;
}

.message-item.user .message-avatar {
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  color: #fff;
}

.message-content {
  max-width: 60%;
  margin: 0 12px;
}

.message-item.user .message-content {
  text-align: right;
}

.message-text {
  background: #F5F7FA;
  padding: 12px 16px;
  border-radius: 0 12px 12px 12px;
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
}

.message-item.user .message-text {
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  color: #fff;
  border-radius: 12px 0 12px 12px;
}

.message-time {
  font-size: 12px;
  color: #C0C4CC;
  margin-top: 4px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #F5F7FA;
  border-radius: 0 12px 12px 12px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #909399;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-input {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #E4E7ED;
}

.chat-input .el-input {
  flex: 1;
}
</style>
