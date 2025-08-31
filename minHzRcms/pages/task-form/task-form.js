// pages/task-form/task-form.js
const api = require('../../utils/api.js')

Page({
  data: {
    // 操作模式：create-创建，edit-编辑
    operationMode: 'create',
    editingTaskId: null,
    editingTask: null, // 编辑模式下的任务数据
    
    taskForm: {
      title: '',
      projectIndex: undefined,
      assigneeIndex: undefined,
      statusIndex: 0, // 编辑模式下使用
      priorityIndex: 1, // 默认中优先级
      deadline: '',
      description: '',
      attachments: []
    },
    
    // 项目数据
    projects: [
      { id: 1, name: '客户管理系统开发' },
      { id: 2, name: '销售数据分析平台' },
      { id: 3, name: '团队协作工具优化' },
      { id: 4, name: '市场调研报告系统' }
    ],
    
    // 团队成员数据
    teamMembers: [
      { id: 1, name: '张经理' },
      { id: 2, name: '李主管' },
      { id: 3, name: '王专员' },
      { id: 4, name: '赵助理' },
      { id: 5, name: '陈顾问' }
    ],
    
    priorities: ['低', '中', '高'],
    taskStatuses: ['待处理', '进行中', '已完成'] // 编辑模式下使用
  },

  onLoad: function(options) {
    // 检查是否是编辑模式
    if (options.task) {
      this.setData({
        operationMode: 'edit'
      });
      
      // 设置页面标题
      wx.setNavigationBarTitle({
        title: '编辑任务'
      });
      
      try {
        const task = JSON.parse(decodeURIComponent(options.task));
        console.log('编辑任务数据:', task);
        // 保存任务数据，等数据加载完成后再设置
        this.setData({
          editingTask: task
        });
      } catch (error) {
        console.error('解析任务数据失败:', error);
        wx.showToast({
          title: '数据解析失败',
          icon: 'none'
        });
      }
    } else {
      // 创建模式：设置默认截止时间为7天后
      const today = new Date();
      const defaultDeadline = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
      const deadlineStr = defaultDeadline.toISOString().split('T')[0];
      
      this.setData({
        'taskForm.deadline': deadlineStr
      });
      
      // 设置页面标题
      wx.setNavigationBarTitle({
        title: '创建任务'
      });
    }

    // 加载项目列表
    this.loadProjectList();
    // 加载员工列表
    this.loadEmployeeList();
  },

  // 加载项目列表
  loadProjectList: function() {
    console.log('加载项目列表');
    
    // 显示加载提示
    wx.showLoading({
      title: '加载项目列表...'
    });

    // 调用获取项目列表接口，获取所有项目
    api.getProjectListSimple()
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200 && res.data) {
          console.log('项目列表加载成功:', res.data);
          
          // 转换数据格式，确保符合前端的数据结构
          const projects = res.data.map(project => ({
            id: project.id,
            name: project.name || project.projectName || '未知项目'
          }));
          
          this.setData({
            projects: projects
          });
          
          // 如果是编辑模式且项目列表已加载，尝试设置编辑任务数据
          if (this.data.operationMode === 'edit' && this.data.editingTask) {
            this.setupEditingTask(this.data.editingTask);
          }
        } else {
          console.warn('项目列表返回数据异常:', res);
          // 使用默认数据
          wx.showToast({
            title: '项目列表加载失败，使用默认数据',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('加载项目列表失败:', error);
        
        // 如果是401错误，不显示错误提示，让auth模块处理
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '项目列表加载失败',
            icon: 'none'
          });
        }
        
        // 保留默认数据，确保页面正常使用
        console.log('使用默认项目数据');
      });
  },

  // 设置编辑任务数据
  setupEditingTask: function(task) {
    console.log('设置编辑任务数据:', task);
    console.log('当前项目列表:', this.data.projects);
    console.log('当前员工列表:', this.data.teamMembers);
    
    // 确保数据已加载
    if (!this.data.projects || this.data.projects.length === 0 || 
        !this.data.teamMembers || this.data.teamMembers.length === 0) {
      console.log('数据未完全加载，跳过设置编辑任务数据');
      return;
    }
    
    // 找到对应的索引
    const projectIndex = this.data.projects.findIndex(p => p.name === task.project);
    const assigneeIndex = this.data.teamMembers.findIndex(m => m.name === task.assignee);
    const statusIndex = this.data.taskStatuses.findIndex(s => s === task.status);
    const priorityIndex = this.data.priorities.findIndex(p => p === task.priorityText);
    
    console.log('找到的索引:', { projectIndex, assigneeIndex, statusIndex, priorityIndex });
    
    this.setData({
      'taskForm.title': task.title,
      'taskForm.projectIndex': projectIndex !== -1 ? projectIndex : undefined,
      'taskForm.assigneeIndex': assigneeIndex !== -1 ? assigneeIndex : undefined,
      'taskForm.statusIndex': statusIndex !== -1 ? statusIndex : 0,
      'taskForm.priorityIndex': priorityIndex !== -1 ? priorityIndex : 1,
      'taskForm.deadline': task.deadline,
      'taskForm.description': task.description || '',
      'taskForm.attachments': task.attachments || [],
      editingTaskId: task.id
    });
    
    console.log('编辑任务数据设置完成');
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
            name: employee.name || employee.employeeName || '未知'
          }));
          
          this.setData({
            teamMembers: teamMembers
          });
          
          // 如果是编辑模式且员工列表已加载，尝试设置编辑任务数据
          if (this.data.operationMode === 'edit' && this.data.editingTask) {
            this.setupEditingTask(this.data.editingTask);
          }
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

  // 附件变化事件处理
  onAttachmentChange: function(e) {
    const { attachments } = e.detail;
    this.setData({
      'taskForm.attachments': attachments
    });
  },

  // 创建或更新任务
  submitTask: function() {
    const { taskForm, projects, teamMembers, priorities, taskStatuses, operationMode, editingTaskId } = this.data;
    
    // 表单验证
    if (!taskForm.title || !taskForm.title.trim()) {
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

    // 验证截止时间不能早于今天
    const today = new Date();
    const deadline = new Date(taskForm.deadline);
    if (deadline < today) {
      wx.showToast({
        title: '截止时间不能早于今天',
        icon: 'none'
      });
      return;
    }

    // 构建发送给后端的任务数据
    const projectId = projects[taskForm.projectIndex].id;
    const assigneeId = teamMembers[taskForm.assigneeIndex].id;
    const priorityMap = { '低': 'LOW', '中': 'MEDIUM', '高': 'HIGH' };
    const statusMap = { '待处理': 'PENDING', '进行中': 'IN_PROGRESS', '已完成': 'COMPLETED' };
    
    const taskData = {
      title: taskForm.title.trim(),
      projectId: projectId,
      assigneeId: assigneeId,
      priority: priorityMap[priorities[taskForm.priorityIndex]],
      deadline: taskForm.deadline,
      description: taskForm.description ? taskForm.description.trim() : '',
      attachments: taskForm.attachments
    };

    // 编辑模式下添加ID和状态
    if (operationMode === 'edit') {
      taskData.id = editingTaskId;
      taskData.status = statusMap[taskStatuses[taskForm.statusIndex]];
    }

    console.log('发送给后端的任务数据:', taskData);

    // 显示加载提示
    const loadingTitle = operationMode === 'edit' ? '更新任务中...' : '创建任务中...';
    wx.showLoading({
      title: loadingTitle
    });

    // 根据操作模式调用相应的API
    const apiCall = operationMode === 'edit' ? api.updateTask(taskData) : api.createTask(taskData);
    
    apiCall.then(res => {
      wx.hideLoading();
      
      if (res && res.code === 200) {
        console.log(operationMode === 'edit' ? '任务更新成功:' : '任务创建成功:', res.data);
        
        // 显示成功提示
        const successTitle = operationMode === 'edit' ? '任务更新成功' : '任务创建成功';
        wx.showToast({
          title: successTitle,
          icon: 'success',
          duration: 2000
        });

        // 延迟返回上一页
        setTimeout(() => {
          wx.navigateBack();
        }, 2000);
      } else {
        const errorTitle = operationMode === 'edit' ? '更新任务失败' : '创建任务失败';
        wx.showToast({
          title: res.message || errorTitle,
          icon: 'none'
        });
      }
    }).catch(error => {
      wx.hideLoading();
      const errorTitle = operationMode === 'edit' ? '更新任务失败' : '创建任务失败';
      console.error(errorTitle + ':', error);
      
      if (error.statusCode !== 401) {
        wx.showToast({
          title: errorTitle,
          icon: 'none'
        });
      }
    });
  }
}) 