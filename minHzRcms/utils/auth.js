// utils/auth.js

const api = require('./api.js')
const config = require('../config/wx-config.js')

// 获取当前配置
const getCurrentConfig = () => {
  try {
    if (typeof wx !== 'undefined' && wx.getSystemInfoSync) {
      const systemInfo = wx.getSystemInfoSync()
      if (systemInfo.platform === 'devtools') {
        // 在微信开发者工具中，使用本地服务器
        return {
          ...config,
          API_BASE_URL: 'http://localhost:8080/api'
        }
      }
    }
  } catch (e) {
    console.log('无法获取系统信息，使用默认配置')
  }
  return config
}

// 静默微信登录（只获取openid，不获取用户信息）
const wxSilentLogin = () => {
  return new Promise((resolve, reject) => {
    console.log('🔄 开始静默登录流程...')
    
    // 测试：显示一个简单的提示，确认方法被调用
    wx.showToast({
      title: '开始微信登录',
      icon: 'none',
      duration: 1500
    })
    
    // 1. 获取微信登录code
    console.log('📱 调用wx.login获取code...')
    wx.login({
      success: (loginRes) => {
        console.log('✅ wx.login成功:', loginRes)
        wx.showToast({
          title: '获取code成功',
          icon: 'success',
          duration: 1500
        })
        
        if (loginRes.code) {
          console.log('🔑 获取到登录code:', loginRes.code)
          // 2. 调用后端登录接口，只传递code
          console.log('🌐 调用后端登录接口...')
          api.wxLogin(loginRes.code)
            .then((response) => {
              console.log('📡 API登录响应:', response)
              if (response.code === 200 && response.data.success) {
                const currentConfig = getCurrentConfig()
                // 登录成功，保存token和用户信息
                wx.setStorageSync(currentConfig.STORAGE_KEYS.TOKEN, response.data.token)
                wx.setStorageSync(currentConfig.STORAGE_KEYS.USER_INFO, response.data.userInfo)
                wx.setStorageSync(currentConfig.STORAGE_KEYS.OPENID, response.data.openid)
                wx.setStorageSync(currentConfig.STORAGE_KEYS.UNIONID, response.data.unionid)
                wx.showToast({
                  title: '登录成功',
                  icon: 'success',
                  duration: 2000
                })
                
                // 3. 返回登录结果，让app.js处理新用户绑定
                resolve(response.data)
              } else {
                console.error('❌ 登录失败，响应数据:', response)
                // 不显示登录失败弹框，静默处理
                reject(new Error(response.message || '登录失败'))
              }
            })
            .catch((error) => {
              console.error('❌ API调用失败:', error)
              // 不显示API调用失败弹框，静默处理
              reject(error)
            })
        } else {
          console.error('❌ 获取登录code失败')
          // 不显示获取code失败弹框，静默处理
          reject(new Error('获取登录code失败'))
        }
      },
      fail: (error) => {
        console.error('❌ wx.login失败:', error)
        // 不显示微信登录失败弹框，静默处理
        reject(new Error('微信登录失败'))
      }
    })
  })
}

// 显示员工号输入弹窗（必须绑定，无法取消）
const showEmployeeIdInputModal = (token) => {
  // 先显示确认弹窗
  wx.showModal({
    title: '员工号绑定',
    content: '检测到您是首次登录，必须绑定员工号才能使用系统功能。',
    showCancel: false, // 不显示取消按钮
    confirmText: '立即绑定',
    success: (modalRes) => {
      if (modalRes.confirm) {
        // 用户确认后，显示输入弹窗
        showEmployeeIdInputPrompt(token)
      } else {
        // 用户没有确认，重新显示弹窗
        setTimeout(() => {
          showEmployeeIdInputModal(token)
        }, 500)
      }
    }
  })
}

// 显示员工号输入提示框
const showEmployeeIdInputPrompt = (token) => {
  wx.showModal({
    title: '输入员工号',
    content: '',
    editable: true,
    placeholderText: '请输入员工号',
    showCancel: false, // 不显示取消按钮
    confirmText: '确认绑定',
    success: (inputRes) => {
      if (inputRes.confirm && inputRes.content) {
        const employeeCode = inputRes.content.trim()
        if (employeeCode) {
          // 调用绑定接口
          bindEmployee(employeeCode)
            .then((response) => {
              console.log('员工绑定成功:', response)
              
              // 绑定成功后，更新本地存储的用户信息和token
              if (response.code === 200 && response.data) {
                const currentConfig = getCurrentConfig()
                
                // 更新用户信息
                if (response.data.userInfo) {
                  wx.setStorageSync(currentConfig.STORAGE_KEYS.USER_INFO, response.data.userInfo)
                  console.log('绑定成功后，用户信息已更新到本地存储:', response.data.userInfo)
                  console.log('👤 绑定后用户信息详细字段:', Object.keys(response.data.userInfo))
                  console.log('👤 绑定后用户信息完整内容:', JSON.stringify(response.data.userInfo, null, 2))
                }
                
                // 更新token（如果返回了新的token）
                if (response.data.token) {
                  wx.setStorageSync(currentConfig.STORAGE_KEYS.TOKEN, response.data.token)
                  console.log('绑定成功后，token已更新到本地存储:', response.data.token)
                }
              }
              
              wx.showToast({
                title: '绑定成功',
                icon: 'success'
              })
            })
            .catch((error) => {
              console.error('员工绑定失败:', error)
              wx.showToast({
                title: error.message || '绑定失败',
                icon: 'none'
              })
              // 绑定失败后重新显示输入弹窗
              setTimeout(() => {
                showEmployeeIdInputPrompt(token)
              }, 1500)
            })
        } else {
          wx.showToast({
            title: '员工号不能为空',
            icon: 'none'
          })
          // 重新显示输入弹窗
          setTimeout(() => {
            showEmployeeIdInputPrompt(token)
          }, 1500)
        }
      } else {
        // 用户没有输入内容，重新显示弹窗
        setTimeout(() => {
          showEmployeeIdInputPrompt(token)
        }, 500)
      }
    }
  })
}

