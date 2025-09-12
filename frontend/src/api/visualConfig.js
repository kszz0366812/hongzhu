import request from '../utils/request'

// 分页获取可视化模板列表
export function getVisualTemplatePage(current, size, params) {
  return request({
    url: '/pvTemplate/page',
    method: 'get',
    params: {
      current,
      size,
      ...params
    }
  })
}

// 获取所有可配置数据表
export function addVisualTemplate(data) {
  return request({
    url: '/pvTemplate',
    method: 'post',
    data: data
  })
}

// 更新可视化模版
export function updateVisualTemplate(data) {
  return request({
    url: '/pvTemplate',
    method: 'put',
    data: data
  })  
}

export function deleteVisualTemplate(id) {
  return request({
    url: '/pvTemplate/' + id,
    method: 'delete',
    params: { id: id }
  })
}