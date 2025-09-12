import request from '@/utils/request';

// 分页获取接口模板列表
export function getTemplateListPage(current, size, params) {
  return request({
    url: '/itfTemplate/page',
    method: 'get',
    params: {
      current,
      size,
      ...params
    }
  });
}
// 获取接口模板列表
export function getTemplateList(data) {
  return request({
    url: '/itfTemplate/getlist',
    method: 'post',
    data
  });
}

// 创建接口模板
export function createTemplate(data) {
  return request({
    url: '/itfTemplate',
    method: 'post',
    data
  });
}

// 更新接口模板
export function updateTemplate(data) {
  return request({
    url: '/itfTemplate',
    method: 'put',
    data
  });
}

// 删除接口模板
export function deleteTemplate(id) {
  return request({
    url: `/itfTemplate/${id}`,
    method: 'delete'
  });
}

  //调用自定义接口
  export function callCustomInterface(id,data) {
    return request({
      url: `/customize/getInterfaceInfo/${id}`,
      method: 'post',
      data
    });
} 
//分页调用自定义接口 
export function getCustomInterfacePage(id, current, size, data) {
  return request({
    url: `/customize/getInterfaceInfoPage/${id}/${current}/${size}`,
    method: 'post',
    data
  });
}
//自定义接口参数列表
export function getCustomInterfaceParams(id) {
  return request({
    url: `/customize/getInterfaceParam/${id}`,
    method: 'get'
  });
}
//自定义接口字段列表
export function getCustomInterfaceField(id) {
  return request({
    url: `/customize/getInterfaceColumn/${id}`,
    method: 'get'
  });
}
