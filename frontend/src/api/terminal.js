import request from '@/utils/request'

// 终端管理API
export const terminalApi = {
  // 分页查询终端列表
  getPage(params) {
    return request({
      url: '/terminal/page',
      method: 'get',
      params
    })
  },

  // 获取终端列表(不分页)
  getList(params) {
    return request({
      url: '/terminal/list',
      method: 'get',
      params
    })
  },

  // 获取终端详情
  getById(id) {
    return request({
      url: `/terminal/getById/${id}`,
      method: 'get'
    })
  },

  // 新增终端
  save(data) {
    return request({
      url: '/terminal/save',
      method: 'post',
      data
    })
  },

  // 修改终端
  update(data) {
    return request({
      url: '/terminal/update',
      method: 'post',
      data
    })
  },

  // 删除终端
  delete(id) {
    return request({
      url: `/terminal/delete/${id}`,
      method: 'delete'
    })
  }
} 