/**
 * 头像URL处理工具
 */

// 后端服务器地址
const BASE_URL = 'http://localhost:8080'

/**
 * 处理头像URL，确保返回完整的可访问地址
 * @param {string} avatarUrl - 头像URL（可能是相对路径或完整URL）
 * @param {string} defaultSeed - 默认头像种子（用户名）
 * @returns {string} 完整的头像URL
 */
export function processAvatarUrl(avatarUrl, defaultSeed = '') {
  // 如果没有头像URL，返回默认头像
  if (!avatarUrl) {
    return `https://api.dicebear.com/7.x/initials/svg?seed=${defaultSeed}`
  }
  
  // 如果已经是完整的HTTP/HTTPS URL，直接返回
  if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) {
    return avatarUrl
  }
  
  // 如果是相对路径，补充完整的后端服务器地址
  if (avatarUrl.startsWith('/uploads/')) {
    return `${BASE_URL}${avatarUrl}`
  }
  
  // 如果路径不以/开头，添加/
  if (avatarUrl.startsWith('uploads/')) {
    return `${BASE_URL}/${avatarUrl}`
  }
  
  // 其他情况，直接拼接
  return `${BASE_URL}/uploads/${avatarUrl}`
}

/**
 * 获取默认头像URL
 * @param {string} username - 用户名
 * @returns {string} 默认头像URL
 */
export function getDefaultAvatarUrl(username) {
  return `https://api.dicebear.com/7.x/initials/svg?seed=${username}`
}
