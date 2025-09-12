// pages/system-settings/system-settings.js
Page({
  data: {
    settings: {
      messageNotification: true,
      taskReminder: true,
      reportReminder: false,
      dataSync: true,
      locationAccess: false,
      darkMode: false
    },
    fontSizes: ['小', '中', '大'],
    fontSizeIndex: 1
  },

  onLoad: function() {
    this.loadSettings();
  },

  // 加载设置
  loadSettings: function() {
    // 从本地存储加载设置
    const settings = wx.getStorageSync('appSettings');
    if (settings) {
      this.setData({ settings: settings });
    }
  },

  // 切换设置开关
  toggleSetting: function(e) {
    const key = e.currentTarget.dataset.key;
    const value = e.detail.value;
    
    this.setData({
      [`settings.${key}`]: value
    });
    
    // 保存到本地存储
    wx.setStorageSync('appSettings', this.data.settings);
    
    // 显示提示
    wx.showToast({
      title: '设置已保存',
      icon: 'success'
    });
  },

  // 更改字体大小
  changeFontSize: function(e) {
    const index = e.detail.value;
    this.setData({
      fontSizeIndex: index
    });
    
    // 保存字体大小设置
    wx.setStorageSync('fontSizeIndex', index);
    
    wx.showToast({
      title: '字体大小已调整',
      icon: 'success'
    });
  },

  // 修改密码
  changePassword: function() {
    wx.showModal({
      title: '修改密码',
      content: '修改密码功能开发中',
      showCancel: false
    });
  },

  // 清除缓存
  clearCache: function() {
    wx.showModal({
      title: '清除缓存',
      content: '确定要清除应用缓存吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({
            title: '清除中...'
          });
          
          setTimeout(() => {
            wx.hideLoading();
            wx.showToast({
              title: '缓存已清除',
              icon: 'success'
            });
          }, 1500);
        }
      }
    });
  },

  // 关于应用
  aboutApp: function() {
    wx.showModal({
      title: '关于应用',
      content: '报告管理小程序\n版本：1.0.0\n\n专注于团队协作和项目管理',
      showCancel: false
    });
  },

  // 退出登录
  logout: function() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除登录状态
          wx.removeStorageSync('userInfo');
          wx.removeStorageSync('token');
          
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          });
          
          // 返回登录页面或首页
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/daily-weekly-report/daily-weekly-report'
            });
          }, 1500);
        }
      }
    });
  }
}) 