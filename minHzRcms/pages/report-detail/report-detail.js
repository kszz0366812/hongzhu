// pages/report-detail/report-detail.js
const api = require('../../utils/api.js')

Page({
  data: {
    reportInfo: {
      id: '',
      title: '',
      type: '',
      author: '',
      createTime: '',
      status: '',
      summary: '',
      workContent: '',
      nextPlan: '',
      attachments: []
    },
    loading: false
  },

  onLoad: function(options) {
    console.log('报告详情页面加载，参数:', options);
    
    if (options.report) {
      try {
        const reportData = JSON.parse(decodeURIComponent(options.report));
        console.log('从参数获取报告数据:', reportData);
        this.setData({
          reportInfo: reportData
        });
      } catch (error) {
        console.error('解析报告数据失败:', error);
      }
    } else if (options.id) {
      console.log('通过ID加载报告详情:', options.id);
      // 直接从API获取数据，因为页面栈可能已经变化
      this.loadReportFromAPI(options.id);
    } else {
      console.error('缺少必要的参数');
    }
  },



  // 从API获取报告详情
  loadReportFromAPI: function(reportId) {
    console.log('开始从API获取报告详情，ID:', reportId);
    this.setData({ loading: true });
    
    // 注意：API文档中没有单独的获取报告详情接口
    // 这里我们通过获取报告列表并筛选来获取单个报告详情
    api.getReportList({ page: 1, size: 100 })
      .then(res => {
        console.log('获取报告列表成功:', res);
        
        if (res.code === 200 && res.data) {
          const reports = res.data.records || [];
          console.log('API返回的报告数量:', reports.length);
          
          const report = reports.find(r => r.id === parseInt(reportId));
          
          if (report) {
            console.log('从API找到报告:', report);
            // 转换为详情页面期望的格式
            const reportData = {
              id: report.id,
              title: report.title,
              type: report.type === '1' ? '日报' : report.type === '2' ? '周报' : report.type,
              author: report.authorName || report.author || '未知',
              createTime: report.createTime || report.reportDate || '未知',
              status: report.status || '已完成',
              summary: report.summary || report.content || '',
              workContent: report.workContent || report.content || '',
              nextPlan: report.nextPlan || report.plan || '',
              attachments: report.attachments || []
            };
            
            console.log('转换后的报告数据:', reportData);
            this.setData({
              reportInfo: reportData
            });
          } else {
            console.log('在API返回的数据中未找到报告，ID:', reportId);
            // 如果找不到报告，使用模拟数据
            this.loadMockReportDetail(reportId);
          }
        } else {
          console.log('API返回错误:', res);
          // 如果API请求失败，使用模拟数据
          this.loadMockReportDetail(reportId);
        }
      })
      .catch(error => {
        console.error('获取报告详情失败:', error);
        // 如果API请求失败，使用模拟数据
        this.loadMockReportDetail(reportId);
      })
      .finally(() => {
        this.setData({ loading: false });
      });
  },

  // 加载模拟数据（备用方案）
  loadMockReportDetail: function(reportId) {
    // 模拟数据，实际应该从服务器获取
    const mockReport = {
      id: reportId,
      title: '本周工作总结',
      type: '周报',
      author: '张三',
      createTime: '2024-01-15',
      status: '已完成',
      summary: '本周完成了用户界面设计和数据库架构设计，项目进度良好。',
      workContent: '1. 完成了用户登录页面的UI设计\n2. 完成了数据库表结构设计\n3. 完成了API接口文档编写',
      nextPlan: '1. 开始前端页面开发\n2. 进行数据库性能优化\n3. 准备下周的测试计划',
      attachments: []
    };

    this.setData({
      reportInfo: mockReport
    });
  },

  // 编辑报告
  editReport: function() {
    const reportData = encodeURIComponent(JSON.stringify(this.data.reportInfo));
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
  shareReport: function() {
    console.log('分享功能开发中');
  },

  // 返回列表
  goBack: function() {
    wx.navigateBack();
  }
}) 