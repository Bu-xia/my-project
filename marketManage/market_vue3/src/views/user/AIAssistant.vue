<template>
  <div class="ai-assistant">
    <PageHeader 
      title="AI助手" 
      description="智能咨询服务，为您解答市场相关问题"
    />

    <div class="assistant-container">
      <!-- 聊天区域 -->
      <DataCard class="chat-card">
        <div class="chat-header">
          <div class="assistant-info">
            <div class="assistant-avatar">
              <el-icon><Service /></el-icon>
            </div>
            <div class="assistant-details">
              <h3>跳蚤市场AI助手</h3>
              <p>我可以帮您解答摊位租赁、费用支付、市场规则等问题</p>
            </div>
          </div>
          <div class="chat-actions">
            <el-button size="small" @click="clearChat">
              <el-icon><Delete /></el-icon>
              清空对话
            </el-button>
          </div>
        </div>

        <div class="chat-messages" ref="messagesContainer">
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-content">
              <el-icon class="welcome-icon"><ChatDotRound /></el-icon>
              <h4>欢迎使用AI助手</h4>
              <p>您可以询问关于摊位租赁、费用支付、市场规则等任何问题</p>
            </div>
          </div>
          
          <div
            v-for="(message, index) in messages"
            :key="index"
            class="message-item"
            :class="{ 'user-message': message.type === 'user', 'ai-message': message.type === 'ai' }"
          >
            <div class="message-avatar">
              <el-icon v-if="message.type === 'user'"><User /></el-icon>
              <el-icon v-else><Service /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
          
          <!-- AI正在输入提示 -->
          <div v-if="isTyping" class="message-item ai-message typing">
            <div class="message-avatar">
              <el-icon><Service /></el-icon>
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

        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题..."
            :disabled="isTyping"
            @keydown.enter.exact="handleSend"
            @keydown.enter.shift.exact.prevent="inputMessage += '\n'"
          />
          <div class="input-actions">
            <div class="input-tips">
              <span>按 Enter 发送，Shift + Enter 换行</span>
            </div>
            <el-button
              type="primary"
              :loading="isTyping"
              :disabled="!inputMessage.trim()"
              @click="handleSend"
            >
              <el-icon><Promotion /></el-icon>
              发送
            </el-button>
          </div>
        </div>
      </DataCard>

      <!-- 快捷问题 -->
      <DataCard title="常见问题" class="quick-questions-card">
        <div class="quick-questions">
          <div
            v-for="(question, index) in quickQuestions"
            :key="index"
            class="question-item"
            @click="askQuestion(question.content)"
          >
            <div class="question-icon">
              <el-icon><component :is="question.icon" /></el-icon>
            </div>
            <div class="question-content">
              <h4>{{ question.title }}</h4>
              <p>{{ question.description }}</p>
            </div>
          </div>
        </div>
      </DataCard>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  User, 
  Delete, 
  ChatDotRound, 
  Promotion,
  Shop,
  Money,
  Document,
  QuestionFilled,
  Phone,
  Setting,
  Service
} from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import DataCard from '@/components/DataCard.vue'
import { chatWithAI } from '@/api/ai'

const messagesContainer = ref()
const inputMessage = ref('')
const isTyping = ref(false)

const messages = ref([])

// 快捷问题配置
const quickQuestions = [
  {
    title: '如何租赁摊位？',
    description: '了解摊位租赁流程和要求',
    content: '我想了解如何租赁摊位，需要什么条件和流程？',
    icon: 'Shop'
  },
  {
    title: '费用如何计算？',
    description: '租金、押金等费用说明',
    content: '摊位的租金和押金是如何计算的？有哪些费用？',
    icon: 'Money'
  },
  {
    title: '合同相关问题',
    description: '租赁合同条款和注意事项',
    content: '租赁合同有哪些重要条款？如何签署和履行？',
    icon: 'Document'
  },
  {
    title: '市场管理规定',
    description: '市场运营规则和管理制度',
    content: '市场有哪些管理规定？经营时需要注意什么？',
    icon: 'Setting'
  },
  {
    title: '联系客服',
    description: '获取人工客服联系方式',
    content: '如何联系人工客服？客服的工作时间是什么？',
    icon: 'Phone'
  },
  {
    title: '其他问题',
    description: '其他常见问题解答',
    content: '我有其他问题需要咨询',
    icon: 'QuestionFilled'
  }
]

