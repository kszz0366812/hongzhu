// utils/test-config.js

const config = require('../config/wx-config.js')

// 测试配置是否正确加载
const testConfig = () => {
  try {
    console.log('配置加载成功:', {
      API_BASE_URL: config.API_BASE_URL,
      APP_ID: config.WECHAT.APP_ID,
      STORAGE_KEYS: config.STORAGE_KEYS
    })
    return true
  } catch (error) {
    console.error('配置加载失败:', error)
    return false
  }
}

// 测试环境检测
const testEnvironment = () => {
  try {
    if (typeof wx !== 'undefined' && wx.getSystemInfoSync) {
      const systemInfo = wx.getSystemInfoSync()
      console.log('系统信息:', {
        platform: systemInfo.platform,
        system: systemInfo.system,
        version: systemInfo.version
      })
      return systemInfo.platform === 'devtools'
    } else {
      console.log('wx对象不可用')
      return false
    }
  } catch (error) {
    console.error('环境检测失败:', error)
    return false
  }
}

module.exports = {
  testConfig,
  testEnvironment
} 