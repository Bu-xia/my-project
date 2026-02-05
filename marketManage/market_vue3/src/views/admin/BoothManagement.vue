<template>
  <div class="booth-management">
    <PageHeader 
      title="摊位管理" 
      description="管理市场所有摊位的基本信息"
    >
      <template #extra>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增摊位
        </el-button>
      </template>
    </PageHeader>

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
            placeholder="请输入摊位编号或名称"
            clearable
            style="width: 250px"
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
            <el-option label="可租" :value="0" />
            <el-option label="已租" :value="1" />
            <el-option label="维修中" :value="2" />
          </el-select>
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 摊位列表 -->
    <DataCard>
      <el-table
        :data="boothList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="boothNo" label="摊位编号" width="120" />
        <el-table-column prop="boothName" label="摊位名称" min-width="150" />
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column label="面积(㎡)" width="100">
          <template #default="{ row }">
            {{ row.area }}
          </template>
        </el-table-column>
        <el-table-column label="租金(元/月)" width="120">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              查看
            </el-button>
            <el-button link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
              删除
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      top="5vh"
      :close-on-click-modal="false"
    >
      <el-form
        ref="boothFormRef"
        :model="boothForm"
        :rules="boothRules"
        label-width="100px"
      >
        <el-form-item label="摊位编号" prop="boothNo">
          <el-input v-model="boothForm.boothNo" placeholder="请输入摊位编号" />
        </el-form-item>
        <el-form-item label="摊位名称" prop="boothName">
          <el-input v-model="boothForm.boothName" placeholder="请输入摊位名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="boothForm.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="面积(㎡)" prop="area">
          <el-input-number 
            v-model="boothForm.area" 
            :min="0" 
            :precision="2"
            :controls="false"
            placeholder="请输入面积"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="租金(元/月)" prop="price">
          <el-input-number 
            v-model="boothForm.price" 
            :min="0" 
            :precision="2"
            :controls="false"
            placeholder="请输入租金"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="boothForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="可租" :value="0" />
            <el-option label="已租" :value="1" />
            <el-option label="维修中" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片URL" prop="images">
          <el-input 
            v-model="boothForm.images" 
            placeholder="请输入图片URL"
            type="textarea"
            :rows="2"
          />
          <div v-if="boothForm.images" class="image-preview">
            <el-image 
              :src="boothForm.images"
              :preview-src-list="[boothForm.images]"
              fit="cover"
              style="width: 120px; height: 80px; border-radius: 6px; cursor: pointer; margin-top: 8px;"
              alt="摊位图片预览"
            >
              <template #error>
                <div class="preview-error">
                  <el-icon><Picture /></el-icon>
                  <span style="font-size: 12px;">预览失败</span>
                </div>
              </template>
            </el-image>
          </div>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="boothForm.description" 
            type="textarea"
            :rows="3"
            placeholder="请输入摊位描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="摊位详情"
      width="600px"
      top="5vh"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentBooth.id }}</el-descriptions-item>
        <el-descriptions-item label="摊位编号">{{ currentBooth.boothNo }}</el-descriptions-item>
        <el-descriptions-item label="摊位名称">{{ currentBooth.boothName }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ currentBooth.location }}</el-descriptions-item>
        <el-descriptions-item label="面积">{{ currentBooth.area }} ㎡</el-descriptions-item>
        <el-descriptions-item label="租金">¥{{ currentBooth.price }}/月</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentBooth.status)">
            {{ getStatusText(currentBooth.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="摊位图片" :span="2">
          <div v-if="currentBooth.images" class="booth-image-container">
            <el-image 
              :src="currentBooth.images"
              :preview-src-list="[currentBooth.images]"
              fit="cover"
              style="width: 200px; height: 150px; border-radius: 8px; cursor: pointer;"
              :alt="currentBooth.boothName"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
            <div class="image-tip">点击图片可查看大图</div>
          </div>
          <span v-else class="no-image">暂无图片</span>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">
          {{ currentBooth.description || '暂无描述' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ formatDateTime(currentBooth.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">
          {{ formatDateTime(currentBooth.updateTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { getBoothList, getBoothDetail, addBooth, updateBooth, deleteBooth } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataCard from '@/components/DataCard.vue'

const loading = ref(false)
const submitLoading = ref(false)
const boothList = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const currentBooth = ref({})
const boothFormRef = ref()

const searchParams = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const boothForm = reactive({
  id: null,
  boothNo: '',
  boothName: '',
  location: '',
  area: null,
  price: null,
  status: 0,
  images: '',
  description: ''
})

const boothRules = {
  boothNo: [
    { required: true, message: '请输入摊位编号', trigger: 'blur' }
  ],
  boothName: [
    { required: true, message: '请输入摊位名称', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入位置', trigger: 'blur' }
  ],
  area: [
    { required: true, message: '请输入面积', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入租金', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => isEdit.value ? '编辑摊位' : '新增摊位')

// 获取状态类型
const getStatusType = (status) => {
  const types = { 0: 'success', 1: 'warning', 2: 'danger' }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = { 0: '可租', 1: '已租', 2: '维修中' }
  return texts[status] || '未知'
}

// 加载摊位列表
const loadBoothList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchParams
    }
    const result = await getBoothList(params)
    boothList.value = result.records || []
    pagination.total = result.total || 0
  } catch (error) {
    ElMessage.error('加载摊位列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = (params) => {
  Object.assign(searchParams, params)
  pagination.current = 1
  loadBoothList()
}

// 重置
const handleReset = () => {
  searchParams.keyword = ''
  searchParams.status = null
  pagination.current = 1
  loadBoothList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadBoothList()
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  loadBoothList()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(boothForm, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = async (row) => {
  try {
    currentBooth.value = await getBoothDetail(row.id)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取摊位详情失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该摊位吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBooth(row.id)
      ElMessage.success('删除成功')
      loadBoothList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  try {
    await boothFormRef.value.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateBooth(boothForm)
      ElMessage.success('更新成功')
    } else {
      await addBooth(boothForm)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadBoothList()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  boothForm.id = null
  boothForm.boothNo = ''
  boothForm.boothName = ''
  boothForm.location = ''
  boothForm.area = null
  boothForm.price = null
  boothForm.status = 0
  boothForm.images = ''
  boothForm.description = ''
  boothFormRef.value?.clearValidate()
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 19)
}

// 初始化
loadBoothList()
</script>

<style scoped>
.booth-management {
  padding: 0;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.booth-image-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.image-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.no-image {
  color: #999;
  font-style: italic;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 200px;
  height: 150px;
  background-color: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  color: #c0c4cc;
  font-size: 14px;
}

.image-error .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.image-preview {
  margin-top: 8px;
}

.preview-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 80px;
  background-color: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  color: #c0c4cc;
}

.preview-error .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}
</style>

