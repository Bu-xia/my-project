<template>
  <div class="tenant-management">
    <PageHeader 
      title="租户管理" 
      description="管理市场所有租户的基本信息"
    >
      <template #extra>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增租户
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
            placeholder="请输入商户名称或联系人"
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
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 租户列表 -->
    <DataCard>
      <el-table
        :data="tenantList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="businessName" label="商户名称" min-width="150" />
        <el-table-column prop="businessType" label="经营类型" width="120" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
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
      :close-on-click-modal="false"
    >
      <el-form
        ref="tenantFormRef"
        :model="tenantForm"
        :rules="tenantRules"
        label-width="100px"
      >
        <el-form-item label="商户名称" prop="businessName">
          <el-input v-model="tenantForm.businessName" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="经营类型" prop="businessType">
          <el-select v-model="tenantForm.businessType" placeholder="请选择经营类型" style="width: 100%">
            <el-option label="服装鞋帽" value="服装鞋帽" />
            <el-option label="餐饮美食" value="餐饮美食" />
            <el-option label="日用百货" value="日用百货" />
            <el-option label="家用电器" value="家用电器" />
            <el-option label="图书文具" value="图书文具" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="tenantForm.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="tenantForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="营业执照" prop="businessLicense">
          <div class="business-license-form">
            <el-input 
              v-model="tenantForm.businessLicense" 
              placeholder="请输入营业执照图片URL"
              style="margin-bottom: 10px;"
            />
            <div v-if="tenantForm.businessLicense" class="license-preview">
              <el-image 
                :src="tenantForm.businessLicense"
                :preview-src-list="[tenantForm.businessLicense]"
                fit="cover"
                style="width: 120px; height: 80px; border-radius: 6px; cursor: pointer;"
                alt="营业执照预览"
              >
                <template #error>
                  <div class="preview-error">
                    <el-icon><Picture /></el-icon>
                    <span style="font-size: 12px;">预览失败</span>
                  </div>
                </template>
              </el-image>
              <div class="preview-tip">点击可预览大图</div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="tenantForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
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
      title="租户详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentTenant.id }}</el-descriptions-item>
        <el-descriptions-item label="商户名称">{{ currentTenant.businessName }}</el-descriptions-item>
        <el-descriptions-item label="经营类型">{{ currentTenant.businessType }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentTenant.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentTenant.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentTenant.status === 1 ? 'success' : 'danger'">
            {{ currentTenant.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="营业执照" :span="2">
          <div v-if="currentTenant.businessLicense" class="license-image-container">
            <el-image 
              :src="currentTenant.businessLicense"
              :preview-src-list="[currentTenant.businessLicense]"
              fit="cover"
              style="width: 200px; height: 150px; border-radius: 8px; cursor: pointer;"
              :alt="currentTenant.businessName + '营业执照'"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>营业执照加载失败</span>
                </div>
              </template>
            </el-image>
            <div class="license-tip">点击图片可查看大图</div>
          </div>
          <span v-else class="no-license">暂无营业执照</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ formatDateTime(currentTenant.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">
          {{ formatDateTime(currentTenant.updateTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { getTenantList, getTenantDetail, addTenant, updateTenant, deleteTenant } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataCard from '@/components/DataCard.vue'

const loading = ref(false)
const submitLoading = ref(false)
const tenantList = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const currentTenant = ref({})
const tenantFormRef = ref()

const searchParams = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tenantForm = reactive({
  id: null,
  businessName: '',
  businessType: '',
  contactPerson: '',
  contactPhone: '',
  businessLicense: '',
  status: 1
})

const tenantRules = {
  businessName: [
    { required: true, message: '请输入商户名称', trigger: 'blur' }
  ],
  businessType: [
    { required: true, message: '请选择经营类型', trigger: 'change' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => isEdit.value ? '编辑租户' : '新增租户')

// 加载租户列表
const loadTenantList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchParams
    }
    const result = await getTenantList(params)
    tenantList.value = result.records || []
    pagination.total = result.total || 0
  } catch (error) {
    ElMessage.error('加载租户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = (params) => {
  Object.assign(searchParams, params)
  pagination.current = 1
  loadTenantList()
}

// 重置
const handleReset = () => {
  searchParams.keyword = ''
  searchParams.status = null
  pagination.current = 1
  loadTenantList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadTenantList()
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  pagination.current = current
  loadTenantList()
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
  Object.assign(tenantForm, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = async (row) => {
  try {
    currentTenant.value = await getTenantDetail(row.id)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取租户详情失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该租户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTenant(row.id)
      ElMessage.success('删除成功')
      loadTenantList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  try {
    await tenantFormRef.value.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateTenant(tenantForm)
      ElMessage.success('更新成功')
    } else {
      await addTenant(tenantForm)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadTenantList()
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
  tenantForm.id = null
  tenantForm.businessName = ''
  tenantForm.businessType = ''
  tenantForm.contactPerson = ''
  tenantForm.contactPhone = ''
  tenantForm.businessLicense = ''
  tenantForm.status = 1
  tenantFormRef.value?.clearValidate()
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 19)
}

// 初始化
loadTenantList()
</script>

<style scoped>
.tenant-management {
  padding: 0;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.license-image-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.license-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.no-license {
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

.business-license-form .license-preview {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
}

.business-license-form .preview-tip {
  font-size: 12px;
  color: #999;
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
  font-size: 12px;
}

.preview-error .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}
</style>

