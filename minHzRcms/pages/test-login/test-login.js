// pages/test-login/test-login.js

const auth = require('../../utils/auth.js')
const api = require('../../utils/api.js')
const { createPage } = require('../../utils/page-base.js')

createPage({
  data: {
    isLoggedIn: false,
    userInfo: null,
    logs: [],
    loginStatus: '未登录',
    token: '',
    openid: '',
    isNewUser: false,
    testResult: '', // 新增测试结果显示
    
    // 成员选择器测试数据
    showMemberSelector: false,
    testMembers: [
      { id: 1, name: '张经理', role: '项目经理' },
      { id: 2, name: '李主管', role: '技术主管' },
      { id: 3, name: '王专员', role: '开发工程师' },
      { id: 4, name: '赵助理', role: '产品助理' },
      { id: 5, name: '陈顾问', role: '业务顾问' },
      { id: 6, name: '刘设计师', role: 'UI设计师' },
      { id: 7, name: '孙测试', role: '测试工程师' },
      { id: 8, name: '周运维', role: '运维工程师' },
      { id: 9, name: '吴产品', role: '产品经理' },
      { id: 10, name: '郑市场', role: '市场专员' },
      { id: 11, name: '冯前端', role: '前端工程师' },
      { id: 12, name: '朱后端', role: '后端工程师' },
      { id: 13, name: '秦数据', role: '数据分析师' },
      { id: 14, name: '许算法', role: '算法工程师' },
      { id: 15, name: '何安全', role: '安全工程师' },
      { id: 16, name: '吕架构', role: '架构师' },
      { id: 17, name: '施测试', role: '自动化测试' },
      { id: 18, name: '韩运维', role: '系统运维' },
      { id: 19, name: '杨产品', role: '高级产品经理' },
      { id: 20, name: '梁设计', role: '交互设计师' },
      { id: 21, name: '宋前端', role: '资深前端' },
      { id: 22, name: '唐后端', role: '资深后端' },
      { id: 23, name: '魏数据', role: '数据科学家' },
      { id: 24, name: '蒋算法', role: '机器学习工程师' },
      { id: 25, name: '韦安全', role: '网络安全专家' },
      { id: 26, name: '傅架构', role: '技术架构师' },
      { id: 27, name: '沈测试', role: '性能测试工程师' },
      { id: 28, name: '卢运维', role: 'DevOps工程师' },
      { id: 29, name: '姚产品', role: '产品总监' },
      { id: 30, name: '方设计', role: '视觉设计师' }
    ],
    selectedMemberIds: []
  },

  // 页面加载时的处理（会被基类调用）
  pageOnLoad: function (options) {
    console.log('测试登录页面加载')
    this.updateLoginStatus()
    
    // 自动测试全局静默登录
    this.addLog('页面加载完成，开始自动测试全局静默登录...')
    this.autoTestGlobalLogin()
  },

  // 页面显示时的处理（会被基类调用）
  pageOnShow: function () {
    this.updateLoginStatus()
  },

  // 自动测试全局登录
  autoTestGlobalLogin: function() {
    console.log('🧪 开始自动测试全局登录...')
    
    const app = getApp()
    app.performGlobalSilentLogin()
      .then((result) => {
        console.log('✅ 全局登录测试成功:', result)
        this.setData({
          testResult: '全局登录测试成功',
          userInfo: result.userInfo || app.globalData.userInfo
        })
      })
      .catch((error) => {
        console.error('❌ 全局登录测试失败:', error)
        this.setData({
          testResult: '全局登录测试失败: ' + error.message
        })
      })
  },

  // 接收全局用户信息更新通知
  onUserInfoUpdate: function(userInfo, isLoggedIn) {
    console.log('🧪 测试页面收到用户信息更新通知:', {
      userInfo: userInfo,
      isLoggedIn: isLoggedIn
    })
    
    this.setData({
      userInfo: userInfo,
      isLoggedIn: isLoggedIn
    })
  },

  // 更新登录状态
  updateLoginStatus: function() {
    const isLoggedIn = auth.checkLoginStatus()
    const token = auth.getToken()
    const openid = auth.getOpenid()
    
    // 使用异步方式获取用户信息，确保绑定检查
    auth.getUserInfo()
      .then(userInfo => {
        this.setData({
          isLoggedIn: isLoggedIn,
          userInfo: userInfo,
          token: token || '',
          openid: openid || '',
          loginStatus: isLoggedIn ? '已登录' : '未登录'
        })
        this.addLog(`登录状态更新: ${isLoggedIn ? '已登录' : '未登录'}`)
      })
      .catch(error => {
        console.log('测试页面：用户未登录或未绑定，等待绑定完成')
        // 用户会被强制停留在绑定页面
      })
  },

  // 执行静默登录
  performLogin: function() {
    this.addLog('开始执行静默登录...')
    
    const app = getApp()
    app.performGlobalSilentLogin()
      .then((result) => {
        this.addLog('静默登录成功')
        this.addLog(`登录结果: ${JSON.stringify(result, null, 2)}`)
        
        // 检查是否为新用户
        if (result.isNewUser) {
          this.addLog('检测到新用户，需要绑定员工号')
          this.setData({ isNewUser: true })
        } else {
          this.addLog('老用户，无需绑定员工号')
          this.setData({ isNewUser: false })
        }
        
        this.updateLoginStatus()
      })
      .catch((error) => {
        this.addLog(`静默登录失败: ${error.message}`)
        console.error('静默登录失败:', error)
      })
  },

  // 获取用户信息
  getUserProfile: function() {
    this.addLog('开始获取用户信息...')
    
    auth.getUserProfile()
      .then((userInfo) => {
        this.addLog('获取用户信息成功')
        this.addLog(`用户信息: ${JSON.stringify(userInfo, null, 2)}`)
        this.updateLoginStatus()
      })
      .catch((error) => {
        this.addLog(`获取用户信息失败: ${error.message}`)
        console.error('获取用户信息失败:', error)
      })
  },

  // 测试员工绑定
  testEmployeeBinding: function() {
    this.addLog('开始测试员工绑定...')
    
    // 测试绑定员工号
    const testEmployeeCode = 'EMP001'
    this.addLog(`测试员工号: ${testEmployeeCode}`)
    
    auth.bindEmployee(testEmployeeCode)
      .then((response) => {
        this.addLog('员工绑定成功')
        this.addLog(`绑定结果: ${JSON.stringify(response, null, 2)}`)
        
        // 更新页面显示
        this.updateLoginStatus()
      })
      .catch((error) => {
        this.addLog(`员工绑定测试失败: ${error.message}`)
        console.error('员工绑定测试失败:', error)
      })
  },

  // 测试绑定状态
  testBindingStatus: function() {
    this.addLog('开始测试绑定状态...')
    
    const app = getApp()
    
    // 使用异步方式获取用户信息，确保绑定检查
    auth.getUserInfo()
      .then(userInfo => {
        this.addLog(`全局绑定状态: ${app.globalData.isBindingInProgress ? '进行中' : '未进行'}`)
        this.addLog(`用户信息: ${userInfo ? JSON.stringify(userInfo) : '无'}`)
        this.addLog(`员工号: ${userInfo ? userInfo.employeeCode : '无'}`)
        this.addLog(`是否已绑定: ${userInfo && userInfo.employeeCode ? '是' : '否'}`)
      })
      .catch(error => {
        this.addLog(`用户未绑定员工号: ${error.message}`)
        // 用户会被强制停留在绑定页面
      })
  },

  // 测试配置
  testConfig: function() {
    this.addLog('开始测试配置...')
    
    const config = require('../../config/wx-config.js')
    this.addLog(`API基础地址: ${config.API_BASE_URL}`)
    this.addLog(`微信AppID: ${config.WECHAT.APP_ID}`)
    this.addLog('配置测试完成')
  },

  // 环境调试
  runDebugCheck: function() {
    this.addLog('开始环境调试检查...')
    
    try {
      const systemInfo = wx.getSystemInfoSync()
      this.addLog(`系统信息: ${JSON.stringify(systemInfo, null, 2)}`)
      
      // 检查存储
      const token = wx.getStorageSync('token')
      const userInfo = wx.getStorageSync('userInfo')
      this.addLog(`存储的Token: ${token || '无'}`)
      this.addLog(`存储的用户信息: ${userInfo ? JSON.stringify(userInfo) : '无'}`)
      
      this.addLog('环境调试检查完成')
    } catch (error) {
      this.addLog(`环境调试检查失败: ${error.message}`)
    }
  },

  // 测试新用户绑定流程
  testNewUserFlow: function() {
    this.addLog('开始测试新用户绑定流程...')
    
    // 模拟新用户登录响应
    const mockNewUserResponse = {
      code: 200,
      message: 'success',
      data: {
        success: true,
        token: 'mock_token_123',
        openid: 'mock_openid_123',
        unionid: 'mock_unionid_123',
        isNewUser: true,
        userInfo: {
          id: 1,
          nickname: '测试用户',
          avatarUrl: '',
          gender: 1,
          country: '中国',
          province: '广东',
          city: '深圳',
          employeeCode: null
        }
      }
    }
    
    this.addLog('模拟新用户登录响应')
    this.addLog(`响应数据: ${JSON.stringify(mockNewUserResponse, null, 2)}`)
    
    // 检查是否为新用户
    if (mockNewUserResponse.data.isNewUser) {
      this.addLog('检测到新用户，应该直接显示员工绑定弹窗')
      this.setData({ isNewUser: true })
      
      // 显示弹窗提示
      wx.showModal({
        title: '新用户检测',
        content: '检测到新用户，是否模拟显示员工绑定弹窗？',
        success: (res) => {
          if (res.confirm) {
            // 模拟显示员工绑定弹窗
            this.simulateEmployeeIdInputModal()
          }
        }
      })
    }
  },

  // 模拟员工号输入弹窗（必须绑定，无法取消）
  simulateEmployeeIdInputModal: function() {
    this.addLog('模拟显示员工绑定弹窗...')
    
    // 先显示确认弹窗
    wx.showModal({
      title: '员工号绑定',
      content: '内部系统，检测到您是首次登录，请先绑定员工号',
      showCancel: false, // 不显示取消按钮
      confirmText: '立即绑定',
      success: (modalRes) => {
        if (modalRes.confirm) {
          this.addLog('用户选择立即绑定，显示输入弹窗')
          this.simulateEmployeeIdInputPrompt()
        } else {
          this.addLog('用户没有确认，重新显示弹窗')
          setTimeout(() => {
            this.simulateEmployeeIdInputModal()
          }, 500)
        }
      }
    })
  },

  // 模拟员工号输入提示框
  simulateEmployeeIdInputPrompt: function() {
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
          this.addLog(`用户输入员工号: ${employeeCode}`)
          
          if (employeeCode) {
            // 模拟绑定成功
            this.addLog('模拟绑定成功')
            wx.showToast({
              title: '绑定成功',
              icon: 'success'
            })
          } else {
            wx.showToast({
              title: '员工号不能为空',
              icon: 'none'
            })
            // 重新显示输入弹窗
            setTimeout(() => {
              this.simulateEmployeeIdInputPrompt()
            }, 1500)
          }
        } else {
          // 用户没有输入内容，重新显示弹窗
          setTimeout(() => {
            this.simulateEmployeeIdInputPrompt()
          }, 500)
        }
      }
    })
  },

  // 清除日志
  clearLogs: function() {
    this.setData({ logs: [] })
  },

  // 添加日志
  addLog: function(message) {
    const timestamp = new Date().toLocaleTimeString()
    const log = `[${timestamp}] ${message}`
    
    this.setData({
      logs: [...this.data.logs, log]
    })
    
    console.log(log)
  },

  // 测试成员选择器
  testMemberSelector: function() {
    this.addLog('开始测试成员选择器...')
    this.setData({
      showMemberSelector: true,
      selectedMemberIds: []
    })
  },

  // 成员选择器关闭
  onMemberSelectorClose: function() {
    this.addLog('成员选择器已关闭')
    this.setData({
      showMemberSelector: false
    })
  },

  // 成员选择器选择结果
  onMemberSelectorSelect: function(e) {
    const { members } = e.detail
    this.addLog(`选择了 ${members.length} 个成员`)
    this.addLog(`选中的成员: ${members.map(m => m.name).join(', ')}`)
    
    this.setData({
      showMemberSelector: false,
      selectedMemberIds: members.map(m => m.id)
    })
    
    wx.showToast({
      title: `已选择 ${members.length} 个成员`,
      icon: 'success'
    })
  }
}) 