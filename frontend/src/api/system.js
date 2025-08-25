import request from '../utils/request.js';

// 用户管理接口
export function getUserList(params) {
  return request({
    url: '/user/list',
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
    url: '/dictionary/list',
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