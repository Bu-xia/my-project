<template>
  <div class="user-layout">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <el-icon><Shop /></el-icon>
          <span>跳蚤市场</span>
        </div>
        <div class="nav-menu">
          <el-menu
            :default-active="activeIndex"
            mode="horizontal"
            @select="handleMenuSelect"
            background-color="transparent"
            text-color="#333"
            active-text-color="#409EFF"
          >
            <el-menu-item index="/user/booth">摊位查找</el-menu-item>
            <el-menu-item index="/user/apply">租赁申请</el-menu-item>
            <el-menu-item index="/user/rental">我的租赁</el-menu-item>
            <el-menu-item index="/user/payment">费用支付</el-menu-item>
            <el-menu-item index="/user/notice">公告通知</el-menu-item>
            <el-menu-item index="/user/ai-assistant">AI助手</el-menu-item>
          </el-menu>
        </div>
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="user-name">
              <div class="user-avatar">
                <el-avatar 
                  :size="32"  
                  :src="processAvatarUrl(userStore.user?.avatar, userStore.user?.username)" 
                  :icon="User"
                >
                  {{ userStore.user?.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
              </div>
              <span class="username">{{ userStore.user?.username || '用户' }}</span>
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
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <el-main class="main-content">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { processAvatarUrl } from '@/utils/avatar'
import { Shop, User, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeIndex = computed(() => route.path)

// 初始化用户信息
onMounted(() => {
  userStore.initUser()
})

// 菜单选择处理
const handleMenuSelect = (index) => {
  router.push(index)
}

// 用户下拉菜单处理
const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.clearUser()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/user/profile')
  }
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.logo .el-icon {
  margin-right: 8px;
  font-size: 24px;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
}

.nav-menu .el-menu {
  border-bottom: none;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-name:hover {
  background-color: #f5f7fa;
}

.user-avatar {
  margin-right: 8px;
}

.username {
  margin-right: 4px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>
