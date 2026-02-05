<template>
  <div class="my-rental">
    <PageHeader 
      title="我的租赁" 
      description="查看和管理您的摊位租赁信息"
    />

    <!-- 当前租赁 -->
    <DataCard title="当前租赁" class="current-rental-card">
      <div v-if="currentRentals.length === 0" class="empty-state">
        <el-empty description="暂无当前租赁">
          <el-button type="primary" @click="$router.push('/user/booth')">
            去租赁摊位
          </el-button>
        </el-empty>
      </div>
      <div v-else class="rental-grid">
        <div
          v-for="rental in currentRentals"
          :key="rental.id"
          class="rental-card"
        >
          <div class="rental-header">
            <div class="booth-info">
              <h3>{{ rental.boothNumber }}</h3>
              <p class="location">
                <el-icon><Location /></el-icon>
                {{ rental.location }}
              </p>
            </div>
            <div class="rental-status">
              <el-tag :type="getRentalStatusType(rental.status)">
                {{ getRentalStatusText(rental.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="rental-body">
            <div class="rental-info">
              <div class="info-item">
                <span class="label">租赁期限：</span>
                <span>{{ rental.startDate }} 至 {{ rental.endDate }}</span>
              </div>
              <div class="info-item">
                <span class="label">月租金：</span>
                <span class="price">¥{{ rental.monthlyRent }}</span>
              </div>
              <div class="info-item">
                <span class="label">剩余天数：</span>
                <span :class="getRemainingDaysClass(rental.remainingDays)">
                  {{ rental.remainingDays }}天
                </span>
              </div>
            </div>
            
            <!-- 倒计时提醒 -->
            <div v-if="rental.remainingDays <= 30" class="countdown-alert">
              <el-alert
                :title="`租期即将到期，剩余${rental.remainingDays}天`"
                type="warning"
                :closable="false"
                show-icon
              />
            </div>
          </div>
          
          <div class="rental-actions">
            <el-button size="small" @click="viewRentalDetail(rental)">
              查看详情
            </el-button>
            <el-button 
              v-if="rental.remainingDays <= 60"
              type="primary" 
              size="small" 
              @click="renewRental(rental)"
            >
              申请续租
            </el-button>
            <el-button 
              size="small" 
              @click="viewContract(rental)"
            >
              查看合同
            </el-button>
          </div>
        </div>
      </div>
    </DataCard>

    <!-- 历史租赁 -->
    <DataCard title="历史租赁" class="history-rental-card">
      <el-table :data="historyRentals" v-loading="historyLoading">
        <el-table-column prop="boothNumber" label="摊位编号" width="120" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="duration" label="租期" width="80">
          <template #default="{ row }">
            {{ row.duration }}个月
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总费用" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getRentalStatusType(row.status)">
              {{ getRentalStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewRentalDetail(row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="pagination.current"
          :page-size="pagination.size"
          :total="pagination.total"
          layout="prev, pager, next"
          @current-change="fetchHistoryRentals"
        />
      </div>
    </DataCard>

    <!-- 租赁详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="租赁详情"
      width="700px"
      :before-close="handleCloseDetail"
    >
      <div v-if="currentRentalDetail" class="rental-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-section">
              <h4>摊位信息</h4>
              <el-descriptions :column="1" size="small">
                <el-descriptions-item label="摊位编号">
                  {{ currentRentalDetail.boothNumber }}
                </el-descriptions-item>
                <el-descriptions-item label="位置">
                  {{ currentRentalDetail.location }}
                </el-descriptions-item>
                <el-descriptions-item label="面积">
                  {{ currentRentalDetail.area }}㎡
                </el-descriptions-item>
                <el-descriptions-item label="月租金">
                  ¥{{ currentRentalDetail.monthlyRent }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-section">
              <h4>租赁信息</h4>
              <el-descriptions :column="1" size="small">
                <el-descriptions-item label="租赁期限">
                  {{ currentRentalDetail.startDate }} 至 {{ currentRentalDetail.endDate }}
                </el-descriptions-item>
                <el-descriptions-item label="租期">
                  {{ currentRentalDetail.duration }}个月
                </el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="getRentalStatusType(currentRentalDetail.status)">
                    {{ getRentalStatusText(currentRentalDetail.status) }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="总费用">
                  ¥{{ currentRentalDetail.totalAmount }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-col>
        </el-row>
        
        <div class="detail-section">
          <h4>费用明细</h4>
          <el-table :data="currentRentalDetail.feeDetails" size="small">
            <el-table-column prop="type" label="费用类型" />
            <el-table-column prop="amount" label="金额">
              <template #default="{ row }">
                ¥{{ row.amount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === 'paid' ? 'success' : 'warning'">
                  {{ row.status === 'paid' ? '已支付' : '待支付' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="payTime" label="支付时间" />
          </el-table>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 合同查看弹窗 -->
    <el-dialog
      v-model="contractVisible"
      title="租赁合同"
      width="800px"
      :before-close="handleCloseContract"
    >
      <div v-if="currentContract" class="contract-content">
        <div class="contract-header">
          <h3>跳蚤市场摊位租赁合同</h3>
          <p>合同编号：{{ currentContract.contractNumber }}</p>
        </div>
        
        <div class="contract-body">
          <div class="contract-section">
            <h4>甲方（出租方）</h4>
            <p>跳蚤市场管理方</p>
          </div>
          
          <div class="contract-section">
            <h4>乙方（承租方）</h4>
            <p>{{ currentContract.tenantName }}</p>
            <p>联系电话：{{ currentContract.tenantPhone }}</p>
          </div>
          
          <div class="contract-section">
            <h4>租赁标的</h4>
            <p>摊位编号：{{ currentContract.boothNumber }}</p>
            <p>摊位位置：{{ currentContract.location }}</p>
            <p>摊位面积：{{ currentContract.area }}平方米</p>
          </div>
          
          <div class="contract-section">
            <h4>租赁期限及费用</h4>
            <p>租赁期限：{{ currentContract.startDate }} 至 {{ currentContract.endDate }}</p>
            <p>月租金：{{ currentContract.monthlyRent }}元</p>
            <p>押金：{{ currentContract.deposit }}元</p>
            <p>总费用：{{ currentContract.totalAmount }}元</p>
          </div>
          
          <div class="contract-section">
            <h4>其他条款</h4>
            <p>1. 乙方应按时支付租金，不得拖欠。</p>
            <p>2. 乙方应遵守市场管理规定，保持摊位整洁。</p>
            <p>3. 租赁期满后，乙方应及时清理摊位。</p>
            <p>4. 违约责任按相关规定执行。</p>
          </div>
        </div>
        
        <div class="contract-footer">
          <p>签约时间：{{ currentContract.signTime }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="contractVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { getCurrentRental, getHistoryRental, getRentalDetail, renewRental as renewRentalApi } from '@/api/rental'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import DataCard from '@/components/DataCard.vue'

const userStore = useUserStore()
const historyLoading = ref(false)
const detailVisible = ref(false)
const contractVisible = ref(false)

const currentRentals = ref([])
const historyRentals = ref([])
const currentRentalDetail = ref(null)
const currentContract = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取当前租赁
const fetchCurrentRentals = async () => {
  try {
    const rentals = await getCurrentRental(userStore.user.id)
    // 计算剩余天数
    currentRentals.value = rentals.map(rental => ({
      ...rental,
      remainingDays: calculateRemainingDays(rental.endDate)
    }))
  } catch (error) {
    console.error('获取当前租赁失败:', error)
  }
}

// 获取历史租赁
const fetchHistoryRentals = async (page) => {
  try {
    historyLoading.value = true
    if (page) {
      pagination.current = page
    }
    const params = {
      current: pagination.current,
      size: pagination.size,
      userId: userStore.user.id
    }
    
    const result = await getHistoryRental(params)
    historyRentals.value = result.records
    pagination.total = result.total
  } catch (error) {
    console.error('获取历史租赁失败:', error)
  } finally {
    historyLoading.value = false
  }
}

// 计算剩余天数
const calculateRemainingDays = (endDate) => {
  const end = new Date(endDate)
  const now = new Date()
  const diffTime = end - now
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return Math.max(0, diffDays)
}

// 获取剩余天数样式类
const getRemainingDaysClass = (days) => {
  if (days <= 7) return 'text-danger'
  if (days <= 30) return 'text-warning'
  return 'text-success'
}

// 查看租赁详情
const viewRentalDetail = async (rental) => {
  try {
    const detail = await getRentalDetail(rental.id)
    // 模拟费用明细数据
    detail.feeDetails = [
      { type: '租金', amount: detail.monthlyRent * detail.duration, status: 'paid', payTime: detail.startDate },
      { type: '押金', amount: detail.deposit || 0, status: 'paid', payTime: detail.startDate }
    ]
    currentRentalDetail.value = detail
    detailVisible.value = true
  } catch (error) {
    console.error('获取租赁详情失败:', error)
  }
}

// 关闭详情弹窗
const handleCloseDetail = () => {
  detailVisible.value = false
  currentRentalDetail.value = null
}

// 申请续租
const renewRental = async (rental) => {
  try {
    await ElMessageBox.confirm(
      `确定要申请续租摊位 ${rental.boothNumber} 吗？`,
      '续租确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await renewRentalApi(rental.id)
    ElMessage.success('续租申请已提交，请等待审核')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('申请续租失败:', error)
    }
  }
}

// 查看合同
const viewContract = (rental) => {
  // 使用真实的租赁数据
  currentContract.value = {
    contractNumber: rental.contractNo || '-',
    tenantName: rental.tenantName || userStore.user.realName || userStore.user.username,
    tenantPhone: rental.contactPhone || userStore.user.phone || '-',
    boothNumber: rental.boothNumber || '-',
    location: rental.location || '-',
    area: rental.area || 0,
    startDate: rental.startDate || '-',
    endDate: rental.endDate || '-',
    monthlyRent: rental.monthlyRent || 0,
    deposit: rental.deposit || 0,
    totalAmount: rental.totalAmount || 0,
    signTime: rental.startDate || '-'
  }
  contractVisible.value = true
}

// 关闭合同弹窗
const handleCloseContract = () => {
  contractVisible.value = false
  currentContract.value = null
}

// 租赁状态相关方法
const getRentalStatusText = (status) => {
  const statusMap = {
    1: '租赁中',
    2: '已到期',
    3: '已终止'
  }
  return statusMap[status] || '未知'
}

const getRentalStatusType = (status) => {
  const typeMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return typeMap[status] || 'info'
}

onMounted(() => {
  fetchCurrentRentals()
  fetchHistoryRentals()
})
</script>

<style scoped>
.my-rental {
  padding: 0;
}

.current-rental-card {
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.rental-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.rental-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
}

.rental-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.booth-info h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.location {
  margin: 0;
  display: flex;
  align-items: center;
  color: #606266;
  font-size: 14px;
}

.location .el-icon {
  margin-right: 4px;
}

.rental-body {
  margin-bottom: 16px;
}

.rental-info {
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  color: #606266;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.text-danger {
  color: #f56c6c;
  font-weight: 600;
}

.text-warning {
  color: #e6a23c;
  font-weight: 600;
}

.text-success {
  color: #67c23a;
  font-weight: 600;
}

.countdown-alert {
  margin-top: 12px;
}

.rental-actions {
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.rental-detail {
  padding: 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 8px;
}

.contract-content {
  max-height: 500px;
  overflow-y: auto;
  padding: 20px;
  line-height: 1.6;
}

.contract-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e4e7ed;
}

.contract-header h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
  font-weight: 600;
}

.contract-section {
  margin-bottom: 20px;
}

.contract-section h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.contract-section p {
  margin: 5px 0;
  color: #606266;
}

.contract-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
  text-align: right;
}
</style>
