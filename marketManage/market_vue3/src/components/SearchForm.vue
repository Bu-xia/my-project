<template>
  <div class="search-form">
    <el-form
      :model="searchForm"
      :inline="true"
      @submit.prevent="handleSearch"
    >
      <slot :form="searchForm"></slot>
      <el-form-item>
        <el-button type="primary" @click="handleSearch" :loading="loading">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  defaultValues: {
    type: Object,
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['search', 'reset'])

const searchForm = reactive({ ...props.defaultValues })

// 监听defaultValues的变化，同步更新表单
watch(() => props.defaultValues, (newValues) => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = newValues[key] || ''
  })
}, { deep: true })

const handleSearch = () => {
  emit('search', searchForm)
}

const handleReset = () => {
  // 重置为初始值 - 使用 null 或空字符串重置所有字段
  Object.keys(searchForm).forEach(key => {
    if (typeof searchForm[key] === 'number') {
      searchForm[key] = null
    } else {
      searchForm[key] = ''
    }
  })
  
  console.log('SearchForm 重置后的表单数据:', searchForm)
  
  emit('reset', searchForm)
}

defineExpose({
  searchForm
})
</script>

<style scoped>
.search-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.el-form--inline .el-form-item {
  margin-right: 16px;
  margin-bottom: 16px;
}
</style>
