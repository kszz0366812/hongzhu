// pages/task/task.js
const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')

Page({
  data: {
    activePrimaryTab: 'myTasks',
    activeSecondaryTab: 'all',
    statsData: [],
    actionButtons: [],
    primaryTabsData: [],
    secondaryTabsData: [],
    tasks: [],
    filteredTasks: [],
    // 指派弹窗相关
    showAssignModal: false,
    assignTaskId: null,
    assignMembers: []
  },

  onLoad: function() {
    this.initComponentData();
    this.loadTasks();
  },

  onShow: function() {
    this.loadTasks();
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    console.log('下拉刷新任务列表');
    
    // 重新加载数据
    this.loadTasks();
    
    // 停止下拉刷新
    wx.stopPullDownRefresh();
  },

  // 初始化组件数据
  initComponentData: function() {
    this.setData({
      statsData: [
        { number: 0, label: '总任务' },
        { number: 0, label: '进行中' },
        { number: 0, label: '已逾期' },
        { number: 0, label: '已完成' }
      ],
      actionButtons: [
        { text: '+ 创建任务', type: 'secondary' }
      ],
      primaryTabsData: [
        { key: 'myTasks', label: '我的任务' },
        { key: 'assignedByMe', label: '我指派的' }
      ],
      secondaryTabsData: [
        { key: 'all', label: '全部' },
        { key: 'ongoing', label: '进行中' },
        { key: 'pending', label: '待处理' },
        { key: 'completed', label: '已完成' }
      ]
    });
  },

  // 加载任务数据
  loadTasks: function() {
    console.log('加载任务数据');
    
    wx.showLoading({
      title: '加载任务中...'
    });

    // 构建查询参数
    const params = {
      page: 1,
      size: 100
    };

    // 根据当前标签页状态添加筛选条件
    if (this.data.activePrimaryTab === 'myTasks') {
      // 我的任务：传递当前用户的员工ID作为assigneeId
      // 注意：不传递状态筛选，让服务端返回所有状态的任务
      this.getCurrentUserInfo().then(userInfo => {
        params.assigneeId = userInfo.employeeId;
        this.callTaskListAPI(params);
      }).catch(error => {
        console.error('获取用户信息失败:', error);
        wx.hideLoading();
        wx.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        });
      });
      return; // 提前返回，避免重复调用API
    } else if (this.data.activePrimaryTab === 'assignedByMe') {
      // 我指派的：传递当前用户的员工ID作为assignerId
      // 注意：不传递状态筛选，让服务端返回所有状态的任务
      this.getCurrentUserInfo().then(userInfo => {
        params.assignerId = userInfo.employeeId;
        this.callTaskListAPI(params);
      }).catch(error => {
        console.error('获取用户信息失败:', error);
        wx.hideLoading();
        wx.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        });
      });
      return; // 提前返回，避免重复调用API
    }

    // 如果没有匹配的标签页，直接调用API（默认情况）
    this.callTaskListAPI(params);
  },

  // 调用任务列表API的通用方法
  callTaskListAPI: function(params) {
    // 移除状态筛选，让服务端返回所有状态的任务
    // 状态筛选在前端进行，确保数据完整性
    
    api.getTaskList(params)
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200 && res.data) {
          console.log('任务数据加载成功:', res.data);
          
          // 转换后端数据格式为前端格式
          const tasks = this.convertTaskData(res.data.records || res.data);
          
          this.setData({
            tasks: tasks
          });
          
          this.calculateStats();
          this.filterTasks();
        } else {
          console.warn('任务数据返回异常:', res);
          wx.showToast({
            title: '任务数据加载失败',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('加载任务数据失败:', error);
        
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '任务数据加载失败',
            icon: 'none'
          });
        }
      });
  },

  // 获取当前用户信息
  getCurrentUserInfo: function() {
    return auth.getUserInfo();
  },

  // 转换后端任务数据格式为前端格式
  convertTaskData: function(backendTasks) {
    console.log('原始任务数据:', backendTasks);
    
    const convertedTasks = backendTasks.map(task => {
      const convertedTask = {
        id: task.id,
        title: task.title || task.taskTitle || '',
        projectId: task.projectId,
        projectName: task.projectName || task.project || '',
        assigneeId: task.assigneeId,
        assignee: task.assigneeName || task.assignee || '',
        deadline: task.deadline || task.dueDate || '',
        priority: this.convertTaskPriorityText(task.priority),
        priorityClass: this.convertTaskPriority(task.priority),
        status: this.convertTaskStatus(task.status),
        type: this.determineTaskType(task),
        description: task.description || task.taskDescription || '',
        attachments: task.attachments || [],
        // 明确设置进度为undefined，避免显示进度条
        progress: undefined,
        progressLabel: undefined
      };
      
      console.log('转换后的任务数据:', convertedTask);
      return convertedTask;
    });
    
    return convertedTasks;
  },

  // 转换任务状态
  convertTaskStatus: function(status) {
    const statusMap = {
      'PENDING': '待处理',
      'IN_PROGRESS': '进行中',
      'COMPLETED': '已完成',
      'OVERDUE': '已逾期'
    };
    return statusMap[status] || status || '待处理';
  },

  // 转换任务优先级
  convertTaskPriority: function(priority) {
    const priorityMap = {
      'LOW': 'low',
      'MEDIUM': 'medium',
      'HIGH': 'high'
    };
    return priorityMap[priority] || 'medium';
  },

  // 转换任务优先级文本
  convertTaskPriorityText: function(priority) {
    const priorityTextMap = {
      'LOW': '低',
      'MEDIUM': '中',
      'HIGH': '高'
    };
    return priorityTextMap[priority] || '中';
  },

  // 确定任务类型
  determineTaskType: function(task) {
    // 任务类型标识，用于区分任务和项目
    return 'task';
  },

  // 加载员工列表
  loadEmployeeList: function() {
    console.log('加载员工列表');
    
    api.getEmployeeList()
      .then(res => {
        if (res && res.code === 200 && res.data) {
          console.log('员工列表加载成功:', res.data);
          
          const assignMembers = res.data.map(employee => ({
            id: employee.id,
            name: employee.name || employee.employeeName || '未知',
            avatar: '👤'
          }));
          
          this.setData({
            assignMembers: assignMembers
          });
        }
      })
      .catch(error => {
        console.error('加载员工列表失败:', error);
      });
  },

  // 加载项目成员列表（用于指派任务）
  loadProjectMembers: function(projectId, currentAssigneeId) {
    if (!projectId) {
      console.warn('项目ID不存在，无法加载项目成员');
      return;
    }
    
    console.log('加载项目成员列表，项目ID:', projectId, '当前责任人ID:', currentAssigneeId);
    
    api.getProjectDetail(projectId)
      .then(res => {
        if (res && res.code === 200 && res.data && res.data.projectMembers) {
          console.log('项目成员列表加载成功:', res.data.projectMembers);
          
          // 过滤掉当前责任人，避免重复指派
          const assignMembers = res.data.projectMembers
            .filter(member => member.employeeId !== currentAssigneeId)
            .map(member => ({
              id: member.employeeId,
              name: member.employeeName || '未知',
              avatar: member.avatar || '👤',
              role: member.role || ''
            }));
          
          if (assignMembers.length === 0) {
            wx.showToast({
              title: '项目中没有其他可指派的成员',
              icon: 'none'
            });
            return;
          }
          
          this.setData({
            assignMembers: assignMembers
          });
        } else {
          console.warn('项目详情数据返回异常:', res);
          wx.showToast({
            title: '项目成员加载失败',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        console.error('加载项目成员失败:', error);
        wx.showToast({
          title: '项目成员加载失败',
          icon: 'none'
        });
      });
  },

  // 计算统计数据
  calculateStats: function() {
    const tasks = this.data.tasks;
    const totalTasks = tasks.length;
    const ongoingTasks = tasks.filter(t => t.status === '进行中').length;
    const completedTasks = tasks.filter(t => t.status === '已完成').length;
    const overdueTasks = tasks.filter(t => new Date(t.deadline) < new Date() && t.status !== '已完成').length;

    this.setData({
      statsData: [
        { number: totalTasks, label: '总任务' },
        { number: ongoingTasks, label: '进行中' },
        { number: overdueTasks, label: '已逾期' },
        { number: completedTasks, label: '已完成' }
      ]
    });
  },

  // 格式化任务数据为组件格式
  formatTaskForComponent: function(task) {
    return {
      id: task.id,
      title: task.title,
      priority: task.priority,
      priorityClass: task.priorityClass,
      status: task.status,
      info: [
        { label: '项目', value: task.projectName },
        { label: '负责人', value: task.assignee },
        { label: '截止时间', value: task.deadline }
      ],
      description: task.description,
      actions: this.getTaskActions(task)
    };
  },

  // 判断任务状态是否为最终态
  isFinalStatus: function(status) {
    return status === '已完成' || status === '已取消' || status === '已拒绝';
  },

  // 获取任务操作按钮
  getTaskActions: function(task) {
    const actions = [
      { text: '指派', type: '' },
      { text: '编辑', type: '' }
    ];

    // 根据任务状态显示不同的状态按钮名称
    let statusButtonText = '';
    let isDisabled = false;
    
    switch (task.status) {
      case '待处理':
        statusButtonText = '开始任务';
        isDisabled = false;
        break;
      case '进行中':
        statusButtonText = '完成任务';
        isDisabled = false;
        break;
      case '已完成':
        statusButtonText = '已完成';
        isDisabled = true;
        break;
      default:
        statusButtonText = '开始任务';
        isDisabled = false;
        break;
    }
    
    // 添加状态按钮
    actions.push({ 
      text: statusButtonText, 
      type: 'status-btn',
      disabled: isDisabled
    });

    return actions;
  },

  // 筛选任务
  filterTasks: function() {
    let filteredTasks = [];
    const { tasks, activePrimaryTab, activeSecondaryTab } = this.data;

    // 按任务类型筛选（一级标签）
    switch (activePrimaryTab) {
      case 'myTasks':
        // 我的任务：显示所有任务，因为已经通过assigneeId筛选
        filteredTasks = [...tasks];
        break;
      case 'assignedByMe':
        // 我指派的：显示所有任务，因为已经通过assignerId筛选
        filteredTasks = [...tasks];
        break;
      default:
        // 默认情况：显示所有任务
        filteredTasks = [...tasks];
        break;
    }

    // 按状态筛选（二级标签）- 前端筛选，不调用服务端接口
    switch (activeSecondaryTab) {
      case 'all':
        // 不进行状态筛选，显示所有任务
        break;
      case 'ongoing':
        filteredTasks = filteredTasks.filter(task => task.status === '进行中');
        break;
      case 'pending':
        filteredTasks = filteredTasks.filter(task => task.status === '待处理');
        break;
      case 'completed':
        filteredTasks = filteredTasks.filter(task => task.status === '已完成');
        break;
    }

    // 格式化任务数据为组件格式
    const formattedTasks = filteredTasks.map(task => this.formatTaskForComponent(task));

    this.setData({
      filteredTasks: formattedTasks
    });
  },

  // 组件事件处理
  onActionButtonTap: function(e) {
    const { button } = e.detail;
    if (button.text.includes('创建')) {
      this.navigateToCreateTask();
    }
  },

  onPrimaryTabChange: function(e) {
    const { key } = e.detail;
    this.setData({ activePrimaryTab: key });
    // 切换一级标签页时重新加载数据，因为需要传递不同的参数
    this.loadTasks();
  },

  onSecondaryTabChange: function(e) {
    const { key } = e.detail;
    console.log('切换二级标签页:', key);
    
    this.setData({ activeSecondaryTab: key });
    
    // 状态切换只进行前端筛选，不重新调用服务端接口
    // 这样可以确保数据完整性，避免某些状态的任务缺失
    this.filterTasks();
  },

  onTaskActionTap: function(e) {
    const { action } = e.detail;
    const taskId = e.detail.item.id;
    
    // 检查按钮是否被禁用
    if (action.disabled) {
      wx.showToast({
        title: '该操作在当前状态下不可用',
        icon: 'none'
      });
      return;
    }
    
    switch (action.text) {
      case '指派':
        this.assignTask(taskId);
        break;
      case '编辑':
        this.editTask(taskId);
        break;
      case '开始任务':
        this.startTask(taskId);
        break;
      case '完成任务':
        this.completeTask(taskId);
        break;
      default:
        // 处理其他状态按钮
        if (action.type === 'status-btn') {
          // 根据当前任务状态决定执行哪个操作
          const task = this.data.tasks.find(t => t.id === taskId);
          if (task) {
            if (task.status === '待处理') {
              this.startTask(taskId);
            } else if (task.status === '进行中') {
              this.completeTask(taskId);
            }
          }
        }
        break;
    }
  },

  // 导航到任务表单页面
  navigateToCreateTask: function() {
    wx.navigateTo({
      url: '/pages/task-form/task-form'
    });
  },

  // 导航到任务详情页面
  navigateToTaskDetail: function(e) {
    const taskId = e.detail.item.id;
    wx.navigateTo({
      url: `/pages/task-detail/task-detail?id=${taskId}`
    });
  },

  // 指派任务
  assignTask: function(taskId) {
    // 找到对应的任务数据
    const task = this.data.tasks.find(t => t.id === taskId);
    console.log('指派任务，任务ID:', taskId, '任务数据:', task);
    
    if (task) {
      console.log('任务项目ID:', task.projectId, '当前责任人ID:', task.assigneeId);
      
      // 先加载项目成员，然后显示指派弹窗
      this.loadProjectMembers(task.projectId, task.assigneeId);
      
      this.setData({
        showAssignModal: true,
        assignTaskId: taskId
      });
    } else {
      wx.showToast({
        title: '任务数据不存在',
        icon: 'error'
      });
    }
  },

  // 编辑任务
  editTask: function(taskId) {
    // 找到对应的任务数据
    const task = this.data.tasks.find(t => t.id === taskId);
    if (task) {
      // 将任务数据转换为编辑页面期望的格式
      const taskData = {
        id: task.id,
        title: task.title,
        project: task.projectName,
        assignee: task.assignee,
        status: task.status,
        priorityText: task.priority,
        deadline: task.deadline,
        description: task.description || '',
        attachments: task.attachments || []
      };
      
      wx.navigateTo({
        url: `/pages/task-form/task-form?task=${encodeURIComponent(JSON.stringify(taskData))}`
      });
    } else {
      wx.showToast({
        title: '任务数据不存在',
        icon: 'error'
      });
    }
  },

  // 开始任务
  startTask: function(taskId) {
    wx.showLoading({
      title: '更新状态中...'
    });

    const taskData = {
      id: taskId,
      status: 'IN_PROGRESS'
    };

    api.updateTask(taskData)
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200) {
          wx.showToast({
            title: '任务已开始',
            icon: 'success'
          });
          this.loadTasks();
        } else {
          wx.showToast({
            title: res.message || '状态更新失败',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('开始任务失败:', error);
        
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '状态更新失败',
            icon: 'none'
          });
        }
      });
  },

  // 完成任务
  completeTask: function(taskId) {
    wx.showLoading({
      title: '更新状态中...'
    });

    const taskData = {
      id: taskId,
      status: 'COMPLETED'
    };

    api.updateTask(taskData)
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200) {
          wx.showToast({
            title: '任务已完成',
            icon: 'success'
          });
          this.loadTasks();
        } else {
          wx.showToast({
            title: res.message || '状态更新失败',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('完成任务失败:', error);
        
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '状态更新失败',
            icon: 'none'
          });
        }
      });
  },

  // 指派弹窗相关方法
  // 关闭指派弹窗
  closeAssignModal: function() {
    this.setData({
      showAssignModal: false,
      assignTaskId: null
    });
  },

  // 人员选择组件事件处理
  onAssignModalClose: function() {
    this.setData({
      showAssignModal: false,
      assignTaskId: null
    });
  },

  onAssignMemberSelect: function(e) {
    const { member } = e.detail;
    const taskId = this.data.assignTaskId;
    
    wx.showLoading({
      title: '指派中...'
    });

    const taskData = {
      id: taskId,
      assigneeId: member.id
    };

    api.updateTask(taskData)
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200) {
          // 更新本地数据
          const tasks = this.data.tasks.map(task => {
            if (task.id === taskId) {
              return { ...task, assignee: member.name };
            }
            return task;
          });
          
          this.setData({
            tasks: tasks
          });
          
          // 重新筛选和格式化任务
          this.calculateStats();
          this.filterTasks();
          
          // 关闭弹窗
          this.setData({
            showAssignModal: false,
            assignTaskId: null
          });
          
          // 显示成功提示
          wx.showToast({
            title: `已指派给${member.name}`,
            icon: 'success'
          });
        } else {
          wx.showToast({
            title: res.message || '指派失败',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('指派任务失败:', error);
        
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '指派任务失败',
            icon: 'none'
          });
        }
      });
  }
})
