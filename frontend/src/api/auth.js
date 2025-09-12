import request from '@/utils/request';

// 登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  });
}

// 登出
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  });
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'post'
  });
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/user/change-password',
    method: 'post',
    params: data
  });
}

// 更新用户信息
export function updateUserProfile(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  });
} 