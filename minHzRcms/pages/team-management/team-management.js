// pages/team-management/team-management.js
Page({
  data: {
    stats: {
      totalMembers: 6,
      activeProjects: 4,
      completedTasks: 28
    },
    teamInfo: {
      name: '销售精英团队',
      leader: '张经理',
      createTime: '2024-01-15',
      description: '专注于客户开发和销售业绩提升的专业团队'
    },
    members: [
      {
        id: 1,
        name: '张经理',
        role: '团队负责人',
        status: '在线'
      },
      {
        id: 2,
        name: '李主管',
        role: '高级销售',
        status: '在线'
      },
      {
        id: 3,
        name: '王专员',
        role: '销售专员',
        status: '离线'
      },
      {
        id: 4,
        name: '赵助理',
        role: '销售助理',
        status: '在线'
      },
      {
        id: 5,
        name: '陈顾问',
        role: '销售顾问',
        status: '在线'
      },
      {
        id: 6,
        name: '刘代表',
        role: '客户代表',
        status: '离线'
      }
    ],
    projects: [
      {
        id: 1,
        name: '客户管理系统开发',
        status: '进行中',
        members: ['张经理', '李主管', '王专员']
      },
      {
        id: 2,
        name: '销售数据分析平台',
        status: '进行中',
        members: ['张经理', '赵助理']
      },
      {
        id: 3,
        name: '团队协作工具优化',
        status: '已完成',
        members: ['李主管', '陈顾问']
      },
      {
        id: 4,
        name: '市场调研报告系统',
        status: '进行中',
        members: ['王专员', '刘代表']
      }
    ]
  },

  onLoad: function() {
    this.calculateStats();
  },

  // 计算统计数据
  calculateStats: function() {
    const members = this.data.members;
    const projects = this.data.projects;
    
    const totalMembers = members.length;
    const activeProjects = projects.filter(p => p.status === '进行中').length;
    const completedTasks = 28; // 模拟数据

    this.setData({
      stats: { totalMembers, activeProjects, completedTasks }
    });
  },

  // 添加成员
  addMember: function() {
    wx.showToast({
      title: '添加成员功能开发中',
      icon: 'none'
    });
  },

  // 编辑成员
  editMember: function(e) {
    const memberId = e.currentTarget.dataset.id;
    wx.showToast({
      title: '编辑成员功能开发中',
      icon: 'none'
    });
  },

  // 移除成员
  removeMember: function(e) {
    const memberId = e.currentTarget.dataset.id;
    
    wx.showModal({
      title: '确认移除',
      content: '确定要移除该成员吗？',
      success: (res) => {
        if (res.confirm) {
          const members = this.data.members.filter(m => m.id !== memberId);
          this.setData({ members: members });
          this.calculateStats();
          
          wx.showToast({
            title: '成员已移除',
            icon: 'success'
          });
        }
      }
    });
  },

  // 分配项目
  assignProject: function(e) {
    const projectId = e.currentTarget.dataset.id;
    wx.showToast({
      title: '项目分配功能开发中',
      icon: 'none'
    });
  }
}) 