<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo-container">
        <transition name="fade">
          <div v-if="!isCollapse" class="logo">
            <el-icon><Operation /></el-icon>
            <span>管理后台</span>
          </div>
          <div v-else class="logo-collapsed">
            <el-icon><Operation /></el-icon>
          </div>
        </transition>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        background-color="transparent"
        text-color="#fff"
        active-text-color="#fef3c7"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/tenant">
          <el-icon><User /></el-icon>
          <template #title>租户管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/apply">
          <el-icon><Document /></el-icon>
          <template #title>申请管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/contract">
          <el-icon><Document /></el-icon>
          <template #title>合同管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/finance">
          <el-icon><Money /></el-icon>
          <template #title>费用收支</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/notice">
          <el-icon><Bell /></el-icon>
          <template #title>公告通知</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/inventory">
          <el-icon><Box /></el-icon>
          <template #title>物资仓储</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/booth">
          <el-icon><Shop /></el-icon>
          <template #title>摊位管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-container :class="['main-container', { collapsed: isCollapse }]">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleCollapse">
            <Expand v-if="isCollapse" />
            <Fold v-else />
          </el-icon>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="admin-info">
              <div class="admin-avatar">
                <el-avatar 
                  :size="32" 
                  :src="processAvatarUrl(adminStore.admin?.avatar, adminStore.admin?.username)"
                  :icon="User"
                >
                  {{ adminStore.admin?.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
              </div>
              <span class="admin-name">{{ adminStore.admin?.realName || adminStore.admin?.username || '管理员' }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import { processAvatarUrl } from '@/utils/avatar'
import { 
  Operation, 
  DataAnalysis, 
  User, 
  Document, 
  Money, 
  Bell, 
  Box,
  Shop,
  Expand,
  Fold,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

// 初始化管理员信息
onMounted(() => {
  adminStore.initAdmin()
})

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 菜单选择处理
const handleMenuSelect = (index) => {
  router.push(index)
}

// 下拉菜单处理
const handleCommand = (command) => {
  if (command === 'logout') {
    adminStore.clearAdmin()
    ElMessage.success('退出登录成功')
    router.push('/admin/login')
  } else if (command === 'profile') {
    router.push('/admin/profile')
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f0f2f5;
  position: relative;
}

.sidebar {
  background: linear-gradient(180deg, #10b981 0%, #06b6d4 100%);
  overflow-x: hidden;
  overflow-y: auto;
  transition: width 0.3s ease;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 1000;
  height: 100vh;
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.1);
  padding: 0 16px;
}

.logo {
  display: flex;
  align-items: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  white-space: nowrap;
}

.logo .el-icon {
  font-size: 24px;
  margin-right: 12px;
}

.logo-collapsed {
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.el-menu {
  border-right: none;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 200px;
  transition: margin-left 0.3s ease;
}

.main-container.collapsed {
  margin-left: 64px;
}

.header {
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
  position: sticky;
  top: 0;
  z-index: 999;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.collapse-icon:hover {
  background-color: #f5f7fa;
}

.header-right {
  display: flex;
  align-items: center;
}

.admin-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.admin-info:hover {
  background-color: #f5f7fa;
}

.admin-avatar {
  margin-right: 8px;
}

.admin-name {
  margin-right: 4px;
  font-size: 14px;
  color: #333;
}

.main-content {
  padding: 24px;
  background-color: #f0f2f5;
  overflow-y: auto;
  flex: 1;
  height: calc(100vh - 64px);
  max-height: calc(100vh - 64px);
}

/* 菜单项样式 */
:deep(.el-menu-item) {
  transition: all 0.3s ease;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.15) !important;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(255, 255, 255, 0.25) !important;
  font-weight: 600;
}

/* 自定义滚动条 */
.main-content::-webkit-scrollbar {
  width: 8px;
}

.main-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.main-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

.sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}
</style>

