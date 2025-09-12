// pages/my-tasks/my-tasks.js
Page({
  data: {
    activeFilter: 'all',
    stats: {
      total: 8,
      pending: 3,
      completed: 5
    },
    tasks: [
      {
        id: 1,
        title: '客户需求调研',
        status: '待处理',
        project: '客户管理系统开发',
        priority: 'high',
        priorityText: '高',
        deadline: '2024-06-20',
        description: '对客户进行深度访谈，收集功能需求和用户体验反馈',
        isOverdue: false
      },
      {
        id: 2,
        title: '数据库设计',
        status: '进行中',
        project: '销售数据分析平台',
        priority: 'medium',
        priorityText: '中',
        deadline: '2024-06-25',
        description: '设计销售数据存储结构，优化查询性能',
        isOverdue: false
      },
      {
        id: 3,
        title: '用户界面设计',
        status: '已完成',
        project: '团队协作工具优化',
        priority: 'low',
        priorityText: '低',
        deadline: '2024-06-15',
        description: '重新设计用户界面，提升用户体验',
        isOverdue: false
      },
      {
        id: 4,
        title: 'API接口开发',
        status: '待处理',
        project: '市场调研报告系统',
        priority: 'high',
        priorityText: '高',
        deadline: '2024-06-18',
        description: '开发数据获取和处理的API接口',
        isOverdue: true
      },
      {
        id: 5,
        title: '系统测试',
        status: '已完成',
        project: '员工培训管理系统',
        priority: 'medium',
        priorityText: '中',
        deadline: '2024-06-10',
        description: '进行系统功能测试和性能测试',
        isOverdue: false
      },
      {
        id: 6,
        title: '文档编写',
        status: '进行中',
        project: '客户管理系统开发',
        priority: 'low',
        priorityText: '低',
        deadline: '2024-06-30',
        description: '编写系统使用手册和技术文档',
        isOverdue: false
      },
      {
        id: 7,
        title: '性能优化',
        status: '已完成',
        project: '销售数据分析平台',
        priority: 'high',
        priorityText: '高',
        deadline: '2024-06-12',
        description: '优化系统性能，提升响应速度',
        isOverdue: false
      },
      {
        id: 8,
        title: '安全审计',
        status: '待处理',
        project: '市场调研报告系统',
        priority: 'medium',
        priorityText: '中',
        deadline: '2024-06-28',
        description: '进行系统安全审计和漏洞修复',
        isOverdue: false
      }
    ],
    filteredTasks: []
  },

  onLoad: function() {
    this.filterTasks();
  },

  // 筛选任务
  filterTasks: function(e) {
    const filter = e ? e.currentTarget.dataset.filter : this.data.activeFilter;
    let filteredTasks = [];

    if (filter === 'all') {
      filteredTasks = this.data.tasks;
    } else if (filter === 'pending') {
      filteredTasks = this.data.tasks.filter(task => task.status === '待处理');
    } else if (filter === 'completed') {
      filteredTasks = this.data.tasks.filter(task => task.status === '已完成');
    }

    this.setData({
      activeFilter: filter,
      filteredTasks: filteredTasks
    });
  },

  // 判断任务状态是否为最终态
  isFinalStatus: function(status) {
    return status === '已完成' || status === '已取消' || status === '已拒绝';
  },

  // 更新任务状态
  updateTaskStatus: function(e) {
    const { id, status } = e.currentTarget.dataset;
    
    // 检查当前任务状态，如果是最终态则不允许更新
    const currentTask = this.data.tasks.find(task => task.id === id);
    if (this.isFinalStatus(currentTask.status)) {
      wx.showToast({
        title: '任务已完成，无法修改状态',
        icon: 'none'
      });
      return;
    }

    const tasks = this.data.tasks.map(task => {
      if (task.id === id) {
        return { ...task, status: status };
      }
      return task;
    });

    this.setData({ tasks: tasks });
    this.filterTasks();
    this.calculateStats();

    wx.showToast({
      title: '状态更新成功',
      icon: 'success'
    });
  },

  // 查看任务详情
  viewTaskDetail: function(e) {
    const taskId = e.currentTarget.dataset.id;
    wx.showToast({
      title: '任务详情功能开发中',
      icon: 'none'
    });
  },

  // 计算统计数据
  calculateStats: function() {
    const tasks = this.data.tasks;
    const total = tasks.length;
    const pending = tasks.filter(t => t.status === '待处理').length;
    const completed = tasks.filter(t => t.status === '已完成').length;

    this.setData({
      stats: { total, pending, completed }
    });
  }
}) 