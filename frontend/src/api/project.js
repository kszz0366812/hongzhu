import request from '@/utils/request'

// 项目管理API
export const projectApi = {
  // 分页查询项目列表
  getPage(params) {
    return request({
      url: '/project/page',
      method: 'get',
      params
    })
  },

  // 获取项目列表(不分页)
  getList(params) {
    return request({
      url: '/project/getlist',
      method: 'get',
      params
    })
  },

  // 获取所有项目列表(不分页，带成员信息)
  getAll(params) {
    return request({
      url: '/project/all',
      method: 'get',
      params
    })
  },

  // 获取项目详情
  getDetail(id) {
    return request({
      url: `/project/detail/${id}`,
      method: 'get'
    })
  },

  // 获取项目统计信息
  getStatistics() {
    return request({
      url: '/project/statistics',
      method: 'get'
    })
  },

  // 创建项目
  create(data) {
    return request({
      url: '/project/create',
      method: 'post',
      data
    })
  },

  // 更新项目
  update(data) {
    return request({
      url: '/project/update',
      method: 'post',
      data
    })
  },

  // 删除项目
  delete(id) {
    return request({
      url: `/project/delete/${id}`,
      method: 'delete'
    })
  },

  // 同步项目成员
  syncMembers(projectId, members) {
    return request({
      url: `/project/sync-members/${projectId}`,
      method: 'post',
      data: members
    })
  }
}

// 项目进度管理API
export const projectProgressApi = {
  // 分页查询项目进度列表
  getPage(params) {
    return request({
      url: '/project/progress/list',
      method: 'get',
      params
    })
  },

  // 获取项目进度历史
  getHistory(projectId) {
    return request({
      url: `/project/progress/history/${projectId}`,
      method: 'get'
    })
  },

  // 获取最新项目进度
  getLatest(projectId) {
    return request({
      url: `/project/progress/latest/${projectId}`,
      method: 'get'
    })
  },

  // 获取项目进度统计信息
  getStatistics(projectId) {
    return request({
      url: '/project/progress/statistics',
      method: 'get',
      params: { projectId }
    })
  },

  // 创建项目进度
  create(data) {
    return request({
      url: '/project/progress/create',
      method: 'post',
      data
    })
  },

  // 更新项目进度
  update(data) {
    return request({
      url: '/project/progress/update',
      method: 'post',
      data
    })
  },

  // 删除项目进度
  delete(id) {
    return request({
      url: `/project/progress/delete/${id}`,
      method: 'delete'
    })
  }
}

// 项目任务管理API
export const projectTaskApi = {
  // 分页查询项目任务列表
  getPage(params) {
    return request({
      url: '/project-task/list',
      method: 'get',
      params
    })
  },

  // 获取项目任务列表(不分页)
  getAll(params) {
    return request({
      url: '/project-task/all',
      method: 'get',
      params
    })
  },

  // 获取项目任务统计信息
  getStatistics() {
    return request({
      url: '/project-task/statistics',
      method: 'get'
    })
  },

  // 创建项目任务
  create(data) {
    return request({
      url: '/project-task/create',
      method: 'post',
      data
    })
  },

  // 更新项目任务
  update(data) {
    return request({
      url: '/project-task/update',
      method: 'post',
      data
    })
  },

  // 删除项目任务
  delete(id) {
    return request({
      url: `/project-task/delete/${id}`,
      method: 'delete'
    })
  }
}
