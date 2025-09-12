// utils/debug.js

// 调试工具
const debug = {
  // 检查微信环境
  checkWxEnvironment: () => {
    console.log('=== 微信环境检查 ===')
    try {
      if (typeof wx !== 'undefined') {
        console.log('✅ wx对象存在')
        
        // 检查系统信息
        try {
          const systemInfo = wx.getSystemInfoSync()
          console.log('✅ 系统信息获取成功:', systemInfo)
          return {
            success: true,
            platform: systemInfo.platform,
            isDevTools: systemInfo.platform === 'devtools'
          }
        } catch (e) {
          console.log('❌ 系统信息获取失败:', e)
          return {
            success: false,
            error: e.message
          }
        }
      } else {
        console.log('❌ wx对象不存在')
        return {
          success: false,
          error: 'wx对象不存在'
        }
      }
    } catch (e) {
      console.log('❌ 环境检查失败:', e)
      return {
        success: false,
        error: e.message
      }
    }
  },

  // 检查存储功能
  checkStorage: () => {
    console.log('=== 存储功能检查 ===')
    try {
      if (typeof wx !== 'undefined' && wx.setStorageSync) {
        console.log('✅ 存储API存在')
        
        // 测试存储
        const testKey = 'debug_test_key'
        const testValue = 'test_value_' + Date.now()
        
        wx.setStorageSync(testKey, testValue)
        const retrievedValue = wx.getStorageSync(testKey)
        
        if (retrievedValue === testValue) {
          console.log('✅ 存储功能正常')
          wx.removeStorageSync(testKey)
          return { success: true }
        } else {
          console.log('❌ 存储功能异常')
          return { success: false, error: '存储值不匹配' }
        }
      } else {
        console.log('❌ 存储API不存在')
        return { success: false, error: '存储API不存在' }
      }
    } catch (e) {
      console.log('❌ 存储检查失败:', e)
      return { success: false, error: e.message }
    }
  },

  // 检查网络请求
  checkNetwork: () => {
    console.log('=== 网络请求检查 ===')
    return new Promise((resolve) => {
      try {
        if (typeof wx !== 'undefined' && wx.request) {
          console.log('✅ 网络请求API存在')
          
          // 测试请求
          wx.request({
            url: 'https://httpbin.org/get',
            method: 'GET',
            timeout: 5000,
            success: (res) => {
              console.log('✅ 网络请求成功:', res.statusCode)
              resolve({ success: true, statusCode: res.statusCode })
            },
            fail: (err) => {
              console.log('❌ 网络请求失败:', err)
              resolve({ success: false, error: err.errMsg })
            }
          })
        } else {
          console.log('❌ 网络请求API不存在')
          resolve({ success: false, error: '网络请求API不存在' })
        }
      } catch (e) {
        console.log('❌ 网络检查失败:', e)
        resolve({ success: false, error: e.message })
      }
    })
  },

  // 运行完整检查
  runFullCheck: async () => {
    console.log('=== 开始完整环境检查 ===')
    
    const results = {
      wxEnvironment: debug.checkWxEnvironment(),
      storage: debug.checkStorage(),
      network: await debug.checkNetwork()
    }
    
    console.log('=== 检查结果汇总 ===')
    console.log(results)
    
    return results
  }
}

module.exports = debug 