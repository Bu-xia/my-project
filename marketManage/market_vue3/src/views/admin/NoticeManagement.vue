<template>
  <div class="notice-management">
    <PageHeader 
      title="公告通知管理" 
      description="管理系统公告和通知信息"
    >
      <template #extra>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          发布公告
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
            placeholder="请输入标题关键词"
            clearable
            style="width: 250px"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select 
            v-model="form.noticeType" 
            placeholder="请选择类型" 
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="系统公告" :value="1" />
            <el-option label="活动通知" :value="2" />
            <el-option label="紧急通知" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="form.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 公告列表 -->
    <DataCard>
      <el-table
        :data="noticeList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.noticeType)">
              {{ getTypeText(row.noticeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isTop === 1" type="warning" size="small">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="publisher" label="发布人" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              查看
            </el-button>
            <el-button link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              link 
              :type="row.isTop === 1 ? 'warning' : 'success'" 
              @click="handleTop(row)"
            >
              {{ row.isTop === 1 ? '取消置顶' : '置顶' }}
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
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="noticeFormRef"
        :model="noticeForm"
        :rules="noticeRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="类型" prop="noticeType">
          <el-radio-group v-model="noticeForm.noticeType">
            <el-radio :label="1">系统公告</el-radio>
            <el-radio :label="2">活动通知</el-radio>
            <el-radio :label="3">紧急通知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="noticeForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="发布人" prop="publisher">
          <el-input v-model="noticeForm.publisher" placeholder="请输入发布人" />
        </el-form-item>
        <el-form-item label="是否置顶" prop="isTop">
          <el-switch v-model="noticeForm.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="noticeForm.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
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
      title="公告详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentNotice.id }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="getTypeTag(currentNotice.noticeType)">
            {{ getTypeText(currentNotice.noticeType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentNotice.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">
          <div style="white-space: pre-wrap;">{{ currentNotice.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">{{ currentNotice.publisher }}</el-descriptions-item>
        <el-descriptions-item label="浏览量">{{ currentNotice.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="是否置顶">
          <el-tag v-if="currentNotice.isTop === 1" type="warning">置顶</el-tag>
          <span v-else>否</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentNotice.status === 1 ? 'success' : 'info'">
            {{ currentNotice.status === 1 ? '已发布' : '已下架' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="2">
          {{ formatDateTime(currentNotice.publishTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { getNoticeList, addNotice, updateNotice, deleteNotice, topNotice } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataCard from '@/components/DataCard.vue'

const loading = ref(false)
const submitLoading = ref(false)
const noticeList = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const currentNotice = ref({})
const noticeFormRef = ref()

const searchParams = reactive({
  keyword: '',
  noticeType: null,
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const noticeForm = reactive({
  id: null,
  title: '',
  content: '',
  noticeType: 1,
  isTop: 0,
  publisher: '',
  status: 1
})

const noticeRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入发布人', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => isEdit.value ? '编辑公告' : '发布公告')

// 获取类型文本
const getTypeText = (type) => {
  const typeMap = {
    1: '系统公告',
    2: '活动通知',
    3: '紧急通知'
  }
  return typeMap[type] || '-'
}

// 获取类型标签
const getTypeTag = (type) => {
  const tagMap = {
    1: 'primary',
    2: 'success',
    3: 'danger'
  }
  return tagMap[type] || 'info'
}

// 加载公告列表
const loadNoticeList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchParams
    }
    const result = await getNoticeList(params)
    noticeList.value = result.records || []
    pagination.total = result.total || 0
  } catch (error) {
    ElMessage.error('加载公告列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = (params) => {
  Object.assign(searchParams, params)
  pagination.current = 1
  loadNoticeList()
}

// 重置
const handleReset = () => {
  searchParams.keyword = ''
  searchParams.noticeType = null
  searchParams.status = null
  pagination.current = 1
  loadNoticeList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadNoticeList()
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  loadNoticeList()
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
  Object.assign(noticeForm, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  currentNotice.value = row
  detailVisible.value = true
}

// 置顶/取消置顶
const handleTop = async (row) => {
  try {
    await topNotice(row.id)
    ElMessage.success(row.isTop === 1 ? '取消置顶成功' : '置顶成功')
    loadNoticeList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNotice(row.id)
      ElMessage.success('删除成功')
      loadNoticeList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  try {
    await noticeFormRef.value.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateNotice(noticeForm)
      ElMessage.success('更新成功')
    } else {
      await addNotice(noticeForm)
      ElMessage.success('发布成功')
    }
    
    dialogVisible.value = false
    loadNoticeList()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '更新失败' : '发布失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  noticeForm.id = null
  noticeForm.title = ''
  noticeForm.content = ''
  noticeForm.noticeType = 1
  noticeForm.isTop = 0
  noticeForm.publisher = ''
  noticeForm.status = 1
  noticeFormRef.value?.clearValidate()
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 19)
}

// 初始化
loadNoticeList()
</script>

<style scoped>
.notice-management {
  padding: 0;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>

