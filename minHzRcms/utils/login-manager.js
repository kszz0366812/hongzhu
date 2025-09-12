// utils/login-manager.js

const auth = require('./auth.js')

// 全局登录状态管理器
const LoginManager = {
  // 检查登录状态
  checkLoginStatus: function() {
    const app = getApp()
    return app.checkLoginAndBinding()
  },
  
  // 页面跳转前的权限检查
  navigateWithCheck: function(url, type = 'navigateTo') {
    const app = getApp()
    const status = app.checkLoginAndBinding()
    
    if (!status.isLoggedIn) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return false
    }
    
    if (!status.hasEmployeeBinding) {
      wx.showModal({
        title: '员工号绑定',
        content: '请先绑定员工号才能使用此功能',
        showCancel: false,
        confirmText: '去绑定',
        success: (res) => {
          if (res.confirm) {
            wx.switchTab({
              url: '/pages/personal/personal'
            })
          }
        }
      })
      return false
    }
    
    // 权限检查通过，执行跳转
    switch (type) {
      case 'navigateTo':
        wx.navigateTo({ url })
        break
      case 'redirectTo':
        wx.redirectTo({ url })
        break
      case 'switchTab':
        wx.switchTab({ url })
        break
      case 'reLaunch':
        wx.reLaunch({ url })
        break
      default:
        wx.navigateTo({ url })
    }
    
    return true
  },
  
  // 获取用户信息
  getUserInfo: function() {
    return auth.getUserInfo()
  },
  
  // 检查是否已登录
  isLoggedIn: function() {
    return auth.checkLoginStatus()
  },
  
  // 检查是否已绑定员工号
  hasEmployeeBinding: function() {
    const userInfo = auth.getUserInfo()
    return userInfo && userInfo.employeeCode
  },
  
  // 获取员工号
  getEmployeeCode: function() {
    const userInfo = auth.getUserInfo()
    return userInfo ? userInfo.employeeCode : null
  },
  
  // 获取用户昵称
  getNickname: function() {
    const userInfo = auth.getUserInfo()
    return userInfo ? userInfo.nickname : null
  },
  
  // 获取用户头像
  getAvatarUrl: function() {
    const userInfo = auth.getUserInfo()
    return userInfo ? userInfo.avatarUrl : null
  },
  
  // 强制重新登录
  forceRelogin: function() {
    const app = getApp()
    return app.performGlobalSilentLogin()
  },
  
  // 清除登录状态
  clearLoginStatus: function() {
    const config = require('../config/wx-config.js')
    wx.removeStorageSync(config.STORAGE_KEYS.TOKEN)
    wx.removeStorageSync(config.STORAGE_KEYS.USER_INFO)
    wx.removeStorageSync(config.STORAGE_KEYS.OPENID)
    wx.removeStorageSync(config.STORAGE_KEYS.UNIONID)
    
    console.log('登录状态已清除')
  },
  
  // 监听登录状态变化
  onLoginStatusChange: function(callback) {
    const app = getApp()
    app.setLoginSuccessCallback(callback)
  },
  
  // 监听登录失败
  onLoginFail: function(callback) {
    const app = getApp()
    app.setLoginFailCallback(callback)
  },
  
  // 监听绑定成功
  onBindingSuccess: function(callback) {
    const app = getApp()
    app.setBindingSuccessCallback(callback)
  }
}

// 页面权限装饰器
function requireLogin(pageConfig) {
  const originalOnLoad = pageConfig.onLoad
  const originalOnShow = pageConfig.onShow
  
  pageConfig.onLoad = function(options) {
    if (originalOnLoad) {
      originalOnLoad.call(this, options)
    }
  }
  
  pageConfig.onShow = function() {
    if (originalOnShow) {
      originalOnShow.call(this)
    }
  }
  
  return pageConfig
}

module.exports = {
  LoginManager,
  requireLogin
} 