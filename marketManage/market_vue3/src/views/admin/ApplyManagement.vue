<template>
  <div class="apply-management">
    <PageHeader 
      title="申请管理" 
      description="管理摊位租赁申请的审核和处理"
    />

    <!-- 统计概览 -->
    <div class="statistics-overview">
      <el-row :gutter="20">
        <el-col :span="6">
          <DataCard class="stat-card">
            <div class="stat-item">
              <div class="stat-icon total">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
                <div class="stat-label">总申请数</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        <el-col :span="6">
          <DataCard class="stat-card">
            <div class="stat-item">
              <div class="stat-icon pending">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
                <div class="stat-label">待审核</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        <el-col :span="6">
          <DataCard class="stat-card">
            <div class="stat-item">
              <div class="stat-icon approved">
                <el-icon><CircleCheck /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.approvedCount || 0 }}</div>
                <div class="stat-label">已通过</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        <el-col :span="6">
          <DataCard class="stat-card">
            <div class="stat-item">
              <div class="stat-icon rejected">
                <el-icon><CircleClose /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.rejectedCount || 0 }}</div>
                <div class="stat-label">已拒绝</div>
              </div>
            </div>
          </DataCard>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索表单 -->
    <SearchForm
      :default-values="searchParams"
      :loading="loading"
      @search="handleSearch"
      @reset="handleReset"
    >
      <template #default="{ form }">
        <el-form-item label="关键词">
          <el-input
            v-model="form.keyword"
            placeholder="请输入摊位编号、用户名或姓名"
            clearable
            style="width: 250px"
          />
        </el-form-item>
        <el-form-item label="申请状态">
          <el-select 
            v-model="form.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 申请列表 -->
    <DataCard>
      <el-table
        :data="applyList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column label="申请人" min-width="120">
          <template #default="{ row }">
            <div>
              <div style="font-weight: 600;">{{ row.realName || row.username }}</div>
              <div style="font-size: 12px; color: #999;">{{ row.phone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="摊位信息" min-width="150">
          <template #default="{ row }">
            <div>
              <div style="font-weight: 600;">{{ row.boothNo }}</div>
              <div style="font-size: 12px; color: #666;">{{ row.location }}</div>
              <div style="font-size: 12px; color: #999;">¥{{ row.price }}/月</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="businessType" label="经营类型" width="100">
          <template #default="{ row }">
            {{ getBusinessTypeText(row.businessType) }}
          </template>
        </el-table-column>
        <el-table-column label="租赁信息" min-width="120">
          <template #default="{ row }">
            <div>
              <div>{{ row.leaseMonths }}个月</div>
              <div style="font-size: 12px; color: #666;">{{ row.startDate }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button link type="primary" @click="handleView(row)">
                查看详情
              </el-button>
              <el-button 
                v-if="row.status === 0"
                link 
                type="success" 
                @click="handleApprove(row)"
              >
                通过
              </el-button>
              <el-button 
                v-if="row.status === 0"
                link 
                type="danger" 
                @click="handleReject(row)"
              >
                拒绝
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="pagination.current"
          :page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </DataCard>

    <!-- 申请详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="申请详情"
      width="1000px"
      top="5vh"
      :before-close="handleCloseDetail"
    >
      <div v-if="currentApply" class="apply-detail">
        <!-- 申请人信息 -->
        <div class="detail-section">
          <h4 class="section-title">申请人信息</h4>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="用户名">{{ currentApply.username }}</el-descriptions-item>
            <el-descriptions-item label="真实姓名">{{ currentApply.realName }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentApply.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱" :span="3">{{ currentApply.email }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 摊位信息 -->
        <div class="detail-section">
          <h4 class="section-title">摊位信息</h4>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="摊位编号">{{ currentApply.boothNo }}</el-descriptions-item>
            <el-descriptions-item label="摊位名称">{{ currentApply.boothName }}</el-descriptions-item>
            <el-descriptions-item label="位置">{{ currentApply.location }}</el-descriptions-item>
            <el-descriptions-item label="面积">{{ currentApply.area }}㎡</el-descriptions-item>
            <el-descriptions-item label="月租金">¥{{ currentApply.price }}</el-descriptions-item>
            <el-descriptions-item label="摊位描述" :span="3">{{ currentApply.boothDescription }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 申请信息 -->
        <div class="detail-section">
          <h4 class="section-title">申请信息</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="租赁期限">{{ currentApply.leaseMonths }}个月</el-descriptions-item>
            <el-descriptions-item label="开始日期">{{ currentApply.startDate }}</el-descriptions-item>
            <el-descriptions-item label="经营类型">{{ getBusinessTypeText(currentApply.businessType) }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentApply.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="申请原因" :span="2">{{ currentApply.applyReason }}</el-descriptions-item>
            <el-descriptions-item label="经营计划" :span="2">{{ currentApply.businessPlan }}</el-descriptions-item>
            <el-descriptions-item label="经营描述" :span="2">{{ currentApply.businessDescription }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 身份证件 -->
        <div v-if="currentApply.idCardImages" class="detail-section">
          <h4 class="section-title">身份证件</h4>
          <div class="id-card-images">
            <el-image
              v-for="(img, index) in getImageList(currentApply.idCardImages)"
              :key="index"
              :src="img"
              :preview-src-list="getImageList(currentApply.idCardImages)"
              fit="cover"
              class="id-card-image"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
        </div>

        <!-- 审核信息 -->
        <div class="detail-section">
          <h4 class="section-title">审核信息</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="申请状态">
              <el-tag :type="getStatusType(currentApply.status)">
                {{ getStatusText(currentApply.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ formatDateTime(currentApply.applyTime) }}</el-descriptions-item>
            <el-descriptions-item v-if="currentApply.auditTime" label="审核时间">
              {{ formatDateTime(currentApply.auditTime) }}
            </el-descriptions-item>
            <el-descriptions-item v-if="currentApply.rejectReason" label="拒绝原因" :span="2">
              {{ currentApply.rejectReason }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button 
            v-if="currentApply && currentApply.status === 0"
            type="success"
            @click="handleApproveInDetail"
          >
            通过申请
          </el-button>
          <el-button 
            v-if="currentApply && currentApply.status === 0"
            type="danger"
            @click="handleRejectInDetail"
          >
            拒绝申请
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog
      v-model="auditVisible"
      :title="auditType === 'approve' ? '通过申请' : '拒绝申请'"
      width="500px"
    >
      <div class="audit-form">
        <div class="audit-info">
          <p><strong>申请人：</strong>{{ currentAuditApply?.realName || currentAuditApply?.username }}</p>
          <p><strong>摊位：</strong>{{ currentAuditApply?.boothNo }} - {{ currentAuditApply?.location }}</p>
          <p><strong>租赁期限：</strong>{{ currentAuditApply?.leaseMonths }}个月</p>
        </div>
        
        <el-form v-if="auditType === 'reject'" :model="auditForm" label-width="80px">
          <el-form-item label="拒绝原因" required>
            <el-input
              v-model="auditForm.rejectReason"
              type="textarea"
              :rows="4"
              placeholder="请输入拒绝原因"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
        
        <div v-else class="confirm-message">
          <el-alert
            title="确认通过该申请吗？"
            description="通过后将自动创建租户、合同、租赁记录和支付记录，并更新摊位状态。"
            type="success"
            :closable="false"
            show-icon
          />
        </div>
      </div>
      
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button
          :type="auditType === 'approve' ? 'success' : 'danger'"
          :loading="auditLoading"
          @click="submitAudit"
        >
          {{ auditType === 'approve' ? '确认通过' : '确认拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getApplyList, getApplyDetail, auditApply, getApplyStatistics } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, Clock, CircleCheck, CircleClose, Picture 
} from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataCard from '@/components/DataCard.vue'

const loading = ref(false)
const detailVisible = ref(false)
const auditVisible = ref(false)
const auditLoading = ref(false)
const auditType = ref('') // 'approve' | 'reject'

const applyList = ref([])
const currentApply = ref(null)
const currentAuditApply = ref(null)
const statistics = ref({})

const searchParams = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const auditForm = reactive({
  rejectReason: ''
})

// 加载申请列表
const loadApplyList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchParams
    }
    const result = await getApplyList(params)
    applyList.value = result.records || []
    pagination.total = result.total || 0
  } catch (error) {
    console.error('加载申请列表失败:', error)
    ElMessage.error('加载申请列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const result = await getApplyStatistics()
    statistics.value = result
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 搜索
const handleSearch = (params) => {
  Object.assign(searchParams, params)
  pagination.current = 1
  loadApplyList()
}

// 重置
const handleReset = () => {
  searchParams.keyword = ''
  searchParams.status = null
  pagination.current = 1
  loadApplyList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadApplyList()
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  loadApplyList()
}

// 查看详情
const handleView = async (row) => {
  try {
    currentApply.value = await getApplyDetail(row.id)
    detailVisible.value = true
  } catch (error) {
    console.error('获取申请详情失败:', error)
    ElMessage.error('获取申请详情失败')
  }
}

// 关闭详情弹窗
const handleCloseDetail = () => {
  detailVisible.value = false
  currentApply.value = null
}

// 通过申请
const handleApprove = (row) => {
  currentAuditApply.value = row
  auditType.value = 'approve'
  auditVisible.value = true
}

// 拒绝申请
const handleReject = (row) => {
  currentAuditApply.value = row
  auditType.value = 'reject'
  auditForm.rejectReason = ''
  auditVisible.value = true
}

// 在详情中通过申请
const handleApproveInDetail = () => {
  currentAuditApply.value = currentApply.value
  auditType.value = 'approve'
  detailVisible.value = false
  auditVisible.value = true
}

// 在详情中拒绝申请
const handleRejectInDetail = () => {
  currentAuditApply.value = currentApply.value
  auditType.value = 'reject'
  auditForm.rejectReason = ''
  detailVisible.value = false
  auditVisible.value = true
}

// 提交审核
const submitAudit = async () => {
  if (auditType.value === 'reject' && !auditForm.rejectReason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    auditLoading.value = true
    
    const params = {
      id: currentAuditApply.value.id,
      status: auditType.value === 'approve' ? 1 : 2,
      rejectReason: auditType.value === 'reject' ? auditForm.rejectReason : null
    }
    
    await auditApply(params)
    
    const message = auditType.value === 'approve' ? '申请已通过' : '申请已拒绝'
    ElMessage.success(message)
    
    auditVisible.value = false
    loadApplyList()
    loadStatistics()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败: ' + (error.message || '未知错误'))
  } finally {
    auditLoading.value = false
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
    3: '已取消'
  }
  return statusMap[status] || '-'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    3: 'info'
  }
  return typeMap[status] || 'info'
}

// 经营类型文本转换
const getBusinessTypeText = (type) => {
  const typeMap = {
    'clothing': '服装鞋帽',
    'electronics': '数码电子',
    'daily': '生活用品',
    'food': '食品饮料',
    'books': '图书文具',
    'other': '其他'
  }
  return typeMap[type] || type || '-'
}

// 获取图片列表
const getImageList = (imagesStr) => {
  if (!imagesStr) return []
  return imagesStr.split(',').filter(img => img.trim())
}

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

// 初始化
onMounted(() => {
  loadApplyList()
  loadStatistics()
})
</script>

<style scoped>
.apply-management {
  padding: 0;
}

.statistics-overview {
  margin-bottom: 20px;
}

.stat-card {
  height: 80px;
  padding: 0;
}

.stat-item {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background: #409eff;
}

.stat-icon.pending {
  background: #e6a23c;
}

.stat-icon.approved {
  background: #67c23a;
}

.stat-icon.rejected {
  background: #f56c6c;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.apply-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 2px solid #409eff;
}

.id-card-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.id-card-image {
  width: 200px;
  height: 120px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.image-slot {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 14px;
}

.image-slot .el-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.audit-form {
  padding: 0;
}

.audit-info {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.audit-info p {
  margin: 8px 0;
  color: #606266;
}

.confirm-message {
  margin: 16px 0;
}
</style>
