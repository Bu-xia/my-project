<template>
  <div class="admin-profile">
    <PageHeader 
      title="个人中心" 
      description="管理您的管理员账户信息和设置"
    />

    <div class="profile-content">
      <!-- 个人信息卡片 -->
      <DataCard title="个人信息" class="profile-info-card">
        <div class="profile-header">
          <div class="avatar-section">
            <el-avatar :size="80" :src="adminInfo.avatar" :icon="User">
              {{ adminInfo.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <el-upload
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              action="/api/admin/avatar"
              :headers="uploadHeaders"
              :data="uploadData"
              name="file"
              class="avatar-upload"
            >
              <el-button size="small" type="primary" :loading="avatarUploading">
                {{ avatarUploading ? '上传中...' : '更换头像' }}
              </el-button>
            </el-upload>
          </div>
          <div class="user-basic-info">
            <h3>{{ adminInfo.realName || adminInfo.username }}</h3>
            <el-tag type="success">管理员</el-tag>
            <el-tag :type="adminInfo.status === 1 ? 'success' : 'danger'" style="margin-left: 8px">
              {{ adminInfo.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </div>
        </div>
      </DataCard>

      <!-- 编辑信息表单 -->
      <DataCard title="编辑资料" class="profile-edit-card">
        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          label-width="100px"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="profileForm.username" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button type="primary" :loading="updateLoading" @click="handleUpdateProfile">
              保存修改
            </el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </DataCard>

      <!-- 修改密码 -->
      <DataCard title="修改密码" class="password-card">
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
          style="max-width: 400px"
        >
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入当前密码"
              show-password
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请确认新密码"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
              修改密码
            </el-button>
            <el-button @click="resetPasswordForm">重置</el-button>
          </el-form-item>
        </el-form>
      </DataCard>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import DataCard from '@/components/DataCard.vue'
import { processAvatarUrl } from '@/utils/avatar'
import { updateAdminInfo, changeAdminPassword } from '@/api/admin'

const adminStore = useAdminStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const updateLoading = ref(false)
const passwordLoading = ref(false)
const avatarUploading = ref(false)

// 上传请求头
const uploadHeaders = computed(() => {
  const token = adminStore.token
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 上传数据
const uploadData = computed(() => {
  return {
    adminId: adminStore.admin?.id
  }
})

const adminInfo = ref({})

const profileForm = reactive({
  id: null,
  username: '',
  realName: '',
  email: '',
  phone: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  realName: [
    { max: 20, message: '真实姓名不能超过20个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 获取管理员信息
const fetchAdminInfo = async () => {
  try {
    // 使用store中的管理员信息
    const admin = adminStore.admin
    if (admin && admin.id) {
      // 确保头像URL是正确的完整地址
      const processedAdmin = {
        ...admin,
        avatar: processAvatarUrl(admin.avatar, admin.username)
      }
      
      adminInfo.value = processedAdmin
      Object.assign(profileForm, {
        id: processedAdmin.id,
        username: processedAdmin.username,
        realName: processedAdmin.realName || '',
        email: processedAdmin.email || '',
        phone: processedAdmin.phone || '',
        avatar: processedAdmin.avatar
      })
    }
  } catch (error) {
    console.error('获取管理员信息失败:', error)
    ElMessage.error('获取管理员信息失败')
  }
}

// 头像上传前检查
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  
  avatarUploading.value = true
  return true
}

// 头像上传成功
const handleAvatarSuccess = (response) => {
  avatarUploading.value = false
  if (response.code === 200) {
    // 使用工具函数处理头像URL
    const avatarUrl = processAvatarUrl(response.data, adminStore.admin?.username)
    
    // 更新本地显示
    adminInfo.value.avatar = avatarUrl
    profileForm.avatar = avatarUrl
    // 更新store中的管理员信息
    adminStore.setAdmin({ ...adminStore.admin, avatar: avatarUrl })
    ElMessage.success('头像更新成功')
  } else {
    ElMessage.error(response.msg || '头像上传失败')
  }
}

// 头像上传失败
const handleAvatarError = (error) => {
  avatarUploading.value = false
  ElMessage.error('头像上传失败，请重试')
}

// 更新个人信息
const handleUpdateProfile = async () => {
  try {
    await profileFormRef.value.validate()
    updateLoading.value = true

    // 调用后端接口更新管理员信息
    // 注意：响应拦截器已经处理了错误情况，成功时直接返回data
    const data = await updateAdminInfo(profileForm)
    
    // 更新本地store
    const updatedAdmin = { ...adminStore.admin, ...profileForm }
    adminStore.setAdmin(updatedAdmin)
    adminInfo.value = updatedAdmin
    
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    if (error !== false) {
      console.error('更新个人信息失败:', error)
      // 这里不要重复显示错误消息，因为响应拦截器已经显示了
    }
  } finally {
    updateLoading.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true

    // 调用后端接口修改密码
    const requestData = {
      adminId: adminStore.admin?.id,
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    }
    
    // 注意：响应拦截器已经处理了错误情况，成功时直接返回data
    const data = await changeAdminPassword(requestData)
    
    ElMessage.success('密码修改成功，请重新登录')
    resetPasswordForm()
    
    // 退出登录
    setTimeout(() => {
      adminStore.clearAdmin()
      window.location.href = '/admin/login'
    }, 1500)
  } catch (error) {
    if (error !== false) {
      console.error('修改密码失败:', error)
      // 这里不要重复显示错误消息，因为响应拦截器已经显示了
    }
  } finally {
    passwordLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  fetchAdminInfo()
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordFormRef.value?.resetFields()
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

onMounted(() => {
  fetchAdminInfo()
})
</script>

<style scoped>
.admin-profile {
  padding: 0;
}

.profile-content {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.profile-info-card {
  margin-bottom: 20px;
}

.profile-header {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  margin-bottom: 24px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-upload {
  margin-top: 8px;
}

.user-basic-info {
  flex: 1;
  padding-top: 16px;
}

.user-basic-info h3 {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.profile-edit-card,
.password-card {
  margin-bottom: 20px;
}

.profile-edit-card :deep(.el-form-item) {
  margin-bottom: 20px;
}

.password-card :deep(.el-form-item) {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .user-basic-info {
    width: 100%;
  }
}
</style>

