// pages/task-detail/task-detail.js
const api = require('../../utils/api.js')

Page({
  data: {
    taskId: null,
    task: null,
    loading: true,
    showStatusModal: false,
    showAssignModal: false,
    availableStatuses: ['待处理', '进行中', '已完成'],
    selectedStatus: '',
    assignMembers: []
  },

  onLoad: function(options) {
    if (options.id) {
      this.setData({
        taskId: options.id
      });
      this.loadTaskDetail();
      this.loadProjectList();
      this.loadEmployeeList();
    } else if (options.task) {
      // 从编辑页面传入的任务数据
      try {
        const taskData = JSON.parse(decodeURIComponent(options.task));
        this.setData({
          task: taskData,
          loading: false
        });
      } catch (error) {
        console.error('解析任务数据失败:', error);
        wx.showToast({
          title: '数据解析失败',
          icon: 'none'
        });
      }
    } else {
      wx.showToast({
        title: '任务ID无效',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  // 加载任务详情
  loadTaskDetail: function() {
    console.log('加载任务详情:', this.data.taskId);
    
    wx.showLoading({
      title: '加载任务详情...'
    });

    // 这里需要根据实际的后端接口来获取任务详情
    // 暂时使用任务列表接口来模拟
    api.getTaskList({ page: 1, size: 100 })
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200 && res.data) {
          const tasks = res.data.records || res.data;
          const task = tasks.find(t => t.id == this.data.taskId);
          
          if (task) {
            // 转换数据格式
            const convertedTask = this.convertTaskData([task])[0];
            this.setData({
              task: convertedTask,
              loading: false
            });
          } else {
            wx.showToast({
              title: '任务不存在',
              icon: 'none'
            });
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          }
        } else {
          wx.showToast({
            title: '加载任务详情失败',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('加载任务详情失败:', error);
        
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '加载任务详情失败',
            icon: 'none'
          });
        }
      });
  },

  // 加载项目列表
  loadProjectList: function() {
    console.log('加载项目列表');
    
    api.getProjectListSimple()
      .then(res => {
        if (res && res.code === 200 && res.data) {
          console.log('项目列表加载成功:', res.data);
          
          // 这里可以根据需要处理项目数据
          // 暂时只是记录日志，后续可以根据业务需求扩展
        }
      })
      .catch(error => {
        console.error('加载项目列表失败:', error);
      });
  },

  // 加载员工列表
  loadEmployeeList: function() {
    api.getEmployeeList()
      .then(res => {
        if (res && res.code === 200 && res.data) {
          const assignMembers = res.data.map(employee => ({
            id: employee.id,
            name: employee.name || employee.employeeName || '未知'
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

  // 转换任务数据格式
  convertTaskData: function(backendTasks) {
    return backendTasks.map(task => ({
      id: task.id,
      title: task.title || task.taskTitle || '',
      project: task.projectName || task.project || '',
      assignee: task.assigneeName || task.assignee || '',
      assigner: task.assignerName || task.assigner || '',
      status: this.convertTaskStatus(task.status),
      priority: this.convertTaskPriority(task.priority),
      priorityText: this.convertTaskPriorityText(task.priority),
      deadline: this.formatTimeDisplay(task.deadline || task.dueDate || ''),
      description: task.description || task.taskDescription || '',
      isOverdue: this.checkTaskOverdue(task.deadline, task.status),
      createTime: this.formatTimeDisplay(task.createTime || task.createDate || ''),
      updateTime: this.formatTimeDisplay(task.updateTime || task.updateDate || ''),
      attachments: task.attachments || []
    }));
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

  // 检查任务是否逾期
  checkTaskOverdue: function(deadline, status) {
    if (!deadline || status === '已完成') {
      return false;
    }
    const deadlineDate = new Date(deadline);
    const today = new Date();
    return deadlineDate < today;
  },

  // 格式化时间显示，将T替换为空格
  formatTimeDisplay: function(timeStr) {
    if (!timeStr) return '';
    
    // 如果是ISO格式的时间字符串（包含T），将T替换为空格
    if (typeof timeStr === 'string' && timeStr.includes('T')) {
      return timeStr.replace('T', ' ');
    }
    
    return timeStr;
  },

  // 编辑任务
  editTask: function() {
    if (this.data.task) {
      const taskData = encodeURIComponent(JSON.stringify(this.data.task));
      wx.navigateTo({
        url: `/pages/task-form/task-form?task=${taskData}`
      });
    }
  },

  // 删除任务
  deleteTask: function() {
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个任务吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({
            title: '删除中...'
          });
          
          api.deleteTask(this.data.taskId)
            .then(res => {
              wx.hideLoading();
              
              if (res && res.code === 200) {
                wx.showToast({
                  title: '任务已删除',
                  icon: 'success'
                });
                
                setTimeout(() => {
                  wx.navigateBack();
                }, 1500);
              } else {
                wx.showToast({
                  title: res.message || '删除失败',
                  icon: 'none'
                });
              }
            })
            .catch(error => {
              wx.hideLoading();
              console.error('删除任务失败:', error);
              
              if (error.statusCode !== 401) {
                wx.showToast({
                  title: '删除任务失败',
                  icon: 'none'
                });
              }
            });
        }
      }
    });
  },

  // 显示状态更新弹窗
  showStatusUpdate: function() {
    this.setData({
      showStatusModal: true,
      selectedStatus: this.data.task.status
    });
  },

  // 关闭状态更新弹窗
  closeStatusModal: function() {
    this.setData({
      showStatusModal: false
    });
  },

  // 选择状态
  selectStatus: function(e) {
    const status = e.currentTarget.dataset.status;
    this.setData({
      selectedStatus: status
    });
  },

  // 确认状态更新
  confirmStatusUpdate: function() {
    if (!this.data.selectedStatus || this.data.selectedStatus === this.data.task.status) {
      this.closeStatusModal();
      return;
    }

    wx.showLoading({
      title: '更新状态中...'
    });

    const statusMap = { '待处理': 'PENDING', '进行中': 'IN_PROGRESS', '已完成': 'COMPLETED' };
    
    const taskData = {
      id: this.data.taskId,
      status: statusMap[this.data.selectedStatus]
    };

    api.updateTask(taskData)
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200) {
          // 更新本地数据
          const task = { ...this.data.task };
          task.status = this.data.selectedStatus;
          task.isOverdue = this.checkTaskOverdue(task.deadline, task.status);
          
          this.setData({
            task: task
          });
          
          this.closeStatusModal();
          
          wx.showToast({
            title: '状态更新成功',
            icon: 'success'
          });
        } else {
          wx.showToast({
            title: res.message || '状态更新失败',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('状态更新失败:', error);
        
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '状态更新失败',
            icon: 'none'
          });
        }
      });
  },

  // 显示指派弹窗
  showAssignModal: function() {
    this.setData({
      showAssignModal: true
    });
  },

  // 关闭指派弹窗
  closeAssignModal: function() {
    this.setData({
      showAssignModal: false
    });
  },

  // 选择指派人员
  onAssignMemberSelect: function(e) {
    const { member } = e.detail;
    
    wx.showLoading({
      title: '指派中...'
    });

    const taskData = {
      id: this.data.taskId,
      assigneeId: member.id
    };

    api.updateTask(taskData)
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200) {
          // 更新本地数据
          const task = { ...this.data.task };
          task.assignee = member.name;
          
          this.setData({
            task: task
          });
          
          this.closeAssignModal();
          
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