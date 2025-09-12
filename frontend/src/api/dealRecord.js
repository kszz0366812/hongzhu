import request from '@/utils/request'

export const dealRecordApi = {
  // 获取成交记录列表
  getList(params) {
    return request({
      url: '/deal-record/list',
      method: 'get',
      params
    })
  },

  // 分页查询成交记录
  getPage(params) {
    return request({
      url: '/deal-record/page',
      method: 'get',
      params
    })
  },

  // 获取成交记录详情
  getById(id) {
    return request({
      url: `/deal-record/getById/${id}`,
      method: 'get'
    })
  },

  // 新增成交记录
  save(data) {
    return request({
      url: '/deal-record/save',
      method: 'post',
      data
    })
  },

  // 更新成交记录
  update(data) {
    return request({
      url: '/deal-record/update',
      method: 'post',
      data
    })
  },

  // 删除成交记录
  delete(id) {
    return request({
      url: `/deal-record/delete/${id}`,
      method: 'delete'
    })
  },

  // 获取成交记录统计
  getStats(params) {
    return request({
      url: '/deal-record/stats',
      method: 'get',
      params
    })
  },

  // 批量导入成交记录
  batchImport(data) {
    return request({
      url: '/deal-record/import',
      method: 'post',
      data
    })
  }
}
