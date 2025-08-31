// pages/assign-task/assign-task.js
const api = require('../../utils/api.js')

Page({
  data: {
    taskForm: {
      title: '',
      projectIndex: undefined,
      assigneeIndex: undefined,
      priorityIndex: 1,
      deadline: '',
      description: ''
    },
    
    // 选项数据
    projects: [
      { 
        id: 1, 
        name: '客户管理系统开发',
        description: '开发客户关系管理系统，提升客户服务质量',
        startDate: '2024-06-01',
        endDate: '2024-08-31'
      },
      { 
        id: 2, 
        name: '销售数据分析平台',
        description: '构建销售数据分析和可视化平台',
        startDate: '2024-06-15',
        endDate: '2024-09-30'
      },
      { 
        id: 3, 
        name: '团队协作工具优化',
        description: '优化现有团队协作工具，提升工作效率',
        startDate: '2024-07-01',
        endDate: '2024-10-31'
      },
      { 
        id: 4, 
        name: '市场调研报告系统',
        description: '建立市场调研数据收集和分析系统',
        startDate: '2024-06-10',
        endDate: '2024-09-15'
      }
    ],
    
    teamMembers: [
      { id: 1, name: '张经理', role: '项目经理' },
      { id: 2, name: '李主管', role: '技术主管' },
      { id: 3, name: '王专员', role: '开发工程师' },
      { id: 4, name: '赵助理', role: '产品助理' },
      { id: 5, name: '陈顾问', role: '业务顾问' }
    ],
    
    priorities: ['低', '中', '高']
  },

  onLoad: function(options) {
    // 加载员工列表
    this.loadEmployeeList();

    // 如果有传入的任务数据，说明是编辑现有任务
    if (options.task) {
      const task = JSON.parse(decodeURIComponent(options.task));
      console.log('编辑任务数据:', task);
      
      // 设置页面标题
      wx.setNavigationBarTitle({
        title: '编辑任务'
      });
      
      // 延迟处理，确保teamMembers已加载
      setTimeout(() => {
        this.setupEditingTask(task);
      }, 100);
    } else {
      // 设置页面标题
      wx.setNavigationBarTitle({
        title: '指派任务'
      });
      
      // 如果有传入的项目ID，自动选择该项目
      if (options.projectId) {
        const projectIndex = this.data.projects.findIndex(p => p.id == options.projectId);
        if (projectIndex !== -1) {
          this.setData({
            'taskForm.projectIndex': projectIndex
          });
        }
      }
    }
  },

  // 设置编辑任务数据
  setupEditingTask: function(task) {
    // 找到对应的索引
    const projectIndex = this.data.projects.findIndex(p => p.name === task.project);
    const assigneeIndex = this.data.teamMembers.findIndex(m => m.name === task.assignee);
    const priorityIndex = this.data.priorities.findIndex(p => p === task.priorityText);
    
    this.setData({
      'taskForm.title': task.title,
      'taskForm.projectIndex': projectIndex !== -1 ? projectIndex : undefined,
      'taskForm.assigneeIndex': assigneeIndex !== -1 ? assigneeIndex : undefined,
      'taskForm.priorityIndex': priorityIndex !== -1 ? priorityIndex : 1,
      'taskForm.deadline': task.deadline,
      'taskForm.description': task.description || '',
      editingTaskId: task.id
    });
  },

  // 加载员工列表
  loadEmployeeList: function() {
    console.log('加载员工列表');
    
    // 显示加载提示
    wx.showLoading({
      title: '加载员工列表...'
    });

    api.getEmployeeList()
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200 && res.data) {
          console.log('员工列表加载成功:', res.data);
          
          // 转换数据格式，确保符合前端的数据结构
          const teamMembers = res.data.map(employee => ({
            id: employee.id,
            name: employee.name || employee.employeeName || '未知',
            role: employee.position || employee.role || '员工'
          }));
          
          this.setData({
            teamMembers: teamMembers
          });
        } else {
          console.warn('员工列表返回数据异常:', res);
          // 使用默认数据
          wx.showToast({
            title: '员工列表加载失败，使用默认数据',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('加载员工列表失败:', error);
        
        // 如果是401错误，不显示错误提示，让auth模块处理
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '员工列表加载失败',
            icon: 'none'
          });
        }
        
        // 保留默认数据，确保页面正常使用
        console.log('使用默认员工数据');
      });
  },

  // 更新表单数据
  updateForm: function(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`taskForm.${field}`]: value
    });
  },

  // 提交任务
  submitTask: function() {
    const { taskForm, projects, teamMembers, priorities, editingTaskId } = this.data;
    
    // 表单验证
    if (!taskForm.title.trim()) {
      wx.showToast({
        title: '请输入任务标题',
        icon: 'none'
      });
      return;
    }
    
    if (taskForm.projectIndex === undefined) {
      wx.showToast({
        title: '请选择所属项目',
        icon: 'none'
      });
      return;
    }
    
    if (taskForm.assigneeIndex === undefined) {
      wx.showToast({
        title: '请选择负责人',
        icon: 'none'
      });
      return;
    }
    
    if (!taskForm.deadline) {
      wx.showToast({
        title: '请选择截止时间',
        icon: 'none'
      });
      return;
    }

    // 获取全局任务数据
    const pages = getCurrentPages();
    const taskPage = pages.find(p => p.route === 'pages/task/task');
    
    if (taskPage) {
      let tasks = [...taskPage.data.tasks];
      
      if (editingTaskId) {
        // 更新现有任务
        const taskIndex = tasks.findIndex(t => t.id === editingTaskId);
        if (taskIndex !== -1) {
          const priorityMap = { '低': 'low', '中': 'medium', '高': 'high' };
          tasks[taskIndex] = {
            ...tasks[taskIndex],
            title: taskForm.title.trim(),
            project: projects[taskForm.projectIndex].name,
            assignee: teamMembers[taskForm.assigneeIndex].name,
            priority: priorityMap[priorities[taskForm.priorityIndex]],
            priorityText: priorities[taskForm.priorityIndex],
            deadline: taskForm.deadline,
            description: taskForm.description.trim() || '暂无描述'
          };
        }
      } else {
        // 创建新任务
    const newTask = {
      id: Date.now(),
      title: taskForm.title.trim(),
      project: projects[taskForm.projectIndex].name,
      assignee: teamMembers[taskForm.assigneeIndex].name,
      assigner: '张经理', // 当前用户
      status: '待处理',
      priority: taskForm.priorityIndex === 0 ? 'low' : taskForm.priorityIndex === 1 ? 'medium' : 'high',
      priorityText: priorities[taskForm.priorityIndex],
      deadline: taskForm.deadline,
      description: taskForm.description.trim() || '暂无描述',
      isOverdue: false,
      type: 'assignedByMe',
      createTime: new Date().toISOString()
    };
        tasks = [newTask, ...tasks];
      }
      
      // 更新任务管理页面的数据
      taskPage.setData({ tasks: tasks });
      taskPage.calculateStats();
      taskPage.filterTasks();
    }

    // 显示成功提示
    const successMessage = editingTaskId ? '任务更新成功' : '任务指派成功';
    wx.showToast({
      title: successMessage,
      icon: 'success',
      duration: 2000
    });

    // 延迟返回上一页
    setTimeout(() => {
      wx.navigateBack();
    }, 2000);
  },

  // 预览项目详情
  previewProject: function(e) {
    const projectId = e.currentTarget.dataset.id;
    const project = this.data.projects.find(p => p.id === projectId);
    
    if (project) {
      wx.showModal({
        title: project.name,
        content: `描述：${project.description}\n时间：${project.startDate} - ${project.endDate}`,
        showCancel: false,
        confirmText: '确定'
      });
    }
  },

  // 预览成员详情
  previewMember: function(e) {
    const memberId = e.currentTarget.dataset.id;
    const member = this.data.teamMembers.find(m => m.id === memberId);
    
    if (member) {
      wx.showModal({
        title: member.name,
        content: `角色：${member.role}`,
        showCancel: false,
        confirmText: '确定'
      });
    }
  }
}) 