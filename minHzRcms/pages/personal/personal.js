// pages/personal/personal.js

const auth = require('../../utils/auth.js')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    statsData: [
      { number: 0, label: '我的项目' },
      { number: 0, label: '我的任务' },
      { number: 0, label: '我的报告' },
      { number: 0, label: '团队数量' }
    ],
    actionButtons: [
      { text: '我的项目', type: 'primary' },
      { text: '我的任务', type: 'primary' },
      { text: '我的报告', type: 'primary' }
    ],
    menuItems: [
      {
        icon: '👤',
        title: '个人资料',
        path: '/pages/personal-info/personal-info'
      },
      {
        icon: '⚙️',
        title: '系统设置',
        path: '/pages/system-settings/system-settings'
      },
      {
        icon: '📊',
        title: '数据统计',
        path: '/pages/statistics/statistics'
      },
      {
        icon: '👥',
        title: '团队管理',
        path: '/pages/team-management/team-management'
      }
    ]
  },

  onLoad: function (options) {
    // 确保数据在页面加载时就初始化
    this.setData({
      statsData: [
        { number: 0, label: '我的项目' },
        { number: 0, label: '我的任务' },
        { number: 0, label: '我的报告' },
        { number: 0, label: '团队数量' }
      ],
      actionButtons: [
        { text: '我的项目', type: 'primary' },
        { text: '我的任务', type: 'primary' },
        { text: '我的报告', type: 'primary' }
      ]
    });
    
    this.loadUserInfo();
    this.loadStats();
  },

  onShow: function () {
    console.log('📱 个人中心页面显示');
    this.loadUserInfo();
    this.loadStats();
    
    // 延迟刷新，确保登录成功后能立即更新
    setTimeout(() => {
      this.loadUserInfo();
      this.loadStats();
    }, 1000);
  },

  // 加载用户信息
  loadUserInfo: function() {
    // 所有用户都必须绑定员工号，使用异步检查
    auth.getUserInfo()
      .then(userInfo => {
        const isLoggedIn = auth.checkLoginStatus()
        this.setData({
          userInfo: userInfo,
          isLoggedIn: isLoggedIn
        })
      })
      .catch(error => {
        console.log('个人中心：用户未登录或未绑定，等待绑定完成')
        // 用户会被强制停留在绑定页面，无法访问个人中心
      })
  },

  // 加载统计数据
  loadStats: function() {
    // 模拟统计数据，实际应该从服务器获取
    const statsData = [
      { number: 5, label: '我的项目' },
      { number: 12, label: '我的任务' },
      { number: 8, label: '我的报告' },
      { number: 3, label: '团队数量' }
    ];
    
    this.setData({
      statsData: statsData
    });
  },

  // 组件事件处理
  onActionButtonTap: function(e) {
    console.log('📱 个人中心按钮点击事件:', e.detail);
    const { button } = e.detail;
    if (button.text.includes('我的项目')) {
      console.log('📱 导航到项目页面');
      this.navigateToMyProjects();
    } else if (button.text.includes('我的任务')) {
      console.log('📱 导航到任务页面');
      this.navigateToMyTasks();
    } else if (button.text.includes('我的报告')) {
      console.log('📱 导航到报告页面');
      this.navigateToMyReports();
    }
  },

  // 导航到我的项目
  navigateToMyProjects: function() {
    wx.navigateTo({
      url: '/pages/project/project'
    });
  },

  // 导航到我的任务
  navigateToMyTasks: function() {
    wx.navigateTo({
      url: '/pages/task/task'
    });
  },

  // 导航到我的报告
  navigateToMyReports: function() {
    wx.navigateTo({
      url: '/pages/report/report'
    });
  },

  // 导航到菜单项
  navigateToMenu: function(e) {
    const index = e.currentTarget.dataset.index;
    const item = this.data.menuItems[index];
    
    wx.navigateTo({
      url: item.path
    });
  },

  // 导航到测试页面
  navigateToTest: function() {
    wx.navigateTo({
      url: '/pages/test-login/test-login'
    });
  },

  // 编辑资料
  editProfile: function() {
    wx.navigateTo({
      url: '/pages/personal-info/personal-info'
    });
  },

  // 接收全局用户信息更新通知
  onUserInfoUpdate: function(userInfo, isLoggedIn) {
    console.log('📱 个人中心收到用户信息更新通知:', {
      userInfo: userInfo,
      isLoggedIn: isLoggedIn
    });
    
    this.setData({
      userInfo: userInfo,
      isLoggedIn: isLoggedIn
    });
  }
})
