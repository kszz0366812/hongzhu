// utils/page-base.js

const auth = require('./auth.js')

// 页面基类，提供全局登录状态检查
const PageBase = {
  // 页面加载时的处理
  onLoad: function(options) {
    console.log('页面基类 onLoad:', this.route)
    
    // 调用子类的 onLoad
    if (this.pageOnLoad) {
      this.pageOnLoad(options)
    }
  },
  
  // 页面显示时的处理
  onShow: function() {
    console.log('页面基类 onShow:', this.route)
    
    // 调用子类的 onShow
    if (this.pageOnShow) {
      this.pageOnShow()
    }
  },
  
  // 页面跳转前的权限检查
  navigateTo: function(url) {
    const app = getApp()
    if (app.checkBeforeNavigate(url)) {
      wx.navigateTo({ url })
    }
  },
  
  // 重定向前的权限检查
  redirectTo: function(url) {
    const app = getApp()
    if (app.checkBeforeNavigate(url)) {
      wx.redirectTo({ url })
    }
  },
  
  // 切换标签页前的权限检查
  switchTab: function(url) {
    const app = getApp()
    if (app.checkBeforeNavigate(url)) {
      wx.switchTab({ url })
    }
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
  
  // 获取登录状态
  getLoginStatus: function() {
    const app = getApp()
    return app.checkLoginAndBinding()
  }
}

// 创建带登录检查的页面
function createPage(pageConfig) {
  // 保存原始的 onLoad 和 onShow
  const originalOnLoad = pageConfig.onLoad
  const originalOnShow = pageConfig.onShow
  
  // 重写 onLoad
  pageConfig.onLoad = function(options) {
    // 先执行基类的 onLoad
    PageBase.onLoad.call(this, options)
    
    // 再执行原始的 onLoad
    if (originalOnLoad) {
      originalOnLoad.call(this, options)
    }
  }
  
  // 重写 onShow
  pageConfig.onShow = function() {
    // 先执行基类的 onShow
    PageBase.onShow.call(this)
    
    // 再执行原始的 onShow
    if (originalOnShow) {
      originalOnShow.call(this)
    }
  }
  
  // 合并基类方法
  Object.assign(pageConfig, PageBase)
  
  // 创建页面
  return Page(pageConfig)
}

module.exports = {
  PageBase,
  createPage
} 