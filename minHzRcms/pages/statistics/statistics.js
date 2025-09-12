// pages/statistics/statistics.js
Page({
  data: {
    // 总体概览数据
    overview: {
      totalReports: 0,
      totalProjects: 0,
      totalTasks: 0,
      completedTasks: 0
    },
    
    // 报告统计数据
    reportStats: [
      { type: '日报', count: 15, percentage: 60 },
      { type: '周报', count: 6, percentage: 24 },
      { type: '月报', count: 3, percentage: 12 },
      { type: '其他', count: 1, percentage: 4 }
    ],
    
    // 项目统计数据
    projectStats: [
      { status: 'ongoing', statusText: '进行中', count: 4, percentage: 50 },
      { status: 'completed', statusText: '已完成', count: 3, percentage: 37.5 },
              { status: 'pending', statusText: '待安排', count: 1, percentage: 12.5 }
    ],
    
    // 任务统计数据
    taskStats: [
      { status: 'ongoing', statusText: '进行中', count: 6, percentage: 40 },
      { status: 'completed', statusText: '已完成', count: 9, percentage: 60 },
      { status: 'overdue', statusText: '已逾期', count: 0, percentage: 0 }
    ],
    
    // 趋势数据
    trendData: [
      { date: '6/1', count: 2, height: 40 },
      { date: '6/2', count: 1, height: 20 },
      { date: '6/3', count: 3, height: 60 },
      { date: '6/4', count: 0, height: 0 },
      { date: '6/5', count: 2, height: 40 },
      { date: '6/6', count: 1, height: 20 },
      { date: '6/7', count: 4, height: 80 },
      { date: '6/8', count: 2, height: 40 },
      { date: '6/9', count: 1, height: 20 },
      { date: '6/10', count: 3, height: 60 },
      { date: '6/11', count: 0, height: 0 },
      { date: '6/12', count: 2, height: 40 },
      { date: '6/13', count: 1, height: 20 },
      { date: '6/14', count: 3, height: 60 },
      { date: '6/15', count: 2, height: 40 },
      { date: '6/16', count: 1, height: 20 },
      { date: '6/17', count: 0, height: 0 },
      { date: '6/18', count: 2, height: 40 },
      { date: '6/19', count: 1, height: 20 },
      { date: '6/20', count: 3, height: 60 },
      { date: '6/21', count: 2, height: 40 },
      { date: '6/22', count: 1, height: 20 },
      { date: '6/23', count: 0, height: 0 },
      { date: '6/24', count: 2, height: 40 },
      { date: '6/25', count: 1, height: 20 },
      { date: '6/26', count: 3, height: 60 },
      { date: '6/27', count: 2, height: 40 },
      { date: '6/28', count: 1, height: 20 },
      { date: '6/29', count: 0, height: 0 },
      { date: '6/30', count: 2, height: 40 }
    ],
    
    // 部门统计数据
    departmentStats: [
      { name: '销售部', reports: 12, projects: 3, progress: 85 },
      { name: '技术部', reports: 8, projects: 4, progress: 92 },
      { name: '市场部', reports: 5, projects: 2, progress: 78 },
      { name: '人事部', reports: 3, projects: 1, progress: 65 },
      { name: '财务部', reports: 2, projects: 1, progress: 88 }
    ]
  },

  onLoad: function() {
    this.calculateOverview();
  },

  // 计算总体概览数据
  calculateOverview: function() {
    const totalReports = this.data.reportStats.reduce((sum, item) => sum + item.count, 0);
    const totalProjects = this.data.projectStats.reduce((sum, item) => sum + item.count, 0);
    const totalTasks = this.data.taskStats.reduce((sum, item) => sum + item.count, 0);
    const completedTasks = this.data.taskStats.find(item => item.status === 'completed')?.count || 0;

    this.setData({
      overview: {
        totalReports,
        totalProjects,
        totalTasks,
        completedTasks
      }
    });
  },

  // 刷新统计数据
  refreshStats: function() {
    // 这里可以添加刷新逻辑，比如从服务器重新获取数据
    wx.showToast({
      title: '数据已刷新',
      icon: 'success'
    });
  },

  // 查看详细报告
  viewReportDetail: function(e) {
    const reportType = e.currentTarget.dataset.type;
    wx.showToast({
      title: `查看${reportType}详情`,
      icon: 'none'
    });
  },

  // 查看项目详情
  viewProjectDetail: function(e) {
    const status = e.currentTarget.dataset.status;
    wx.showToast({
      title: `查看${status}项目`,
      icon: 'none'
    });
  },

  // 查看任务详情
  viewTaskDetail: function(e) {
    const status = e.currentTarget.dataset.status;
    wx.showToast({
      title: `查看${status}任务`,
      icon: 'none'
    });
  },

  // 查看部门详情
  viewDepartmentDetail: function(e) {
    const department = e.currentTarget.dataset.name;
    wx.showToast({
      title: `查看${department}详情`,
      icon: 'none'
    });
  }
}) 