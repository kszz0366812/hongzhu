import request from '../utils/request.js';

// 用户管理接口
export function getUserPage(params) {
  return request({
    url: '/user/page',
    method: 'get',
    params
  });
}

export function getUserById(id) {
  return request({
    url: `/user/getById/${id}`,
    method: 'get'
  });
}

export function addUser(data) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  });
}

export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  });
}

export function deleteUser(id) {
  return request({
    url: `/user/delete/${id}`,
    method: 'post'
  });
}

export function resetUserPassword(id) {
  return request({
    url: `/user/reset-password/${id}`,
    method: 'post'
  });
}

export function changeUserStatus(id, status) {
  return request({
    url: `/user/change-status/${id}`,
    method: 'post',
    params: { status }
  });
}

export function getUserRoles(userId) {
  return request({
    url: `/user/roles/${userId}`,
    method: 'get'
  });
}

export function assignUserRoles(userId, roleIds) {
  return request({
    url: `/user/assign-roles/${userId}`,
    method: 'post',
    data: { roleIds }
  });
}

// 员工管理接口
export function getEmployeeList(params = {}) {
  return request({
    url: '/employee/list',
    method: 'get',
    params
  });
}

export function getEmployeePage(params = {}) {
  return request({
    url: '/employee/page',
    method: 'get',
    params
  });
}

export function getEmployeeById(id) {
  return request({
    url: `/employee/getById/${id}`,
    method: 'get'
  });
}

export function addEmployee(data) {
  return request({
    url: '/employee/save',
    method: 'post',
    data
  });
}

export function updateEmployee(data) {
  return request({
    url: '/employee',
    method: 'post',
    data
  });
}

export function deleteEmployee(id) {
  return request({
    url: `/employee/delete/${id}`,
    method: 'post'
  });
}

export function searchEmployees(keyword) {
  return request({
    url: '/employee/list',
    method: 'get',
    params: { keyword }
  });
}

// Excel导入相关接口 - 通用接口
export function importExcelData(file, importType) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('importType', importType);
  
  return request({
    url: '/excel/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

export function downloadExcelTemplate(importType) {
  return request({
    url: '/excel/template',
    method: 'get',
    params: { importType },
    responseType: 'blob'
  });
}

// 获取支持的Excel导入类型
export function getSupportedExcelTypes() {
  return request({
    url: '/excel/supported-types',
    method: 'get'
  });
}

// 获取导入类型详细信息
export function getExcelTypeInfo(importType) {
  return request({
    url: '/excel/type-info',
    method: 'get',
    params: { importType }
  });
}

// 角色管理接口
export function getRoleList(params = {}) {
  return request({
    url: '/role/list',
    method: 'get',
    params
  });
}

export function addRole(data) {
  return request({
    url: '/role/add',
    method: 'post',
    data
  });
}

export function updateRole(data) {
  return request({
    url: '/role/update',
    method: 'post',
    data
  });
}

export function changeRoleStatus(id, status) {
  return request({
    url: `/role/status/${id}`,
    method: 'post',
    data: { status }
  });
}

export function deleteRole(roleId) {
  return request({
    url: `/role/delete/${roleId}`,
    method: 'post'
  });
}

// 权限管理接口
export function getPermissionTree() {
  return request({
    url: '/system/permission/tree',
    method: 'get'
  });
}

export function getRolePermissions(roleId) {
  return request({
    url: `/system/role/${roleId}/permissions`,
    method: 'get'
  });
}

export function updateRolePermissions(roleId, permissionIds) {
  return request({
    url: `/system/role/${roleId}/permissions`,
    method: 'post',
    data: { permissionIds }
  });
}

// 数据字典管理接口
export function getDictionaryList(params) {
  return request({
    url: '/dictionary/page',
    method: 'get',
    params
  });
}

export function getDictionaryTree(params) {
  return request({
    url: '/dictionary/tree',
    method: 'get',
    params
  });
}

export function getDictionaryByType(type) {
  return request({
    url: `/dictionary/type/${type}`,
    method: 'get'
  });
}

export function getDictionaryByParentId(parentId) {
  return request({
    url: `/dictionary/parent/${parentId}`,
    method: 'get'
  });
}

export function addDictionary(data) {
  return request({
    url: '/dictionary/add',
    method: 'post',
    data
  });
}

export function updateDictionary(data) {
  return request({
    url: '/dictionary/update',
    method: 'post',
    data
  });
}

export function deleteDictionary(id) {
  return request({
    url: `/dictionary/delete/${id}`,
    method: 'post'
  });
}

export function batchDeleteDictionary(ids) {
  return request({
    url: '/dictionary/batch-delete',
    method: 'post',
    data: ids
  });
}

export function clearDictionaryCache() {
  return request({
    url: '/dictionary/clear-cache',
    method: 'post'
  });
}

// 菜单管理接口
export function getMenuTree() {
  return request({
    url: '/menu/tree',
    method: 'get'
  });
}

export function getUserMenus(userId) {
  return request({
    url: `/menu/user/${userId}`,
    method: 'get'
  });
}

export function getUserPerms(userId) {
  return request({
    url: `/menu/perms/${userId}`,
    method: 'get'
  });
}

export function createMenu(data) {
  return request({
    url: '/menu/createMenu',
    method: 'post',
    data
  });
}

export function updateMenu(data) {
  return request({
    url: '/menu/updateMenu',
    method: 'post',
    data
  });
}

export function deleteMenu(menuId) {
  return request({
    url: `/menu/deleteMenu/${menuId}`,
    method: 'post'
  });
}

// 角色菜单管理接口
export function getRoleMenus(roleId) {
  return request({
    url: `/role/listRoleMenus/${roleId}`,
    method: 'get'
  });
}

export function assignRoleMenus(roleId, menuIds) {
  return request({
    url: `/role/assignRoleMenus/${roleId}`,
    method: 'post',
    data: menuIds
  });
}

// 头像上传接口
export function uploadAvatar(file, userId,oldFilePath) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('refType', 'user');
  formData.append('refId', userId);
  formData.append('oldFilePath', oldFilePath);
  return request({
    url: '/api/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
} 

// 任务目标管理接口
export function getTaskTargetPage(params = {}) {
  return request({
    url: '/task-target/page',
    method: 'get',
    params
  });
}

export function getTaskTargetList(executorId, taskName) {
  return request({
    url: '/task-target/list',
    method: 'get',
    params: { executorId, taskName }
  });
}

export function getTaskTargetById(id) {
  return request({
    url: `/task-target/getById/${id}`,
    method: 'get'
  });
}

export function saveTaskTarget(data) {
  return request({
    url: '/task-target/save',
    method: 'post',
    data
  });
}

export function updateTaskTarget(data) {
  return request({
    url: '/task-target/update',
    method: 'post',
    data
  });
}

export function deleteTaskTarget(id) {
  return request({
    url: `/task-target/delete/${id}`,
    method: 'post'
  });
}

export function updateTaskTargetAchievedAmount(id, achievedAmount) {
  return request({
    url: `/task-target/achieved-amount/${id}`,
    method: 'post',
    params: { achievedAmount }
  });
}

// 任务目标导入相关接口
export function importTaskTargetData(file) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('importType', '任务目标导入');
  
  return request({
    url: '/excel/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

export function downloadTaskTargetTemplate() {
  return request({
    url: '/excel/template',
    method: 'get',
    params: { importType: '任务目标导入' },
    responseType: 'blob'
  });
} 