import request from '@/utils/request'

// 批发商管理API
export const wholesalerApi = {
  // 分页查询批发商列表
  getPage(params) {
    return request({
      url: '/wholesaler/page',
      method: 'get',
      params
    })
  },

  // 获取批发商列表(不分页)
  getList(params) {
    return request({
      url: '/wholesaler/list',
      method: 'get',
      params
    })
  },

  // 获取批发商详情
  getById(id) {
    return request({
      url: `/wholesaler/getById/${id}`,
      method: 'get'
    })
  },

  // 新增批发商
  save(data) {
    return request({
      url: '/wholesaler/save',
      method: 'post',
      data
    })
  },

  // 修改批发商
  update(data) {
    return request({
      url: '/wholesaler/update',
      method: 'post',
      data
    })
  },

  // 删除批发商
  delete(id) {
    return request({
      url: `/wholesaler/delete/${id}`,
      method: 'delete'
    })
  },

  // 获取批发商等级列表
  getLevels() {
    return request({
      url: '/wholesaler/levels',
      method: 'get'
    })
  },

  // 获取批发商客户经理
  getManager(id) {
    return request({
      url: `/wholesaler/manager/${id}`,
      method: 'get'
    })
  }
}