// 发送消息
const handleSend = async () => {
  if (!inputMessage.value.trim() || isTyping.value) return
  
  const userMessage = {
    type: 'user',
    content: inputMessage.value.trim(),
    time: formatTime(new Date())
  }
  
  messages.value.push(userMessage)
  const question = inputMessage.value.trim()
  inputMessage.value = ''
  
  // 滚动到底部
  await nextTick()
  scrollToBottom()
  
  // 调用真实AI接口
  isTyping.value = true
  
  try {
    console.log('发送AI请求，问题:', question)
    const response = await chatWithAI(question)
    console.log('收到AI响应:', response)
    const aiMessage = {
      type: 'ai',
      content: formatMessage(response),
      time: formatTime(new Date())
    }
    
    messages.value.push(aiMessage)
  } catch (error) {
    console.error('AI对话失败，详细错误:', error)
    console.error('错误响应:', error.response)
    const errorMessage = {
      type: 'ai',
      content: '抱歉，服务暂时不可用，请稍后再试或联系客服：400-123-4567',
      time: formatTime(new Date())
    }
    messages.value.push(errorMessage)
  } finally {
    isTyping.value = false
    // 滚动到底部
    await nextTick()
    scrollToBottom()
  }
}

// 快捷提问
const askQuestion = (question) => {
  inputMessage.value = question
  handleSend()
}

// 清空对话
const clearChat = () => {
  messages.value = []
  ElMessage.success('对话已清空')
}

// 格式化消息内容
const formatMessage = (content) => {
  return content
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/`(.*?)`/g, '<code>$1</code>')
}

// 格式化时间
const formatTime = (date) => {
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 初始化欢迎消息
onMounted(() => {
  setTimeout(() => {
    const welcomeMessage = {
      type: 'ai',
      content: `您好！我是跳蚤市场AI智能助手👋<br><br>我可以帮您解答摊位租赁、费用支付、市场规则等各类问题。<br><br>您可以直接提问，或点击右侧的<strong>常见问题</strong>快速咨询！`,
      time: formatTime(new Date())
    }
    messages.value.push(welcomeMessage)
    nextTick(() => scrollToBottom())
  }, 500)
})
</script>

<style scoped>
.ai-assistant {
  padding: 0;
}

.assistant-container {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
  min-height: 600px;
}

.chat-card {
  display: flex;
  flex-direction: column;
  min-height: 600px;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.assistant-info {
  display: flex;
  align-items: center;
}

.assistant-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4ade80 0%, #22d3ee 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  margin-right: 12px;
}

.assistant-details h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.assistant-details p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  background: #fafafa;
  min-height: 400px;
}

.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  text-align: center;
}

.welcome-content {
  max-width: 400px;
}

.welcome-icon {
  font-size: 48px;
  color: #909399;
  margin-bottom: 16px;
}

.welcome-content h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.welcome-content p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
}

.user-message {
  flex-direction: row-reverse;
}

.user-message .message-content {
  background: linear-gradient(135deg, #4ade80 0%, #22d3ee 100%);
  color: white;
  margin-right: 12px;
}

.ai-message .message-content {
  background: white;
  color: #303133;
  margin-left: 12px;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: linear-gradient(135deg, #4ade80 0%, #22d3ee 100%);
  color: white;
}

.ai-message .message-avatar {
  background: #f0f0f0;
  color: #606266;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message-text {
  line-height: 1.5;
  word-break: break-word;
}

.message-text :deep(h4) {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
}

.message-text :deep(ul),
.message-text :deep(ol) {
  margin: 8px 0;
  padding-left: 20px;
}

.message-text :deep(li) {
  margin-bottom: 4px;
}

.message-text :deep(p) {
  margin: 8px 0;
}

.message-text :deep(code) {
  background: rgba(0, 0, 0, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.user-message .message-text :deep(code) {
  background: rgba(255, 255, 255, 0.2);
}

.message-time {
  font-size: 12px;
  opacity: 0.7;
  margin-top: 4px;
  text-align: right;
}

.user-message .message-time {
  text-align: left;
}

.typing .message-content {
  padding: 16px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #909399;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  background: white;
  flex-shrink: 0;
}

.chat-input :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.chat-input :deep(.el-textarea__inner):focus {
  border-color: #22d3ee;
  box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.1);
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.input-tips {
  font-size: 12px;
  color: #909399;
}

.input-actions .el-button {
  background: linear-gradient(135deg, #4ade80 0%, #22d3ee 100%);
  border: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.input-actions .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 211, 238, 0.3);
}

.quick-questions-card {
  height: fit-content;
}

.quick-questions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.question-item:hover {
  border-color: #22d3ee;
  background: linear-gradient(135deg, rgba(74, 222, 128, 0.1) 0%, rgba(34, 211, 238, 0.1) 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 211, 238, 0.2);
}

.question-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(74, 222, 128, 0.2) 0%, rgba(34, 211, 238, 0.2) 100%);
  color: #22d3ee;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.question-content h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.question-content p {
  margin: 0;
  font-size: 12px;
  color: #606266;
}

@media (max-width: 768px) {
  .assistant-container {
    grid-template-columns: 1fr;
  }
  
  .chat-card {
    min-height: 500px;
  }
}
</style>