// 获取用户信息（需要用户授权）
const getUserProfile = () => {
  return new Promise((resolve, reject) => {
    console.log('开始获取用户信息...')
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        console.log('获取用户信息成功:', res)
        const userInfo = res.userInfo
        
        // 更新用户信息到后端
        api.updateUserInfo(userInfo)
          .then((response) => {
            console.log('更新用户信息响应:', response)
            if (response.code === 200) {
              const currentConfig = getCurrentConfig()
              // 更新本地存储的用户信息
              wx.setStorageSync(currentConfig.STORAGE_KEYS.USER_INFO, userInfo)
              console.log('用户信息已更新到本地存储')
              resolve(userInfo)
            } else {
              console.error('更新用户信息失败:', response)
              reject(new Error(response.message || '更新用户信息失败'))
            }
          })
          .catch((error) => {
            console.error('更新用户信息API调用失败:', error)
            reject(error)
          })
      },
      fail: (error) => {
        console.error('获取用户信息失败:', error)
        reject(new Error('获取用户信息失败'))
      }
    })
  })
}

// 检查登录状态
const checkLoginStatus = () => {
  const currentConfig = getCurrentConfig()
  const token = wx.getStorageSync(currentConfig.STORAGE_KEYS.TOKEN)
  const openid = wx.getStorageSync(currentConfig.STORAGE_KEYS.OPENID)
  
  const isLoggedIn = !!(token && openid)
  console.log('检查登录状态:', {
    hasToken: !!token,
    hasOpenid: !!openid,
    isLoggedIn: isLoggedIn
  })
  
  return isLoggedIn
}

// 获取用户信息（自动处理登录和绑定）
const getUserInfo = () => {
  const currentConfig = getCurrentConfig()
  const userInfo = wx.getStorageSync(currentConfig.STORAGE_KEYS.USER_INFO)
  
  // 如果没有用户信息，触发静默登录
  if (!userInfo) {
    console.log('🔍 getUserInfo: 用户信息不存在，触发静默登录')
    const app = getApp()
    if (app && app.performGlobalSilentLogin) {
      // 返回Promise，让调用方等待登录完成
      return app.performGlobalSilentLogin()
        .then(() => {
          console.log('🔍 getUserInfo: 静默登录成功，重新获取用户信息')
          const newUserInfo = wx.getStorageSync(currentConfig.STORAGE_KEYS.USER_INFO)
          if (!newUserInfo) {
            throw new Error('登录成功但未获取到用户信息')
          }
          return newUserInfo
        })
        .catch((error) => {
          console.error('🔍 getUserInfo: 静默登录失败:', error)
          // 不显示弹框，让调用方处理
          throw error
        })
    }
    return Promise.reject(new Error('无法执行静默登录'))
  }
  
  // 如果有用户信息但没有员工ID，触发员工绑定
  if (!userInfo.employeeId) {
    console.log('🔍 getUserInfo: 用户未绑定员工号，触发员工绑定')
    const app = getApp()
    if (app && app.showGlobalEmployeeBindingModal) {
      app.showGlobalEmployeeBindingModal()
    }
    // 不返回用户信息，防止越权操作
    return Promise.reject(new Error('用户未绑定员工号，请先完成绑定'))
  }
  
  return Promise.resolve(userInfo)
}



// 获取token
const getToken = () => {
  const currentConfig = getCurrentConfig()
  return wx.getStorageSync(currentConfig.STORAGE_KEYS.TOKEN)
}

// 获取openid
const getOpenid = () => {
  const currentConfig = getCurrentConfig()
  return wx.getStorageSync(currentConfig.STORAGE_KEYS.OPENID)
}

// 绑定员工信息（只需要employeeCode）
const bindEmployee = (employeeCode) => {
  console.log('绑定员工信息:', { employeeCode })
  return api.bindEmployee(employeeCode)
}

// 错误码处理
const handleWxError = (errcode) => {
  const currentConfig = getCurrentConfig()
  return currentConfig.ERROR_CODES[errcode] || '未知错误'
}

module.exports = {
  wxSilentLogin,
  getUserProfile,
  checkLoginStatus,
  getUserInfo,
  getToken,
  getOpenid,
  bindEmployee,
  handleWxError
} 