<template>
  <div class="notice">
    <PageHeader 
      title="公告通知" 
      description="查看市场最新公告和重要通知"
    />

    <!-- 置顶公告 -->
    <div v-if="topNotices.length > 0" class="top-notices">
      <DataCard title="重要公告" class="top-notice-card">
        <div class="notice-carousel">
          <el-carousel 
            :interval="5000" 
            :height="'120px'"
            indicator-position="outside"
            arrow="hover"
          >
            <el-carousel-item v-for="notice in topNotices" :key="notice.id">
              <div class="carousel-item" @click="viewNoticeDetail(notice)">
                <div class="notice-badge">
                  <el-icon><Bell /></el-icon>
                  置顶
                </div>
                <div class="notice-content">
                  <h3>{{ notice.title }}</h3>
                  <p>{{ notice.summary || notice.content.substring(0, 100) + '...' }}</p>
                  <div class="notice-meta">
                    <span>{{ notice.publishTime }}</span>
                    <span>阅读量：{{ notice.viewCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
      </DataCard>
    </div>

    <!-- 公告列表 -->
    <DataCard title="全部公告">
      <template #extra>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索公告标题"
          style="width: 300px"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </template>

      <div class="notice-list">
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="notice-item"
          @click="viewNoticeDetail(notice)"
        >
          <div class="notice-header">
            <div class="notice-title-section">
              <h3 class="notice-title">
                <el-tag v-if="notice.isTop" type="danger" size="small">置顶</el-tag>
                <el-tag v-if="notice.isImportant" type="warning" size="small">重要</el-tag>
                {{ notice.title }}
              </h3>
              <div class="notice-summary">
                {{ notice.summary || notice.content.substring(0, 150) + '...' }}
              </div>
            </div>
            <div class="notice-actions">
              <el-button type="primary" size="small">
                查看详情
              </el-button>
            </div>
          </div>
          
          <div class="notice-footer">
            <div class="notice-meta">
              <span class="meta-item">
                <el-icon><Calendar /></el-icon>
                {{ notice.publishTime }}
              </span>
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ notice.viewCount || 0 }}次阅读
              </span>
              <span v-if="notice.category" class="meta-item">
                <el-icon><Folder /></el-icon>
                {{ getCategoryText(notice.category) }}
              </span>
            </div>
            <div class="notice-status">
              <el-tag :type="getNoticeStatusType(notice.status)" size="small">
                {{ getNoticeStatusText(notice.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="pagination.current"
          :page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </DataCard>

    <!-- 公告详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentNotice?.title"
      width="800px"
      :before-close="handleCloseDetail"
    >
      <div v-if="currentNotice" class="notice-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <div class="meta-row">
              <span class="meta-label">发布时间：</span>
              <span>{{ currentNotice.publishTime }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">阅读量：</span>
              <span>{{ currentNotice.viewCount || 0 }}</span>
            </div>
            <div v-if="currentNotice.category" class="meta-row">
              <span class="meta-label">分类：</span>
              <span>{{ getCategoryText(currentNotice.category) }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">状态：</span>
              <el-tag :type="getNoticeStatusType(currentNotice.status)" size="small">
                {{ getNoticeStatusText(currentNotice.status) }}
              </el-tag>
            </div>
          </div>
          <div class="detail-tags">
            <el-tag v-if="currentNotice.isTop" type="danger" size="small">置顶</el-tag>
            <el-tag v-if="currentNotice.isImportant" type="warning" size="small">重要</el-tag>
          </div>
        </div>
        
        <div class="detail-content" v-html="formatNoticeContent(currentNotice.content)">
        </div>
        
        <div v-if="currentNotice.attachments" class="detail-attachments">
          <h4>附件下载</h4>
          <div class="attachment-list">
            <div
              v-for="(attachment, index) in currentNotice.attachments"
              :key="index"
              class="attachment-item"
              @click="downloadAttachment(attachment)"
            >
              <el-icon><Document /></el-icon>
              <span>{{ attachment.name }}</span>
              <el-icon><Download /></el-icon>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getNoticeList, getNoticeDetail, getTopNotices } from '@/api/notice'
import { ElMessage } from 'element-plus'
import { 
  Bell, 
  Search, 
  Calendar, 
  View, 
  Folder, 
  Document, 
  Download 
} from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import DataCard from '@/components/DataCard.vue'

const loading = ref(false)
const detailVisible = ref(false)
const searchKeyword = ref('')

const notices = ref([])
const topNotices = ref([])
const currentNotice = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 获取公告列表
const fetchNotices = async () => {
  try {
    loading.value = true
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchKeyword.value
    }
    
    const result = await getNoticeList(params)
    notices.value = result.records.map(notice => ({
      ...notice,
      publishTime: formatDateTime(notice.publishTime)
    }))
    pagination.total = result.total
  } catch (error) {
    console.error('获取公告列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  pagination.current = 1 // 重置到第一页
  fetchNotices()
}

// 获取置顶公告
const fetchTopNotices = async () => {
  try {
    const result = await getTopNotices()
    topNotices.value = result.map(notice => ({
      ...notice,
      publishTime: formatDateTime(notice.publishTime)
    }))
  } catch (error) {
    console.error('获取置顶公告失败:', error)
  }
}

// 查看公告详情
const viewNoticeDetail = async (notice) => {
  try {
    const detail = await getNoticeDetail(notice.id)
    currentNotice.value = detail
    detailVisible.value = true
    
    // 刷新列表以更新阅读量
    await fetchNotices()
  } catch (error) {
    console.error('获取公告详情失败:', error)
  }
}

// 关闭详情弹窗
const handleCloseDetail = () => {
  detailVisible.value = false
  currentNotice.value = null
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  fetchNotices()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  fetchNotices()
}

// 下载附件
const downloadAttachment = (attachment) => {
  ElMessage.success(`开始下载：${attachment.name}`)
  // 这里实现实际的下载逻辑
}

// 格式化公告内容
const formatNoticeContent = (content) => {
  // 简单的HTML格式化
  return content
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
}

// 获取分类文本
const getCategoryText = (category) => {
  const categoryMap = {
    'system': '系统公告',
    'market': '市场通知',
    'policy': '政策更新',
    'maintenance': '维护通知',
    'event': '活动公告',
    'other': '其他'
  }
  return categoryMap[category] || '未分类'
}

// 获取公告状态文本
const getNoticeStatusText = (status) => {
  const statusMap = {
    1: '已发布',
    0: '已下架'
  }
  return statusMap[status] || '未知'
}

// 获取公告状态类型
const getNoticeStatusType = (status) => {
  const typeMap = {
    1: 'success',
    0: 'info'
  }
  return typeMap[status] || 'default'
}

onMounted(() => {
  fetchNotices()
  fetchTopNotices()
})
</script>

<style scoped>
.notice {
  padding: 0;
}

.top-notices {
  margin-bottom: 20px;
}

.top-notice-card {
  background: linear-gradient(135deg, #4ade80 0%, #22d3ee 100%);
  color: white;
}

.top-notice-card :deep(.card-title) {
  color: white;
}

.notice-carousel {
  margin: -20px;
}

.carousel-item {
  display: flex;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  height: 120px;
  position: relative;
  overflow: hidden;
}

.carousel-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.carousel-item:hover::before {
  opacity: 1;
}

.notice-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  margin-right: 16px;
  flex-shrink: 0;
}

.notice-content {
  flex: 1;
}

.notice-content h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: white;
}

.notice-content p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.5;
}

.notice-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.notice-list {
  margin-bottom: 20px;
}

.notice-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.notice-item:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
  transform: translateY(-2px);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.notice-title-section {
  flex: 1;
  margin-right: 16px;
}

.notice-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.notice-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.notice-detail {
  padding: 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 0;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.detail-meta {
  flex: 1;
}

.meta-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.meta-label {
  color: #606266;
  margin-right: 8px;
  min-width: 80px;
}

.detail-tags {
  display: flex;
  gap: 8px;
}

.detail-content {
  line-height: 1.8;
  color: #303133;
  margin-bottom: 20px;
}

.detail-content :deep(p) {
  margin-bottom: 12px;
}

.detail-content :deep(h1),
.detail-content :deep(h2),
.detail-content :deep(h3),
.detail-content :deep(h4) {
  margin: 20px 0 12px 0;
  color: #303133;
}

.detail-content :deep(ul),
.detail-content :deep(ol) {
  margin: 12px 0;
  padding-left: 20px;
}

.detail-content :deep(li) {
  margin-bottom: 6px;
}

.detail-attachments {
  border-top: 1px solid #e4e7ed;
  padding-top: 20px;
}

.detail-attachments h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.attachment-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.attachment-item span {
  flex: 1;
  font-size: 14px;
  color: #303133;
}

:deep(.el-carousel__indicator) {
  background: rgba(255, 255, 255, 0.3);
}

:deep(.el-carousel__indicator.is-active) {
  background: white;
}
</style>
