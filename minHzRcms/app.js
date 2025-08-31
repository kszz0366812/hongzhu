// app.js

const auth = require('./utils/auth.js')

// 全局错误处理
if (typeof window !== 'undefined') {
  // 防止 window.__global 相关错误
  if (!window.__global) {
    window.__global = {}
  }
  
  // 提供默认的 getActiveAppWindow 和 getInstanceWindow 函数
  if (!window.__global.getActiveAppWindow) {
    window.__global.getActiveAppWindow = function() {
      return null
    }
  }
  
  if (!window.getInstanceWindow) {
    window.getInstanceWindow = function() {
      return null
    }
  }
  
  if (!window.__global.getInstanceWindow) {
    window.__global.getInstanceWindow = function() {
      return null
    }
  }
}

App({
  onLaunch: function () {
    // 小程序启动时执行的逻辑
    console.log('🚀 App launched - 应用启动')
    console.log('🔐 开始执行全局静默登录...')
    
    // 测试：显示一个简单的提示，确认代码被执行
    wx.showToast({
      title: '应用启动，开始登录',
      icon: 'none',
      duration: 2000
    })
    
    // 立即执行静默登录
    this.performGlobalSilentLogin()
      .then((result) => {
        console.log('✅ 全局静默登录完成:', result)
        wx.showToast({
          title: '登录成功',
          icon: 'success',
          duration: 2000
        })
      })
      .catch((error) => {
        console.error('❌ 全局静默登录失败:', error)
        // 不显示登录失败弹框，静默处理
        console.log('应用启动时登录失败，但不显示错误提示')
      })
    
    // 检查更新
    if (wx.canIUse('getUpdateManager')) {
      const updateManager = wx.getUpdateManager()
      updateManager.onCheckForUpdate(function (res) {
        if (res.hasUpdate) {
          updateManager.onUpdateReady(function () {
            wx.showModal({
              title: '更新提示',
              content: '新版本已经准备好，是否重启应用？',
              success: function (res) {
                if (res.confirm) {
                  updateManager.applyUpdate()
                }
              }
            })
          })
        }
      })
    }
  },
  
  onShow: function () {
    // 小程序显示时执行
    console.log('App onShow')
    
    // 检查登录状态，如果未登录则重新登录
    if (!auth.checkLoginStatus()) {
      console.log('检测到未登录状态，重新执行静默登录')
      this.performGlobalSilentLogin()
    } else {
      console.log('用户已登录，检查用户信息')
      this.updateGlobalData()
    }
  },
  
  onHide: function () {
    // 小程序隐藏时执行
    console.log('App onHide')
  },
  
  // 全局静默登录（核心功能）
  performGlobalSilentLogin: function() {
    console.log('🔐 开始全局静默登录...')
    
    // 如果已经登录，直接返回
    if (auth.checkLoginStatus()) {
      console.log('✅ 用户已登录，跳过静默登录')
      this.updateGlobalData()
      return Promise.resolve()
    }
    
    console.log('🔄 用户未登录，开始执行微信登录...')
    
    return auth.wxSilentLogin()
      .then((data) => {
        console.log('✅ 全局静默登录成功:', data)
        
        // 更新全局数据
        this.updateGlobalData()
        
        // 检查是否为新用户，如果是则自动显示员工绑定弹窗
        if (data.isNewUser === true) {
          console.log('🆕 检测到新用户，检查是否需要绑定员工号')
          
          // 检查是否已经绑定员工号
          const userInfo = auth.getUserInfo()
          if (userInfo && userInfo.employeeCode) {
            console.log('用户已绑定员工号，跳过绑定弹窗')
          } else {
            console.log('用户未绑定员工号，显示绑定弹窗')
            this.showGlobalEmployeeBindingModal()
          }
        } else {
          console.log('👤 老用户，无需绑定员工号')
        }
        
        // 触发登录成功事件
        if (this.loginSuccessCallback) {
          this.loginSuccessCallback(data)
        }
        
        return data
      })
      .catch((error) => {
        console.error('❌ 全局静默登录失败:', error)
        
        // 登录失败时的处理 - 不显示弹框，静默处理
        console.log('静默登录失败，但不显示错误提示')
        
        // 触发登录失败事件
        if (this.loginFailCallback) {
          this.loginFailCallback(error)
        }
        
        throw error
      })
  },
  
  // 全局员工绑定弹窗（两步流程）
  showGlobalEmployeeBindingModal: function() {
    console.log('显示全局员工绑定弹窗')
    
    // 检查是否已经绑定或正在绑定中
    if (this.globalData.isBindingInProgress) {
      console.log('绑定正在进行中，跳过弹窗显示')
      return
    }
    
    // 检查是否已经绑定员工号（使用同步检查避免递归）
    const currentConfig = require('./config/wx-config.js')
    const userInfo = wx.getStorageSync(currentConfig.STORAGE_KEYS.USER_INFO)
    if (userInfo && userInfo.employeeCode) {
      console.log('用户已绑定员工号，跳过弹窗显示')
      return
    }
    
    // 设置绑定进行中标志
    this.globalData.isBindingInProgress = true
    
    // 第一步：确认弹窗
    wx.showModal({
      title: '员工号绑定',
      content: '检测到您是首次登录，必须绑定员工号才能使用系统功能。',
      showCancel: false, // 不显示取消按钮
      confirmText: '立即绑定',
      success: (modalRes) => {
        if (modalRes.confirm) {
          // 用户确认后，显示输入弹窗
          this.showGlobalEmployeeInputPrompt()
        } else {
          // 用户没有确认，重新显示弹窗
          setTimeout(() => {
            this.showGlobalEmployeeBindingModal()
          }, 500)
        }
      }
    })
  },
  
  // 全局员工号输入弹窗
  showGlobalEmployeeInputPrompt: function() {
    // 检查是否已经绑定员工号（使用同步检查避免递归）
    const currentConfig = require('./config/wx-config.js')
    const userInfo = wx.getStorageSync(currentConfig.STORAGE_KEYS.USER_INFO)
    if (userInfo && userInfo.employeeCode) {
      console.log('用户已绑定员工号，跳过输入弹窗显示')
      this.globalData.isBindingInProgress = false
      return
    }
    
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
            this.performGlobalEmployeeBinding(employeeCode)
          } else {
            wx.showToast({
              title: '员工号不能为空',
              icon: 'none'
            })
            // 重新显示输入弹窗
            setTimeout(() => {
              this.showGlobalEmployeeInputPrompt()
            }, 1500)
          }
        } else {
          // 用户没有输入内容，重新显示弹窗
          setTimeout(() => {
            this.showGlobalEmployeeInputPrompt()
          }, 500)
        }
      }
    })
  },
  
  // 执行全局员工绑定
  performGlobalEmployeeBinding: function(employeeCode) {
    console.log('执行全局员工绑定:', employeeCode)
    
    wx.showLoading({ title: '绑定中...' })
    
    auth.bindEmployee(employeeCode)
      .then((response) => {
        wx.hideLoading()
        
        if (response.code === 200) {
          console.log('全局员工绑定成功:', response)
          
          // 绑定成功后，更新本地存储的用户信息和token
          if (response.data) {
            const config = require('./config/wx-config.js')
            
            // 更新用户信息
            if (response.data.userInfo) {
              wx.setStorageSync(config.STORAGE_KEYS.USER_INFO, response.data.userInfo)
              console.log('绑定成功后，用户信息已更新到本地存储:', response.data.userInfo)
            }
            
            // 更新token（如果返回了新的token）
            if (response.data.token) {
              wx.setStorageSync(config.STORAGE_KEYS.TOKEN, response.data.token)
              console.log('绑定成功后，token已更新到本地存储:', response.data.token)
            }
          }
          
          // 更新全局数据
          this.updateGlobalData()
          
          // 设置绑定完成标志
          this.globalData.isBindingInProgress = false
          
          wx.showToast({
            title: '绑定成功',
            icon: 'success'
          })
          
          // 触发绑定成功事件
          if (this.bindingSuccessCallback) {
            this.bindingSuccessCallback(response.data)
          }
          
          // 如果有待执行的操作，继续执行
          if (this.globalData.pendingOperation) {
            console.log('绑定成功，继续执行待处理的操作');
            const operation = this.globalData.pendingOperation;
            this.globalData.pendingOperation = null; // 清空待处理操作
            operation();
          }
        } else {
          wx.showToast({
            title: response.message || '绑定失败',
            icon: 'none'
          })
          // 绑定失败后重新显示输入弹窗
          setTimeout(() => {
            this.showGlobalEmployeeInputPrompt()
          }, 1500)
        }
      })
      .catch((error) => {
        wx.hideLoading()
        console.error('全局员工绑定失败:', error)
        
        wx.showToast({
          title: error.message || '绑定失败',
          icon: 'none'
        })
        // 绑定失败后重新显示输入弹窗
        setTimeout(() => {
          this.showGlobalEmployeeInputPrompt()
        }, 1500)
      })
  },
  
  // 更新全局数据
  updateGlobalData: function() {
    const currentConfig = require('./config/wx-config.js')
    const userInfo = wx.getStorageSync(currentConfig.STORAGE_KEYS.USER_INFO)
    const isLoggedIn = auth.checkLoginStatus()
    const hasEmployeeBinding = userInfo && userInfo.employeeCode
    
    this.globalData.userInfo = userInfo
    this.globalData.isLoggedIn = isLoggedIn
    this.globalData.hasEmployeeBinding = hasEmployeeBinding
    
    // 如果已经绑定员工号，清除绑定进行中标志
    if (hasEmployeeBinding) {
      this.globalData.isBindingInProgress = false
    }
    
    console.log('全局数据已更新:', {
      isLoggedIn: isLoggedIn,
      hasUserInfo: !!userInfo,
      hasEmployeeBinding: hasEmployeeBinding,
      isBindingInProgress: this.globalData.isBindingInProgress,
      userInfo: userInfo
    })
    
    // 通知所有页面用户信息已更新
    const pages = getCurrentPages()
    pages.forEach(page => {
      if (page && typeof page.onUserInfoUpdate === 'function') {
        console.log('通知页面更新用户信息:', page.route)
        page.onUserInfoUpdate(userInfo, isLoggedIn)
      }
    })
  },
  
  // 获取用户信息（需要用户授权）
  getUserProfile: function() {
    return auth.getUserProfile()
      .then((userInfo) => {
        // 更新全局数据
        this.globalData.userInfo = userInfo
        this.globalData.isLoggedIn = true
        return userInfo
      })
      .catch((error) => {
        console.error('获取用户信息失败:', error)
        throw error
      })
  },
  
  // 检查用户是否已绑定员工号
  checkEmployeeBinding: function() {
    const userInfo = auth.getUserInfo()
    return userInfo && userInfo.employeeCode
  },
  
  // 全局登录状态检查
  checkLoginAndBinding: function() {
    const isLoggedIn = auth.checkLoginStatus()
    const hasEmployeeBinding = this.checkEmployeeBinding()
    
    console.log('全局登录和绑定状态检查:', {
      isLoggedIn: isLoggedIn,
      hasEmployeeBinding: hasEmployeeBinding
    })
    
    return {
      isLoggedIn: isLoggedIn,
      hasEmployeeBinding: hasEmployeeBinding,
      canUseApp: isLoggedIn && hasEmployeeBinding
    }
  },
  
  // 页面跳转前的登录检查
  checkBeforeNavigate: function(targetPage) {
    const status = this.checkLoginAndBinding()
    
    if (!status.isLoggedIn) {
      console.log('用户未登录，无法访问页面:', targetPage)
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return false
    }
    
    if (!status.hasEmployeeBinding) {
      console.log('用户未绑定员工号，无法访问页面:', targetPage)
      wx.showToast({
        title: '请先绑定员工号',
        icon: 'none'
      })
      return false
    }
    
    return true
  },
  
  // 设置登录成功回调
  setLoginSuccessCallback: function(callback) {
    this.loginSuccessCallback = callback
  },
  
  // 设置登录失败回调
  setLoginFailCallback: function(callback) {
    this.loginFailCallback = callback
  },
  
  // 设置绑定成功回调
  setBindingSuccessCallback: function(callback) {
    this.bindingSuccessCallback = callback
  },
  
  globalData: {
    userInfo: null,
    isLoggedIn: false,
    hasEmployeeBinding: false,
    isBindingInProgress: false,  // 新增：跟踪绑定进行状态
    pendingOperation: null,      // 新增：待执行的操作
    needRefreshProjects: false   // 新增：标记是否需要刷新项目数据
  }
})
