// utils/api.js

const config = require('../config/wx-config.js')

// 获取当前配置
const getCurrentConfig = () => {
  try {
    if (typeof wx !== 'undefined' && wx.getSystemInfoSync) {
      const systemInfo = wx.getSystemInfoSync()
      if (systemInfo.platform === 'devtools') {
        // 在微信开发者工具中，使用本地服务器
        return {
          ...config,
          API_BASE_URL: 'http://localhost:8080/api'
        }
      }
    }
  } catch (e) {
    console.log('无法获取系统信息，使用默认配置')
  }
  return config
}

// 带重试的请求函数
const requestWithRetry = (url, options, maxRetries = 3) => {
  return new Promise((resolve, reject) => {
    const attempt = (retryCount = 0) => {
      const fullUrl = getCurrentConfig().API_BASE_URL + url
      console.log(`🌐 发起网络请求 (尝试 ${retryCount + 1}/${maxRetries}):`, fullUrl)
      

      
      console.log(`📤 发送请求详情:`, {
        url: fullUrl,
        method: options.method,
        data: options.data,
        header: options.header
      })
      
      wx.request({
        ...options,
        url: fullUrl,
        success: (res) => {
          console.log(`✅ API请求成功 (尝试 ${retryCount + 1}):`, res)
          console.log(`📥 响应详情:`, {
            statusCode: res.statusCode,
            header: res.header,
            data: res.data,
            dataType: typeof res.data
          })
          
          // 检查是否是401错误
          if (res.statusCode === 401) {
            console.error('❌ 401未授权错误，token可能已过期')
            const error = new Error('401 Unauthorized')
            error.statusCode = 401
            error.message = '登录已过期，请重新登录'
            reject(error)
            return
          }
          
          resolve(res.data)
        },
        fail: (error) => {
          console.error(`❌ API请求失败 (尝试 ${retryCount + 1}):`, error)
          if (retryCount < maxRetries - 1) {
            console.log(`🔄 重试请求 (${retryCount + 2}/${maxRetries})...`)
            setTimeout(() => attempt(retryCount + 1), 1000 * (retryCount + 1))
          } else {
            console.error(`💥 所有重试都失败了，最终错误:`, error)
            // 不显示统一的错误提示，让调用方处理
            reject(error)
          }
        }
      })
    }
    attempt()
  })
}

// 微信登录接口
const wxLogin = (code, userInfo = null) => {
  console.log('🌐 调用微信登录接口:', { code, userInfo })
  
  const data = { code }
  if (userInfo) {
    data.userInfo = userInfo
  }
  
  const url = '/auth/wx/login'
  const fullUrl = getCurrentConfig().API_BASE_URL + url
  console.log('📡 请求URL:', fullUrl)
  console.log('📤 请求数据:', data)
  
  return requestWithRetry(url, {
    method: 'POST',
    data: data,
    header: {
      'Content-Type': 'application/json'
    }
  })
}

