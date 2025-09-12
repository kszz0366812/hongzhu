// pages/edit-project/edit-project.js
const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')

Page({
  data: {
    projectForm: {
      id: '',
      name: '',
      description: '',
      startDate: '',
      endDate: '',
      selectedManager: null,
      statusIndex: 0,
      levelIndex: 0,
      priorityIndex: 1,
      attachments: [],
      invitedMembers: []
    },
    teamMembers: [
      { id: 1, name: '张三', department: '技术部' },
      { id: 2, name: '李四', department: '产品部' },
      { id: 3, name: '王五', department: '设计部' },
      { id: 4, name: '赵六', department: '运营部' }
    ],
    levels: ['长期', '紧急', '重要', '一般'],
    priorities: ['低', '中', '高'],
    statuses: ['待启动', '进行中', '已完成'],
    submitting: false,
    // 邀请成员弹窗相关
    showInviteModal: false,
    availableMembers: [],
    invitedMemberIds: [],
    // 负责人选择弹窗相关
    showManagerModal: false

  },

  onLoad: function(options) {
    console.log('编辑项目页面加载，参数:', options);
    
    // 获取传递过来的项目数据
    if (options.project) {
      const project = JSON.parse(decodeURIComponent(options.project));
      console.log('解析的项目数据:', project);
      
      // 设置基本信息
      this.setData({
        'projectForm.id': project.id || '',
        'projectForm.name': project.name || '',
        'projectForm.description': project.description || '',
        'projectForm.startDate': project.startDate || '',
        'projectForm.endDate': project.endDate || '',
        'projectForm.attachments': project.attachments || [],
        'projectForm.invitedMembers': project.invitedMembers || []
      });
      
      // 设置员工列表，供邀请成员使用
      if (project.allTeamMembers && Array.isArray(project.allTeamMembers)) {
        this.setData({
          teamMembers: project.allTeamMembers
        });
        
        // 按照项目列表页面的简单逻辑，直接设置成员数据
        if (project.invitedMembers && Array.isArray(project.invitedMembers)) {
          // 确保每个成员都有id字段
          const processedInvitedMembers = project.invitedMembers.map(member => ({
            id: member.employeeId, 
            name: member.name,
            department: member.department || '未知部门',
            role: member.role || '成员',
            avatar: member.avatar
          }));
          
          this.setData({
            'projectForm.invitedMembers': processedInvitedMembers
          });
          

        }
      }
      

      
      // 设置项目等级索引 - 处理中英文映射
      let levelIndex = 0;
      if (project.level === '长期' || project.level === 'LONG_TERM') {
        levelIndex = 0;
      } else if (project.level === '紧急' || project.level === 'URGENT') {
        levelIndex = 1;
      } else if (project.level === '重要' || project.level === 'IMPORTANT') {
        levelIndex = 2;
      } else if (project.level === '一般' || project.level === 'NORMAL') {
        levelIndex = 3;
      }
      
      this.setData({
        'projectForm.levelIndex': levelIndex
      });
      
      // 设置优先级索引 - 处理中英文映射
      let priorityIndex = 1; // 默认中
      if (project.priority === '低' || project.priority === 'LOW') {
        priorityIndex = 0;
      } else if (project.priority === '中' || project.priority === 'MEDIUM') {
        priorityIndex = 1;
      } else if (project.priority === '高' || project.priority === 'HIGH') {
        priorityIndex = 2;
      }
      
      this.setData({
        'projectForm.priorityIndex': priorityIndex
      });
      
      // 设置状态索引 - 处理中英文状态映射
      let statusIndex = 0;
      if (project.status === '进行中' || project.status === 'ongoing') {
        statusIndex = 1;
      } else if (project.status === '已完成' || project.status === 'completed') {
        statusIndex = 2;
      } else if (project.status === '待安排' || project.status === 'pending') {
        statusIndex = 0;
      }
      
      this.setData({
        'projectForm.statusIndex': statusIndex
      });
      
      // 设置项目负责人（如果有的话）
      if (project.managerName) {
        this.setData({
          'projectForm.selectedManager': {
            id: project.id, // 临时ID，实际应该从后端获取
            name: project.managerName,
            department: '未知部门',
            role: '负责人'
          }
        });
      }
    } else {
      console.log('没有传递项目数据，使用默认值');
    }
  },

  // 更新表单数据
  updateForm: function(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`projectForm.${field}`]: value
    });
  },

  // 更新选择器数据
  updatePicker: function(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`projectForm.${field}`]: value
    });
  },

  // 选择文件
  chooseFiles: function() {
    const that = this;
    wx.chooseMessageFile({
      count: 5,
      type: 'file',
      success: function(res) {
        const newAttachments = res.tempFiles.map(file => ({
          name: file.name,
          size: that.formatFileSize(file.size),
          path: file.path
        }));
        
        const currentAttachments = that.data.projectForm.attachments;
        that.setData({
          'projectForm.attachments': [...currentAttachments, ...newAttachments]
        });
      }
    });
  },

  // 移除附件
  removeAttachment: function(e) {
    const index = e.currentTarget.dataset.index;
    const attachments = this.data.projectForm.attachments;
    attachments.splice(index, 1);
    this.setData({
      'projectForm.attachments': attachments
    });
  },

  // 邀请成员
  inviteMembers: function() {
    const { teamMembers, projectForm } = this.data;
    
    // 获取当前已邀请的成员ID列表
    const invitedMemberIds = (projectForm.invitedMembers || []).map(m => m.id);
    
    // 使用人员选择器组件，传递完整的员工列表
    // 允许用户重新选择成员组合，系统会自动判断是邀请还是删除
    this.setData({
      showInviteModal: true,
      availableMembers: teamMembers, // 传递完整的员工列表
      invitedMemberIds: invitedMemberIds
    });
  },

  // 移除成员
  removeMember: function(e) {
    const memberId = e.currentTarget.dataset.id;
    const invitedMembers = this.data.projectForm.invitedMembers.filter(m => m.id !== memberId);
    
    this.setData({
      'projectForm.invitedMembers': invitedMembers
    });
    
    wx.showToast({
      title: '已移除成员',
      icon: 'success'
    });
  },

  // 格式化文件大小
  formatFileSize: function(size) {
    if (size < 1024) {
      return size + 'B';
    } else if (size < 1024 * 1024) {
      return (size / 1024).toFixed(1) + 'KB';
    } else {
      return (size / (1024 * 1024)).toFixed(1) + 'MB';
    }
  },

  // 保存项目
  saveProject: function() {
    const form = this.data.projectForm;
    
    // 表单验证
    if (!form.name.trim()) {
      wx.showToast({
        title: '请输入项目名称',
        icon: 'none'
      });
      return;
    }

    if (!form.startDate || !form.endDate) {
      wx.showToast({
        title: '请选择项目周期',
        icon: 'none'
      });
      return;
    }

    // 验证结束时间不能早于开始时间
    if (new Date(form.endDate) <= new Date(form.startDate)) {
      wx.showToast({
        title: '结束时间不能早于开始时间',
        icon: 'none'
      });
      return;
    }

    if (this.data.submitting) {
      return;
    }

    this.setData({ submitting: true });

    // 获取用户信息
    auth.getUserInfo()
      .then(userInfo => {
        if (!userInfo.employeeId) {
          console.log('用户未绑定员工号，getUserInfo已自动触发绑定流程');
          const app = getApp();
          app.globalData.pendingOperation = () => {
            console.log('员工绑定成功，继续更新项目');
            this.saveProject();
          };
          return;
        }
        
        // 构建项目数据 - 使用与列表页邀请员工相同的数据结构
        const statusMap = ['pending', 'ongoing', 'completed'];
        const projectData = {
          id: form.id,
          name: form.name.trim(),
          description: form.description ? form.description.trim() : '',
          startDate: form.startDate,
          endDate: form.endDate,
          status: statusMap[form.statusIndex] || 'pending',
          level: this.data.levels[form.levelIndex] || '一般',
          managerId: userInfo.employeeId,
          attachments: form.attachments,
          invitedMembers: form.invitedMembers.map(member => member.id) // 只传递ID列表，与列表页保持一致
        };



        // 调用更新项目API
        return api.updateProject(projectData);
      })
      .then(res => {
        this.setData({ submitting: false });
        
        if (res && res.code === 200) {
          // 设置全局刷新标记，通知相关页面刷新数据
          const app = getApp();
          if (app && app.globalData) {
            app.globalData.needRefreshProjects = true;
            console.log('已设置全局刷新标记，通知相关页面刷新数据');
          }
          
          wx.showToast({
            title: '项目更新成功',
            icon: 'success',
            duration: 2000
          });

          // 延迟返回上一页
          setTimeout(() => {
            wx.navigateBack();
          }, 2000);
        } else {
          wx.showToast({
            title: res?.message || '项目更新失败',
            icon: 'error'
          });
        }
      })
      .catch(error => {
        this.setData({ submitting: false });
        wx.showToast({
          title: '网络请求失败',
          icon: 'error'
        });
      });
  },

  // 返回上一页
  goBack: function() {
    wx.navigateBack();
  },

  // 加载员工列表
  loadEmployeeList: function() {
    // 显示加载提示
    wx.showLoading({
      title: '加载员工列表...'
    });

    api.getEmployeeList()
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200 && res.data) {
          // 转换数据格式，确保符合前端的数据结构
          const teamMembers = res.data.map(employee => ({
            id: employee.id,
            name: employee.name || employee.employeeName || '未知',
            department: employee.department || '未知部门',
            role: employee.position || employee.role || '员工'
          }));
          
          this.setData({
            teamMembers: teamMembers
          });
        } else {
          // 使用默认数据
          wx.showToast({
            title: '员工列表加载失败，使用默认数据',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        // 如果是401错误，不显示错误提示，让auth模块处理
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '员工列表加载失败',
            icon: 'none'
          });
        }
      });
  },

  // 选择负责人
  selectManager: function() {
    this.setData({
      showManagerModal: true
    });
  },

  // 邀请成员 - 完全按照项目列表页面的方法实现
  inviteMembers: function() {
    const { teamMembers, projectForm } = this.data;
    
    // 获取已邀请成员的ID列表
    const invitedMemberIds = (projectForm.invitedMembers || []).map(m => m.id);
    // 显示所有团队成员，让用户可以看到已邀请和未邀请的状态
    const availableMembers = teamMembers;
    

    
    this.setData({
      showInviteModal: true,
      availableMembers: availableMembers,
      invitedMemberIds: invitedMemberIds
    });
  },

  // 移除成员 - 按照项目列表页面的逻辑
  removeMember: function(e) {
    const memberId = e.currentTarget.dataset.id;
    const invitedMembers = this.data.projectForm.invitedMembers.filter(m => m.id !== memberId);
    
    this.setData({
      'projectForm.invitedMembers': invitedMembers
    });
    
    wx.showToast({
      title: '已移除成员',
      icon: 'success'
    });
  },

  // 人员选择组件事件处理
  onInviteModalClose: function() {
    this.setData({
      showInviteModal: false,
      availableMembers: [],
      invitedMemberIds: []
    });
  },

  onInviteMemberSelect: function(e) {
    const { members } = e.detail;
    
    if (!members || members.length === 0) {
      wx.showToast({
        title: '请选择要邀请的成员',
        icon: 'none'
      });
      return;
    }
    
    // 替换整个成员列表，而不是追加
    // 这样用户可以选择新的成员组合，系统会自动判断是邀请还是删除
    this.setData({
      'projectForm.invitedMembers': members
    });
    
    // 关闭弹窗
    this.setData({
      showInviteModal: false,
      availableMembers: [],
      invitedMemberIds: []
    });
    
    // 显示成功提示
    const memberNames = members.map(m => m.name).join('、');
    wx.showToast({
      title: `已更新成员列表：${memberNames}`,
      icon: 'success'
    });
  },

  // 负责人选择器事件处理
  onManagerModalClose: function() {
    this.setData({
      showManagerModal: false
    });
  },

  onManagerSelect: function(e) {
    const { member } = e.detail;
    
    if (!member) {
      wx.showToast({
        title: '请选择负责人',
        icon: 'none'
      });
      return;
    }
    
    // 设置选中的负责人
    this.setData({
      'projectForm.selectedManager': member
    });
    
    // 关闭弹窗
    this.setData({
      showManagerModal: false
    });
    
    // 显示成功提示
    wx.showToast({
      title: `已选择${member.name}为负责人`,
      icon: 'success'
    });
  }
}) 