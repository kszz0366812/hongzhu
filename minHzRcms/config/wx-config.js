// config/wx-config.js

// 微信小程序配置
const config = {
  // API基础地址（请替换为实际的API地址）
  API_BASE_URL: 'http://localhost:8080/api',
  
  // 微信小程序配置
  WECHAT: {
    // 小程序 appId（请替换为实际的小程序 appId）
    APP_ID: 'wxb097295d383c4a0f',
    
    // 小程序 appSecret（服务端使用，前端不需要）
    APP_SECRET: '90d42bdd92e37e01ce5d0329cb1a4229'
  },
  
  // 请求配置
  REQUEST: {
    // 请求超时时间（毫秒）
    TIMEOUT: 10000,
    
    // 重试次数
    RETRY_TIMES: 3,
    
    // 重试间隔（毫秒）
    RETRY_INTERVAL: 1000
  },
  
  // 存储键名
  STORAGE_KEYS: {
    TOKEN: 'token',
    USER_INFO: 'userInfo',
    OPENID: 'openid',
    UNIONID: 'unionid'
  },
  
  // 错误码映射
  ERROR_CODES: {
    40029: 'code无效，请重新登录',
    45011: 'API调用太频繁，请稍候再试',
    40226: '高风险用户，登录被拦截',
    '-1': '系统错误，请稍候再试'
  }
}

// 获取当前环境配置
const getConfig = () => {
  try {
    // 检查是否在微信开发者工具中
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

module.exports = config 