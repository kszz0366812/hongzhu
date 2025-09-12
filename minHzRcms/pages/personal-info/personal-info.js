// pages/personal-info/personal-info.js

const auth = require('../../utils/auth.js')
const api = require('../../utils/api.js')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    hasUserProfile: false,
    loading: false
  },

  onLoad: function (options) {
    this.loadUserInfo()
  },

  onShow: function () {
    this.loadUserInfo()
  },

  // 加载用户信息
  loadUserInfo: function() {
    // 所有用户都必须绑定员工号，使用异步检查
    auth.getUserInfo()
      .then(userInfo => {
        const isLoggedIn = auth.checkLoginStatus()
        this.setData({
          userInfo: userInfo,
          isLoggedIn: isLoggedIn,
          hasUserProfile: !!(userInfo && userInfo.nickName)
        })
      })
      .catch(error => {
        console.log('个人信息：用户未登录或未绑定，等待绑定完成')
        // 用户会被强制停留在绑定页面，无法访问个人信息
      })
  },

  // 获取用户信息
  getUserProfile: function() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    
    const app = getApp()
    app.getUserProfile()
      .then((userInfo) => {
        this.setData({
          userInfo: userInfo,
          hasUserProfile: true,
          loading: false
        })
        
        wx.showToast({
          title: '获取成功',
          icon: 'success'
        })
      })
      .catch((error) => {
        this.setData({ loading: false })
        
        wx.showToast({
          title: error.message || '获取失败',
          icon: 'none'
        })
      })
  },

  // 绑定员工号（强制绑定，无法取消）
  bindEmployee: function() {
    // 先显示确认弹窗
    wx.showModal({
      title: '员工号绑定',
      content: '必须绑定员工号才能使用系统功能。',
      showCancel: false, // 不显示取消按钮
      confirmText: '立即绑定',
      success: (modalRes) => {
        if (modalRes.confirm) {
          // 用户确认后，显示输入弹窗
          this.showEmployeeIdInputPrompt()
        } else {
          // 用户没有确认，重新显示弹窗
          setTimeout(() => {
            this.bindEmployee()
          }, 500)
        }
      }
    })
  },

  // 显示员工号输入提示框
  showEmployeeIdInputPrompt: function() {
    wx.showModal({
      title: '输入员工号',
      content: '',
      editable: true,
      placeholderText: '请输入员工号',
      showCancel: false, // 不显示取消按钮
      confirmText: '确认绑定',
      success: (res) => {
        if (res.confirm && res.content) {
          const employeeId = res.content.trim()
          if (employeeId) {
            // 验证员工号格式
            if (employeeId.length < 3) {
              wx.showToast({
                title: '员工号格式不正确',
                icon: 'none'
              })
              // 重新显示输入弹窗
              setTimeout(() => {
                this.showEmployeeIdInputPrompt()
              }, 1500)
              return
            }
            
            this.performBindEmployee(employeeId)
          } else {
            wx.showToast({
              title: '员工号不能为空',
              icon: 'none'
            })
            // 重新显示输入弹窗
            setTimeout(() => {
              this.showEmployeeIdInputPrompt()
            }, 1500)
          }
        } else {
          // 用户没有输入内容，重新显示弹窗
          setTimeout(() => {
            this.showEmployeeIdInputPrompt()
          }, 500)
        }
      }
    })
  },

  // 执行绑定员工
  performBindEmployee: function(employeeId) {
    wx.showLoading({ title: '绑定中...' })
    
    auth.bindEmployee(employeeId)
      .then((response) => {
        wx.hideLoading()
        
        if (response.code === 200) {
          // 绑定成功后，更新本地存储的用户信息和token
          if (response.data) {
            const config = require('../../config/wx-config.js')
            
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
          
          wx.showToast({
            title: '绑定成功',
            icon: 'success'
          })
          
          // 重新加载用户信息
          this.loadUserInfo()
        } else {
          wx.showToast({
            title: response.message || '绑定失败',
            icon: 'none'
          })
          // 绑定失败后重新显示输入弹窗
          setTimeout(() => {
            this.showEmployeeIdInputPrompt()
          }, 1500)
        }
      })
      .catch((error) => {
        wx.hideLoading()
        wx.showToast({
          title: error.message || '绑定失败',
          icon: 'none'
        })
        // 绑定失败后重新显示输入弹窗
        setTimeout(() => {
          this.showEmployeeIdInputPrompt()
        }, 1500)
      })
  }
}) 