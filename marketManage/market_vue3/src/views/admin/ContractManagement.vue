<template>
  <div class="contract-management">
    <PageHeader 
      title="合同管理" 
      description="管理所有租赁合同信息和状态"
    />

    <!-- 搜索表单 -->
    <SearchForm
      :default-values="searchParams"
      :loading="loading"
      @search="handleSearch"
      @reset="handleReset"
    >
      <template #default="{ form }">
        <el-form-item label="合同编号">
          <el-input
            v-model="form.contractNo"
            placeholder="请输入合同编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="form.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
            <el-option label="已归档" :value="2" />
            <el-option label="已终止" :value="3" />
          </el-select>
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 合同列表 -->
    <DataCard>
      <el-table
        :data="contractList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="contractNo" label="合同编号" width="140" />
        <el-table-column prop="tenantName" label="租户名称" min-width="150" />
        <el-table-column prop="boothNo" label="摊位编号" width="120" />
        <el-table-column label="租赁期限" min-width="200">
          <template #default="{ row }">
            {{ row.startDate }} 至 {{ row.endDate }}
          </template>
        </el-table-column>
        <el-table-column label="月租金" width="120">
          <template #default="{ row }">
            ¥{{ row.monthlyRent }}
          </template>
        </el-table-column>
        <el-table-column label="押金" width="120">
          <template #default="{ row }">
            ¥{{ row.deposit }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              查看
            </el-button>
            <el-button 
              link 
              type="primary" 
              @click="handleUpdateStatus(row)"
              v-if="row.status === 0"
            >
              审核
            </el-button>
            <el-button 
              link 
              type="warning" 
              @click="handleArchive(row)"
              v-if="row.status === 1"
            >
              归档
            </el-button>
            <el-button 
              link 
              type="danger" 
              @click="handleTerminate(row)"
              v-if="row.status === 1"
            >
              终止
            </el-button>
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
          @size-change="loadContractList"
          @current-change="loadContractList"
        />
      </div>
    </DataCard>

    <!-- 合同详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="合同详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ currentContract.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentContract.status)">
            {{ getStatusText(currentContract.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="租户名称">{{ currentContract.tenantName }}</el-descriptions-item>
        <el-descriptions-item label="摊位编号">{{ currentContract.boothNo }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ currentContract.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ currentContract.endDate }}</el-descriptions-item>
        <el-descriptions-item label="月租金">¥{{ currentContract.monthlyRent }}</el-descriptions-item>
        <el-descriptions-item label="押金">¥{{ currentContract.deposit }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentContract.remark || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ formatDateTime(currentContract.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">
          {{ formatDateTime(currentContract.updateTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditVisible"
      title="审核合同"
      width="500px"
    >
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="3">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="auditForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { getContractList, getContractDetail, updateContractStatus } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataCard from '@/components/DataCard.vue'

const loading = ref(false)
const contractList = ref([])
const detailVisible = ref(false)
const auditVisible = ref(false)
const currentContract = ref({})

const searchParams = reactive({
  contractNo: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const auditForm = reactive({
  id: null,
  status: 1,
  remark: ''
})

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已审核',
    2: '已归档',
    3: '已终止'
  }
  return statusMap[status] || '-'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'danger'
  }
  return typeMap[status] || 'info'
}

// 加载合同列表
const loadContractList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchParams
    }
    const result = await getContractList(params)
    contractList.value = result.records || []
    pagination.total = result.total || 0
  } catch (error) {
    ElMessage.error('加载合同列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = (params) => {
  Object.assign(searchParams, params)
  pagination.current = 1
  loadContractList()
}

// 重置
const handleReset = () => {
  searchParams.contractNo = ''
  searchParams.status = null
  pagination.current = 1
  loadContractList()
}

// 查看详情
const handleView = async (row) => {
  try {
    currentContract.value = await getContractDetail(row.id)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取合同详情失败')
  }
}

// 审核
const handleUpdateStatus = (row) => {
  currentContract.value = row
  auditForm.id = row.id
  auditForm.status = 1
  auditForm.remark = ''
  auditVisible.value = true
}

// 提交审核
const submitAudit = async () => {
  try {
    await updateContractStatus(auditForm)
    ElMessage.success('审核成功')
    auditVisible.value = false
    loadContractList()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 归档
const handleArchive = (row) => {
  ElMessageBox.confirm('确定要归档该合同吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateContractStatus({ id: row.id, status: 2, remark: '系统归档' })
      ElMessage.success('归档成功')
      loadContractList()
    } catch (error) {
      ElMessage.error('归档失败')
    }
  }).catch(() => {})
}

// 终止
const handleTerminate = (row) => {
  ElMessageBox.confirm('确定要终止该合同吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateContractStatus({ id: row.id, status: 3, remark: '合同终止' })
      ElMessage.success('终止成功')
      loadContractList()
    } catch (error) {
      ElMessage.error('终止失败')
    }
  }).catch(() => {})
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 19)
}

// 初始化
loadContractList()
</script>

<style scoped>
.contract-management {
  padding: 0;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>

