<template>
  <div class="booth-search">
    <PageHeader 
      title="摊位查找" 
      description="查找适合的摊位，开启您的生意之旅"
    >
      <template #extra>
        <el-button type="primary" @click="viewMode = viewMode === 'grid' ? 'list' : 'grid'">
          <el-icon><Grid v-if="viewMode === 'list'" /><List v-else /></el-icon>
          {{ viewMode === 'grid' ? '列表视图' : '网格视图' }}
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
        <el-form-item label="关键词" class="keyword-item">
          <el-input
            v-model="form.keyword"
            placeholder="请输入摊位编号或位置"
            clearable
            style="width: 250px"
          />
        </el-form-item>
        <el-form-item label="状态" class="status-item">
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
        <el-form-item label="面积范围" class="area-item">
          <div style="display: flex; align-items: center; gap: 8px">
            <el-input
              v-model="form.minArea"
              placeholder="最小面积"
              style="width: 100px"
            />
            <span>-</span>
            <el-input
              v-model="form.maxArea"
              placeholder="最大面积"
              style="width: 100px"
            />
            <span style="color: #909399; font-size: 12px">㎡</span>
          </div>
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 摊位列表 -->
    <DataCard>
      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="booth-grid">
        <div
          v-for="booth in boothList"
          :key="booth.id"
          class="booth-card"
          @click="viewBoothDetail(booth)"
        >
          <div class="booth-image">
            <el-image
              :src="booth.images || 'https://via.placeholder.com/300x200?text=暂无图片'"
              fit="cover"
              class="image"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="booth-status" :class="getStatusClass(booth.status)">
              {{ getStatusText(booth.status) }}
            </div>
          </div>
          <div class="booth-info">
            <h3 class="booth-number">{{ booth.boothNo }}</h3>
            <p class="booth-location">
              <el-icon><Location /></el-icon>
              {{ booth.location }}
            </p>
            <div class="booth-details">
              <span class="area">{{ booth.area }}㎡</span>
              <span class="price">¥{{ booth.price }}/月</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 列表视图 -->
      <el-table
        v-else
        :data="boothList"
        v-loading="loading"
        @row-click="viewBoothDetail"
        class="booth-table"
      >
        <el-table-column prop="boothNo" label="摊位编号" width="120" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column prop="price" label="租金(元/月)" width="120">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click.stop="viewBoothDetail(row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="pagination.current"
          :page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[6, 9, 12]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </DataCard>

    <!-- 摊位详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="摊位详情"
      width="600px"
      :before-close="handleCloseDetail"
    >
      <div v-if="currentBooth" class="booth-detail">
        <div class="detail-image">
          <el-image
            :src="currentBooth.images || 'https://via.placeholder.com/600x200?text=暂无图片'"
            fit="cover"
            style="width: 100%; height: 200px"
          >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
        <div class="detail-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="摊位编号">
              {{ currentBooth.boothNo }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusTagType(currentBooth.status)">
                {{ getStatusText(currentBooth.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="位置">
              {{ currentBooth.location }}
            </el-descriptions-item>
            <el-descriptions-item label="面积">
              {{ currentBooth.area }}㎡
            </el-descriptions-item>
            <el-descriptions-item label="租金">
              ¥{{ currentBooth.price }}/月
            </el-descriptions-item>
            <el-descriptions-item label="押金">
              ¥{{ currentBooth.deposit || '待定' }}
            </el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">
              {{ currentBooth.description || '暂无描述' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentBooth?.status === 0"
          type="primary"
          @click="applyBooth"
        >
          申请租赁
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBoothList, getBoothDetail } from '@/api/booth'
import { ElMessage } from 'element-plus'
import { Grid, List, Picture, Location } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataCard from '@/components/DataCard.vue'

const router = useRouter()
const loading = ref(false)
const viewMode = ref('grid')
const detailVisible = ref(false)
const currentBooth = ref(null)

// 定义初始搜索参数
const initialSearchParams = {
  keyword: '',
  status: null,
  minArea: '',
  maxArea: ''
}

const searchParams = reactive({ ...initialSearchParams })

const pagination = reactive({
  current: 1,
  size: 6,
  total: 0
})

const boothList = ref([])

// 获取摊位列表
const fetchBoothList = async () => {
  try {
    loading.value = true
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchParams.keyword,
      status: searchParams.status,
      minArea: searchParams.minArea,
      maxArea: searchParams.maxArea
    }
    
    console.log('发送筛选参数:', params)
    console.log('当前搜索参数状态:', searchParams)
    
    const result = await getBoothList(params)
    boothList.value = result.records
    pagination.total = result.total
    
    console.log('获取到摊位数据:', result)
    console.log('摊位列表:', boothList.value)
    
  } catch (error) {
    console.error('获取摊位列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = (formData) => {
  console.log('搜索处理，接收到的表单数据:', formData)
  // 更新搜索参数
  Object.assign(searchParams, formData)
  pagination.current = 1
  fetchBoothList()
}

// 重置处理
const handleReset = (formData) => {
  console.log('重置处理，接收到表单数据:', formData)
  
  // 重置搜索参数为初始状态
  Object.assign(searchParams, initialSearchParams)
  
  // 重置分页为第一页
  pagination.current = 1
  
  console.log('重置后的搜索参数:', searchParams)
  
  // 显示重置提示
  ElMessage.success('筛选条件已重置')
  
  // 重新查询
  fetchBoothList()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  fetchBoothList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  fetchBoothList()
}

// 查看摊位详情
const viewBoothDetail = async (booth) => {
  try {
    const detail = await getBoothDetail(booth.id)
    currentBooth.value = detail
    detailVisible.value = true
  } catch (error) {
    console.error('获取摊位详情失败:', error)
  }
}

// 关闭详情弹窗
const handleCloseDetail = () => {
  detailVisible.value = false
  currentBooth.value = null
}

// 申请租赁
const applyBooth = () => {
  router.push({
    path: '/user/apply',
    query: { boothId: currentBooth.value.id }
  })
}

// 状态相关方法
const getStatusText = (status) => {
  const statusMap = {
    0: '可租',
    1: '已租',
    2: '维修中'
  }
  return statusMap[status] || '未知'
}

const getStatusClass = (status) => {
  const classMap = {
    0: 'status-available',
    1: 'status-rented',
    2: 'status-maintenance'
  }
  return classMap[status] || ''
}

const getStatusTagType = (status) => {
  const typeMap = {
    0: 'success',
    1: 'danger',
    2: 'warning'
  }
  return typeMap[status] || 'info'
}

onMounted(() => {
  fetchBoothList()
})
</script>

<style scoped>
.booth-search {
  padding: 0;
}

/* 搜索表单优化 */
.booth-search :deep(.search-form) {
  padding: 24px;
}

.booth-search :deep(.el-form--inline .el-form-item) {
  margin-right: 24px;
  margin-bottom: 20px;
}

.booth-search :deep(.keyword-item .el-form-item__label) {
  width: 60px;
}

.booth-search :deep(.status-item .el-form-item__label) {
  width: 40px;
}

.booth-search :deep(.area-item .el-form-item__label) {
  width: 80px;
}

/* 确保搜索按钮在合适的位置 */
.booth-search :deep(.el-form-item:last-child) {
  margin-left: auto;
}

.booth-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.booth-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.booth-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.booth-image {
  position: relative;
  height: 160px;
}

.image {
  width: 100%;
  height: 100%;
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

.booth-status {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.status-available {
  background: #67c23a;
}

.status-rented {
  background: #f56c6c;
}

.status-maintenance {
  background: #e6a23c;
}

.booth-info {
  padding: 16px;
}

.booth-number {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.booth-location {
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  color: #606266;
  font-size: 14px;
}

.booth-location .el-icon {
  margin-right: 4px;
}

.booth-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.area {
  color: #909399;
  font-size: 14px;
}

.price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 600;
}

.booth-table {
  margin-bottom: 20px;
}

.booth-table .el-table__row {
  cursor: pointer;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.booth-detail {
  margin-bottom: 20px;
}

.detail-image {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.detail-info {
  margin-top: 20px;
}
</style>
