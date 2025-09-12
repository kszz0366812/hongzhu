// pages/report/report.js
const api = require('../../utils/api.js')

Page({
  data: {
    activeTab: 'all',
    statsData: [
      { number: 0, label: '总报告' },
      { number: 0, label: '日报' },
      { number: 0, label: '周报' }
    ],
    actionButtons: [
      { text: '+ 创建报告', type: 'secondary' }
    ],
    tabsData: [
      { key: 'all', label: '全部' },
      { key: 'daily', label: '日报' },
      { key: 'weekly', label: '周报' }
    ],
    reports: [],
    filteredReports: [],
    loading: false,
    currentPage: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad: function() {
    this.initComponentData();
    this.loadReports();
  },

  onShow: function() {
    this.loadReports();
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    this.setData({
      currentPage: 1,
      hasMore: true
    });
    this.loadReports().then(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 上拉加载更多
  onReachBottom: function() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreReports();
    }
  },

  // 初始化组件数据
  initComponentData: function() {
    // 组件数据已在data中初始化
  },

  // 加载报告数据
  loadReports: function() {
    if (this.data.loading) return Promise.resolve();
    
    this.setData({ loading: true });
    
    const params = {
      page: 1,
      size: this.data.pageSize
    };
    
    // 根据当前选中的标签筛选
    if (this.data.activeTab === 'daily') {
      params.type = '日报';
    } else if (this.data.activeTab === 'weekly') {
      params.type = '周报';
    }
    
    // 根据当前选中的标签筛选
    if (this.data.activeTab === 'daily') {
      params.type = '1'; // 1=日报
    } else if (this.data.activeTab === 'weekly') {
      params.type = '2'; // 2=周报
    }
    
    return api.getReportList(params)
      .then(res => {
        console.log('获取报告列表成功:', res);
        
        if (res.code === 200 && res.data) {
          const reports = res.data.records || [];
          const total = res.data.total || 0;
          
          this.setData({
            reports: reports,
            currentPage: 1,
            hasMore: reports.length < total
          });
          
          this.calculateStats();
          this.filterReports();
        } else {
          console.error('获取报告列表失败:', res.message || '获取报告列表失败');
        }
      })
      .catch(error => {
        console.error('获取报告列表失败:', error);
        // 如果API请求失败，使用模拟数据作为备用
        this.loadMockReports();
      })
      .finally(() => {
        this.setData({ loading: false });
      });
  },

  // 加载更多报告
  loadMoreReports: function() {
    if (this.data.loading || !this.data.hasMore) return;
    
    this.setData({ loading: true });
    
    const params = {
      page: this.data.currentPage + 1,
      size: this.data.pageSize
    };
    
    // 根据当前选中的标签筛选
    if (this.data.activeTab === 'daily') {
      params.type = '日报';
    } else if (this.data.activeTab === 'weekly') {
      params.type = '周报';
    }
    
    // 根据当前选中的标签筛选
    if (this.data.activeTab === 'daily') {
      params.type = '1'; // 1=日报
    } else if (this.data.activeTab === 'weekly') {
      params.type = '2'; // 2=周报
    }
    
    api.getReportList(params)
      .then(res => {
        console.log('获取更多报告成功:', res);
        
        if (res.code === 200 && res.data) {
          const newReports = res.data.records || [];
          const total = res.data.total || 0;
          const allReports = [...this.data.reports, ...newReports];
          
          this.setData({
            reports: allReports,
            currentPage: this.data.currentPage + 1,
            hasMore: allReports.length < total
          });
          
          this.calculateStats();
          this.filterReports();
        }
      })
      .catch(error => {
        console.error('获取更多报告失败:', error);
      })
      .finally(() => {
        this.setData({ loading: false });
      });
  },

  // 加载模拟数据（备用方案）
  loadMockReports: function() {
    const reports = [
      {
        id: 1,
        title: '本周工作总结',
        type: '周报',
        typeClass: 'weekly',
        author: '张三',
        createTime: '2024-01-15',
        status: '已完成',
        summary: '本周完成了用户界面设计和数据库架构设计，项目进度良好。',
        workContent: '1. 完成了用户登录页面的UI设计\n2. 完成了数据库表结构设计\n3. 完成了API接口文档编写',
        nextPlan: '1. 开始前端页面开发\n2. 进行数据库性能优化\n3. 准备下周的测试计划'
      },
      {
        id: 2,
        title: '今日工作汇报',
        type: '日报',
        typeClass: 'daily',
        author: '李四',
        createTime: '2024-01-15',
        status: '进行中',
        summary: '今日主要进行API接口开发，已完成60%的进度。',
        workContent: '1. 完成了用户认证API接口开发\n2. 完成了数据查询API接口开发\n3. 进行了接口测试',
        nextPlan: '1. 完成剩余API接口开发\n2. 进行接口联调测试\n3. 准备接口文档'
      },
      {
        id: 3,
        title: '昨日工作总结',
        type: '日报',
        typeClass: 'daily',
        author: '王五',
        createTime: '2024-01-14',
        status: '已完成',
        summary: '昨日完成了用户登录模块的开发，测试通过。',
        workContent: '1. 完成了用户登录功能开发\n2. 完成了密码加密功能\n3. 完成了登录测试',
        nextPlan: '1. 开始用户注册功能开发\n2. 进行安全性测试\n3. 准备用户管理模块'
      }
    ];

    this.setData({
      reports: reports,
      currentPage: 1,
      hasMore: false
    });

    this.calculateStats();
    this.filterReports();
  },

  // 计算统计数据
  calculateStats: function() {
    // 尝试从API获取统计数据
    api.getReportStatistics()
      .then(res => {
        console.log('获取报告统计成功:', res);
        
        if (res.code === 200 && res.data) {
          const stats = res.data;
          this.setData({
            statsData: [
              { number: stats.totalReports || 0, label: '总报告' },
              { number: stats.dailyReports || 0, label: '日报' },
              { number: stats.weeklyReports || 0, label: '周报' }
            ]
          });
        }
      })
      .catch(error => {
        console.error('获取报告统计失败:', error);
        // 如果API失败，使用本地计算
        this.calculateLocalStats();
      });
  },

  // 本地计算统计数据
  calculateLocalStats: function() {
    const reports = this.data.reports;
    const totalReports = reports.length;
    const dailyReports = reports.filter(r => r.type === '日报').length;
    const weeklyReports = reports.filter(r => r.type === '周报').length;

    this.setData({
      statsData: [
        { number: totalReports, label: '总报告' },
        { number: dailyReports, label: '日报' },
        { number: weeklyReports, label: '周报' }
      ]
    });
  },

  // 格式化报告数据为组件格式
  formatReportForComponent: function(report) {
    // 将数字类型转换为显示文本
    const typeText = report.type === '1' ? '日报' : report.type === '2' ? '周报' : report.type;
    const typeClass = report.type === '1' ? 'daily' : 'weekly';
    
    return {
      id: report.id,
      title: report.title,
      priority: typeText,
      priorityClass: typeClass,
      status: report.status || '已完成',
      info: [
        { label: '作者', value: report.authorName || report.author || '未知' },
        { label: '创建时间', value: report.createTime || report.reportDate || '未知' }
      ],
      // 工作内容 - 第一部分
      workContent: report.content || report.workContent || '',
      // 遇到的问题 - 第二部分  
      issues: report.issues || '',
      // 下一步计划 - 第三部分
      nextPlan: report.plan || report.nextPlan || '',
      // 移除进度相关字段，报告不需要进度
      progress: undefined,
      actions: this.getReportActions(report)
    };
  },

  // 获取报告操作按钮
  getReportActions: function(report) {
    return [
      { text: '编辑', type: '' },
      { text: '分享', type: '' },
      { text: '删除', type: 'delete' }
    ];
  },

  // 筛选报告
  filterReports: function() {
    let filteredReports = [];
    const { reports, activeTab } = this.data;

    switch (activeTab) {
      case 'all':
        filteredReports = reports;
        break;
      case 'daily':
        filteredReports = reports.filter(report => report.type === '1'); // 1=日报
        break;
      case 'weekly':
        filteredReports = reports.filter(report => report.type === '2'); // 2=周报
        break;
    }

    // 格式化报告数据为组件格式
    const formattedReports = filteredReports.map(report => this.formatReportForComponent(report));

    this.setData({
      filteredReports: formattedReports
    });
  },

  // 组件事件处理
  onActionButtonTap: function(e) {
    const { button } = e.detail;
    if (button.text.includes('创建')) {
      this.navigateToCreateReport();
    }
  },

  onTabChange: function(e) {
    const { key } = e.detail;
    this.setData({ 
      activeTab: key,
      currentPage: 1,
      hasMore: true
    });
    this.loadReports();
  },

  onReportActionTap: function(e) {
    const { action } = e.detail;
    const reportId = e.detail.item.id;
    
    console.log('报告操作按钮点击:', action.text, '报告ID:', reportId);
    
    switch (action.text) {
      case '编辑':
        console.log('执行编辑操作');
        this.editReport(reportId);
        break;
      case '分享':
        console.log('执行分享操作');
        this.shareReport(reportId);
        break;
      case '删除':
        console.log('执行删除操作');
        this.deleteReport(reportId);
        break;
    }
  },

  // 导航到创建报告页面
  navigateToCreateReport: function() {
    wx.navigateTo({
      url: '/pages/create-report/create-report'
    });
  },

  // 查看报告详情
  viewReportDetail: function(e) {
    const reportId = e.detail.item.id;
    wx.navigateTo({
      url: `/pages/report-detail/report-detail?id=${reportId}`,
      fail: function(err) {
        console.error('跳转失败:', err);
        wx.showToast({
          title: '页面跳转失败',
          icon: 'error'
        });
      }
    });
  },

  // 编辑报告
  editReport: function(reportId) {
    // 找到要编辑的报告数据
    const report = this.data.reports.find(r => r.id === reportId);
    if (!report) {
      wx.showToast({
        title: '报告不存在',
        icon: 'error'
      });
      return;
    }
    
    // 跳转到编辑页面，传递报告数据
    const reportData = encodeURIComponent(JSON.stringify(report));
    wx.navigateTo({
      url: `/pages/create-report/create-report?mode=edit&report=${reportData}`,
      fail: function(err) {
        console.error('跳转失败:', err);
        wx.showToast({
          title: '页面跳转失败',
          icon: 'error'
        });
      }
    });
  },

  // 分享报告
  shareReport: function(reportId) {
    console.log('分享功能开发中');
  },

  // 删除报告
  deleteReport: function(reportId) {
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个报告吗？',
      success: (res) => {
        if (res.confirm) {
          // 调用API删除报告
          api.deleteReport(reportId)
            .then(res => {
              console.log('删除报告成功:', res);
              
              if (res.code === 200) {
                wx.showToast({
                  title: '删除成功',
                  icon: 'success'
                });
                
                // 重新加载报告列表
                this.loadReports();
              } else {
                wx.showToast({
                  title: res.message || '删除失败',
                  icon: 'error'
                });
              }
            })
            .catch(error => {
              console.error('删除报告失败:', error);
            });
        }
      }
    });
  }
})