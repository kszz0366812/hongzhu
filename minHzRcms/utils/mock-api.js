// utils/mock-api.js

// 模拟API响应，用于开发环境测试
const mockApi = {
  // 模拟微信登录
  wxLogin: (code) => {
    console.log('模拟API: wxLogin被调用，code:', code)
    return new Promise((resolve) => {
      setTimeout(() => {
        const response = {
          code: 200,
          message: 'success',
          data: {
            success: true,
            token: 'mock-jwt-token-' + Date.now(),
            openid: 'mock-openid-' + Math.random().toString(36).substr(2, 9),
            unionid: 'mock-unionid-' + Math.random().toString(36).substr(2, 9),
            userInfo: {
              id: 1,
              nickname: '测试用户',
              avatarUrl: 'https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0',
              gender: 1,
              country: '中国',
              province: '广东',
              city: '深圳',
              isBound: 0
            }
          }
        }
        console.log('模拟API: wxLogin返回响应:', response)
        resolve(response)
      }, 500) // 模拟网络延迟
    })
  },

  // 模拟更新用户信息
  updateUserInfo: (userInfo) => {
    console.log('模拟API: updateUserInfo被调用，userInfo:', userInfo)
    return new Promise((resolve) => {
      setTimeout(() => {
        const response = {
          code: 200,
          message: 'success',
          data: userInfo
        }
        console.log('模拟API: updateUserInfo返回响应:', response)
        resolve(response)
      }, 300)
    })
  },

  // 模拟检查员工信息
  checkEmployee: (phone) => {
    console.log('模拟API: checkEmployee被调用，phone:', phone)
    return new Promise((resolve) => {
      setTimeout(() => {
        // 模拟手机号 13800138000 存在员工信息
        let response
        if (phone === '13800138000') {
          response = {
            code: 200,
            message: 'success',
            data: {
              exists: true,
              employeeId: 'EMP001',
              employeeName: '张三',
              phone: phone
            }
          }
        } else {
          response = {
            code: 200,
            message: 'success',
            data: {
              exists: false
            }
          }
        }
        console.log('模拟API: checkEmployee返回响应:', response)
        resolve(response)
      }, 300)
    })
  },

  // 模拟绑定员工信息
  bindEmployee: (employeeId, phone) => {
    console.log('模拟API: bindEmployee被调用，employeeId:', employeeId, 'phone:', phone)
    return new Promise((resolve) => {
      setTimeout(() => {
        const response = {
          code: 200,
          message: '绑定成功',
          data: {
            success: true,
            employeeId: employeeId,
            phone: phone
          }
        }
        console.log('模拟API: bindEmployee返回响应:', response)
        resolve(response)
      }, 500)
    })
  },

  // 模拟获取用户信息
  getUserInfo: () => {
    console.log('模拟API: getUserInfo被调用')
    return new Promise((resolve) => {
      setTimeout(() => {
        const response = {
          code: 200,
          message: 'success',
          data: {
            id: 1,
            nickname: '测试用户',
            avatarUrl: 'https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0',
            gender: 1,
            country: '中国',
            province: '广东',
            city: '深圳',
            isBound: 0
          }
        }
        console.log('模拟API: getUserInfo返回响应:', response)
        resolve(response)
      }, 200)
    })
  }
}

module.exports = mockApi 