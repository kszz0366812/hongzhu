import request from '@/utils/request'

// 拜访记录管理接口
export function getVisitRecordList(params) {
  return request({
    url: '/visit-record/list',
    method: 'get',
    params
  })
}

export function getVisitRecordPage(params) {
  return request({
    url: '/visit-record/page',
    method: 'get',
    params
  })
}

export function getVisitRecordById(id) {
  return request({
    url: `/visit-record/getById/${id}`,
    method: 'get'
  })
}

export function saveVisitRecord(data) {
  return request({
    url: '/visit-record/save',
    method: 'post',
    data
  })
}

export function updateVisitRecord(data) {
  return request({
    url: '/visit-record/update',
    method: 'post',
    data
  })
}

export function deleteVisitRecord(id) {
  return request({
    url: `/visit-record/delete/${id}`,
    method: 'post'
  })
}
