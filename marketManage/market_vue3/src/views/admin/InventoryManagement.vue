<template>
  <div class="inventory-management">
    <PageHeader 
      title="物资仓储管理" 
      description="管理市场物资的入库、出库和库存情况"
    >
      <template #extra>
        <el-button type="success" @click="handleInbound">
          <el-icon><Download /></el-icon>
          物资入库
        </el-button>
        <el-button type="warning" @click="handleOutbound">
          <el-icon><Upload /></el-icon>
          物资出库
        </el-button>
      </template>
    </PageHeader>

    <!-- 库存预警 -->
    <el-alert
      v-if="warningItems.length > 0"
      :title="`有 ${warningItems.length} 项物资库存不足，请及时补充`"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 20px"
    >
      <template #default>
        <div class="warning-list">
          <span v-for="(item, index) in warningItems" :key="index" class="warning-item">
            {{ item.itemName }}（剩余：{{ item.quantity }}{{ item.unit }}）
          </span>
        </div>
      </template>
    </el-alert>

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
            placeholder="请输入物资名称或编号"
            clearable
            style="width: 250px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select 
            v-model="form.category" 
            placeholder="请选择分类" 
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="清洁用品" value="清洁用品" />
            <el-option label="维修工具" value="维修工具" />
            <el-option label="办公用品" value="办公用品" />
            <el-option label="安全设备" value="安全设备" />
            <el-option label="维修材料" value="维修材料" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 物资列表 -->
    <DataCard>
      <el-table
        :data="inventoryList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="itemNo" label="物资编号" width="120" />
        <el-table-column prop="itemName" label="物资名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column label="当前库存" width="120">
          <template #default="{ row }">
            <span :class="row.quantity <= row.warningQuantity ? 'warning-text' : ''">
              {{ row.quantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="warningQuantity" label="预警数量" width="120" />
        <el-table-column label="库存状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.quantity > row.warningQuantity ? 'success' : 'warning'">
              {{ row.quantity > row.warningQuantity ? '充足' : '不足' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="storageLocation" label="存放位置" width="120" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewRecord(row)">
              出入库记录
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
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </DataCard>

    <!-- 入库对话框 -->
    <el-dialog
      v-model="inboundVisible"
      title="物资入库"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="inboundFormRef"
        :model="inboundForm"
        :rules="inboundRules"
        label-width="100px"
      >
        <el-form-item label="物资" prop="inventoryId">
          <el-select v-model="inboundForm.inventoryId" placeholder="请选择物资" style="width: 100%">
            <el-option
              v-for="item in inventoryList"
              :key="item.id"
              :label="`${item.itemName} (${item.itemNo})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="inboundForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input v-model="inboundForm.operator" placeholder="请输入操作人" />
        </el-form-item>
        <el-form-item label="原因说明" prop="reason">
          <el-input
            v-model="inboundForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入原因说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inboundVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitInbound">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 出库对话框 -->
    <el-dialog
      v-model="outboundVisible"
      title="物资出库"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="outboundFormRef"
        :model="outboundForm"
        :rules="outboundRules"
        label-width="100px"
      >
        <el-form-item label="物资" prop="inventoryId">
          <el-select v-model="outboundForm.inventoryId" placeholder="请选择物资" style="width: 100%">
            <el-option
              v-for="item in inventoryList"
              :key="item.id"
              :label="`${item.itemName} (${item.itemNo}) - 库存:${item.quantity}${item.unit}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="outboundForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input v-model="outboundForm.operator" placeholder="请输入操作人" />
        </el-form-item>
        <el-form-item label="原因说明" prop="reason">
          <el-input
            v-model="outboundForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入原因说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="outboundVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitOutbound">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 出入库记录对话框 -->
    <el-dialog
      v-model="recordVisible"
      :title="`${currentItem.itemName} - 出入库记录`"
      width="800px"
    >
      <el-table :data="recordList" stripe>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.recordType === 1 ? 'success' : 'warning'">
              {{ row.recordType === 1 ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="reason" label="原因说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.recordTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getInventoryList, inventoryInbound, inventoryOutbound, getInventoryWarning, getInventoryRecords } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Download, Upload } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataCard from '@/components/DataCard.vue'

const loading = ref(false)
const submitLoading = ref(false)
const inventoryList = ref([])
const warningItems = ref([])
const inboundVisible = ref(false)
const outboundVisible = ref(false)
const recordVisible = ref(false)
const currentItem = ref({})
const recordList = ref([])
const inboundFormRef = ref()
const outboundFormRef = ref()

const searchParams = reactive({
  keyword: '',
  category: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const inboundForm = reactive({
  inventoryId: null,
  quantity: 1,
  operator: '',
  reason: ''
})

const outboundForm = reactive({
  inventoryId: null,
  quantity: 1,
  operator: '',
  reason: ''
})

const inboundRules = {
  inventoryId: [
    { required: true, message: '请选择物资', trigger: 'change' }
  ],
  quantity: [
    { required: true, message: '请输入数量', trigger: 'blur' }
  ],
  operator: [
    { required: true, message: '请输入操作人', trigger: 'blur' }
  ]
}

const outboundRules = {
  inventoryId: [
    { required: true, message: '请选择物资', trigger: 'change' }
  ],
  quantity: [
    { required: true, message: '请输入数量', trigger: 'blur' }
  ],
  operator: [
    { required: true, message: '请输入操作人', trigger: 'blur' }
  ]
}

// 加载库存预警
const loadWarning = async () => {
  try {
    const data = await getInventoryWarning()
    warningItems.value = data || []
  } catch (error) {
    console.error('加载库存预警失败')
  }
}

// 加载物资列表
const loadInventoryList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchParams
    }
    const result = await getInventoryList(params)
    inventoryList.value = result.records || []
    pagination.total = result.total || 0
  } catch (error) {
    ElMessage.error('加载物资列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = (params) => {
  Object.assign(searchParams, params)
  pagination.current = 1
  loadInventoryList()
}

// 重置
const handleReset = () => {
  searchParams.keyword = ''
  searchParams.category = null
  pagination.current = 1
  loadInventoryList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadInventoryList()
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  loadInventoryList()
}

// 入库
const handleInbound = () => {
  resetInboundForm()
  inboundVisible.value = true
}

// 提交入库
const submitInbound = async () => {
  try {
    await inboundFormRef.value.validate()
    submitLoading.value = true
    
    await inventoryInbound(inboundForm)
    ElMessage.success('入库成功')
    
    inboundVisible.value = false
    loadInventoryList()
    loadWarning()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('入库失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 出库
const handleOutbound = () => {
  resetOutboundForm()
  outboundVisible.value = true
}

// 提交出库
const submitOutbound = async () => {
  try {
    await outboundFormRef.value.validate()
    submitLoading.value = true
    
    await inventoryOutbound(outboundForm)
    ElMessage.success('出库成功')
    
    outboundVisible.value = false
    loadInventoryList()
    loadWarning()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('出库失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 查看出入库记录
const handleViewRecord = async (row) => {
  try {
    currentItem.value = row
    recordVisible.value = true
    
    // 调用API获取出入库记录
    const records = await getInventoryRecords(row.id)
    recordList.value = records || []
  } catch (error) {
    console.error('获取出入库记录失败:', error)
    ElMessage.error('获取出入库记录失败')
    recordList.value = []
  }
}

// 重置入库表单
const resetInboundForm = () => {
  inboundForm.inventoryId = null
  inboundForm.quantity = 1
  inboundForm.operator = ''
  inboundForm.reason = ''
  inboundFormRef.value?.clearValidate()
}

// 重置出库表单
const resetOutboundForm = () => {
  outboundForm.inventoryId = null
  outboundForm.quantity = 1
  outboundForm.operator = ''
  outboundForm.reason = ''
  outboundFormRef.value?.clearValidate()
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 19)
}

// 初始化
onMounted(() => {
  loadInventoryList()
  loadWarning()
})
</script>

<style scoped>
.inventory-management {
  padding: 0;
}

.warning-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
}

.warning-item {
  background-color: #fef0f0;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  color: #f56c6c;
}

.warning-text {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>

