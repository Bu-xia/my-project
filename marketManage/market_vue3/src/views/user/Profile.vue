<template>
  <div class="profile">
    <PageHeader 
      title="个人中心" 
      description="管理您的个人信息和账户设置"
    />

    <div class="profile-content">
      <!-- 个人信息卡片 -->
      <DataCard title="个人信息" class="profile-info-card">
        <div class="profile-header">
          <div class="avatar-section">
            <el-avatar :size="80" :src="userInfo.avatar" :icon="User">
              {{ userInfo.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <el-upload
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              action="/api/user/avatar"
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
            <h3>{{ userInfo.realName || userInfo.username }}</h3>
            <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
              {{ userInfo.status === 1 ? '正常' : '禁用' }}
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

          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="profileForm.idCard" placeholder="请输入身份证号" />
          </el-form-item>

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
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import DataCard from '@/components/DataCard.vue'
import { processAvatarUrl } from '@/utils/avatar'

const userStore = useUserStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const updateLoading = ref(false)
const passwordLoading = ref(false)
const avatarUploading = ref(false)

// 上传请求头
const uploadHeaders = computed(() => {
  const token = userStore.token
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// 上传数据
const uploadData = computed(() => {
  return {
    userId: userStore.user?.id
  }
})

const userInfo = ref({})

const profileForm = reactive({
  id: null,
  username: '',
  realName: '',
  email: '',
  phone: '',
  idCard: '',
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
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  idCard: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
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

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    // 这里暂时使用store中的用户信息，实际应该调用后端接口
    const user = userStore.user
    if (user) {
      // 确保头像URL是正确的完整地址
      const processedUser = {
        ...user,
        avatar: processAvatarUrl(user.avatar, user.username)
      }
      
      userInfo.value = processedUser
      Object.assign(profileForm, {
        id: processedUser.id,
        username: processedUser.username,
        realName: processedUser.realName || '',
        email: processedUser.email || '',
        phone: processedUser.phone || '',
        idCard: processedUser.idCard || '',
        avatar: processedUser.avatar
      })
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
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
    const avatarUrl = processAvatarUrl(response.data, userStore.user?.username)
    
    // 更新本地显示
    userInfo.value.avatar = avatarUrl
    profileForm.avatar = avatarUrl
    // 更新store中的用户信息
    userStore.setUser({ ...userStore.user, avatar: avatarUrl })
    ElMessage.success('头像更新成功，已保存到数据库')
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

    // 调用后端接口更新用户信息
    await updateUserInfo(profileForm)
    
    // 更新本地store和显示
    const updatedUser = { ...userStore.user, ...profileForm }
    userStore.setUser(updatedUser)
    userInfo.value = updatedUser

    ElMessage.success('个人信息更新成功')
  } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error('更新失败，请重试')
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
    await changePassword({
      userId: userStore.user.id,
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success('密码修改成功')
    resetPasswordForm()
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('密码修改失败，请检查当前密码是否正确')
  } finally {
    passwordLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  fetchUserInfo()
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
  fetchUserInfo()
})
</script>

<style scoped>
.profile {
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
