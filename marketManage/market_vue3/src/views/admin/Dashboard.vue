<template>
  <div class="dashboard">
    <PageHeader 
      title="数据统计" 
      description="查看系统整体运营数据和趋势分析"
    />

    <!-- 概览数据卡片 -->
    <div class="overview-cards">
      <el-row :gutter="24">
        <el-col :xs="12" :sm="12" :md="6" :lg="6">
          <DataCard hover>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                <el-icon><Shop /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ overview.boothCount || 0 }}</div>
                <div class="stat-label">总摊位数</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        
        <el-col :xs="12" :sm="12" :md="6" :lg="6">
          <DataCard hover>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ overview.tenantCount || 0 }}</div>
                <div class="stat-label">租户总数</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        
        <el-col :xs="12" :sm="12" :md="6" :lg="6">
          <DataCard hover>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ overview.contractCount || 0 }}</div>
                <div class="stat-label">有效合同</div>
              </div>
            </div>
          </DataCard>
        </el-col>
        
        <el-col :xs="12" :sm="12" :md="6" :lg="6">
          <DataCard hover>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">¥{{ (financeData.totalIncome || 0).toLocaleString() }}</div>
                <div class="stat-label">总收入</div>
              </div>
            </div>
          </DataCard>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="24" class="chart-section">
      <!-- 收支分析饼状图 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <DataCard title="收支分析">
          <div class="chart-container">
            <div ref="pieChartRef" class="chart"></div>
          </div>
        </DataCard>
      </el-col>

      <!-- 摊位状态柱状图 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <DataCard title="摊位状态统计">
          <div class="chart-container">
            <div ref="barChartRef" class="chart"></div>
          </div>
        </DataCard>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="24">
      <el-col :span="24">
        <DataCard title="快捷操作">
          <div class="quick-actions">
            <div 
              class="quick-action-item" 
              v-for="action in quickActions" 
              :key="action.path"
              @click="navigateTo(action.path)"
            >
              <div class="action-icon" :style="{ background: action.color }">
                <el-icon>
                  <component :is="action.icon" />
                </el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">{{ action.title }}</div>
                <div class="action-desc">{{ action.desc }}</div>
              </div>
            </div>
          </div>
        </DataCard>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getOverview, getBoothStatistics } from '@/api/admin'
import { getFinanceStatistics } from '@/api/admin'
import PageHeader from '@/components/PageHeader.vue'
import DataCard from '@/components/DataCard.vue'
import { Shop, User, Document, Money, Bell, Box, Files } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const router = useRouter()
const overview = ref({})
const boothStats = ref({})
const financeData = ref({})

// 图表引用
const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null

// 快捷操作配置
const quickActions = [
  {
    title: '申请管理',
    desc: '管理摊位租赁申请的审核和处理',
    icon: 'Files',
    path: '/admin/apply',
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: '租户管理',
    desc: '管理所有租户信息',
    icon: 'User',
    path: '/admin/tenant',
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: '合同管理',
    desc: '查看和管理租赁合同',
    icon: 'Document',
    path: '/admin/contract',
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    title: '费用收支',
    desc: '管理市场收支记录',
    icon: 'Money',
    path: '/admin/finance',
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  },
  {
    title: '公告通知',
    desc: '发布和管理公告',
    icon: 'Bell',
    path: '/admin/notice',
    color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  },
  {
    title: '物资仓储',
    desc: '管理仓储物资',
    icon: 'Box',
    path: '/admin/inventory',
    color: 'linear-gradient(135deg, #fd79a8 0%, #fdcb6e 100%)'
  },
  {
    title: '摊位管理',
    desc: '管理所有摊位信息',
    icon: 'Shop',
    path: '/admin/booth',
    color: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)'
  }
]

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
}

// 加载概览数据
const loadOverview = async () => {
  try {
    overview.value = await getOverview()
  } catch (error) {
    console.error('加载概览数据失败:', error)
    ElMessage.error('加载概览数据失败')
  }
}

// 加载摊位统计
const loadBoothStatistics = async () => {
  try {
    boothStats.value = await getBoothStatistics()
    await nextTick()
    initBarChart()
  } catch (error) {
    console.error('加载摊位统计失败:', error)
    ElMessage.error('加载摊位统计失败')
  }
}

// 加载财务数据
const loadFinanceData = async () => {
  try {
    financeData.value = await getFinanceStatistics()
    await nextTick()
    initPieChart()
  } catch (error) {
    console.error('加载财务数据失败:', error)
    ElMessage.error('加载财务数据失败')
  }
}

// 初始化饼状图
const initPieChart = () => {
  if (!pieChartRef.value || !financeData.value) return
  
  if (pieChart) {
    pieChart.dispose()
  }
  
  pieChart = echarts.init(pieChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '收支分析',
        type: 'pie',
        radius: '50%',
        center: ['50%', '50%'],
        data: [
          {
            value: financeData.value.totalIncome || 0,
            name: '总收入',
            itemStyle: { color: '#67c23a' }
          },
          {
            value: financeData.value.totalExpense || 0,
            name: '总支出',
            itemStyle: { color: '#f56c6c' }
          }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  pieChart.setOption(option)
}

// 初始化柱状图
const initBarChart = () => {
  if (!barChartRef.value || !boothStats.value) return
  
  if (barChart) {
    barChart.dispose()
  }
  
  barChart = echarts.init(barChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: ['可租摊位', '已租摊位', '维修中'],
        axisTick: {
          alignWithLabel: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value'
      }
    ],
    series: [
      {
        name: '摊位数量',
        type: 'bar',
        barWidth: '60%',
        data: [
          {
            value: boothStats.value.availableBooth || 0,
            itemStyle: { color: '#67c23a' }
          },
          {
            value: boothStats.value.rentedBooth || 0,
            itemStyle: { color: '#409eff' }
          },
          {
            value: boothStats.value.repairBooth || 0,
            itemStyle: { color: '#f56c6c' }
          }
        ]
      }
    ]
  }
  
  barChart.setOption(option)
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  if (pieChart) {
    pieChart.resize()
  }
  if (barChart) {
    barChart.resize()
  }
}

onMounted(() => {
  loadOverview()
  loadBoothStatistics()
  loadFinanceData()
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

// 销毁图表
const destroyCharts = () => {
  if (pieChart) {
    pieChart.dispose()
    pieChart = null
  }
  if (barChart) {
    barChart.dispose()
    barChart = null
  }
  window.removeEventListener('resize', handleResize)
}

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  destroyCharts()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.overview-cards {
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
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.chart-section {
  margin-bottom: 24px;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart {
  width: 100%;
  height: 100%;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  padding: 16px 0;
}

.quick-action-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
}

.quick-action-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border-color: #10b981;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-icon .el-icon {
  font-size: 24px;
  color: white;
}

.action-content {
  flex: 1;
}

.action-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px;
  color: #666;
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

  .quick-actions {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 250px;
  }
}

@media (max-width: 576px) {
  .overview-cards .el-col {
    margin-bottom: 16px;
  }
}
</style>