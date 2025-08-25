// pages/daily-weekly-report/daily-weekly-report.js
Page({
  data: {
    activeTab: 'daily',
    reports: [
      {
        id: 1,
        type: 'daily',
        date: '2024-06-15 日报',
        department: '销售部',
        author: '张经理',
        summary: '今日完成客户拜访3家,签订新订单2份,总金额45万元。明日计划拜访重点客户A公司...',
        likes: 12,
        comments: 5
      },
      {
        id: 2,
        type: 'daily',
        date: '2024-06-14 日报',
        department: '市场部',
        author: '李主管',
        summary: '今日完成市场调研报告初稿,收集竞品信息5份。下周计划组织部门会议讨论Q3营销方案...',
        likes: 8,
        comments: 3
      },
      {
        id: 3,
        type: 'weekly',
        date: '2024年第24周周报',
        department: '运营部',
        author: '王总监',
        summary: '本周平台总GMV达到824万元,环比增长12%。新增注册用户324人,用户留存率提升至68%...',
        likes: 24,
        comments: 7
      }
    ],
    filteredReports: []
  },

  onLoad: function() {
    this.filterReports();
  },

  // 切换标签页
  switchTab: function(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      activeTab: tab
    });
    this.filterReports();
  },

  // 筛选报告
  filterReports: function() {
    const filtered = this.data.reports.filter(report => report.type === this.data.activeTab);
    this.setData({
      filteredReports: filtered
    });
  },

  // 跳转到新建报告页面
  navigateToCreateReport: function() {
    wx.navigateTo({
      url: '/pages/create-report/create-report'
    });
  },

  // 格式化日期
  formatDate: function(type) {
    const now = new Date();
    if (type === 'daily') {
      return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} 日报`;
    } else {
      // 计算当前是第几周
      const startOfYear = new Date(now.getFullYear(), 0, 1);
      const days = Math.floor((now - startOfYear) / (24 * 60 * 60 * 1000));
      const weekNumber = Math.ceil((days + startOfYear.getDay() + 1) / 7);
      return `${now.getFullYear()}年第${weekNumber}周周报`;
    }
  }
})