// 绑定员工信息接口
const bindEmployee = (employeeCode) => {
  console.log('调用绑定员工接口:', { employeeCode })
  
  const params = {
    employeeCode: employeeCode
  }
  
  // 构建查询参数
  let url = `/auth/wx/bind-employee?params=${encodeURIComponent(JSON.stringify(params))}`
  
  return requestWithRetry(url, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取用户信息接口
const getUserInfo = () => {
  console.log('调用获取用户信息接口')
  
  return requestWithRetry('/user/info', {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 更新用户信息接口
const updateUserInfo = (userInfo) => {
  console.log('调用更新用户信息接口:', userInfo)
  
  return requestWithRetry('/user/update', {
    method: 'POST',
    data: userInfo,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// ==================== 报告相关接口 ====================

// 创建报告
const createReport = (reportData) => {
  console.log('调用创建报告接口:', reportData)
  
  return requestWithRetry('/report/create', {
    method: 'POST',
    data: reportData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 更新报告
const updateReport = (reportData) => {
  console.log('调用更新报告接口:', reportData)
  
  return requestWithRetry('/report/update', {
    method: 'POST',
    data: reportData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 删除报告
const deleteReport = (reportId) => {
  console.log('调用删除报告接口:', reportId)
  
  return requestWithRetry(`/report/delete/${reportId}`, {
    method: 'POST',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取报告列表
const getReportList = (params = {}) => {
  console.log('调用获取报告列表接口:', params)
  
  const { page = 1, size = 10, type, department } = params
  
  // 构建查询参数
  let url = `/report/list?page=${page}&size=${size}`
  if (type) {
    url += `&type=${encodeURIComponent(type)}`
  }
  if (department) {
    url += `&department=${encodeURIComponent(department)}`
  }
  
  return requestWithRetry(url, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取报告统计信息
const getReportStatistics = () => {
  console.log('调用获取报告统计信息接口')
  
  return requestWithRetry('/report/statistics', {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// ==================== 项目相关接口 ====================

// 创建项目
const createProject = (projectData) => {
  console.log('调用创建项目接口:', projectData)
  
  return requestWithRetry('/project/create', {
    method: 'POST',
    data: projectData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 更新项目
const updateProject = (projectData) => {
  return requestWithRetry('/project/update', {
    method: 'POST',
    data: projectData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 删除项目
const deleteProject = (projectId) => {
  console.log('调用删除项目接口:', projectId)
  
  return requestWithRetry(`/project/delete/${projectId}`, {
    method: 'DELETE',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取项目列表
const getProjectList = (params = {}) => {
  console.log('调用获取项目列表接口:', params)
  
  const { page = 1, size = 10, status, level, managerId } = params
  
  // 构建查询参数
  let url = `/project/list?page=${page}&size=${size}`
  if (status) {
    url += `&status=${encodeURIComponent(status)}`
  }
  if (level) {
    url += `&level=${encodeURIComponent(level)}`
  }
  if (managerId) {
    url += `&managerId=${encodeURIComponent(managerId)}`
  }
  
  return requestWithRetry(url, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取项目列表(不分页) - 用于创建任务等场景
const getProjectListSimple = (params = {}) => {
  console.log('调用获取项目列表(不分页)接口:', params)
  
  const { status, keyword } = params
  
  // 构建查询参数
  let url = `/project/getlist`
  const queryParams = []
  
  if (status) {
    queryParams.push(`status=${encodeURIComponent(status)}`)
  }
  if (keyword) {
    queryParams.push(`keyword=${encodeURIComponent(keyword)}`)
  }
  
  if (queryParams.length > 0) {
    url += `?${queryParams.join('&')}`
  }
  
  return requestWithRetry(url, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取项目详情
const getProjectDetail = (projectId) => {
  console.log('调用获取项目详情接口:', projectId)
  
  return requestWithRetry(`/project/detail/${projectId}`, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取项目统计信息
const getProjectStatistics = () => {
  console.log('调用获取项目统计信息接口')
  
  return requestWithRetry('/project/statistics', {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 创建项目进度
const createProjectProgress = (projectId, progressData) => {
  console.log('调用创建项目进度接口:', { projectId, progressData })
  
  // 根据后端ProjectProgressDTO结构调整数据结构
  const adjustedProgressData = {
    projectId: projectId,
    progressPercentage: progressData.progress,
    progressContent: progressData.description || ''
  }
  
  console.log('调整后的进度数据:', adjustedProgressData)
  
  return requestWithRetry('/project/progress/create', {
    method: 'POST',
    data: adjustedProgressData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 更新项目进度
const updateProjectProgress = (projectId, progressData) => {
  console.log('调用更新项目进度接口:', { projectId, progressData })
  
  // 根据后端ProjectProgressDTO结构调整数据结构
  const adjustedProgressData = {
    projectId: projectId,
    progressPercentage: progressData.progress,
    progressContent: progressData.description || ''
  }
  
  console.log('调整后的进度数据:', adjustedProgressData)
  
  return requestWithRetry('/project/progress/update', {
    method: 'POST',
    data: adjustedProgressData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 更新进度记录（编辑模式）
const updateProgressRecord = (progressId, progressData) => {
  console.log('调用更新进度记录接口:', { progressId, progressData })
  
  // 根据后端ProjectProgressDTO结构调整数据结构
  const adjustedProgressData = {
    id: progressId,
    progressPercentage: progressData.progress,
    progressContent: progressData.description || ''
  }
  
  console.log('调整后的进度记录数据:', adjustedProgressData)
  
  return requestWithRetry('/project/progress/update', {
    method: 'POST',
    data: adjustedProgressData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取项目进度历史记录
const getProjectProgressHistory = (projectId) => {
  console.log('调用获取项目进度历史记录接口:', projectId)
  
  return requestWithRetry(`/project/progress/history/${projectId}`, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取最新项目进度
const getLatestProjectProgress = (projectId) => {
  console.log('调用获取最新项目进度接口:', projectId)
  
  return requestWithRetry(`/project/progress/latest/${projectId}`, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取项目成员列表
const getProjectMembers = (projectId) => {
  console.log('调用获取项目成员列表接口:', projectId)
  
  return requestWithRetry(`/project/members/${projectId}`, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 添加项目成员
const addProjectMember = (projectId, memberData) => {
  console.log('调用添加项目成员接口:', { projectId, memberData })
  
  return requestWithRetry(`/project/members/${projectId}`, {
    method: 'POST',
    data: memberData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 移除项目成员
const removeProjectMember = (projectId, memberId) => {
  console.log('调用移除项目成员接口:', { projectId, memberId })
  
  return requestWithRetry(`/project/members/${projectId}/${memberId}`, {
    method: 'DELETE',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取项目附件列表
const getProjectAttachments = (projectId) => {
  console.log('调用获取项目附件列表接口:', projectId)
  
  return requestWithRetry(`/project/attachments/${projectId}`, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 上传项目附件
const uploadProjectAttachment = (projectId, fileData) => {
  console.log('调用上传项目附件接口:', { projectId, fileData })
  
  return requestWithRetry(`/project/attachments/${projectId}`, {
    method: 'POST',
    data: fileData,
    header: {
      'Content-Type': 'multipart/form-data',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 删除项目附件
const deleteProjectAttachment = (projectId, attachmentId) => {
  console.log('调用删除项目附件接口:', { projectId, attachmentId })
  
  return requestWithRetry(`/project/attachments/${projectId}/${attachmentId}`, {
    method: 'DELETE',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 项目置顶/取消置顶
const toggleProjectTop = (projectId, isTop) => {
  console.log('调用项目置顶接口:', { projectId, isTop })
  
  // 构建更新数据，包含项目ID和topUp字段
  const updateData = {
    id: projectId,
    topUp: isTop ? 1 : 0  // 1表示置顶，0表示不置顶
  }
  
  return requestWithRetry('/project/update', {
    method: 'POST',
    data: updateData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// ==================== 任务相关接口 ====================

// 获取任务列表
const getTaskList = (params = {}) => {
  console.log('调用获取任务列表接口:', params)
  
  const { page = 1, size = 10, type, status, projectId, assignerId, assigneeId } = params
  
  // 构建查询参数
  let url = `/project-task/list?page=${page}&size=${size}`
  if (type) {
    url += `&type=${encodeURIComponent(type)}`
  }
  if (status) {
    url += `&status=${encodeURIComponent(status)}`
  }
  if (projectId) {
    url += `&projectId=${projectId}`
  }
  if (assignerId) {
    url += `&assignerId=${encodeURIComponent(assignerId)}`
  }
  if (assigneeId) {
    url += `&assigneeId=${encodeURIComponent(assigneeId)}`
  }
  
  return requestWithRetry(url, {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取任务统计信息
const getTaskStatistics = () => {
  console.log('调用获取任务统计信息接口')
  
  return requestWithRetry('/project-task/statistics', {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 创建任务
const createTask = (taskData) => {
  console.log('调用创建任务接口:', taskData)
  
  return requestWithRetry('/project-task/create', {
    method: 'POST',
    data: taskData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 更新任务
const updateTask = (taskData) => {
  console.log('调用更新任务接口:', taskData)
  
  return requestWithRetry('/project-task/update', {
    method: 'POST',
    data: taskData,
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 删除任务
const deleteTask = (taskId) => {
  console.log('调用删除任务接口:', taskId)
  
  return requestWithRetry(`/project-task/delete/${taskId}`, {
    method: 'POST',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// ==================== 员工相关接口 ====================

// 获取员工列表
const getEmployeeList = () => {
  console.log('调用获取员工列表接口')
  
  return requestWithRetry('/employee/list', {
    method: 'GET',
    header: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    }
  })
}

// 获取token的辅助函数
const getToken = () => {
  const currentConfig = getCurrentConfig()
  return wx.getStorageSync(currentConfig.STORAGE_KEYS.TOKEN) || ''
}

module.exports = {
  wxLogin,
  bindEmployee,
  getUserInfo,
  updateUserInfo,
  // 报告相关接口
  createReport,
  updateReport,
  deleteReport,
  getReportList,
  getReportStatistics,
  // 项目相关接口
  createProject,
  updateProject,
  deleteProject,
  getProjectList,
  getProjectListSimple,
  getProjectDetail,
  getProjectStatistics,
  createProjectProgress,
  updateProjectProgress,
  updateProgressRecord,
  getProjectProgressHistory,
  getLatestProjectProgress,
  getProjectMembers,
  addProjectMember,
  removeProjectMember,
  getProjectAttachments,
  uploadProjectAttachment,
  deleteProjectAttachment,
  toggleProjectTop,
  // 任务相关接口
  getTaskList,
  getTaskStatistics,
  createTask,
  updateTask,
  deleteTask,
  // 员工相关接口
  getEmployeeList
} 