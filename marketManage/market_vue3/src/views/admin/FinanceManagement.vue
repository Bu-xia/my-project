<template>
  <div class="finance-management">
    <PageHeader 
      title="费用收支管理" 
      description="管理市场的所有收支记录和统计"
    />

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="24">
        <el-col :xs="12" :sm="12" :md="8" :lg="8">
          <DataCard hover>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">¥{{ (statistics.totalIncome || 0).toLocaleString() }}</div>
                <div class="stat-label">总收入</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        
        <el-col :xs="12" :sm="12" :md="8" :lg="8">
          <DataCard hover>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
                <el-icon><Sell /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">¥{{ (statistics.totalExpense || 0).toLocaleString() }}</div>
                <div class="stat-label">总支出</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        
        <el-col :xs="12" :sm="12" :md="8" :lg="8">
          <DataCard hover>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                <el-icon><Wallet /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">¥{{ (statistics.balance || 0).toLocaleString() }}</div>
                <div class="stat-label">净收益</div>
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
        <el-form-item label="记录类型">
          <el-select 
            v-model="form.recordType" 
            placeholder="请选择类型" 
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="收入" :value="1" />
            <el-option label="支出" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-input
            v-model="form.category"
            placeholder="请输入分类"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 280px"
          />
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 收支列表 -->
    <DataCard>
      <el-table
        :data="financeList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :summary-method="getSummaries"
        show-summary
      >
        <el-table-column prop="recordNo" label="记录编号" width="120" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.recordType === 1 ? 'success' : 'danger'">
              {{ row.recordType === 1 ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column label="金额" width="400" align="right">
          <template #default="{ row }">
            <span :class="row.recordType === 1 ? 'income-amount' : 'expense-amount'">
              {{ row.recordType === 1 ? '+' : '-' }}¥{{ row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="relatedPerson" label="相关人员" width="100" />
        <el-table-column prop="description" label="说明" min-width="150" show-overflow-tooltip />
        <el-table-column prop="recordDate" label="记录日期" width="100" />
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
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
          @size-change="loadFinanceList"
          @current-change="loadFinanceList"
        />
      </div>
    </DataCard>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFinanceList, getFinanceStatistics } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { TrendCharts, Sell, Wallet } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataCard from '@/components/DataCard.vue'

const loading = ref(false)
const financeList = ref([])
const statistics = ref({})

const searchParams = reactive({
  recordType: null,
  category: '',
  dateRange: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    statistics.value = await getFinanceStatistics()
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

// 加载收支列表
const loadFinanceList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      recordType: searchParams.recordType,
      category: searchParams.category
    }
    
    if (searchParams.dateRange && searchParams.dateRange.length === 2) {
      params.startDate = searchParams.dateRange[0].toISOString().split('T')[0]
      params.endDate = searchParams.dateRange[1].toISOString().split('T')[0]
    }
    
    const result = await getFinanceList(params)
    financeList.value = result.records || []
    pagination.total = result.total || 0
  } catch (error) {
    ElMessage.error('加载收支列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = (params) => {
  Object.assign(searchParams, params)
  pagination.current = 1
  loadFinanceList()
}

// 重置
const handleReset = () => {
  searchParams.recordType = null
  searchParams.category = ''
  searchParams.dateRange = null
  pagination.current = 1
  loadFinanceList()
}


// 合计行
const getSummaries = (param) => {
  const { columns, data } = param
  const sums = []
  
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    
    if (index === 3) { // 金额列
      const income = data.filter(item => item.recordType === 1).reduce((sum, item) => sum + Number(item.amount), 0)
      const expense = data.filter(item => item.recordType === 2).reduce((sum, item) => sum + Number(item.amount), 0)
      const balance = income - expense
      sums[index] = `收入: ¥${income.toLocaleString()}\u00A0\u00A0\u00A0\u00A0支出: ¥${expense.toLocaleString()}\u00A0\u00A0\u00A0\u00A0净收益: ¥${balance.toLocaleString()}`
    } else {
      sums[index] = ''
    }
  })
  
  return sums
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 19)
}

// 初始化
onMounted(() => {
  loadStatistics()
  loadFinanceList()
})
</script>

<style scoped>
.finance-management {
  padding: 0;
}

.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon .el-icon {
  font-size: 32px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.income-amount {
  color: #67c23a;
  font-weight: bold;
}

.expense-amount {
  color: #f56c6c;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stat-card {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin-right: 0;
    margin-bottom: 12px;
  }
}

/* 自定义合计行样式 */
:deep(.el-table__footer-wrapper .el-table__footer .cell) {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
  white-space: nowrap;
  overflow: visible;
  min-width: 400px;
  text-align: right;
  padding: 0 8px;
}

:deep(.el-table .el-table__footer-wrapper tbody td) {
  border-top: 2px solid #EBEEF5;
  background-color: #FAFAFA;
  overflow: visible;
}
</style>

