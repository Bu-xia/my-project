<template>
  <div class="payment">
    <PageHeader 
      title="费用支付" 
      description="查看账单信息，完成在线支付"
    />

    <!-- 账单概览 -->
    <div class="payment-overview">
      <el-row :gutter="20">
        <el-col :span="8">
          <DataCard class="overview-card">
            <div class="overview-item">
              <div class="overview-icon unpaid">
                <el-icon><Money /></el-icon>
              </div>
              <div class="overview-content">
                <div class="overview-value">¥{{ overviewData.unpaidAmount }}</div>
                <div class="overview-label">待支付金额</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        <el-col :span="8">
          <DataCard class="overview-card">
            <div class="overview-item">
              <div class="overview-icon paid">
                <el-icon><SuccessFilled /></el-icon>
              </div>
              <div class="overview-content">
                <div class="overview-value">¥{{ overviewData.paidAmount }}</div>
                <div class="overview-label">已支付金额</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        <el-col :span="8">
          <DataCard class="overview-card">
            <div class="overview-item">
              <div class="overview-icon total">
                <el-icon><Wallet /></el-icon>
              </div>
              <div class="overview-content">
                <div class="overview-value">{{ overviewData.unpaidCount }}</div>
                <div class="overview-label">待支付账单</div>
              </div>
            </div>
          </DataCard>
        </el-col>
      </el-row>
    </div>

    <!-- 账单列表 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 待支付账单 -->
      <el-tab-pane label="待支付账单" name="unpaid">
        <DataCard>
          <el-table
            :data="unpaidPayments"
            v-loading="unpaidLoading"
          >
            <el-table-column prop="paymentNo" label="账单编号" width="200" />
            <el-table-column prop="paymentType" label="费用类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getPaymentTypeColor(row.paymentType)">
                  {{ getPaymentTypeText(row.paymentType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="boothNo" label="摊位编号" width="120" />
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                <span class="amount-text">¥{{ row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="生成时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <div style="display: flex; gap: 8px;">
                  <el-button
                    type="primary"
                    size="small"
                    @click="payBill(row)"
                  >
                    立即支付
                  </el-button>
                  <el-button
                    size="small"
                    @click="viewPaymentDetail(row)"
                  >
                    详情
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-wrapper">
            <el-pagination
              :current-page="unpaidPagination.current"
              :page-size="unpaidPagination.size"
              :total="unpaidPagination.total"
              layout="prev, pager, next"
              @current-change="fetchUnpaidPayments"
            />
          </div>
        </DataCard>
      </el-tab-pane>

      <!-- 已支付账单 -->
      <el-tab-pane label="已支付账单" name="paid">
        <DataCard>
          <el-table :data="paidPayments" v-loading="paidLoading">
            <el-table-column prop="paymentNo" label="账单编号" width="200" />
            <el-table-column prop="paymentType" label="费用类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getPaymentTypeColor(row.paymentType)">
                  {{ getPaymentTypeText(row.paymentType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="boothNo" label="摊位编号" width="120" />
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                <span class="amount-text">¥{{ row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="paymentMethod" label="支付方式" width="100" />
            <el-table-column prop="paymentTime" label="支付时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.paymentTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button
                  size="small"
                  @click="viewPaymentDetail(row)"
                >
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-wrapper">
            <el-pagination
              :current-page="paidPagination.current"
              :page-size="paidPagination.size"
              :total="paidPagination.total"
              layout="prev, pager, next"
              @current-change="fetchPaidPayments"
            />
          </div>
        </DataCard>
      </el-tab-pane>
    </el-tabs>

    <!-- 支付弹窗 -->
    <el-dialog
      v-model="paymentVisible"
      title="选择支付方式"
      width="500px"
      :before-close="handleClosePayment"
    >
      <div v-if="currentPayment" class="payment-dialog">
        <div class="payment-info">
          <h4>支付信息</h4>
          <div class="payment-details">
            <div class="detail-item">
              <span>账单编号：</span>
              <span>{{ currentPayment.paymentNo }}</span>
            </div>
            <div class="detail-item">
              <span>费用类型：</span>
              <span>{{ getPaymentTypeText(currentPayment.paymentType) }}</span>
            </div>
            <div class="detail-item">
              <span>摊位编号：</span>
              <span>{{ currentPayment.boothNo }}</span>
            </div>
            <div class="detail-item total">
              <span>支付金额：</span>
              <span class="amount">¥{{ getTotalAmount() }}</span>
            </div>
          </div>
        </div>
        
        <div class="payment-methods">
          <h4>选择支付方式</h4>
          <el-radio-group v-model="selectedPaymentMethod" class="method-group">
            <div class="method-item">
              <el-radio label="alipay">
                <div class="method-content">
                  <div class="method-icon alipay">
                    <el-icon><Money /></el-icon>
                  </div>
                  <div class="method-info">
                    <div class="method-name">支付宝</div>
                    <div class="method-desc">推荐使用支付宝快捷支付</div>
                  </div>
                </div>
              </el-radio>
            </div>
            <div class="method-item">
              <el-radio label="wechat">
                <div class="method-content">
                  <div class="method-icon wechat">
                    <el-icon><ChatDotSquare /></el-icon>
                  </div>
                  <div class="method-info">
                    <div class="method-name">微信支付</div>
                    <div class="method-desc">使用微信扫码支付</div>
                  </div>
                </div>
              </el-radio>
            </div>
            <div class="method-item">
              <el-radio label="bank">
                <div class="method-content">
                  <div class="method-icon bank">
                    <el-icon><CreditCard /></el-icon>
                  </div>
                  <div class="method-info">
                    <div class="method-name">银行卡</div>
                    <div class="method-desc">支持各大银行储蓄卡</div>
                  </div>
                </div>
              </el-radio>
            </div>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="paymentVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="payLoading"
          @click="confirmPayment"
          :disabled="!selectedPaymentMethod"
        >
          确认支付
        </el-button>
      </template>
    </el-dialog>

    <!-- 账单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="账单详情"
      width="600px"
      :before-close="handleCloseDetail"
    >
      <div v-if="currentPaymentDetail" class="payment-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="账单编号" :span="2">
            {{ currentPaymentDetail.paymentNo }}
          </el-descriptions-item>
          <el-descriptions-item label="费用类型">
            <el-tag :type="getPaymentTypeColor(currentPaymentDetail.paymentType)">
              {{ getPaymentTypeText(currentPaymentDetail.paymentType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="摊位编号">
            {{ currentPaymentDetail.boothNo }}
          </el-descriptions-item>
          <el-descriptions-item label="金额">
            ¥{{ currentPaymentDetail.amount }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentPaymentDetail.status === 1 ? 'success' : 'warning'">
              {{ currentPaymentDetail.status === 1 ? '已支付' : '待支付' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="生成时间">
            {{ formatDateTime(currentPaymentDetail.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="金额">
            ¥{{ currentPaymentDetail.amount }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentPaymentDetail.paymentTime" label="支付时间">
            {{ formatDateTime(currentPaymentDetail.paymentTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentPaymentDetail.paymentMethod" label="支付方式">
            {{ currentPaymentDetail.paymentMethod }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ currentPaymentDetail.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { getUnpaidPayments, getPaidPayments, payBill as payBillApi, getPaymentDetail } from '@/api/payment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Money, SuccessFilled, Wallet, ChatDotSquare, CreditCard } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import DataCard from '@/components/DataCard.vue'

const userStore = useUserStore()
const activeTab = ref('unpaid')
const paymentVisible = ref(false)
const detailVisible = ref(false)
const unpaidLoading = ref(false)
const paidLoading = ref(false)
const payLoading = ref(false)

const selectedPaymentMethod = ref('')
const currentPayment = ref(null)
const currentPaymentDetail = ref(null)

const unpaidPayments = ref([])
const paidPayments = ref([])

const overviewData = reactive({
  unpaidAmount: 0,
  paidAmount: 0,
  unpaidCount: 0
})

const unpaidPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const paidPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取待支付账单
const fetchUnpaidPayments = async (page) => {
  try {
    unpaidLoading.value = true
    if (page) {
      unpaidPagination.current = page
    }
    const params = {
      current: unpaidPagination.current,
      size: unpaidPagination.size,
      userId: userStore.user.id
    }
    
    const result = await getUnpaidPayments(params)
    unpaidPayments.value = result.records
    unpaidPagination.total = result.total
    
    // 更新概览数据
    overviewData.unpaidAmount = unpaidPayments.value.reduce((sum, item) => sum + item.amount, 0)
    overviewData.unpaidCount = unpaidPayments.value.length
  } catch (error) {
    console.error('获取待支付账单失败:', error)
  } finally {
    unpaidLoading.value = false
  }
}

// 获取已支付账单
const fetchPaidPayments = async (page) => {
  try {
    paidLoading.value = true
    if (page) {
      paidPagination.current = page
    }
    const params = {
      current: paidPagination.current,
      size: paidPagination.size,
      userId: userStore.user.id
    }
    
    const result = await getPaidPayments(params)
    paidPayments.value = result.records
    paidPagination.total = result.total
    
    // 更新概览数据
    overviewData.paidAmount = paidPayments.value.reduce((sum, item) => sum + item.amount, 0)
  } catch (error) {
    console.error('获取已支付账单失败:', error)
  } finally {
    paidLoading.value = false
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  if (tabName === 'unpaid') {
    fetchUnpaidPayments()
  } else {
    fetchPaidPayments()
  }
}

// 单个支付
const payBill = (payment) => {
  currentPayment.value = payment
  selectedPaymentMethod.value = ''
  paymentVisible.value = true
}

// 确认支付
const confirmPayment = async () => {
  try {
    payLoading.value = true
    
    const paymentMethodMap = {
      alipay: '支付宝',
      wechat: '微信支付',
      bank: '银行卡'
    }
    
    await payBillApi(currentPayment.value.id, selectedPaymentMethod.value)
    
    ElMessage.success(`使用${paymentMethodMap[selectedPaymentMethod.value]}支付成功`)
    paymentVisible.value = false
    
    // 刷新列表
    fetchUnpaidPayments()
    fetchPaidPayments()
  } catch (error) {
    console.error('支付失败:', error)
  } finally {
    payLoading.value = false
  }
}

// 关闭支付弹窗
const handleClosePayment = () => {
  paymentVisible.value = false
  currentPayment.value = null
  selectedPaymentMethod.value = ''
}

// 查看账单详情
const viewPaymentDetail = async (payment) => {
  try {
    const detail = await getPaymentDetail(payment.id)
    currentPaymentDetail.value = detail
    detailVisible.value = true
  } catch (error) {
    console.error('获取账单详情失败:', error)
  }
}

// 关闭详情弹窗
const handleCloseDetail = () => {
  detailVisible.value = false
  currentPaymentDetail.value = null
}

// 计算总金额
const getTotalAmount = () => {
  if (Array.isArray(currentPayment.value)) {
    return currentPayment.value.reduce((sum, item) => sum + item.amount, 0)
  }
  return currentPayment.value?.amount || 0
}

// 获取支付类型文本
const getPaymentTypeText = (type) => {
  const typeMap = {
    1: '租金',
    2: '押金',
    3: '水电费',
    4: '管理费',
    5: '违约金'
  }
  return typeMap[type] || '其他'
}

// 获取支付类型颜色
const getPaymentTypeColor = (type) => {
  const colorMap = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'info',
    5: 'danger'
  }
  return colorMap[type] || 'default'
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
  fetchUnpaidPayments()
})
</script>

<style scoped>
.payment {
  padding: 0;
}

.payment-overview {
  margin-bottom: 20px;
}

.overview-card {
  height: 100px;
}

.overview-item {
  display: flex;
  align-items: center;
  height: 100%;
}

.overview-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.overview-icon.unpaid {
  background: #f56c6c;
}

.overview-icon.paid {
  background: #67c23a;
}

.overview-icon.total {
  background: #409eff;
}

.overview-content {
  flex: 1;
}

.overview-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.overview-label {
  font-size: 14px;
  color: #606266;
}

.table-toolbar {
  margin-bottom: 16px;
}

.amount-text {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.payment-dialog {
  padding: 0;
}

.payment-info {
  margin-bottom: 24px;
}

.payment-info h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.payment-details {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-item.total {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
  font-weight: 600;
}

.detail-item .amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
}

.payment-methods h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.method-group {
  width: 100%;
}

.method-item {
  margin-bottom: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
}

.method-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.method-content {
  display: flex;
  align-items: center;
  margin-left: 24px;
}

.method-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 20px;
  color: white;
}

.method-icon.alipay {
  background: #1677ff;
}

.method-icon.wechat {
  background: #07c160;
}

.method-icon.bank {
  background: #722ed1;
}

.method-info {
  flex: 1;
}

.method-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.method-desc {
  font-size: 12px;
  color: #909399;
}

.payment-detail {
  padding: 0;
}
</style>
