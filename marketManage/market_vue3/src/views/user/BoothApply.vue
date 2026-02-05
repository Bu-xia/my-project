<template>
  <div class="booth-apply">
    <PageHeader 
      title="摊位租赁申请" 
      description="填写申请信息，开始您的摊位租赁之旅"
    />

    <div class="apply-content">
      <!-- 申请表单 -->
      <DataCard title="申请信息" class="apply-form-card">
        <el-form
          ref="applyFormRef"
          :model="applyForm"
          :rules="applyRules"
          label-width="100px"
          class="apply-form"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="选择摊位" prop="boothId">
                <el-select
                  v-model="applyForm.boothId"
                  placeholder="请选择要申请的摊位"
                  filterable
                  @change="handleBoothChange"
                >
                  <el-option
                    v-for="booth in availableBooths"
                    :key="booth.id"
                    :label="`${booth.boothNo} - ${booth.location}`"
                    :value="booth.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="租赁期限" prop="duration">
                <el-select v-model="applyForm.duration" placeholder="请选择租赁期限">
                  <el-option label="1个月" :value="1" />
                  <el-option label="3个月" :value="3" />
                  <el-option label="6个月" :value="6" />
                  <el-option label="12个月" :value="12" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="开始日期" prop="startDate">
                <el-date-picker
                  v-model="applyForm.startDate"
                  type="date"
                  placeholder="请选择开始日期"
                  :disabled-date="disabledDate"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="经营类型" prop="businessType">
                <el-select v-model="applyForm.businessType" placeholder="请选择经营类型">
                  <el-option label="服装鞋帽" value="clothing" />
                  <el-option label="数码电子" value="electronics" />
                  <el-option label="生活用品" value="daily" />
                  <el-option label="食品饮料" value="food" />
                  <el-option label="图书文具" value="books" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="联系电话" prop="contactPhone">
            <el-input
              v-model="applyForm.contactPhone"
              placeholder="请输入联系电话"
              maxlength="11"
            />
          </el-form-item>

          <el-form-item label="申请原因" prop="applyReason">
            <el-input
              v-model="applyForm.applyReason"
              type="textarea"
              :rows="3"
              placeholder="请说明您申请该摊位的原因"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="经营计划" prop="businessPlan">
            <el-input
              v-model="applyForm.businessPlan"
              type="textarea"
              :rows="4"
              placeholder="请详细描述您的经营计划，包括目标客户、经营策略等"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="经营描述" prop="businessDescription">
            <el-input
              v-model="applyForm.businessDescription"
              type="textarea"
              :rows="3"
              placeholder="请简要描述您的经营内容"
              maxlength="300"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="身份证件">
            <el-upload
              :file-list="fileList"
              :action="uploadAction"
              :data="uploadData"
              :headers="uploadHeaders"
              :before-upload="beforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :on-remove="handleRemove"
              list-type="picture-card"
              :limit="3"
              name="file"
            >
              <el-icon><Plus /></el-icon>
              <template #tip>
                <div class="el-upload__tip">
                  请上传身份证正反面及营业执照（如有），支持jpg/png格式，单个文件不超过5MB
                </div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item>
            <el-checkbox v-model="applyForm.agree">
              我已阅读并同意
              <el-link type="primary" @click="showAgreement = true">《租赁协议》</el-link>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="submitLoading"
              @click="handleSubmit"
            >
              提交申请
            </el-button>
            <el-button size="large" @click="handleReset">
              重置表单
            </el-button>
          </el-form-item>
        </el-form>
      </DataCard>

      <!-- 摊位信息预览 -->
      <DataCard v-if="selectedBooth" title="摊位信息" class="booth-info-card">
        <div class="booth-preview">
          <div class="booth-image">
            <el-image
              :src="selectedBooth.images || 'https://via.placeholder.com/300x200?text=暂无图片'"
              fit="cover"
              style="width: 100%; height: 150px"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          <div class="booth-details">
            <el-descriptions :column="1" size="small">
              <el-descriptions-item label="摊位编号">
                {{ selectedBooth.boothNo }}
              </el-descriptions-item>
              <el-descriptions-item label="位置">
                {{ selectedBooth.location }}
              </el-descriptions-item>
              <el-descriptions-item label="面积">
                {{ selectedBooth.area }}㎡
              </el-descriptions-item>
              <el-descriptions-item label="月租金">
                <span class="price">¥{{ selectedBooth.price }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="押金">
                <span class="price">¥{{ selectedBooth.deposit || '待定' }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
        
        <!-- 费用预算 -->
        <div v-if="applyForm.duration" class="cost-summary">
          <h4>费用预算</h4>
          <div class="cost-item">
            <span>月租金：</span>
            <span>¥{{ selectedBooth.price }}</span>
          </div>
          <div class="cost-item">
            <span>租赁期限：</span>
            <span>{{ applyForm.duration }}个月</span>
          </div>
          <div class="cost-item">
            <span>租金小计：</span>
            <span>¥{{ selectedBooth.price * applyForm.duration }}</span>
          </div>
          <div class="cost-item">
            <span>押金：</span>
            <span>¥{{ selectedBooth.deposit || 0 }}</span>
          </div>
          <div class="cost-item total">
            <span>预计总费用：</span>
            <span class="total-price">¥{{ calculateTotal() }}</span>
          </div>
        </div>
      </DataCard>
    </div>

    <!-- 我的申请列表 -->
    <DataCard title="我的申请" class="my-apply-card">
      <el-table :data="myApplyList" v-loading="listLoading" style="width: 100%">
        <el-table-column prop="boothNo" label="摊位编号" min-width="120">
          <template #default="{ row }">
            {{ row.boothNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="120">
          <template #default="{ row }">
            {{ row.location || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="businessType" label="经营类型" min-width="120">
          <template #default="{ row }">
            {{ getBusinessTypeText(row.businessType) }}
          </template>
        </el-table-column>
        <el-table-column prop="leaseMonths" label="期限" min-width="90">
          <template #default="{ row }">
            {{ row.leaseMonths ? row.leaseMonths + '个月' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" min-width="110" />
        <el-table-column prop="status" label="状态" min-width="90">
          <template #default="{ row }">
            <el-tag :type="getApplyStatusType(row.status)">
              {{ getApplyStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" min-width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === 0"
                type="danger"
                size="small"
                @click="cancelApply(row.id)"
              >
                取消申请
              </el-button>
              <el-button
                type="primary"
                size="small"
                @click="viewApplyDetail(row)"
              >
                查看详情
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="applyPagination.current"
          :page-size="applyPagination.size"
          :total="applyPagination.total"
          layout="prev, pager, next"
          @current-change="fetchMyApplyList"
        />
      </div>
    </DataCard>

    <!-- 租赁协议弹窗 -->
    <el-dialog v-model="showAgreement" title="租赁协议" width="600px">
      <div class="agreement-content">
        <h3>跳蚤市场摊位租赁协议</h3>
        <p>1. 租赁双方应遵守市场管理规定...</p>
        <p>2. 租金按月支付，不得拖欠...</p>
        <p>3. 租赁期间应保持摊位整洁...</p>
        <p>4. 违约责任及处理办法...</p>
        <!-- 更多协议内容 -->
      </div>
      <template #footer>
        <el-button @click="showAgreement = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 申请详情弹窗 -->
    <el-dialog v-model="showDetail" title="申请详情" width="1200px" top="5vh">
      <div v-if="currentApply" class="detail-content">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4 class="section-title">基本信息</h4>
          <el-descriptions :column="4" border size="small">
            <el-descriptions-item label="申请ID" label-align="right">
              {{ currentApply.id || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="用户ID" label-align="right">
              {{ currentApply.userId || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="摊位ID" label-align="right">
              {{ currentApply.boothId || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="摊位编号" label-align="right">
              {{ currentApply.boothNo || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="位置" label-align="right">
              {{ currentApply.location || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="租赁期限" label-align="right">
              {{ currentApply.leaseMonths ? currentApply.leaseMonths + '个月' : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="开始日期" label-align="right">
              {{ currentApply.startDate || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="经营类型" label-align="right">
              {{ getBusinessTypeText(currentApply.businessType) }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话" label-align="right">
              {{ currentApply.contactPhone || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态" label-align="right" :span="3">
              <el-tag :type="getApplyStatusType(currentApply.status)">
                {{ getApplyStatusText(currentApply.status) }}
              </el-tag>
              <span v-if="currentApply.rejectReason" style="margin-left: 10px; color: #f56c6c;">
                拒绝原因: {{ currentApply.rejectReason }}
              </span>
            </el-descriptions-item>
            <el-descriptions-item label="申请时间" label-align="right" :span="2">
              {{ formatDateTime(currentApply.applyTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="审核时间" label-align="right" :span="2">
              {{ formatDateTime(currentApply.auditTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 申请内容 -->
        <div class="detail-section">
          <h4 class="section-title">申请内容</h4>
          <div class="content-grid">
            <div class="content-item">
              <label>申请原因：</label>
              <div class="content-text">{{ currentApply.applyReason || '-' }}</div>
            </div>
            <div class="content-item">
              <label>经营计划：</label>
              <div class="content-text">{{ currentApply.businessPlan || '-' }}</div>
            </div>
            <div class="content-item">
              <label>经营描述：</label>
              <div class="content-text">{{ currentApply.businessDescription || '-' }}</div>
            </div>
          </div>
        </div>

        <!-- 身份证件图片 -->
        <div v-if="currentApply.idCardImages" class="detail-section">
          <h4 class="section-title">身份证件</h4>
          <div class="image-list">
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
      </div>
      <template #footer>
        <el-button type="primary" @click="showDetail = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAvailableBooths, getBoothDetail } from '@/api/booth'
import { submitApply, getMyApplyList, cancelApply as cancelApplyApi } from '@/api/apply'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import DataCard from '@/components/DataCard.vue'

const route = useRoute()
const userStore = useUserStore()
const applyFormRef = ref()
const submitLoading = ref(false)
const listLoading = ref(false)
const showAgreement = ref(false)
const showDetail = ref(false)
const currentApply = ref(null)

const availableBooths = ref([])
const selectedBooth = ref(null)
const myApplyList = ref([])
const fileList = ref([])

const applyForm = reactive({
  boothId: null,
  duration: null,
  startDate: '',
  businessType: '',
  contactPhone: '',
  applyReason: '',
  businessPlan: '',
  businessDescription: '',
  agree: false
})

const applyPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const applyRules = {
  boothId: [
    { required: true, message: '请选择摊位', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请选择租赁期限', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  businessType: [
    { required: true, message: '请选择经营类型', trigger: 'change' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  applyReason: [
    { required: true, message: '请填写申请原因', trigger: 'blur' },
    { min: 10, message: '申请原因至少10个字符', trigger: 'blur' }
  ],
  businessPlan: [
    { required: true, message: '请填写经营计划', trigger: 'blur' },
    { min: 20, message: '经营计划至少20个字符', trigger: 'blur' }
  ],
  businessDescription: [
    { required: true, message: '请填写经营描述', trigger: 'blur' },
    { min: 10, message: '经营描述至少10个字符', trigger: 'blur' }
  ]
}

const uploadAction = '/api/user/upload' // 上传接口地址

// 上传请求头
const uploadHeaders = computed(() => {
  const token = userStore.token
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 上传数据
const uploadData = computed(() => {
  return {
    userId: userStore.user?.id,
    type: 'documents' // 文档类型
  }
})

// 获取可租摊位
const fetchAvailableBooths = async () => {
  try {
    const booths = await getAvailableBooths()
    availableBooths.value = booths
    
    // 如果从摊位查找页面跳转过来，自动选择摊位
    const boothId = route.query.boothId
    if (boothId) {
      applyForm.boothId = parseInt(boothId)
      handleBoothChange(applyForm.boothId)
    }
  } catch (error) {
    console.error('获取可租摊位失败:', error)
  }
}

// 摊位选择变化
const handleBoothChange = async (boothId) => {
  if (boothId) {
    try {
      const booth = await getBoothDetail(boothId)
      selectedBooth.value = booth
    } catch (error) {
      console.error('获取摊位详情失败:', error)
    }
  } else {
    selectedBooth.value = null
  }
}

// 计算总费用
const calculateTotal = () => {
  if (!selectedBooth.value || !applyForm.duration) return 0
  const rent = selectedBooth.value.price * applyForm.duration
  const deposit = selectedBooth.value.deposit || 0
  return rent + deposit
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 文件上传前检查
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  
  console.log('准备上传文件:', {
    name: file.name,
    type: file.type,
    size: (file.size / 1024 / 1024).toFixed(2) + 'MB'
  })
  
  return true
}

// 上传成功
const handleUploadSuccess = (response, file, fileListFromUpload) => {
  console.log('上传响应:', response)
  console.log('上传的文件:', file)
  
  if (response.code === 200) {
    // 构建完整的文件URL
    const fileUrl = response.data.startsWith('http') 
      ? response.data 
      : `http://localhost:8080${response.data}`
    
    // 更新文件列表，添加服务器返回的URL
    const fileItem = {
      name: file.name,
      url: fileUrl,
      uid: file.uid
    }
    
    // 查找并更新文件列表中的项
    const index = fileList.value.findIndex(item => item.uid === file.uid)
    if (index > -1) {
      fileList.value[index] = fileItem
    } else {
      fileList.value.push(fileItem)
    }
    
    console.log('文件上传成功:', fileItem)
    console.log('当前文件列表:', fileList.value)
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error(response.msg || '文件上传失败')
    // 移除上传失败的文件
    fileList.value = fileList.value.filter(item => item.uid !== file.uid)
  }
}

// 上传失败
const handleUploadError = (error, file) => {
  console.error('文件上传失败:', error)
  ElMessage.error('文件上传失败，请重试')
  // 移除上传失败的文件
  fileList.value = fileList.value.filter(item => item.uid !== file.uid)
}

// 移除文件
const handleRemove = (file) => {
  console.log('移除文件:', file)
  const index = fileList.value.findIndex(item => item.uid === file.uid)
  if (index > -1) {
    fileList.value.splice(index, 1)
  }
  console.log('移除后的文件列表:', fileList.value)
}

// 提交申请
const handleSubmit = async () => {
  try {
    if (!applyForm.agree) {
      ElMessage.warning('请先同意租赁协议')
      return
    }

    await applyFormRef.value.validate()
    
    // 检查文件是否上传
    console.log('当前文件列表:', fileList.value)
    if (fileList.value.length === 0) {
      ElMessage.warning('请上传身份证件')
      return
    }
    
    submitLoading.value = true

    // 格式化日期为 YYYY-MM-DD 格式
    let formattedDate = ''
    if (applyForm.startDate) {
      const date = new Date(applyForm.startDate)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      formattedDate = `${year}-${month}-${day}`
    }

    // 提取文件URL
    const imageUrls = fileList.value.map(file => file.url).filter(url => url).join(',')
    console.log('图片URLs:', imageUrls)

    const submitData = {
      userId: userStore.user.id,
      boothId: applyForm.boothId,
      leaseMonths: applyForm.duration,
      startDate: formattedDate,
      businessType: applyForm.businessType,
      contactPhone: applyForm.contactPhone,
      applyReason: applyForm.applyReason,
      businessPlan: applyForm.businessPlan,
      businessDescription: applyForm.businessDescription,
      idCardImages: imageUrls
    }
    
    console.log('==================== 前端提交的申请数据 ====================')
    console.log('用户ID:', submitData.userId)
    console.log('摊位ID:', submitData.boothId)
    console.log('租赁月数:', submitData.leaseMonths)
    console.log('开始日期:', submitData.startDate)
    console.log('经营类型:', submitData.businessType)
    console.log('联系电话:', submitData.contactPhone)
    console.log('申请原因:', submitData.applyReason)
    console.log('经营计划:', submitData.businessPlan)
    console.log('经营描述:', submitData.businessDescription)
    console.log('身份证件:', submitData.idCardImages)
    console.log('==========================================================')

    await submitApply(submitData)
    ElMessage.success('申请提交成功，请等待审核')
    
    // 重置表单并刷新申请列表
    handleReset()
    fetchMyApplyList()
  } catch (error) {
    console.error('提交申请失败:', error)
    ElMessage.error('提交失败: ' + (error.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const handleReset = () => {
  applyFormRef.value?.resetFields()
  fileList.value = []
  selectedBooth.value = null
}

// 获取我的申请列表
const fetchMyApplyList = async () => {
  try {
    listLoading.value = true
    const params = {
      current: applyPagination.current,
      size: applyPagination.size,
      userId: userStore.user.id
    }
    
    console.log('查询申请列表参数:', params)
    
    const result = await getMyApplyList(params)
    
    console.log('申请列表查询结果:', result)
    
    myApplyList.value = result.records || []
    applyPagination.total = result.total || 0
  } catch (error) {
    console.error('获取申请列表失败:', error)
  } finally {
    listLoading.value = false
  }
}

// 取消申请
const cancelApply = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消这个申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cancelApplyApi(id)
    ElMessage.success('申请已取消')
    fetchMyApplyList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消申请失败:', error)
    }
  }
}

// 查看申请详情
const viewApplyDetail = (apply) => {
  currentApply.value = apply
  showDetail.value = true
  console.log('申请详情数据:', apply)
}

// 获取图片列表
const getImageList = (imagesStr) => {
  if (!imagesStr) return []
  return imagesStr.split(',').filter(img => img.trim())
}

// 申请状态相关方法
const getApplyStatusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
    3: '已取消'
  }
  return statusMap[status] || '未知'
}

const getApplyStatusType = (status) => {
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

onMounted(() => {
  fetchAvailableBooths()
  fetchMyApplyList()
})
</script>

<style scoped>
.booth-apply {
  padding: 0;
}

.apply-content {
  display: grid;
  grid-template-columns: 3fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.apply-form-card {
  grid-column: 1;
  min-width: 0; /* 防止内容溢出 */
}

.booth-info-card {
  grid-column: 2;
  min-width: 0; /* 防止内容溢出 */
}

.my-apply-card {
  grid-column: 1 / -1;
  width: 100%;
}

.my-apply-card :deep(.el-table) {
  margin-bottom: 16px;
  width: 100%;
}

.my-apply-card :deep(.el-table th) {
  padding: 12px 8px;
  font-weight: 600;
}

.my-apply-card :deep(.el-table td) {
  padding: 12px 8px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.apply-form {
  max-width: none;
}

.booth-preview {
  margin-bottom: 20px;
}

.booth-image {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.cost-summary {
  margin-top: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.cost-summary h4 {
  margin: 0 0 12px 0;
  color: #303133;
}

.cost-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.cost-item.total {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
  font-weight: 600;
}

.total-price {
  color: #f56c6c;
  font-size: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.agreement-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 20px;
  line-height: 1.6;
}

.agreement-content h3 {
  margin-bottom: 20px;
  text-align: center;
  color: #303133;
}

.agreement-content p {
  margin-bottom: 12px;
  color: #606266;
}

.detail-content {
  padding: 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
  border-bottom: 2px solid #409eff;
}

.detail-content :deep(.el-descriptions) {
  margin-bottom: 0;
}

.detail-content :deep(.el-descriptions__label) {
  width: 100px;
  font-weight: 500;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.content-item {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.content-item label {
  display: block;
  margin-bottom: 8px;
  color: #606266;
  font-size: 13px;
  font-weight: 500;
}

.content-text {
  color: #303133;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-all;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.id-card-image {
  width: 200px;
  height: 130px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

@media (max-width: 768px) {
  .apply-content {
    grid-template-columns: 1fr;
  }
  
  .apply-form-card,
  .booth-info-card {
    grid-column: 1;
  }
}
</style>
